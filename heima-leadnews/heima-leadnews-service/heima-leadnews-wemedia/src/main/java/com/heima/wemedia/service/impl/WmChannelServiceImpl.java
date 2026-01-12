package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.articlecore.ArticleChannelClient;
import com.heima.model.articlecore.entity.ArticleChannel;
import com.heima.model.articlecore.vo.AuthorChannelVo;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.service.WmChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {


    @Autowired
    private ArticleChannelClient articleChannelClient;


    @Override
    public List<WmChannel> findAll() {
        List<WmChannel> list = lambdaQuery()
                .eq(WmChannel::getStatus, 1)
                .orderByAsc(WmChannel::getOrd)
                .list();

        return list;
    }


    @Override
    public List<AuthorChannelVo> listForAuthor() {
        return articleChannelClient.listForAuthor();
    }


}
