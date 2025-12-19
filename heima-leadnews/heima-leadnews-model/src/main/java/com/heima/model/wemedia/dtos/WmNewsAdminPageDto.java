package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

import java.util.Date;

@Data
public class WmNewsAdminPageDto extends PageRequestDto {


    private String title;
    private Integer status;
    private Integer page;
    private Integer size;

}