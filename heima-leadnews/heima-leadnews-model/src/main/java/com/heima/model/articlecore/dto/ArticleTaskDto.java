package com.heima.model.articlecore.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;


@Data
public class ArticleTaskDto {


    private Long articleId;
    private LocalDateTime publishTime;
    private String action;
    private String parameters;


}
