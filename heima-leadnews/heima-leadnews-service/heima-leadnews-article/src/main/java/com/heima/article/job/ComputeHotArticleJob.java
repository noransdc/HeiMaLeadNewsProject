package com.heima.article.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ComputeHotArticleJob {

    private int num = 0;

    @XxlJob("computeHotArticleJob")
    public void handle(){
        log.info("xxl job num:{}", num);

        num++;

    }

}
