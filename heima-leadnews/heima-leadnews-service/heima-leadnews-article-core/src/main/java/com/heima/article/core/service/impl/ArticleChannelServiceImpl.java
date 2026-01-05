package com.heima.article.core.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.core.mapper.ArticleChannelMapper;
import com.heima.article.core.service.ArticleChannelService;
import com.heima.article.core.service.ArticleService;
import com.heima.common.exception.CustomException;
import com.heima.model.articlecore.entity.ArticleChannel;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ArticleChannelServiceImpl extends ServiceImpl<ArticleChannelMapper, ArticleChannel> implements ArticleChannelService {

    @Autowired
    private ArticleService articleService;

    @Override
    public void add() {

    }

    public List<ArticleChannel> getChannelList() {
        List<ArticleChannel> list = lambdaQuery()
                .eq(ArticleChannel::getIsEnabled, 1)
                .orderByDesc(ArticleChannel::getSort)
                .list();

        articleService.scanPendingAuditList();

        return list;
    }

    @Override
    public ArticleChannel getChannel(Long id) {
        if (id == null){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        ArticleChannel channel = getById(id);

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

        if (channel.getIsEnabled() != 1){
            throw new CustomException(AppHttpCodeEnum.RPC_CHANNEL_DISABLE);
        }
    }


}
