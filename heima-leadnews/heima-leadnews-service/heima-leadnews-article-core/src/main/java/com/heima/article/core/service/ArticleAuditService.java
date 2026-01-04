package com.heima.article.core.service;


import com.heima.model.articlecore.entity.Article;
import com.heima.model.articlecore.entity.ArticleContent;

public interface ArticleAuditService {


    void audit(Long articleId);

}
