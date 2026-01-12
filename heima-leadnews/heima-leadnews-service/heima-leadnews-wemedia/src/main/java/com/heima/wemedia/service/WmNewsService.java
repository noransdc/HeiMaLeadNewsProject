package com.heima.wemedia.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.articlecore.vo.AuthorArticleVo;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.*;
import com.heima.model.wemedia.pojos.WmNews;

import java.util.List;


public interface WmNewsService extends IService<WmNews> {

//    ResponseResult findList( WmNewsPageReqDto dto);
//
//    ResponseResult submitNews(WmNewsDto dto);


    ResponseResult findOne(Integer id);

    ResponseResult downOrUp( WmNewsDto dto);

    void authFail(WmNewsAuthFailDto dto);

    void authPass(WmNewsAuthPassDto dto);

    void submitRemote(WmNewsDto dto);

    PageResponseResult<List<AuthorArticleVo>> getPageListRemote(WmNewsPageReqDto dto);

    AuthorArticleVo getArticleVo(Long id);

}
