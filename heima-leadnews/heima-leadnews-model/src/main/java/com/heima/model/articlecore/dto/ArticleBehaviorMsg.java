package com.heima.model.articlecore.dto;


import lombok.Data;

@Data
public class ArticleBehaviorMsg {

    private String eventId;
    private Long articleId;
    private String eventType;

}
