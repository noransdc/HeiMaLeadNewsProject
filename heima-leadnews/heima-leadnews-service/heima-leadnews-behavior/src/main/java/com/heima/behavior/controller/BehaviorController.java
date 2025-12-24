package com.heima.behavior.controller;


import com.heima.behavior.service.BehaviorService;
import com.heima.model.behavior.dto.*;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1")
public class BehaviorController {

    @Autowired
    private BehaviorService behaviorService;

    @PostMapping("/likes_behavior")
    public ResponseResult like(@RequestBody LikeBehaviorDto dto){
        behaviorService.like(dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @PostMapping("/un_likes_behavior")
    public ResponseResult dislike(@RequestBody DislikeBehaviorDto dto){
        behaviorService.dislike(dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @PostMapping("/read_behavior")
    public ResponseResult read(@RequestBody ReadBehaviorDto dto){
        behaviorService.read(dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }



}
