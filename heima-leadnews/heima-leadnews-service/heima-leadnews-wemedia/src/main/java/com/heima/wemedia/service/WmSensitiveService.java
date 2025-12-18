package com.heima.wemedia.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmSensitivePageDto;
import com.heima.model.wemedia.dtos.WmSensitiveAddDto;
import com.heima.model.wemedia.dtos.WmSensitiveUpdateDto;
import com.heima.model.wemedia.pojos.WmSensitive;

import java.util.List;


public interface WmSensitiveService extends IService<WmSensitive> {

    void add(WmSensitiveAddDto dto);

    void delete(Integer id);

    void update(WmSensitiveUpdateDto dto);

    IPage<WmSensitive> pageList(WmSensitivePageDto dto);


}
