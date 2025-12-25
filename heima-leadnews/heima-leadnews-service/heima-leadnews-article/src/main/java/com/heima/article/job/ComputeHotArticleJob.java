package com.heima.article.job;

import com.heima.article.service.ApArticleService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ComputeHotArticleJob {


    @Autowired
    private ApArticleService apArticleService;

    @XxlJob("computeHotArticleJob")
    public void handle(){
        log.info("xxl job calculateArticleScore pre");
        apArticleService.calculateArticleScore();
        log.info("xxl job calculateArticleScore after");

    }

}
