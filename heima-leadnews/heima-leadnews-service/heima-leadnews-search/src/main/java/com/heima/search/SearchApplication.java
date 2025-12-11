package com.heima.search;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@MapperScan("com.heima.search.mapper")
@EnableDiscoveryClient//是 Spring Cloud 中用于启用服务注册与发现功能的关键注解。
@EnableAsync
public class SearchApplication {


    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class);
    }

}
