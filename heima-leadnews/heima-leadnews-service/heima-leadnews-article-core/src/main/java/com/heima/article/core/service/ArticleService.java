package com.heima.article.core.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.articlecore.dto.ArticleSubmitDto;
import com.heima.model.articlecore.entity.Article;


public interface ArticleService extends IService<Article> {


    void submit(ArticleSubmitDto dto);

}
