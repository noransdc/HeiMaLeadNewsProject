package com.heima.admin;


import com.heima.apis.config.FeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;


@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(
        basePackages = "com.heima.apis",
        defaultConfiguration = FeignConfig.class
)
public class AdminApplication {


    public static void main(String[] args){
        SpringApplication.run(AdminApplication.class);
    }


}
