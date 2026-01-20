package com.heima.article.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;


@Configuration
public class ArticleCoreThreadPoolConfig {

    @Bean("articleCoreExecutor")
    public ThreadPoolTaskExecutor articleCoreExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数：并发执行任务的稳定数量
        executor.setCorePoolSize(10);

        // 最大线程数：高峰兜底
        executor.setMaxPoolSize(20);

        // 队列容量：任务堆积缓冲
        executor.setQueueCapacity(500);

        // 线程空闲多久回收（秒）
        executor.setKeepAliveSeconds(60);

        // 线程名前缀（非常重要，方便排查问题）
        executor.setThreadNamePrefix("article-core-");

        // 拒绝策略：队列满了怎么办
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();

        return executor;
    }


}