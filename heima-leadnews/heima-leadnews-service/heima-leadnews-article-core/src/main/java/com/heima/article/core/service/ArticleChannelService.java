package com.heima.article.core.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.articlecore.dto.ArticleChannelAddDto;
import com.heima.model.articlecore.dto.ArticleChannelPageDto;
import com.heima.model.articlecore.dto.ArticleChannelUpdateDto;
import com.heima.model.articlecore.entity.ArticleChannel;
import com.heima.model.articlecore.vo.AdminChannelVo;
import com.heima.model.articlecore.vo.AuthorChannelVo;
import com.heima.model.common.dtos.PageResponseResult;

import java.util.List;


public interface ArticleChannelService extends IService<ArticleChannel> {


    void add(ArticleChannelAddDto dto);

    ArticleChannel getChannel(Long id);

    void validateChannel(Long id);

    void update(ArticleChannelUpdateDto dto);

    void delete(Long id);

    List<ArticleChannel> listEnable();

    PageResponseResult<List<ArticleChannel>> listPage(ArticleChannelPageDto dto);

}
