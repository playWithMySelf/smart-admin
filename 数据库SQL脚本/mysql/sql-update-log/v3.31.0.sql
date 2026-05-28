-- 工作项积分模块

CREATE TABLE IF NOT EXISTS `t_work_item_type` (
  `work_item_type_id` bigint NOT NULL AUTO_INCREMENT COMMENT '工作项类型ID',
  `type_name` varchar(50) NOT NULL COMMENT '类型名称',
  `sort` int DEFAULT 0 COMMENT '排序',
  `disabled_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '禁用状态',
  `deleted_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`work_item_type_id`),
  KEY `idx_deleted_disabled` (`deleted_flag`, `disabled_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工作项类型';

CREATE TABLE IF NOT EXISTS `t_work_item` (
  `work_item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '工作项ID',
  `work_item_type_id` bigint NOT NULL COMMENT '工作项类型ID',
  `work_item_name` varchar(100) NOT NULL COMMENT '工作项名称',
  `description` varchar(1000) DEFAULT NULL COMMENT '描述',
  `score_standard` varchar(2000) DEFAULT NULL COMMENT '评分标准',
  `standard_score` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '标准分',
  `sort` int DEFAULT 0 COMMENT '排序',
  `disabled_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '禁用状态',
  `deleted_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`work_item_id`),
  KEY `idx_type_deleted_disabled` (`work_item_type_id`, `deleted_flag`, `disabled_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工作项';

CREATE TABLE IF NOT EXISTS `t_work_daily_report` (
  `work_daily_report_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日报ID',
  `employee_id` bigint NOT NULL COMMENT '员工ID',
  `employee_name` varchar(100) NOT NULL COMMENT '员工姓名',
  `department_id` bigint DEFAULT NULL COMMENT '部门ID',
  `department_name` varchar(200) DEFAULT NULL COMMENT '部门名称',
  `report_date` date NOT NULL COMMENT '日报日期',
  `status` int NOT NULL COMMENT '状态：1草稿 2待审核 3审核通过 4审核失败',
  `total_score` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '总分',
  `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
  `latest_audit_employee_id` bigint DEFAULT NULL COMMENT '最新审核人ID',
  `latest_audit_employee_name` varchar(100) DEFAULT NULL COMMENT '最新审核人姓名',
  `latest_audit_time` datetime DEFAULT NULL COMMENT '最新审核时间',
  `latest_fail_reason` varchar(1000) DEFAULT NULL COMMENT '最新审核失败原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`work_daily_report_id`),
  UNIQUE KEY `uk_employee_report_date` (`employee_id`, `report_date`),
  KEY `idx_report_date_status` (`report_date`, `status`),
  KEY `idx_employee_status` (`employee_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工作项日报';

CREATE TABLE IF NOT EXISTS `t_work_daily_report_item` (
  `work_daily_report_item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日报明细ID',
  `work_daily_report_id` bigint NOT NULL COMMENT '日报ID',
  `work_item_id` bigint NOT NULL COMMENT '工作项ID',
  `work_item_type_id` bigint NOT NULL COMMENT '工作项类型ID快照',
  `work_item_type_name` varchar(50) NOT NULL COMMENT '工作项类型名称快照',
  `work_item_name` varchar(100) NOT NULL COMMENT '工作项名称快照',
  `description` varchar(1000) DEFAULT NULL COMMENT '工作项描述快照',
  `score_standard` varchar(2000) DEFAULT NULL COMMENT '评分标准快照',
  `standard_score` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '标准分快照',
  `final_score` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '最终分',
  `deduct_reason` varchar(1000) DEFAULT NULL COMMENT '扣分原因',
  `finish_remark` varchar(2000) DEFAULT NULL COMMENT '完成说明',
  `sort` int DEFAULT 0 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`work_daily_report_item_id`),
  UNIQUE KEY `uk_report_work_item` (`work_daily_report_id`, `work_item_id`),
  KEY `idx_type` (`work_item_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工作项日报明细';

CREATE TABLE IF NOT EXISTS `t_work_daily_report_file` (
  `work_daily_report_file_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日报明细图片ID',
  `work_daily_report_item_id` bigint NOT NULL COMMENT '日报明细ID',
  `file_id` bigint DEFAULT NULL COMMENT '文件ID',
  `file_key` varchar(200) NOT NULL COMMENT '文件key',
  `file_name` varchar(100) DEFAULT NULL COMMENT '文件名称',
  `sort` int DEFAULT 0 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`work_daily_report_file_id`),
  KEY `idx_report_item` (`work_daily_report_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工作项日报明细图片';

CREATE TABLE IF NOT EXISTS `t_work_daily_report_audit` (
  `work_daily_report_audit_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日报审核历史ID',
  `work_daily_report_id` bigint NOT NULL COMMENT '日报ID',
  `audit_result` int NOT NULL COMMENT '审核结果：3审核通过 4审核失败',
  `fail_reason` varchar(1000) DEFAULT NULL COMMENT '审核失败原因',
  `audit_employee_id` bigint NOT NULL COMMENT '审核人ID',
  `audit_employee_name` varchar(100) NOT NULL COMMENT '审核人姓名',
  `audit_time` datetime NOT NULL COMMENT '审核时间',
  PRIMARY KEY (`work_daily_report_audit_id`),
  KEY `idx_report` (`work_daily_report_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='工作项日报审核历史';

INSERT INTO `t_config` (`config_name`, `config_key`, `config_value`, `remark`)
SELECT '工作项允许补填天数', 'work_item_allow_replenish_days', '2', '员工可补填最近N天内的工作项日报'
WHERE NOT EXISTS (SELECT 1 FROM `t_config` WHERE `config_key` = 'work_item_allow_replenish_days');

INSERT INTO t_menu (menu_name, menu_type, parent_id, sort, path, component, icon, frame_flag, cache_flag, visible_flag, disabled_flag, deleted_flag, create_user_id)
SELECT '工作项积分', 1, 0, 20, '/workitem', NULL, 'ProfileOutlined', 0, 0, 1, 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE menu_name = '工作项积分' AND parent_id = 0);

SELECT menu_id INTO @workitem_parent_id FROM t_menu WHERE menu_name = '工作项积分' AND parent_id = 0 LIMIT 1;

INSERT INTO t_menu (menu_name, menu_type, parent_id, sort, path, component, icon, frame_flag, cache_flag, visible_flag, disabled_flag, deleted_flag, create_user_id)
SELECT '工作项维护', 2, @workitem_parent_id, 1, '/workitem/maintain', '/business/workitem/work-item-maintain.vue', 'AppstoreOutlined', 0, 1, 1, 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE path = '/workitem/maintain');

INSERT INTO t_menu (menu_name, menu_type, parent_id, sort, path, component, icon, frame_flag, cache_flag, visible_flag, disabled_flag, deleted_flag, create_user_id)
SELECT '我的日报', 2, @workitem_parent_id, 2, '/workitem/my-daily-report', '/business/workitem/my-daily-report.vue', 'EditOutlined', 0, 1, 1, 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE path = '/workitem/my-daily-report');

INSERT INTO t_menu (menu_name, menu_type, parent_id, sort, path, component, icon, frame_flag, cache_flag, visible_flag, disabled_flag, deleted_flag, create_user_id)
SELECT '日报审核', 2, @workitem_parent_id, 3, '/workitem/daily-review', '/business/workitem/daily-review.vue', 'AuditOutlined', 0, 1, 1, 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE path = '/workitem/daily-review');

