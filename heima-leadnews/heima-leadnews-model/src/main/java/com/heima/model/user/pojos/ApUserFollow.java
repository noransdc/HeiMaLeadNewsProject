package com.heima.model.user.pojos;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ap_user_follow")
public class ApUserFollow {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("follow_id")
    private Long followId;

    @TableField("follow_name")
    private String followName;

    @TableField("level")
    private Integer level;

    @TableField("is_notice")
    private Integer isNotice;

    @TableField("created_time")
    private Date createdTime;


}
