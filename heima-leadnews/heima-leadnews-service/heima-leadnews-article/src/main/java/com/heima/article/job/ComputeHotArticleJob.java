package com.heima.article.job;

import com.alibaba.fastjson.JSON;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.HotArticleConstants;
import com.heima.model.article.pojos.UpdateArticleMsg;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ComputeHotArticleJob {


    @Autowired
    private ApArticleService apArticleService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    @XxlJob("computeHotArticleJob")
    public void handle(){
        log.info("xxl job calculateArticleScore pre");

        //测试代码
//        UpdateArticleMsg msg = new UpdateArticleMsg();
//        msg.setArticleId(1994038386045882369L);
//        msg.setType(UpdateArticleMsg.UpdateArticleType.LIKES);
//        msg.setAdd(1);
//        kafkaTemplate.send(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC, JSON.toJSONString(msg));

        apArticleService.calculateArticleScore();
        log.info("xxl job calculateArticleScore after");


    }

}
