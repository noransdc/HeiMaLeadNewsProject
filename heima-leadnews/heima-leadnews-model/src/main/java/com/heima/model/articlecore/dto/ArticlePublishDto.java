package com.heima.model.articlecore.dto;


import lombok.Data;

import java.util.Date;


@Data
public class ArticlePublishDto {


    private Long articleId;
    private Date publishTime;
    private String action;


}
