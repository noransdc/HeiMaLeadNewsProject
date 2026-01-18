package com.heima.model.articlecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@TableName("article_event_consumed")
public class ArticleEventConsumed {


    @TableId(value = "event_id", type = IdType.INPUT)
    private String eventId;

    @TableField("event_type")
    private String eventType;

    @TableField("article_id")
    private Long articleId;

    @TableField("create_time")
    private LocalDateTime createTime;


}
