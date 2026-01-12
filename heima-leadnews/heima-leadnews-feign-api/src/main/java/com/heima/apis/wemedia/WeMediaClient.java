package com.heima.apis.wemedia;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;


@FeignClient(name = "leadnews-wemedia",
        path = "/internal/wemedia",
        contextId = "weMediaClient"
)
public interface WeMediaClient {


    @PostMapping("/name/batch")
    Map<Long, String> getAuthorNameMap(@RequestBody List<Long> authorIds);




}
