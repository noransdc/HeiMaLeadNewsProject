package com.heima.article.core.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.articlecore.entity.ArticleInteraction;


public interface ArticleInteractionService extends IService<ArticleInteraction> {


    void add(Long articleId, String eventType);

}
