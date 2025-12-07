package com.heima.article.listener;


import com.alibaba.fastjson.JSON;
import com.heima.article.service.ApArticleConfigService;
import com.heima.common.constants.WmNewsMessageConstants;
import com.heima.model.article.pojos.ApArticleEnable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Slf4j
public class ArticleMessageListener {


    @Autowired
    private ApArticleConfigService apArticleConfigService;

    @KafkaListener(topics = WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC)
    public void onMessage(String message){
        log.info("onMessage:{}", message);
        if (StringUtils.isBlank(message)){
            return;
        }
        ApArticleEnable apArticleEnable = JSON.parseObject(message, ApArticleEnable.class);
        if (apArticleEnable == null){
            return;
        }
        apArticleConfigService.downOrUp(apArticleEnable.getArticleId(), apArticleEnable.getEnable());

    }

}
