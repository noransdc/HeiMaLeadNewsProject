package com.heima.article.core.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.common.enums.ArticleAuditEnum;
import com.heima.model.articlecore.dto.ArticleDetailDto;
import com.heima.model.articlecore.dto.ArticleSubmitDto;
import com.heima.model.articlecore.entity.Article;


public interface ArticleService extends IService<Article> {


    void submit(ArticleSubmitDto dto);

    void updateAuditStatus(Long articleId, ArticleAuditEnum status, String reason);

    ArticleDetailDto getArticleDetail(Long articleId);


}
