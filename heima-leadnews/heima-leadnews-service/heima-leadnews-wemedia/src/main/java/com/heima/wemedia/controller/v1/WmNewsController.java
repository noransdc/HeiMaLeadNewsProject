package com.heima.wemedia.controller.v1;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.*;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {

    @Autowired
    private WmNewsService wmNewsService;

    @PostMapping("/list")
    public ResponseResult findList(@RequestBody WmNewsPageReqDto dto){
        return wmNewsService.getPageListRemote(dto);
    }

    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto dto){
        wmNewsService.submitRemote(dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

//    @GetMapping("/one/{id}")
//    public ResponseResult selectOne(@PathVariable Long id){
//        return ResponseResult.okResult(wmNewsService.getArticleVo(id));
//    }

    @PostMapping("/down_or_up")
    public ResponseResult downOrUp(@RequestBody WmNewsDto dto){
        return wmNewsService.downOrUp(dto);
    }

    @GetMapping("/one_vo/{id}")
    public ResponseResult getOne(@PathVariable Integer id){
        return wmNewsService.findOne(id);
    }

    @PostMapping("/auth_fail")
    public ResponseResult authFailed(@RequestBody WmNewsAuthFailDto dto){
        wmNewsService.authFail(dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @PostMapping("/auth_pass")
    public ResponseResult authPass(@RequestBody WmNewsAuthPassDto dto){
        wmNewsService.authPass(dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }


}
