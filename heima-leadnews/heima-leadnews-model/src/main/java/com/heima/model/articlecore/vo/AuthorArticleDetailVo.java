package com.heima.model.articlecore.vo;


import lombok.Data;

import java.time.LocalDateTime;


@Data
public class AuthorArticleDetailVo {


    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private Long channelId;
    private Integer status;
    private LocalDateTime publishTime;


    private String labels;
    private String images;
    private LocalDateTime createdTime;
    private Integer type;
    private String authorName;
    private LocalDateTime updateTime;


}
