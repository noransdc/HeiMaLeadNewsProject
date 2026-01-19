package com.heima.article.core.service.impl;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
import com.heima.model.articlecore.vo.AuthorArticleListVo;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


@Slf4j
@Service
public class HotArticleRankServiceImpl implements HotArticleRankService {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleChannelService articleChannelService;

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
    public List<AuthorArticleListVo> getHotArticlesByChannel(ArticleHomeDto dto) {
        if (StringUtils.isBlank(dto.getTag())) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        String key;

        if ("__all__".equals(dto.getTag())) {
            key = ArticleConstants.ARTICLE_HOT_CACHE_KEY_ALL;
        } else {
            key = ArticleConstants.ARTICLE_HOT_CACHE_KEY_CHANNEL + dto.getTag();
        }

        Set<String> articleIds = stringRedisTemplate.opsForZSet()
                .reverseRange(key, 0, HOT_LIMIT - 1);

        if (articleIds.isEmpty()){
            return Collections.emptyList();
        }

        List<AuthorArticleListVo> voList = articleService.getOrderedArticlesByIds(new ArrayList<>(articleIds));

        return voList;
    }

    //整理路径
    //定时任务集中裁剪
    //写删分离
    //无并发竞争
    @Scheduled(initialDelay = 10 * 1000L, fixedDelay = 30 * 1000L)
    public void hotRankTrimJob(){
        trim(ArticleConstants.ARTICLE_HOT_CACHE_KEY_ALL, null);

        List<ArticleChannel> channelList = articleChannelService.listEnable();
        for (ArticleChannel channel : channelList) {
            trim(ArticleConstants.ARTICLE_HOT_CACHE_KEY_CHANNEL, channel.getId());
        }
    }

    private void trim(String key, Long channelId){
        if (channelId != null){
            key += channelId;
        }
        Long size = stringRedisTemplate.opsForZSet().size(key);
        if (size != null && size > HOT_LIMIT){
            stringRedisTemplate.opsForZSet().removeRange(key, 0, size - HOT_LIMIT - 1);
        }
    }


    private double calculateScore(ArticleBehaviorAgg agg) {
        return agg.getViewCount()
                + agg.getLikeCount() * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT
                + agg.getCommentCount() * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT
                + agg.getCollectCount() * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
    }


}
