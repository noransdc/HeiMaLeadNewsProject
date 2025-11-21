ALTER TABLE leadnews_wemedia.wm_material
  ADD COLUMN is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '0正常 1已删除' AFTER is_collection,
  ADD COLUMN delete_time DATETIME NULL COMMENT '删除时间' AFTER is_deleted;


