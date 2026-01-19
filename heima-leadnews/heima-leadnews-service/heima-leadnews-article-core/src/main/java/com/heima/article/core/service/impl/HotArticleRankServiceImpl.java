package com.heima.article.core.service.impl;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.heima.apis.wemedia.WeMediaClient;
import com.heima.article.core.convert.ArticleConvert;
import com.heima.article.core.service.ArticleChannelService;
import com.heima.article.core.service.ArticleService;
import com.heima.article.core.service.HotArticleRankService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.articlecore.dto.ArticleBehaviorAgg;
import com.heima.model.articlecore.dto.ArticleBehaviorWindowResult;
import com.heima.model.articlecore.entity.Article;
import com.heima.model.articlecore.entity.ArticleChannel;
import com.heima.model.articlecore.vo.FrontArticleListVo;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service
public class HotArticleRankServiceImpl implements HotArticleRankService {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleChannelService articleChannelService;

    @Autowired
    private WeMediaClient weMediaClient;

    private final int HOT_LIMIT = 10;


    //写路径
    //Streams / 下游只做 ZADD
    //无裁剪、无条件、幂等
    @Override
    public void refreshHotRank(ArticleBehaviorWindowResult windowResult) {
        if (windowResult.getArticleId() == null || windowResult.getAgg() == null) {
            log.warn("refreshHotRank param error:{}", windowResult);
            return;
        }

        Article article = articleService.getValidArticle(windowResult.getArticleId());

        ArticleBehaviorAgg agg = windowResult.getAgg();

        double score = calculateScore(agg);

        String allKey = ArticleConstants.ARTICLE_HOT_CACHE_KEY_ALL;
        String channelKey = ArticleConstants.ARTICLE_HOT_CACHE_KEY_CHANNEL + article.getChannelId();

        stringRedisTemplate.opsForZSet()
                .add(allKey, String.valueOf(windowResult.getArticleId()), score);

        stringRedisTemplate.opsForZSet()
                .add(channelKey, String.valueOf(windowResult.getArticleId()), score);

    }

    //读路径
    //只读 Redis
    //TopN 取值正确
    //顺序由 Redis 决定，Java 层补排序
    @Override
    public List<FrontArticleListVo> getHotArticlesByChannel(ArticleHomeDto dto) {
        if (StringUtils.isBlank(dto.getTag())) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        String key;

        if ("__all__".equals(dto.getTag())) {
            key = ArticleConstants.ARTICLE_HOT_CACHE_KEY_ALL;
        } else {
            key = ArticleConstants.ARTICLE_HOT_CACHE_KEY_CHANNEL + dto.getTag();
        }

        Set<String> idSet = stringRedisTemplate.opsForZSet()
                .reverseRange(key, 0, HOT_LIMIT - 1);

        if (CollectionUtils.isEmpty(idSet)) {
            return Collections.emptyList();
        }

        List<String> articleIds = new ArrayList<>(idSet);

        List<FrontArticleListVo> voList = new ArrayList<>();

        for (String id : articleIds) {
            Long articleId = Long.parseLong(id);
            if (isNullId(articleId)) {
                continue;
            }
            FrontArticleListVo vo = getCacheFrontArticleVo(articleId);
            if (vo != null) {
                voList.add(vo);
            } else {
                Article article = articleService.getValidArticle(articleId);
                log.info("articleService.getValidArticle:{}", article);
                if (article != null) {
                    String authorName = getAuthorName(article.getAuthorId());
                    vo = ArticleConvert.toFrontArticleVo(article, authorName);
                    voList.add(vo);
                    putCacheFrontArticleVo(vo);
                } else {
                    putCacheNullId(articleId);
                }
            }
        }

        return voList;
    }

    //整理路径
    //定时任务集中裁剪
    //写删分离
    //无并发竞争
    @Scheduled(initialDelay = 10 * 1000L, fixedDelay = 30 * 1000L)
    public void hotRankTrimJob() {
        trim(ArticleConstants.ARTICLE_HOT_CACHE_KEY_ALL, null);

        List<ArticleChannel> channelList = articleChannelService.listEnable();
        for (ArticleChannel channel : channelList) {
            trim(ArticleConstants.ARTICLE_HOT_CACHE_KEY_CHANNEL, channel.getId());
        }

    }

    private String getAuthorName(Long authorId){
        String key = "author:info:" + authorId;
        Object value = stringRedisTemplate.opsForHash().get(key, "name");
        if (value != null){
            return value.toString();
        }

        Map<Long, String> nameMap = weMediaClient.getAuthorNameMap(Collections.singletonList(authorId));
        if (CollectionUtils.isNotEmpty(nameMap)){
            String name = nameMap.get(authorId);
            if (StringUtils.isNotBlank(name)){
                stringRedisTemplate.opsForHash().put(key, "name", name);
                stringRedisTemplate.expire(key, 1, TimeUnit.DAYS);
                return name;
            }
        }

        return "未知作者";
    }

    private FrontArticleListVo getCacheFrontArticleVo(Long articleId) {
        String key = "article:info:" + articleId;

        List<String> fields = Arrays.asList("id", "title", "images", "authorName");

        List<Object> values = stringRedisTemplate.opsForHash()
                .multiGet(key, new ArrayList<>(fields));

        if (CollectionUtils.isEmpty(values) || values.get(0) == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<>();

        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);
            if (value != null){
                map.put(fields.get(i), value);
            }
        }

        FrontArticleListVo vo = new FrontArticleListVo();
        vo.setId(Long.parseLong((String) map.get("id")));
        vo.setTitle((String) map.get("title"));
        vo.setImages((String) map.get("images"));
        vo.setAuthorName((String) map.get("authorName"));

        log.info("getCacheFrontArticleVo:{}", vo);

        return vo;
    }

    private void putCacheFrontArticleVo(FrontArticleListVo vo) {
        String key = "article:info:" + vo.getId();
        Map<String, String> map = new HashMap<>();
        map.put("id", vo.getId().toString());
        map.put("title", Optional.ofNullable(vo.getTitle()).orElse(""));
        map.put("images", Optional.ofNullable(vo.getImages()).orElse(""));
        map.put("authorName", Optional.ofNullable(vo.getAuthorName()).orElse(""));

        stringRedisTemplate.opsForHash().putAll(key, map);
        stringRedisTemplate.expire(key, 1, TimeUnit.DAYS);
    }

    private void putCacheNullId(Long articleId) {
        stringRedisTemplate.opsForValue().set("article:info:null:" + articleId, "1", 5, TimeUnit.MINUTES);
    }

    private boolean isNullId(Long articleId) {
        String result = stringRedisTemplate.opsForValue().get("article:info:null:" + articleId);
        return result != null;
    }

    private void trim(String key, Long channelId) {
        if (channelId != null) {
            key += channelId;
        }
        Long size = stringRedisTemplate.opsForZSet().size(key);
        if (size != null && size > HOT_LIMIT) {
            stringRedisTemplate.opsForZSet().removeRange(key, 0, size - HOT_LIMIT - 1);
        }

        //只在没有 TTL 时设置，避免一直更新超时时间，导致永久有效
        Long ttl = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (ttl == null || ttl == -1){
            // -1 = key存在但没有TTL
            stringRedisTemplate.expire(key, 5, TimeUnit.MINUTES);
        }
    }


    private double calculateScore(ArticleBehaviorAgg agg) {
        return agg.getViewCount()
                + agg.getLikeCount() * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT
                + agg.getCommentCount() * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT
                + agg.getCollectCount() * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
    }


}
