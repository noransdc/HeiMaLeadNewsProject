package com.heima.model.article.vo;

import lombok.Data;

import java.util.Date;

@Data
public class EsArticleVo {


    private Long id;
    private String title;
    private Long authorId;
    private String authorName;
    private Integer layout;
    private String images;
    private String staticUrl;
    private Date publishTime;
    private String content;




}
