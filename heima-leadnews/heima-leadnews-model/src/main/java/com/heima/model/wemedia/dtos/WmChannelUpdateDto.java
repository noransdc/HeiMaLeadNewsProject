package com.heima.model.wemedia.dtos;


import lombok.Data;

@Data
public class WmChannelUpdateDto {

    private String description;
    private Integer id;
    private Boolean isDefault;
    private String name;
    private Integer ord;
    private Boolean status;


}
