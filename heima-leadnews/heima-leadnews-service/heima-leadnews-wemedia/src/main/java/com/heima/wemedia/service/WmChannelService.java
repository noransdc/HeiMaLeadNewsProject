package com.heima.wemedia.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.wemedia.pojos.WmChannel;

import java.util.List;


public interface WmChannelService extends IService<WmChannel> {


    List<WmChannel> findAll();


}
