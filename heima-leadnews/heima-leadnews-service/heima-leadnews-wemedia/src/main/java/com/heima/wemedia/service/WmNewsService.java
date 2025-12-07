package com.heima.wemedia.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


public interface WmNewsService extends IService<WmNews> {

    ResponseResult findList(@RequestBody WmNewsPageReqDto dto);

    ResponseResult submitNews(@RequestBody WmNewsDto dto);


    ResponseResult findOne(@PathVariable Integer id);

    ResponseResult downOrUp(@RequestBody WmNewsDto dto);

}
