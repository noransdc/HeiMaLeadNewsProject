package com.heima.model.articlecore.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GreenScanRspDto {


    private Integer riskLevel;
    private String suggestion;


}
