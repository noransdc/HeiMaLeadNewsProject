package com.heima.article.core.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.articlecore.entity.ArticleChannel;

import java.util.List;


public interface ArticleChannelService extends IService<ArticleChannel> {


    void add();

    List<ArticleChannel> getChannelList();

    ArticleChannel getChannel(Long id);

    void validateChannel(Long id);


}
