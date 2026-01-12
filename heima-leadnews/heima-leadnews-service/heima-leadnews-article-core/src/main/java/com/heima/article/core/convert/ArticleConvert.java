package com.heima.article.core.convert;


import com.heima.model.articlecore.entity.Article;
import com.heima.model.articlecore.vo.AdminArticleVo;
import com.heima.model.articlecore.vo.AuthorArticleVo;
import com.heima.model.common.dtos.PageResponseResult;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;



public final class ArticleConvert {


    private ArticleConvert(){}


    public static AdminArticleVo toAdminVo(Article article){
        if (article == null){
            return null;
        }
        AdminArticleVo adminArticleVo = new AdminArticleVo();

        BeanUtils.copyProperties(article, adminArticleVo);

        adminArticleVo.setImages(article.getCoverImgUrl());
        adminArticleVo.setStatus(article.getAuditStatus());
        adminArticleVo.setType(article.getCoverType());

        return adminArticleVo;

    }

    public static AuthorArticleVo toAuthorVo(Article article){
        if (article == null){
            return null;
        }
        AuthorArticleVo authorArticleVo = new AuthorArticleVo();

        BeanUtils.copyProperties(article, authorArticleVo);

        authorArticleVo.setImages(article.getCoverImgUrl());

        return authorArticleVo;
    }

    public static List<AdminArticleVo> toAdminVoList(List<Article> list){
        if (CollectionUtils.isEmpty(list)){
            return Collections.emptyList();
        }

        return list.stream()
                .map(ArticleConvert::toAdminVo)
                .collect(Collectors.toList());
    }

    public static List<AuthorArticleVo> toAuthorVoList(List<Article> list){
        if (CollectionUtils.isEmpty(list)){
            return Collections.emptyList();
        }

        return list.stream()
                .map(ArticleConvert::toAuthorVo)
                .collect(Collectors.toList());
    }

    public static PageResponseResult<List<AdminArticleVo>> toAdminVoPage(PageResponseResult<List<Article>> pageRsp){
        if (pageRsp == null){
            return null;
        }
        PageResponseResult<List<AdminArticleVo>> result = new PageResponseResult<>();
        result.setCurrentPage(pageRsp.getCurrentPage());
        result.setSize(pageRsp.getSize());
        result.setTotal(pageRsp.getTotal());
        result.setData(
                pageRsp.getData().stream()
                        .map(ArticleConvert::toAdminVo)
                        .collect(Collectors.toList())
        );

        return result;
    }


    public static PageResponseResult<List<AuthorArticleVo>> toAuthorVoPage(PageResponseResult<List<Article>> pageRsp){
        if (pageRsp == null){
            return null;
        }

        PageResponseResult<List<AuthorArticleVo>> result = new PageResponseResult<>();
        result.setCurrentPage(pageRsp.getCurrentPage());
        result.setSize(pageRsp.getSize());
        result.setTotal(pageRsp.getTotal());
        result.setData(
                pageRsp.getData().stream()
                        .map(ArticleConvert::toAuthorVo)
                        .collect(Collectors.toList())
        );

        return result;
    }

}
