package com.heima.wemedia.controller.v1;


import com.heima.wemedia.service.WmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/internal/wemedia")
public class WmInternalController {

    @Autowired
    private WmUserService wmUserService;

    @PostMapping("/name/batch")
    public Map<Long, String> getAuthorNameMap(@RequestBody List<Long> authorIds){
        return wmUserService.getAuthorNameMap(authorIds);
    }



}
