package com.heima.user.controller.v1;


import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.ApCollectionDto;
import com.heima.model.user.dtos.ApFollowDto;
import com.heima.user.service.ApUserCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/v1/user")
public class UserArticleController {

    @Autowired
    private ApUserCollectionService apUserCollectionService;

//    @PostMapping("/user_follow")
//    public ResponseResult follow(@RequestBody ApFollowDto dto){
//        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
//    }

    @PostMapping("/collection_behavior")
    public ResponseResult collection(@RequestBody ApCollectionDto dto){
        apUserCollectionService.collectArticle(dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }




}
