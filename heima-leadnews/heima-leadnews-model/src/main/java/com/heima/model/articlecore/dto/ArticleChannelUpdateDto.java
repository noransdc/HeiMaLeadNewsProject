package com.heima.model.articlecore.dto;


import lombok.Data;


@Data
public class ArticleChannelUpdateDto {


    private Long id;
    private String name;
    private String description;
    private Boolean status;
    private Boolean isDefault;
    private Integer ord;



}
