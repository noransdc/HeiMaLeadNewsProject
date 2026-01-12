package com.heima.article.core.convert;


import com.heima.model.articlecore.entity.Article;
import com.heima.model.articlecore.vo.AdminArticleListVo;
import com.heima.model.articlecore.vo.AuthorArticleListVo;
import com.heima.model.common.dtos.PageResponseResult;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;



public final class ArticleConvert {


    private ArticleConvert(){}


    public static AdminArticleListVo toAdminListVo(Article article){
        if (article == null){
            return null;
        }
        AdminArticleListVo vo = new AdminArticleListVo();

        BeanUtils.copyProperties(article, vo);

        vo.setImages(article.getCoverImgUrl());
        vo.setStatus(article.getAuditStatus());
        vo.setType(article.getCoverType());
        vo.setCreatedTime(article.getCreateTime());
        vo.setSubmitedTime(article.getCreateTime());
        vo.setLabels(article.getLabel());

        return vo;
    }

    public static AuthorArticleListVo toAuthorListVo(Article article){
        if (article == null){
            return null;
        }
        AuthorArticleListVo vo = new AuthorArticleListVo();

        BeanUtils.copyProperties(article, vo);

        vo.setStatus(article.getAuditStatus());
        vo.setImages(article.getCoverImgUrl());

        return vo;
    }

    public static List<AdminArticleListVo> toAdminVoList(List<Article> list){
        if (CollectionUtils.isEmpty(list)){
            return Collections.emptyList();
        }

        return list.stream()
                .map(ArticleConvert::toAdminListVo)
                .collect(Collectors.toList());
    }

    public static List<AuthorArticleListVo> toAuthorVoList(List<Article> list){
        if (CollectionUtils.isEmpty(list)){
            return Collections.emptyList();
        }

        return list.stream()
                .map(ArticleConvert::toAuthorListVo)
                .collect(Collectors.toList());
    }

    public static PageResponseResult<List<AdminArticleListVo>> toAdminVoPage(PageResponseResult<List<Article>> pageRsp){
        if (pageRsp == null){
            return null;
        }
        PageResponseResult<List<AdminArticleListVo>> result = new PageResponseResult<>();
        result.setCurrentPage(pageRsp.getCurrentPage());
        result.setSize(pageRsp.getSize());
        result.setTotal(pageRsp.getTotal());
        result.setData(
                pageRsp.getData().stream()
                        .map(ArticleConvert::toAdminListVo)
                        .collect(Collectors.toList())
        );

        return result;
    }


    public static PageResponseResult<List<AuthorArticleListVo>> toAuthorVoPage(PageResponseResult<List<Article>> pageRsp){
        if (pageRsp == null){
            return null;
        }

        PageResponseResult<List<AuthorArticleListVo>> result = new PageResponseResult<>();
        result.setCurrentPage(pageRsp.getCurrentPage());
        result.setSize(pageRsp.getSize());
        result.setTotal(pageRsp.getTotal());
        result.setData(
                pageRsp.getData().stream()
                        .map(ArticleConvert::toAuthorListVo)
                        .collect(Collectors.toList())
        );

        return result;
    }

}
