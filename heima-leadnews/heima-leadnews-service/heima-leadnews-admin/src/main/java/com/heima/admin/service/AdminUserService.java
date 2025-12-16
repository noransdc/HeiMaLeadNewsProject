package com.heima.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dto.AdminLoginDto;
import com.heima.model.admin.pojos.AdminUser;
import com.heima.model.common.dtos.ResponseResult;

public interface AdminUserService extends IService<AdminUser> {


    ResponseResult login(AdminLoginDto dto);


}
