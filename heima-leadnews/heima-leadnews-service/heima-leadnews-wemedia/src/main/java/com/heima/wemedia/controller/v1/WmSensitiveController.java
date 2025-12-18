package com.heima.wemedia.controller.v1;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmSensitivePageDto;
import com.heima.model.wemedia.dtos.WmSensitiveAddDto;
import com.heima.model.wemedia.dtos.WmSensitiveUpdateDto;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.wemedia.service.WmSensitiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/sensitive")
public class WmSensitiveController {

    @Autowired
    private WmSensitiveService wmSensitiveService;


    @PostMapping("/save")
    public ResponseResult add(@RequestBody WmSensitiveAddDto dto){
        wmSensitiveService.add(dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @DeleteMapping("/del/{id}")
    public ResponseResult delete(@PathVariable Integer id){
        if (id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        wmSensitiveService.delete(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @PostMapping("/update")
    public ResponseResult update(@RequestBody WmSensitiveUpdateDto dto){
        wmSensitiveService.update(dto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @PostMapping("/list")
    public ResponseResult pageList(@RequestBody WmSensitivePageDto dto){
        IPage<WmSensitive> page =  wmSensitiveService.pageList(dto);
        PageResponseResult result = new PageResponseResult(dto.getPage(), dto.getSize(), (int)page.getTotal());
        result.setData(page.getRecords());

        return result;
    }



}
