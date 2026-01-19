package com.heima.article.core.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.core.mapper.ArticleChannelMapper;
import com.heima.article.core.service.ArticleChannelService;
import com.heima.article.core.service.ArticleConstraintQuery;
import com.heima.common.exception.CustomException;
import com.heima.model.articlecore.dto.ArticleChannelAddDto;
import com.heima.model.articlecore.dto.ArticleChannelPageDto;
import com.heima.model.articlecore.dto.ArticleChannelUpdateDto;
import com.heima.model.articlecore.entity.ArticleChannel;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class ArticleChannelServiceImpl extends ServiceImpl<ArticleChannelMapper, ArticleChannel> implements ArticleChannelService {

    @Autowired
    private ArticleConstraintQuery articleConstraintQuery;


    @Override
    public void add(ArticleChannelAddDto dto) {
        if (StringUtils.isBlank(dto.getName()) || dto.getStatus() == null){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        ArticleChannel channel = new ArticleChannel();
        channel.setName(dto.getName());
        channel.setDescription(dto.getDescription());
        channel.setSort(dto.getOrd());
        if (dto.getStatus()){
            channel.setIsDefault(1);
        } else {
            channel.setIsDefault(0);
        }
        channel.setCreateTime(LocalDateTime.now());

        save(channel);
    }

    @Override
    public List<ArticleChannel> listEnable() {
        return lambdaQuery()
                .eq(ArticleChannel::getIsEnabled, 1)
                .eq(ArticleChannel::getIsDelete, 0)
                .orderByDesc(ArticleChannel::getSort)
                .orderByDesc(ArticleChannel::getCreateTime)
                .list();
    }

    @Override
    public PageResponseResult<List<ArticleChannel>> listPage(ArticleChannelPageDto dto) {
        dto.checkParam();

        LambdaQueryWrapper<ArticleChannel> query = Wrappers.lambdaQuery();

        if (StringUtils.isNotBlank(dto.getName())){
            query.like(ArticleChannel::getName, dto.getName());
        }

        query.eq(ArticleChannel::getIsDelete, 0)
                .orderByDesc(ArticleChannel::getSort)
                .orderByDesc(ArticleChannel::getCreateTime);

        Page<ArticleChannel> page = page(new Page<>(dto.getPage(), dto.getSize()), query);

        List<ArticleChannel> channelList = page.getRecords();

        PageResponseResult<List<ArticleChannel>> pageRsp = new PageResponseResult<>(dto.getPage(), dto.getSize(), (int)page.getTotal());
        pageRsp.setData(channelList);

        return pageRsp;
    }

    @Override
    public ArticleChannel getValidChannel(Long id) {
        if (id == null){
            throw new CustomException(AppHttpCodeEnum.RPC_PARAM_INVALID, "频道ID为null");
        }

        ArticleChannel channel = lambdaQuery()
                .eq(ArticleChannel::getId, id)
                .eq(ArticleChannel::getIsDelete, 0)
                .one();

        if (channel == null){
            throw new CustomException(AppHttpCodeEnum.RPC_CHANNEL_NOT_EXIST);
        }

        return channel;
    }

    @Override
    public void validateChannel(Long id) {
        if (id == null){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        ArticleChannel channel = getById(id);

        if (channel == null){
            throw new CustomException(AppHttpCodeEnum.RPC_CHANNEL_NOT_EXIST);
        }

        if (channel.getIsEnabled() != 1 || channel.getIsDelete() == 1){
            throw new CustomException(AppHttpCodeEnum.RPC_CHANNEL_DISABLE);
        }
    }

    @Override
    public void update(ArticleChannelUpdateDto dto) {
        ArticleChannel channel = getValidChannel(dto.getId());

        if (StringUtils.isNotBlank(dto.getName())){
            channel.setName(dto.getName());
        }

        if (StringUtils.isNotBlank(dto.getDescription())){
            channel.setDescription(dto.getDescription());
        }

        if (dto.getStatus() != null){
            channel.setIsEnabled(dto.getStatus()? 1:0);
        }

        if (dto.getIsDefault() != null){
            channel.setIsDefault(dto.getIsDefault()? 1:0);
        }

        if (dto.getOrd() != null){
            channel.setSort(dto.getOrd());
        }

        updateById(channel);

    }

    @Override
    public void delete(Long id) {
        ArticleChannel channel = getValidChannel(id);

        boolean isExist = articleConstraintQuery.existArticleUnderChannel(id);
        if (isExist){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID, "该频道下有文章存在，不可删除");
        }

        channel.setIsDelete(1);
        channel.setDeleteTime(LocalDateTime.now());

        updateById(channel);
    }


}
