package com.heima.admin.controller.v1;


import com.heima.apis.articlecore.ArticleSensitiveClient;
import com.heima.model.articlecore.dto.SensitiveAddDto;
import com.heima.model.articlecore.dto.SensitivePageDto;
import com.heima.model.articlecore.dto.SensitiveUpdateDto;
import com.heima.model.articlecore.vo.SensitiveVo;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/wemedia/api/v1/sensitive")
public class AdminSensitiveController {


    @Autowired
    private ArticleSensitiveClient articleSensitiveClient;


    @PostMapping("/list")
    public PageResponseResult<List<SensitiveVo>> pageList(@RequestBody SensitivePageDto dto){
        return articleSensitiveClient.pageList(dto);
    }

    @PostMapping("/save")
    public ResponseResult add(@RequestBody SensitiveAddDto dto){
        articleSensitiveClient.add(dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @PostMapping("/update")
    public ResponseResult update(@RequestBody SensitiveUpdateDto dto){
        articleSensitiveClient.update(dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @DeleteMapping("/del/{id}")
    public ResponseResult delete(@PathVariable Long id){
        articleSensitiveClient.delete(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }


}
