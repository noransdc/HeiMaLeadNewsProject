package com.heima.behavior.service.impl;

import com.heima.behavior.service.BehaviorService;
import com.heima.common.exception.CustomException;
import com.heima.common.redis.CacheService;
import com.heima.model.behavior.dto.*;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.signature.qual.PolySignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class BehaviorServiceImpl implements BehaviorService {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void like(LikeBehaviorDto dto) {
        if (dto.getArticleId() == null || dto.getType() == null || dto.getType() > 2){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null){
            throw new CustomException(AppHttpCodeEnum.AP_USER_DATA_NOT_EXIST);
        }


        String key = "";
        String value = user.getId().toString();

        switch (dto.getType()){
            case 0:
                key = "like:article:" + dto.getArticleId();
                break;

            case 1:
                key = "like:moment:" + dto.getArticleId();
                break;

            case 2:
                key = "like:comment:" + dto.getArticleId();
                break;

            default:
                break;
        }

        if (dto.getOperation() == 1){
            cacheService.sAdd(key, value);
        } else {
            cacheService.sRemove(key, value);
        }

    }

    @Override
    public void dislike(DislikeBehaviorDto dto) {
        if (dto.getArticleId() == null){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null){
            throw new CustomException(AppHttpCodeEnum.AP_USER_DATA_NOT_EXIST);
        }

        String key = "dislike:article:" + dto.getArticleId();
        String value = user.getId().toString();

        if (dto.getType() == 1){
            cacheService.sAdd(key, value);
        } else {
            cacheService.sRemove(key, value);
        }

    }

    @Override
    public void read(ReadBehaviorDto dto) {
        if (dto.getArticleId() == null){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null){
            throw new CustomException(AppHttpCodeEnum.AP_USER_DATA_NOT_EXIST);
        }

        String key = "read:article:" + dto.getArticleId();
//        cacheService.incrBy(key, 1);
        stringRedisTemplate.opsForValue().increment(key, 1);

    }


}
