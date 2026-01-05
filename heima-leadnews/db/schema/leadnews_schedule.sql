
-- V1.0 initial schedule task
create table schedule_task
(
    id           bigint unsigned  not null auto_increment comment '主键id',
    task_type    varchar(50)      not null comment '任务类型',
    biz_key      varchar(128)     not null comment '业务唯一键',
    execute_time datetime         not null comment '执行时间',
    status       tinyint unsigned not null default 0 comment '0 INIT, 1 RUNNING, 2 SUCCESS, 3 FAIL, 4 CANCELLED, 5 PAUSED',
    retry_count  tinyint unsigned not null default 0 comment '重试次数',
    max_retry    tinyint unsigned not null default 3 comment '最大重试次数',
    parameters   JSON             not null comment '任务参数',
    error_msg    varchar(512)              default null comment '错误信息',
    create_time  datetime         not null default current_timestamp comment '创建时间',
    update_time  datetime         not null default current_timestamp
        on update current_timestamp comment '更新时间',
    lock_time    datetime                  default null comment '锁定时间',
    lock_owner   varchar(64)               default null comment '执行节点',

    primary key (id),
    unique key uk_biz_key (biz_key),
    key (status, execute_time)

) engine = InnoDB
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
    comment = '通用任务调度表';