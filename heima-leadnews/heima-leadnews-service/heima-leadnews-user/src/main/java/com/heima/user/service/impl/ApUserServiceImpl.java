package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.wemedia.IWeMediaClient;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.ApCollectionDto;
import com.heima.model.user.dtos.ApFollowDto;
import com.heima.model.user.dtos.ApUserPageDto;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.pojos.ApUserCollection;
import com.heima.model.user.pojos.ApUserFollow;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.thread.AppThreadLocalUtil;
import com.heima.user.mapper.ApUserCollectionMapper;
import com.heima.user.mapper.ApUserFanMapper;
import com.heima.user.mapper.ApUserFollowMapper;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional
@Slf4j
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {


    @Autowired
    private ApUserFanMapper apUserFanMapper;

    @Autowired
    private ApUserFollowMapper apUserFollowMapper;

    @Autowired
    private IWeMediaClient weMediaClient;

    @Autowired
    private ApUserCollectionMapper apUserCollectionMapper;


    /**
     * app端登录功能
     * @param dto
     * @return
     */
    @Override
    public ResponseResult login(LoginDto dto) {
        //1.正常登录 用户名和密码
        if(StringUtils.isNotBlank(dto.getPhone()) && StringUtils.isNotBlank(dto.getPassword())){
            //1.1 根据手机号查询用户信息
            ApUser dbUser = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, dto.getPhone()));
            if(dbUser == null){
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户信息不存在");
            }

            //1.2 比对密码
            String salt = dbUser.getSalt();
            String password = dto.getPassword();
            String pswd = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if(!pswd.equals(dbUser.getPassword())){
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }

            //1.3 返回数据  jwt  user
            String token = AppJwtUtil.getToken(dbUser.getId().longValue());
            log.info("login token:{}", token);

            Map<String,Object> map = new HashMap<>();
            map.put("token",token);
            dbUser.setSalt("");
            dbUser.setPassword("");
            map.put("user",dbUser);

            return ResponseResult.okResult(map);
        }else {
            //2.游客登录
            Map<String,Object> map = new HashMap<>();
            map.put("token",AppJwtUtil.getToken(0L));
            return ResponseResult.okResult(map);
        }

    }

    @Override
    public IPage<ApUser> pageList(ApUserPageDto dto) {
        if (dto.getPage() < 1){
            dto.setPage(1);
        }

        if (dto.getSize() < 10){
            dto.setSize(10);
        }

        LambdaQueryWrapper<ApUser> query = Wrappers.lambdaQuery();
        if (dto.getStatus() != null){
            query.eq(ApUser::getStatus, dto.getStatus());
        }

        IPage<ApUser> iPage = new Page<>(dto.getPage(), dto.getSize());
        IPage<ApUser> pageResult = page(iPage, query);

        List<ApUser> userList = pageResult.getRecords();

        for (ApUser user : userList) {
            user.setStatus(null);
        }

        return pageResult;
    }

    @Override
    @Transactional
    public void follow(ApFollowDto dto) {
        if (dto.getAuthorId() == null || dto.getOperation() == null){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        if (dto.getOperation() != 0 && dto.getOperation() != 1){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null){
            throw new CustomException(AppHttpCodeEnum.USER_NOT_EXIST);
        }

        if ((long)user.getId() == dto.getAuthorId()){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        WmUser author = weMediaClient.getUser(dto.getAuthorId());

        if (author == null){
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        if (dto.getOperation() == 1){
            ApUserFollow apUserFollow = new ApUserFollow();
            apUserFollow.setUserId(user.getId());
            apUserFollow.setFollowId(author.getId());
            apUserFollow.setFollowName(author.getName());
            apUserFollow.setLevel(1);
            apUserFollow.setIsNotice(1);
            apUserFollow.setCreatedTime(new Date());

            try {
                apUserFollowMapper.insert(apUserFollow);

            } catch (DuplicateKeyException e){
                // 已关注，幂等成功，什么都不做
            }

        } else {
            LambdaQueryWrapper<ApUserFollow> query = Wrappers.lambdaQuery();
            query.eq(ApUserFollow::getUserId, user.getId())
                            .eq(ApUserFollow::getFollowId, dto.getAuthorId());
            apUserFollowMapper.delete(query);
        }

    }

    @Override
    public void collection(ApCollectionDto dto) {
        Long articleId = dto.getEntryId();
        Integer operation = dto.getOperation();

        if (articleId == null || operation == null){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        if (operation != 0 && operation != 1){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApUser apUser = AppThreadLocalUtil.getUser();
        if (apUser == null){
            throw new CustomException(AppHttpCodeEnum.USER_NOT_EXIST);
        }
        Integer userId = apUser.getId();

        if (operation == 1){
            ApUserCollection apUserCollection = new ApUserCollection();
            apUserCollection.setUserId((long)userId);
            apUserCollection.setArticleId(articleId);
            apUserCollection.setCreatedTime(new Date());

            try {
                apUserCollectionMapper.insert(apUserCollection);

            } catch (DuplicateKeyException e){
                //已收藏，幂等成功
            }

        } else {
            LambdaQueryWrapper<ApUserCollection> query = Wrappers.lambdaQuery();
            query.eq(ApUserCollection::getUserId, userId)
                    .eq(ApUserCollection::getArticleId, articleId);
            apUserCollectionMapper.delete(query);
        }


    }


}
