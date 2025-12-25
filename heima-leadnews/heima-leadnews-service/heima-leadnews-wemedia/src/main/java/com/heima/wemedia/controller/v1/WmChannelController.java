package com.heima.wemedia.controller.v1;


import com.heima.model.wemedia.dtos.WmChannelAddDto;
import com.heima.model.wemedia.dtos.WmChannelPageReqDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmChannelUpdateDto;
import com.heima.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/channel")
public class WmChannelController {

    @Autowired
    private WmChannelService wmChannelService;

    @GetMapping("/channels")
    public ResponseResult findAll(){
        return ResponseResult.okResult(wmChannelService.findAll());
    }

    @DeleteMapping("del/{id}")
    public ResponseResult delete(@PathVariable Integer id){
        return wmChannelService.delete(id);
    }

    @PostMapping("/list")
    public ResponseResult getList(@RequestBody WmChannelPageReqDto dto){
        return wmChannelService.pageList(dto);
    }

    @PostMapping("/save")
    public ResponseResult save(@RequestBody WmChannelAddDto dto){
        return wmChannelService.save(dto);
    }

    @PostMapping("/update")
    public ResponseResult update(@RequestBody WmChannelUpdateDto dto){
        return wmChannelService.update(dto);
    }


}
