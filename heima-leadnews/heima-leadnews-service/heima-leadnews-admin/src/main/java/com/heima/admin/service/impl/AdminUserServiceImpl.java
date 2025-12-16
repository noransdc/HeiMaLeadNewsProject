package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdminUserMapper;
import com.heima.admin.service.AdminUserService;
import com.heima.model.admin.dto.AdminLoginDto;
import com.heima.model.admin.pojos.AdminUser;
import com.heima.model.admin.vo.AdminUserVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.common.AppJwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {



    @Override
    public ResponseResult login(AdminLoginDto dto) {
        String name = dto.getName();
        String password = dto.getPassword();
        if (StringUtils.isBlank(name) || StringUtils.isBlank(password)){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        AdminUser user = lambdaQuery().eq(AdminUser::getName, name)
                .one();

        if (user == null ){
            return ResponseResult.errorResult(AppHttpCodeEnum.AP_USER_DATA_NOT_EXIST);
        }

        String pwdMd5 = DigestUtils.md5DigestAsHex((password + user.getSalt()).getBytes());

        if (!StringUtils.equals(pwdMd5, user.getPassword())){
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }

        String token = AppJwtUtil.getToken(user.getId().longValue());
        AdminUserVo adminUserVo = new AdminUserVo();
        BeanUtils.copyProperties(user, adminUserVo);
        Map<String, Object> map = new HashMap<>(2);
        map.put("user", adminUserVo);
        map.put("token", token);

        return ResponseResult.okResult(map);
    }




}
