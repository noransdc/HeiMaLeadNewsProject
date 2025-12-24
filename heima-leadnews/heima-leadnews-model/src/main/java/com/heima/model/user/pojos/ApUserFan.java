package com.heima.model.user.pojos;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ap_user_fan")
public class ApUserFan {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Integer userId;

    @TableField("fans_id")
    private Integer fansId;

    @TableField("fans_name")
    private String fansName;

    @TableField("level")
    private Integer level;

    @TableField("create_time")
    private Date createTime;

    @TableField("is_display")
    private Integer isDisplay;

    @TableField("is_shield_letter")
    private Integer isShieldLetter;

    @TableField("is_shield_comment")
    private Integer isShieldComment;



}
