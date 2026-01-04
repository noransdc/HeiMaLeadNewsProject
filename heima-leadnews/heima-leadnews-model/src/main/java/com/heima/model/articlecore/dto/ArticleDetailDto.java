package com.heima.model.articlecore.dto;


import com.heima.model.articlecore.entity.Article;
import com.heima.model.articlecore.entity.ArticleContent;
import lombok.Data;


@Data
public class ArticleDetailDto {

    private Article article;
    private ArticleContent articleContent;


}
