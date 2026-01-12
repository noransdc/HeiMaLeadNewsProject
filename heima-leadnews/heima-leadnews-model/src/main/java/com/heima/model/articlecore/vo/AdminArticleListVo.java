package com.heima.model.articlecore.vo;


import lombok.Data;

import java.time.LocalDateTime;


@Data
public class AdminArticleListVo {

    private Long id;
    private Long authorId;
    private String title;
    private Integer isEnabled;
    private LocalDateTime publishTime;


    private String images;
    //审核状态：0草稿，1已提交，2自动审核失败，3人工审核失败，8审核成功，9已发布
    private Integer status;
    private Integer type;
    private LocalDateTime createdTime;
    private LocalDateTime submitedTime;
    private String labels;
    private String authorName;

}
