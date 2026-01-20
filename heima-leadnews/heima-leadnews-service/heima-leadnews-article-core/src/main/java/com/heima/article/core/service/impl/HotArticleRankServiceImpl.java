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



    /**
     * 文章内容修改后的缓存处理（Update 场景）
     *
     * Cache Write Rules：
     *
     * 一、语义规则（模式层）
     * 1. 采用 Cache Aside 模式，数据库为唯一事实源
     * 2. 写操作不直接更新缓存内容，只做缓存失效
     * 3. 修改仅影响文章内容本身，不改变文章可见性
     *
     * 二、工程实现策略（延迟双删）
     * 1. 为降低并发读写下旧数据回填缓存的风险，
     *    工程上采用延迟双删策略：
     *    - 第一次删除 article:info 缓存
     *    - 更新数据库内容
     *    - 异步延迟一段时间（如 500ms ~ 2s，视系统复杂度而定）
     *    - 第二次删除 article:info 缓存作为兜底
     *
     * 三、约束与说明
     * 1. 该写路径为低频操作，允许短暂缓存 miss
     * 2. 允许短暂不一致，保证最终一致
     * 3. 不负责热榜结构维护（ZSET 不受影响）
     * 4. 不处理文章下架/删除语义
     */
    @Override
    public void handleArticleUpdated(Long articleId) {
        // TODO: 2026/1/20  implementation later

    }


    /**
     * 下架后，需要：
     * 删除文章基本信息缓存，避免 Redis 命中
     * 从热榜 ZSET 中剔除该文章
     * 不在 down 写路径中做热榜重算或补位，补位交由热榜生成机制自然完成
     * @param articleId
     * @param channelId
     */
    @Override
    public void handleArticleDisabled(Long articleId, Long channelId) {
        //Step 3：缓存失效策略（可补偿）
        //规则目标：
        //任何用户请求都不应该再从缓存中拿到文章
        //方法规则：
        //直接删除：
        //DEL article:detail:{id}
        //不要尝试“更新缓存为 DOWN 状态”，原因：
        //缓存是副本，不是状态机
        //写路径越简单越可靠

        String key = "article:info:" + articleId;
        stringRedisTemplate.delete(key);

        putCacheNullId(articleId);

        //Step 4：热榜剔除（核心业务动作）
        //规则目标：
        //下架文章立即失去曝光能力
        //方法规则：
        //从所有热榜 ZSET 中 ZREM
        //不做分数调整，不做惰性清理
        //这是一个强一致业务规则。

        String allKey = ArticleConstants.ARTICLE_HOT_CACHE_KEY_ALL;
        stringRedisTemplate.opsForZSet().remove(allKey, articleId);

        String channelKey = ArticleConstants.ARTICLE_HOT_CACHE_KEY_CHANNEL + channelId;
        stringRedisTemplate.opsForZSet().remove(channelKey, articleId);

        //Step 5：补偿与兜底设计（不是立刻实现，但规则要有）
        //你现在不一定实现，但规则要提前想清楚：
        //如果 Redis 操作失败：
        //不回滚 DB
        //通过：
        //MQ
        //延迟任务
        //定时扫描 DOWN 状态文章补偿清理
        //规则优先级：
        //DB 正确 > Redis 最终一致
        //详情见ArticleDisableCacheCleanupJob


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
                stringRedisTemplate.expire(key, 5, TimeUnit.MINUTES);
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
        stringRedisTemplate.expire(key, 5, TimeUnit.MINUTES);
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
