package com.heima.wemedia.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.wemedia.dtos.WmChannelAddDto;
import com.heima.model.wemedia.dtos.WmChannelPageReqDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmChannelUpdateDto;
import com.heima.model.wemedia.pojos.WmChannel;

import java.util.List;


public interface WmChannelService extends IService<WmChannel> {


    List<WmChannel> findAll();

    ResponseResult pageList(WmChannelPageReqDto dto);

    ResponseResult save(WmChannelAddDto dto);

    ResponseResult update(WmChannelUpdateDto dto);

    ResponseResult delete(Integer id);


}
