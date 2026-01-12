package com.heima.admin.service.impl;

import com.heima.admin.service.AdminArticleService;
import com.heima.apis.articlecore.ArticleCoreClient;
import com.heima.model.articlecore.dto.AdminArticlePageDto;
import com.heima.model.articlecore.vo.AdminArticleListVo;
import com.heima.model.common.dtos.PageResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AdminArticleServiceImpl implements AdminArticleService {

    @Autowired
    private ArticleCoreClient articleCoreClient;

    @Override
    public PageResponseResult<List<AdminArticleListVo>> pageForAdmin(AdminArticlePageDto dto) {
        return articleCoreClient.pageForAdmin(dto);
    }

    @Override
    public AdminArticleListVo forAdmin(Long id) {
        return articleCoreClient.forAdmin(id);
    }


}
