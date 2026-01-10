package com.heima.article.core.convert;


import com.heima.model.articlecore.entity.ArticleChannel;
import com.heima.model.articlecore.vo.AdminChannelVo;
import com.heima.model.articlecore.vo.AuthorChannelVo;
import com.heima.model.common.dtos.PageResponseResult;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public final class ChannelConvert {


    private ChannelConvert(){}


    public static AuthorChannelVo toAuthorVo(ArticleChannel channel){
        if (channel == null){
            return null;
        }

        AuthorChannelVo vo = new AuthorChannelVo();
        vo.setId(channel.getId());
        vo.setName(channel.getName());
        vo.setDescription(channel.getDescription());
        vo.setOrd(channel.getSort());

        return vo;
    }

    public static AdminChannelVo toAdminVo(ArticleChannel channel){
        if (channel == null){
            return null;
        }

        AdminChannelVo vo = new AdminChannelVo();
        vo.setId(channel.getId());
        vo.setName(channel.getName());
        vo.setDescription(channel.getDescription());
        vo.setOrd(channel.getSort());
        vo.setStatus(channel.getIsEnabled() == 1);

        return vo;
    }

    public static List<AuthorChannelVo> toAuthorVoList(List<ArticleChannel> channelList){
        return channelList
                .stream()
                .map(ChannelConvert::toAuthorVo)
                .collect(Collectors.toList());
    }

    public static PageResponseResult<List<AdminChannelVo>> toAdminVoPage(PageResponseResult<List<ArticleChannel>> pageRsp){
        PageResponseResult<List<AdminChannelVo>> result = new PageResponseResult<>();
        result.setCurrentPage(pageRsp.getCurrentPage());
        result.setSize(pageRsp.getSize());
        result.setTotal(pageRsp.getTotal());
        List<AdminChannelVo> voList = pageRsp.getData().stream().map(ChannelConvert::toAdminVo).collect(Collectors.toList());
        result.setData(voList);
        return result;
    }


}
