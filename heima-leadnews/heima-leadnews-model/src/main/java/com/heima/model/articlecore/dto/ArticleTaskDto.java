package com.heima.model.articlecore.dto;


import lombok.Data;

import java.util.Date;


@Data
public class ArticleTaskDto {


    private Long articleId;
    private Date publishTime;
    private String action;
    private String parameters;


}
