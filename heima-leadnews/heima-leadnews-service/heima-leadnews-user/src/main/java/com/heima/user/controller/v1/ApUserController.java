package com.heima.user.controller.v1;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.ApUserPageDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.service.ApUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/auth")
public class ApUserController {

    @Autowired
    private ApUserService apUserService;

    @PostMapping("/list")
    public ResponseResult pageList(@RequestBody ApUserPageDto dto){

        IPage<ApUser> iPage = apUserService.pageList(dto);
        PageResponseResult result = new PageResponseResult(dto.getPage(), dto.getSize(), (int)iPage.getTotal());
        result.setData(iPage.getRecords());

        return result;
    }


}
