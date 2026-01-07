package com.heima.article.core.service;


import com.heima.model.articlecore.dto.ArticleAuditRsp;
import com.heima.model.articlecore.dto.ArticleDetailDto;
import com.heima.model.articlecore.entity.Article;
import com.heima.model.articlecore.entity.ArticleContent;

public interface ArticleAuditService {


    ArticleAuditRsp audit(ArticleDetailDto articleDetail);

}
