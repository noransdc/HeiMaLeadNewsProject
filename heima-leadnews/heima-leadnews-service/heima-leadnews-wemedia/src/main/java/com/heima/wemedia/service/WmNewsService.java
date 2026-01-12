package com.heima.wemedia.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.articlecore.dto.ArticleAuthFailDto;
import com.heima.model.articlecore.dto.ArticleAuthPassDto;
import com.heima.model.articlecore.vo.AuthorArticleDetailVo;
import com.heima.model.articlecore.vo.AuthorArticleListVo;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.*;
import com.heima.model.wemedia.pojos.WmNews;

import java.util.List;


public interface WmNewsService extends IService<WmNews> {


    ResponseResult downOrUp( WmNewsDto dto);

    void submitRemote(WmNewsDto dto);

    PageResponseResult<List<AuthorArticleListVo>> getPageListRemote(WmNewsPageReqDto dto);

    AuthorArticleDetailVo getArticleVo(Long articleId);

}
