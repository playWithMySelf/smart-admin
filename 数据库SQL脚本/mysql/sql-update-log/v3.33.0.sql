-- 待办工作服务端持久化，Web 和移动端共用同一份数据
CREATE TABLE IF NOT EXISTS `t_to_be_done` (
  `to_be_done_id` bigint NOT NULL AUTO_INCREMENT COMMENT '待办ID',
  `employee_id` bigint NOT NULL COMMENT '员工ID',
  `title` varchar(100) NOT NULL COMMENT '待办标题',
  `done_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '完成状态',
  `star_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '星标状态',
  `deleted_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`to_be_done_id`),
  KEY `idx_employee_done_deleted` (`employee_id`, `done_flag`, `deleted_flag`),
  KEY `idx_employee_deleted` (`employee_id`, `deleted_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='待办工作';
