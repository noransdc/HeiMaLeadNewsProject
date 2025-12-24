package com.heima.model.user.dtos;


import lombok.Data;

@Data
public class ApFollowDto {


    //作者id
    private Integer authorId;

    //0  关注   1  取消
    private Integer operation;


}
