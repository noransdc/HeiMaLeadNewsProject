package com.heima.model.articlecore.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


@Data
@TableName("author_material")
public class AuthorMaterial {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("author_id")
    private Long authorId;

    @TableField("url")
    private String url;

    @TableField("is_collect")
    private Integer isCollect;

    @TableField("is_delete")
    private Integer isDelete;

    @TableField("delete_time")
    private Date deleteTime;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;




}
