package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmLoginDto;
import com.heima.model.wemedia.pojos.WmUser;

import java.util.List;
import java.util.Map;

public interface WmUserService extends IService<WmUser> {

    /**
     * 自媒体端登录
     * @param dto
     * @return
     */
    ResponseResult login(WmLoginDto dto);

    WmUser getUser(Integer id);

    Map<Long, String> getAuthorNameMap(List<Long> ids);

}