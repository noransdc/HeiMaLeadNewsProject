package com.heima.model.articlecore.dto;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

import java.time.LocalDate;


@Data
public class AuthorArticlePageDto extends PageRequestDto {


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

    //
    private Long authorId;


}
