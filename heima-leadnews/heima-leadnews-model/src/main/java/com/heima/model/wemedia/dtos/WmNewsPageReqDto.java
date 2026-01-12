package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class WmNewsPageReqDto extends PageRequestDto {

    /**
     * 状态
     */
    private Integer status;
    /**
     * 开始时间
     */
    private LocalDate beginPubDate;
    /**
     * 结束时间
     */
    private LocalDate endPubDate;
    /**
     * 所属频道ID
     */
    private Long channelId;
    /**
     * 关键字
     */
    private String keyword;
}