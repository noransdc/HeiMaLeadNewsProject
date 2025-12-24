package com.heima.wemedia.feign;


import com.heima.apis.wemedia.IWeMediaClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.service.WmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class WeMediaClient implements IWeMediaClient {

    @Autowired
    private WmUserService wmUserService;

    @Override
    @GetMapping("/api/v1/wemedia/user/{id}")
    public WmUser getUser(@PathVariable Integer id) {
        return wmUserService.getUser(id);
    }


}
