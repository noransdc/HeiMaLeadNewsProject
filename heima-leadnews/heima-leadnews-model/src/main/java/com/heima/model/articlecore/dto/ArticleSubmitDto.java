package com.heima.model.articlecore.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ArticleSubmitDto {



    private String title;
    private String content;
    private String label;
    private Long channelId;

    /**
     * 作者ID，由 author-service 决定，article-service 不做规则校验
     */
    private Long authorId;

    private Date publishTime;

    //文章封面类型  0无图  1单图  3多图  -1自动
    private Integer coverType;

    //是否草稿 0否 1是
    private Integer isDraft;

    //封面图列表
    private List<String> images;





}
