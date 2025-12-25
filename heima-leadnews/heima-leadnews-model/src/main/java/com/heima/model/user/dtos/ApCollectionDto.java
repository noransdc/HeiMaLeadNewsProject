package com.heima.model.user.dtos;


import lombok.Data;

@Data
public class ApCollectionDto {

    //文章id
    private Long entryId;

    //1收藏    0取消收藏
    private Integer operation;


}
