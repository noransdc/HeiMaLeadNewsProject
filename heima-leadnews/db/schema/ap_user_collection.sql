
-- USE leadnews_user; 如果建表时未指定库名，则可以切换到该库

CREATE TABLE leadnews_user.ap_user_collection
(
    id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id      BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    article_id   BIGINT UNSIGNED NOT NULL COMMENT '文章ID',
    created_time DATETIME NOT NULL COMMENT '收藏时间',

    PRIMARY KEY (id),
    UNIQUE KEY uk_user_article (user_id, article_id),
    KEY          idx_user_id (user_id),
    KEY          idx_article_id (article_id)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='用户收藏关系表';