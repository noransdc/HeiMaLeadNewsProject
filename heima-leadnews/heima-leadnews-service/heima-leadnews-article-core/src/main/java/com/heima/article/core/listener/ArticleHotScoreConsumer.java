package com.heima.article.core.listener;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.heima.article.core.service.HotArticleRankService;
import com.heima.common.constants.ArticleBehaviorConstant;
import com.heima.model.articlecore.dto.ArticleBehaviorWindowResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ArticleHotScoreConsumer {


    @Autowired
    private HotArticleRankService hotArticleRankService;


    @KafkaListener(topics = ArticleBehaviorConstant.ARTICLE_BEHAVIOR_AGG)
    public void onMessage(String message){
        if (StringUtils.isBlank(message)){
            return;
        }

        log.info(ArticleBehaviorConstant.ARTICLE_BEHAVIOR_AGG + ":{}", message);

        ArticleBehaviorWindowResult windowResult;


        try {
            windowResult = JSON.parseObject(message, ArticleBehaviorWindowResult.class);

        } catch (Exception e){
            log.warn("json parse error, message:{}", message);
            return;
        }

        if (windowResult.getArticleId() == null || windowResult.getAgg() == null){
            return;
        }

        hotArticleRankService.refreshHotRank(windowResult);


    }


}
