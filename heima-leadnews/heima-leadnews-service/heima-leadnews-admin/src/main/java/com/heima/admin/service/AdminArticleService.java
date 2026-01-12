package com.heima.admin.service;


import com.heima.model.articlecore.dto.AdminArticlePageDto;
import com.heima.model.articlecore.dto.ArticleAuthFailDto;
import com.heima.model.articlecore.vo.AdminArticleListVo;
import com.heima.model.common.dtos.PageResponseResult;

import java.util.List;



public interface AdminArticleService {


    PageResponseResult<List<AdminArticleListVo>> pageForAdmin(AdminArticlePageDto dto);

    AdminArticleListVo forAdmin(Long id);

    void manualAuditReject(ArticleAuthFailDto dto);

    void manualAuditPass(Long articleId);


}
