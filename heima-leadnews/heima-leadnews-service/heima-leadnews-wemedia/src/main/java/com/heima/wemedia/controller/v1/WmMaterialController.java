package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.wemedia.service.WmMaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/material")
@Api(value = "自媒体服务")
public class WmMaterialController {

    @Autowired
    private WmMaterialService materialService;

    @ApiOperation("上传图片")
    @PostMapping("/upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile){
        return ResponseResult.okResult(materialService.uploadPicture(multipartFile));
    }

    @PostMapping("/list")
    public ResponseResult findList(@RequestBody WmMaterialDto dto){
        return materialService.findList(dto);
    }

    @GetMapping("/collect/{id}")
    public ResponseResult addCollection(@PathVariable Integer id){
        return materialService.addCollection(id);
    }

    @GetMapping("/cancel_collect/{id}")
    public ResponseResult cancelCollection(@PathVariable Integer id){
        return materialService.cancelCollection(id);
    }

    @GetMapping("/del_picture/{id}")
    public ResponseResult delete(@PathVariable Integer id){
        return materialService.delete(id);
    }

}
