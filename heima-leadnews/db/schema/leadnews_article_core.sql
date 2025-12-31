/**
default character set utf8mb4 ——“用什么编码存字符串”
    它决定的是：
        字符串是“用什么方式存进数据库”的
    utf8mb4 的特点：
        支持完整 Unicode
        支持：
        中文
        各国语言
        Emoji（🔥👍😂）
        是 MySQL 5.7 / 8.0 的事实标准

collate utf8mb4_unicode_ci ——“字符串怎么比较和排序”
    它决定的是：
        字符串比较规则（大小写、排序、是否区分重音等）
    拆开来看：
        unicode
            按 Unicode 规则比较字符
            对多语言更友好
        ci（case insensitive）
            不区分大小写
            "Java" = "java"

  int(2) 这个写法 在 MySQL 8 已被废弃（只是显示宽度）

  计数器字段：NOT NULL + DEFAULT 0 是底线；

 */

create database if not exists leadnews_article_core
    default character set utf8mb4
    collate utf8mb4_unicode_ci;

-- v1.0: initial article table
create table article
(
    id             bigint unsigned  not null auto_increment comment '主键',
    author_id      bigint unsigned  not null comment '作者id',
    title          varchar(100)     not null comment '标题',
    content_id     bigint unsigned  not null comment '内容id',
    cover_img_url  varchar(255) comment '封面图url',
    channel_id     bigint unsigned  not null comment '频道id',
    label          varchar(20) comment '标签',
    audit_status   tinyint unsigned not null default 0 comment '审核状态：0草稿，1已提交，2自动审核失败，3人工审核失败，8审核成功，9已发布',
    is_delete      tinyint unsigned not null default 0 comment '是否已删除：0未删除，1已删除',
    delete_time    DATETIME                  default null comment '删除时间',
    created_time   DATETIME         not null default current_timestamp comment '创建时间',
    publish_time   DATETIME                  default null comment '发布时间',
    last_edit_time DATETIME         not null default current_timestamp comment '最后修改内容时间',
    update_time    DATETIME         not null default current_timestamp
        on update current_timestamp comment '行更新时间',

    primary key (id),
    key idx_author_id (author_id),
    key idx_channel_id (channel_id),
    key idx_publish_time (publish_time)


) engine = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    comment ='文章核心表';

-- v1.1: rename created_time to create_time
alter table article
    rename column created_time to create_time;

-- v1.2: delete column content_id
alter table article
    drop column content_id;

-- v1.0: initial article content table
create table article_content
(
    id         bigint unsigned not null auto_increment comment '主键id',
    article_id bigint unsigned not null comment '文章id',
    content    mediumtext comment '文章内容',

    primary key (id),
    unique key uk_article_id (article_id)

) engine = InnoDB
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
    comment = '文章内容表';

-- v1.1: delete the column id, set the article id as primary key
alter table article_content
    drop primary key,
    drop column id;

alter table article_content
    add primary key (article_id),
    drop index uk_article_id;


-- v1.0: initial article interaction
create table article_interaction
(
    article_id    bigint unsigned not null comment '文章id',

    view_count    int unsigned    not null default 0 comment '浏览量',
    like_count    int unsigned    not null default 0 comment '点赞数',
    comment_count int unsigned    not null default 0 comment '评论数',
    collect_count int unsigned    not null default 0 comment '收藏量',

    update_time   datetime        not null default current_timestamp
        on update current_timestamp comment '更新时间',

    primary key (article_id)

) engine = InnoDB
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
    comment ='文章交互统计表';


-- v1.0 initial article channel
create table article_channel
(
    id          bigint unsigned   not null auto_increment comment '主键id',
    name        varchar(10)       not null comment '频道名称',
    description varchar(100) comment '频道描述',
    is_default  tinyint unsigned  not null default 0 comment '是否默认频道',
    is_enabled  tinyint unsigned  not null default 1 comment '是否可用',
    sort        smallint unsigned not null default 0 comment '排序',
    create_time datetime          not null default current_timestamp comment '创建时间',
    update_time datetime          not null default current_timestamp
        on update current_timestamp comment '更新时间',

    primary key (id)

) engine = InnoDB
  default charset = utf8mb4
  collate = utf8mb4_unicode_ci
    comment = '文章频道表';