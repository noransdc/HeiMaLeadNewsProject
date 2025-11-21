-- ----------------------------
-- Table structure for wm_material
-- ----------------------------
DROP TABLE IF EXISTS `wm_material`;
CREATE TABLE `wm_material` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) unsigned DEFAULT NULL COMMENT '自媒体用户ID',
  `url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片地址',
  `type` tinyint(1) unsigned DEFAULT NULL COMMENT '素材类型 0图片 1视频',
  `is_collection` tinyint unsigned DEFAULT NULL COMMENT '是否收藏',
  is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '0正常 1已删除',
  delete_time DATETIME NULL COMMENT '删除时间',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='自媒体图文素材信息表';