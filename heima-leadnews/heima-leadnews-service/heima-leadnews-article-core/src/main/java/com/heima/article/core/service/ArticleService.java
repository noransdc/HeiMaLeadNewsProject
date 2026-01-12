package com.heima.article.core.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.articlecore.dto.*;
import com.heima.model.articlecore.entity.Article;
import com.heima.model.articlecore.vo.AdminArticleDetailVo;
import com.heima.model.articlecore.vo.AdminArticleListVo;
import com.heima.model.articlecore.vo.AuthorArticleDetailVo;
import com.heima.model.articlecore.vo.AuthorArticleListVo;
import com.heima.model.common.dtos.PageRequestDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.schedule.dto.ArticleAuditCompensateDto;
import com.heima.model.wemedia.dtos.WmNewsDto;

import java.util.List;


public interface ArticleService extends IService<Article> {


    void submit(ArticleSubmitDto dto);

    void updateAuditStatus(Long articleId, Integer targetStatus, String reason);

    List<ArticleAuditCompensateDto> getArticleAuditCompensateList(PageRequestDto dto);

    void audit(Long articleId);

    void publish(Long articleId);

    PageResponseResult<List<AuthorArticleListVo>> pageOwnArticles(AuthorArticlePageDto dto);

    PageResponseResult<List<AdminArticleListVo>> pageAllArticles(AdminArticlePageDto dto);

    void manualAuditReject(ArticleAuthFailDto dto);

    void manualAuditPass(Long articleId);

    AuthorArticleDetailVo detailForAuthor(Long articleId);

    void downOrUp(WmNewsDto dto);


}
