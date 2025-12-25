package com.heima.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.ApCollectionDto;
import com.heima.model.user.dtos.ApFollowDto;
import com.heima.model.user.dtos.ApUserPageDto;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;

public interface ApUserService extends IService<ApUser> {
    /**
     * app端登录功能
     * @param dto
     * @return
     */
    ResponseResult login(LoginDto dto);

    IPage<ApUser> pageList(ApUserPageDto dto);

    void follow(ApFollowDto dto);

    void collection(ApCollectionDto dto);

}
