package com.heima.model.articlecore.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


@Data
@TableName(value = "article")
public class Article {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("author_id")
    private Long authorId;

    @TableField("title")
    private String title;

    @TableField("cover_img_url")
    private String coverImgUrl;

    @TableField("cover_type")
    private Integer coverType;

    @TableField("channel_id")
    private Long channelId;

    @TableField("label")
    private String label;

    //审核状态：0草稿，1已提交，2自动审核失败，3人工审核失败，8审核成功，9已发布
    @TableField("audit_status")
    private Integer auditStatus;

    @TableField("is_delete")
    private Integer isDelete;

    @TableField("delete_time")
    private Date deleteTime;

    @TableField("is_enabled")
    private Integer isEnabled;

    @TableField("create_time")
    private Date createTime;

    @TableField("publish_time")
    private Date publishTime;

    @TableField("last_edit_time")
    private Date lastEditTime;

    @TableField("update_time")
    private Date updateTime;


}
