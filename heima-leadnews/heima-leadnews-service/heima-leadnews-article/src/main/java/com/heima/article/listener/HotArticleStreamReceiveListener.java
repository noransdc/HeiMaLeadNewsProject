package com.heima.article.listener;


import com.heima.common.constants.HotArticleConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HotArticleStreamReceiveListener {


    @KafkaListener(topics = HotArticleConstants.HOT_ARTICLE_INCR_HANDLE_TOPIC)
    public void onMessage(String message){
      log.info("KafkaStream ArticleIncrHandleListener message:{}", message);


    }


}
