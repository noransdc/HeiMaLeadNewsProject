package com.heima.model.schedule.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleAuditCompensateDto {


    private Long articleId;
    private LocalDateTime publishTime;

}
