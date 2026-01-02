package com.heima.wemedia.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.*;
import com.heima.model.wemedia.pojos.WmNews;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


public interface WmNewsService extends IService<WmNews> {

    ResponseResult findList( WmNewsPageReqDto dto);

    ResponseResult submitNews(WmNewsDto dto);


    ResponseResult findOne(Integer id);

    ResponseResult downOrUp( WmNewsDto dto);

    IPage<WmNews> pageList(WmNewsAdminPageDto dto);

    void authFail(WmNewsAuthFailDto dto);

    void authPass(WmNewsAuthPassDto dto);

    void postSaveNews(WmNewsDto dto);

}
