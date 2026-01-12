package com.heima.model.articlecore.vo;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthorArticleVo {


    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private String images;
    private String label;
    private Long channelId;
    private Integer status;
    private LocalDateTime createdTime;
    private LocalDateTime publishTime;



}
