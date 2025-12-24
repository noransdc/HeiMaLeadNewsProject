package com.heima.model.behavior.dto;

import lombok.Data;

@Data
public class DislikeBehaviorDto {


    //文章id
    private Integer articleId;

    //0 不喜欢   1 取消不喜欢
    private Integer type;


}

