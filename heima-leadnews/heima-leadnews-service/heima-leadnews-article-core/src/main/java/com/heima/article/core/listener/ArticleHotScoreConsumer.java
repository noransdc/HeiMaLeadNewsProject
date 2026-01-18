package com.heima.article.core.listener;


import com.heima.common.constants.ArticleBehaviorConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ArticleHotScoreConsumer {


    @KafkaListener(topics = ArticleBehaviorConstant.ARTICLE_BEHAVIOR_AGG)
    public void onMessage(String message){
        log.info(ArticleBehaviorConstant.ARTICLE_BEHAVIOR_AGG + ":{}", message);



    }


}
