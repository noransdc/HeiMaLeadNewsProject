package com.heima.model.articlecore.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@TableName("article_channel")
public class ArticleChannel {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("is_default")
    private Integer isDefault;

    @TableField("is_enabled")
    private Integer isEnabled;

    @TableField("sort")
    private Integer sort;

    @TableField("is_delete")
    private Integer isDelete;

    @TableField("delete_time")
    private LocalDateTime deleteTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;




}
