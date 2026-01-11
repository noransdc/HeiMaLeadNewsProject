package com.heima.article.listener;


import com.alibaba.fastjson.JSON;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.HotArticleConstants;
import com.heima.model.article.pojos.ArticleVisitStreamMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HotArticleStreamReceiveListener {

    @Autowired
    private ApArticleService apArticleService;


    @KafkaListener(topics = HotArticleConstants.HOT_ARTICLE_INCR_HANDLE_TOPIC)
    public void onMessage(String message) {
        if (StringUtils.isBlank(message)){
            return;
        }
        log.info("KafkaStream ArticleIncrHandleListener message:{}", message);
        ArticleVisitStreamMsg msg = JSON.parseObject(message, ArticleVisitStreamMsg.class);
        apArticleService.updateRedisHotArticle(msg);

    }


}
