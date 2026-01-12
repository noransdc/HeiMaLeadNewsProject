package com.heima.model.articlecore.vo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class AdminArticleVo {

    private Long id;

    private Long authorId;

    private String title;

    private Long channelId;

    private String label;

    private String rejectReason;

    private Integer isEnabled;

    private LocalDateTime createTime;

    private LocalDateTime publishTime;

    private LocalDateTime lastEditTime;

    private LocalDateTime updateTime;



    private String images;
    //审核状态：0草稿，1已提交，2自动审核失败，3人工审核失败，8审核成功，9已发布
    private Integer status;
    private Integer type;

}
