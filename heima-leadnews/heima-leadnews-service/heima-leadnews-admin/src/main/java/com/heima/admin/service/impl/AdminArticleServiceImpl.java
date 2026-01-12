package com.heima.admin.service.impl;

import com.heima.admin.service.AdminArticleService;
import com.heima.apis.articlecore.ArticleCoreClient;
import com.heima.common.exception.CustomException;
import com.heima.model.admin.pojos.AdminUser;
import com.heima.model.articlecore.dto.AdminArticlePageDto;
import com.heima.model.articlecore.dto.AuthorArticlePageDto;
import com.heima.model.articlecore.vo.AdminArticleVo;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.thread.AdminThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AdminArticleServiceImpl implements AdminArticleService {

    @Autowired
    private ArticleCoreClient articleCoreClient;

    @Override
    public PageResponseResult<List<AdminArticleVo>> pageForAdmin(AdminArticlePageDto dto) {
        return articleCoreClient.pageForAdmin(dto);
    }

    @Override
    public AdminArticleVo forAdmin(Long id) {
        return articleCoreClient.forAdmin(id);
    }


}
