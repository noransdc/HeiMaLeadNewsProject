package com.heima.model.articlecore.dto;


import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;


@Data
public class AdminArticlePageDto extends PageRequestDto {


    private String title;
    private Integer status;


}
