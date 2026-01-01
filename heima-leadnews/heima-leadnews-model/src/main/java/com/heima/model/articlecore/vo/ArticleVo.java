package com.heima.model.articlecore.vo;


import lombok.Data;

import java.util.Date;

@Data
public class ArticleVo {


    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private String coverImgUrl;
    private String label;
    private Long channelId;
    private Integer status;
    private Date createdTime;
    private Date publishTime;



}
