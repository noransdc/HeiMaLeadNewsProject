package com.heima.model.schedule.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;



@Data
@TableName("schedule_task")
public class ScheduleTask {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("task_type")
    private String taskType;

    @TableField("biz_key")
    private String bizKey;

    @TableField("execute_time")
    private Date executeTime;

    @TableField("status")
    private Integer status;

    @TableField("retry_count")
    private Integer retryCount;

    @TableField("max_retry")
    private Integer maxRetry;

    @TableField("parameters")
    private String parameters;

    @TableField("error_msg")
    private String errorMsg;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableField("lock_time")
    private Date lockTime;

    @TableField("lock_owner")
    private String lockOwner;


}