INSERT INTO t_menu (menu_name, menu_type, parent_id, sort, path, component, icon, frame_flag, cache_flag, visible_flag, disabled_flag, deleted_flag, create_user_id)
SELECT '积分报表', 2, @workitem_parent_id, 4, '/workitem/score-report', '/business/workitem/score-report.vue', 'BarChartOutlined', 0, 1, 1, 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE path = '/workitem/score-report');

SELECT menu_id INTO @workitem_maintain_id FROM t_menu WHERE path = '/workitem/maintain' LIMIT 1;
SELECT menu_id INTO @workitem_daily_id FROM t_menu WHERE path = '/workitem/my-daily-report' LIMIT 1;
SELECT menu_id INTO @workitem_review_id FROM t_menu WHERE path = '/workitem/daily-review' LIMIT 1;
SELECT menu_id INTO @workitem_report_id FROM t_menu WHERE path = '/workitem/score-report' LIMIT 1;

INSERT INTO t_menu (menu_name, menu_type, parent_id, perms_type, api_perms, web_perms, context_menu_id, frame_flag, cache_flag, visible_flag, disabled_flag, deleted_flag, create_user_id)
SELECT '查询工作项类型', 3, @workitem_maintain_id, 1, 'workitem:type:query', 'workitem:type:query', @workitem_maintain_id, 0, 0, 1, 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE web_perms = 'workitem:type:query');
INSERT INTO t_menu (menu_name, menu_type, parent_id, perms_type, api_perms, web_perms, context_menu_id, frame_flag, cache_flag, visible_flag, disabled_flag, deleted_flag, create_user_id)
SELECT '添加工作项类型', 3, @workitem_maintain_id, 1, 'workitem:type:add', 'workitem:type:add', @workitem_maintain_id, 0, 0, 1, 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE web_perms = 'workitem:type:add');
INSERT INTO t_menu (menu_name, menu_type, parent_id, perms_type, api_perms, web_perms, context_menu_id, frame_flag, cache_flag, visible_flag, disabled_flag, deleted_flag, create_user_id)
SELECT '更新工作项类型', 3, @workitem_maintain_id, 1, 'workitem:type:update', 'workitem:type:update', @workitem_maintain_id, 0, 0, 1, 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE web_perms = 'workitem:type:update');
INSERT INTO t_menu (menu_name, menu_type, parent_id, perms_type, api_perms, web_perms, context_menu_id, frame_flag, cache_flag, visible_flag, disabled_flag, deleted_flag, create_user_id)
SELECT '删除工作项类型', 3, @workitem_maintain_id, 1, 'workitem:type:delete', 'workitem:type:delete', @workitem_maintain_id, 0, 0, 1, 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE web_perms = 'workitem:type:delete');
INSERT INTO t_menu (menu_name, menu_type, parent_id, perms_type, api_perms, web_perms, context_menu_id, frame_flag, cache_flag, visible_flag, disabled_flag, deleted_flag, create_user_id)
SELECT '查询工作项', 3, @workitem_maintain_id, 1, 'workitem:item:query', 'workitem:item:query', @workitem_maintain_id, 0, 0, 1, 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE web_perms = 'workitem:item:query');
INSERT INTO t_menu (menu_name, menu_type, parent_id, perms_type, api_perms, web_perms, context_menu_id, frame_flag, cache_flag, visible_flag, disabled_flag, deleted_flag, create_user_id)
SELECT '添加工作项', 3, @workitem_maintain_id, 1, 'workitem:item:add', 'workitem:item:add', @workitem_maintain_id, 0, 0, 1, 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE web_perms = 'workitem:item:add');
INSERT INTO t_menu (menu_name, menu_type, parent_id, perms_type, api_perms, web_perms, context_menu_id, frame_flag, cache_flag, visible_flag, disabled_flag, deleted_flag, create_user_id)
SELECT '更新工作项', 3, @workitem_maintain_id, 1, 'workitem:item:update', 'workitem:item:update', @workitem_maintain_id, 0, 0, 1, 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE web_perms = 'workitem:item:update');
INSERT INTO t_menu (menu_name, menu_type, parent_id, perms_type, api_perms, web_perms, context_menu_id, frame_flag, cache_flag, visible_flag, disabled_flag, deleted_flag, create_user_id)
SELECT '删除工作项', 3, @workitem_maintain_id, 1, 'workitem:item:delete', 'workitem:item:delete', @workitem_maintain_id, 0, 0, 1, 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE web_perms = 'workitem:item:delete');
INSERT INTO t_menu (menu_name, menu_type, parent_id, perms_type, api_perms, web_perms, context_menu_id, frame_flag, cache_flag, visible_flag, disabled_flag, deleted_flag, create_user_id)
SELECT '查看我的日报', 3, @workitem_daily_id, 1, 'workitem:daily:my', 'workitem:daily:my', @workitem_daily_id, 0, 0, 1, 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE web_perms = 'workitem:daily:my');
INSERT INTO t_menu (menu_name, menu_type, parent_id, perms_type, api_perms, web_perms, context_menu_id, frame_flag, cache_flag, visible_flag, disabled_flag, deleted_flag, create_user_id)
SELECT '保存日报草稿', 3, @workitem_daily_id, 1, 'workitem:daily:save', 'workitem:daily:save', @workitem_daily_id, 0, 0, 1, 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE web_perms = 'workitem:daily:save');
INSERT INTO t_menu (menu_name, menu_type, parent_id, perms_type, api_perms, web_perms, context_menu_id, frame_flag, cache_flag, visible_flag, disabled_flag, deleted_flag, create_user_id)
SELECT '提交日报', 3, @workitem_daily_id, 1, 'workitem:daily:submit', 'workitem:daily:submit', @workitem_daily_id, 0, 0, 1, 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE web_perms = 'workitem:daily:submit');
INSERT INTO t_menu (menu_name, menu_type, parent_id, perms_type, api_perms, web_perms, context_menu_id, frame_flag, cache_flag, visible_flag, disabled_flag, deleted_flag, create_user_id)
SELECT '查看日报审核', 3, @workitem_review_id, 1, 'workitem:daily:review', 'workitem:daily:review', @workitem_review_id, 0, 0, 1, 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE web_perms = 'workitem:daily:review');
INSERT INTO t_menu (menu_name, menu_type, parent_id, perms_type, api_perms, web_perms, context_menu_id, frame_flag, cache_flag, visible_flag, disabled_flag, deleted_flag, create_user_id)
SELECT '审核日报', 3, @workitem_review_id, 1, 'workitem:daily:audit', 'workitem:daily:audit', @workitem_review_id, 0, 0, 1, 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE web_perms = 'workitem:daily:audit');
INSERT INTO t_menu (menu_name, menu_type, parent_id, perms_type, api_perms, web_perms, context_menu_id, frame_flag, cache_flag, visible_flag, disabled_flag, deleted_flag, create_user_id)
SELECT '查看积分报表', 3, @workitem_report_id, 1, 'workitem:score:report', 'workitem:score:report', @workitem_report_id, 0, 0, 1, 0, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM t_menu WHERE web_perms = 'workitem:score:report');
