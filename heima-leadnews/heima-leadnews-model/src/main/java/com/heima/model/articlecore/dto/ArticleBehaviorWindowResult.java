package com.heima.model.articlecore.dto;


import lombok.Data;

@Data
public class ArticleBehaviorWindowResult {

    private Long articleId;
    private long windowEnd;
    private ArticleBehaviorAgg agg;


}
