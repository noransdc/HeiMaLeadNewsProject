package com.heima.model.articlecore.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleAuditRsp {


    private Long articleId;
    private Integer auditStatus;
    private String errorMsg;


}
