package com.heima.apis.wemedia;


import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@FeignClient(value = "leadnews-wemedia")
public interface IWeMediaClient {


    @GetMapping("/api/v1/wemedia/user/{id}")
    WmUser getUser(@PathVariable Integer id);

    @GetMapping("/api/v1/wemedia/channel/list")
    List<WmChannel> getChannelList();

}
