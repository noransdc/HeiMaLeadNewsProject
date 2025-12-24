package com.heima.model.behavior.dto;

import lombok.Data;

@Data
public class LikeBehaviorDto {


    //文章id
    private Long articleId;

    //0 点赞   1 取消点赞
    private Integer operation;

    //0文章  1动态   2评论
    private Integer type;


}

