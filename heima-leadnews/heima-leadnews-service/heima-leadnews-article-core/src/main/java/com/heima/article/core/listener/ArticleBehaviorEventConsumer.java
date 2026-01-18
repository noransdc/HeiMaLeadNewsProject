package com.heima.article.core.listener;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.heima.article.core.service.ArticleEventConsumedService;
import com.heima.common.constants.ArticleBehaviorConstant;
import com.heima.model.articlecore.dto.ArticleBehaviorMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ArticleBehaviorEventConsumer {


    @Autowired
    private ArticleEventConsumedService articleEventConsumedService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = ArticleBehaviorConstant.ARTICLE_BEHAVIOR_EVENT)
    public void onMessage(String message){
        if (StringUtils.isBlank(message)){
            return;
        }

        log.info(ArticleBehaviorConstant.ARTICLE_BEHAVIOR_EVENT + ":{}", message);

        ArticleBehaviorMsg msg = JSON.parseObject(message, ArticleBehaviorMsg.class);

        if (StringUtils.isBlank(msg.getEventId()) || StringUtils.isBlank(msg.getEventType()) || msg.getArticleId() == null){
            return;
        }

        boolean success = articleEventConsumedService.addEvent(msg.getEventId(), msg.getEventType(), msg.getArticleId());
        if (!success){
            return;
        }

        kafkaTemplate.send(ArticleBehaviorConstant.ARTICLE_BEHAVIOR_STREAM, JSON.toJSONString(msg));
    }


}
