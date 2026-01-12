package com.heima.model.articlecore.vo;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthorArticleListVo {


    private Long id;
    private String title;
    private String images;
    private Integer status;


}
