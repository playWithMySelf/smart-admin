-- 工作项积分模块模拟数据
-- 类型：日常、运维、核验、办公、学习
-- 工作项：每个类型 50 条，名称格式为 类型 + 两位编号，例如 日常01

INSERT INTO `t_work_item_type` (`type_name`, `sort`, `disabled_flag`, `deleted_flag`)
SELECT '日常', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM `t_work_item_type` WHERE `type_name` = '日常' AND `deleted_flag` = 0);

INSERT INTO `t_work_item_type` (`type_name`, `sort`, `disabled_flag`, `deleted_flag`)
SELECT '运维', 2, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM `t_work_item_type` WHERE `type_name` = '运维' AND `deleted_flag` = 0);

INSERT INTO `t_work_item_type` (`type_name`, `sort`, `disabled_flag`, `deleted_flag`)
SELECT '核验', 3, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM `t_work_item_type` WHERE `type_name` = '核验' AND `deleted_flag` = 0);

INSERT INTO `t_work_item_type` (`type_name`, `sort`, `disabled_flag`, `deleted_flag`)
SELECT '办公', 4, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM `t_work_item_type` WHERE `type_name` = '办公' AND `deleted_flag` = 0);

INSERT INTO `t_work_item_type` (`type_name`, `sort`, `disabled_flag`, `deleted_flag`)
SELECT '学习', 5, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM `t_work_item_type` WHERE `type_name` = '学习' AND `deleted_flag` = 0);

SELECT `work_item_type_id` INTO @work_item_type_daily_id FROM `t_work_item_type` WHERE `type_name` = '日常' AND `deleted_flag` = 0 LIMIT 1;
SELECT `work_item_type_id` INTO @work_item_type_ops_id FROM `t_work_item_type` WHERE `type_name` = '运维' AND `deleted_flag` = 0 LIMIT 1;
SELECT `work_item_type_id` INTO @work_item_type_check_id FROM `t_work_item_type` WHERE `type_name` = '核验' AND `deleted_flag` = 0 LIMIT 1;
SELECT `work_item_type_id` INTO @work_item_type_office_id FROM `t_work_item_type` WHERE `type_name` = '办公' AND `deleted_flag` = 0 LIMIT 1;
SELECT `work_item_type_id` INTO @work_item_type_study_id FROM `t_work_item_type` WHERE `type_name` = '学习' AND `deleted_flag` = 0 LIMIT 1;

CREATE TEMPORARY TABLE IF NOT EXISTS `tmp_work_item_mock_seq` (
  `seq` int NOT NULL PRIMARY KEY
);

TRUNCATE TABLE `tmp_work_item_mock_seq`;

INSERT INTO `tmp_work_item_mock_seq` (`seq`) VALUES
(1),(2),(3),(4),(5),(6),(7),(8),(9),(10),
(11),(12),(13),(14),(15),(16),(17),(18),(19),(20),
(21),(22),(23),(24),(25),(26),(27),(28),(29),(30),
(31),(32),(33),(34),(35),(36),(37),(38),(39),(40),
(41),(42),(43),(44),(45),(46),(47),(48),(49),(50);

INSERT INTO `t_work_item` (`work_item_type_id`, `work_item_name`, `description`, `score_standard`, `standard_score`, `sort`, `disabled_flag`, `deleted_flag`)
SELECT
  @work_item_type_daily_id,
  CONCAT('日常', LPAD(`seq`, 2, '0')),
  CONCAT('日常工作项模拟数据', LPAD(`seq`, 2, '0')),
  '按完成情况计入标准分',
  1.00,
  `seq`,
  0,
  0
FROM `tmp_work_item_mock_seq`
WHERE NOT EXISTS (
  SELECT 1
  FROM `t_work_item`
  WHERE `work_item_type_id` = @work_item_type_daily_id
    AND `work_item_name` = CONCAT('日常', LPAD(`tmp_work_item_mock_seq`.`seq`, 2, '0'))
    AND `deleted_flag` = 0
);

INSERT INTO `t_work_item` (`work_item_type_id`, `work_item_name`, `description`, `score_standard`, `standard_score`, `sort`, `disabled_flag`, `deleted_flag`)
SELECT
  @work_item_type_ops_id,
  CONCAT('运维', LPAD(`seq`, 2, '0')),
  CONCAT('运维工作项模拟数据', LPAD(`seq`, 2, '0')),
  '按完成情况计入标准分',
  1.00,
  `seq`,
  0,
  0
FROM `tmp_work_item_mock_seq`
WHERE NOT EXISTS (
  SELECT 1
  FROM `t_work_item`
  WHERE `work_item_type_id` = @work_item_type_ops_id
    AND `work_item_name` = CONCAT('运维', LPAD(`tmp_work_item_mock_seq`.`seq`, 2, '0'))
    AND `deleted_flag` = 0
);

INSERT INTO `t_work_item` (`work_item_type_id`, `work_item_name`, `description`, `score_standard`, `standard_score`, `sort`, `disabled_flag`, `deleted_flag`)
SELECT
  @work_item_type_check_id,
  CONCAT('核验', LPAD(`seq`, 2, '0')),
  CONCAT('核验工作项模拟数据', LPAD(`seq`, 2, '0')),
  '按完成情况计入标准分',
  1.00,
  `seq`,
  0,
  0
FROM `tmp_work_item_mock_seq`
WHERE NOT EXISTS (
  SELECT 1
  FROM `t_work_item`
  WHERE `work_item_type_id` = @work_item_type_check_id
    AND `work_item_name` = CONCAT('核验', LPAD(`tmp_work_item_mock_seq`.`seq`, 2, '0'))
    AND `deleted_flag` = 0
);

INSERT INTO `t_work_item` (`work_item_type_id`, `work_item_name`, `description`, `score_standard`, `standard_score`, `sort`, `disabled_flag`, `deleted_flag`)
SELECT
  @work_item_type_office_id,
  CONCAT('办公', LPAD(`seq`, 2, '0')),
  CONCAT('办公工作项模拟数据', LPAD(`seq`, 2, '0')),
  '按完成情况计入标准分',
  1.00,
  `seq`,
  0,
  0
FROM `tmp_work_item_mock_seq`
WHERE NOT EXISTS (
  SELECT 1
  FROM `t_work_item`
  WHERE `work_item_type_id` = @work_item_type_office_id
    AND `work_item_name` = CONCAT('办公', LPAD(`tmp_work_item_mock_seq`.`seq`, 2, '0'))
    AND `deleted_flag` = 0
);

INSERT INTO `t_work_item` (`work_item_type_id`, `work_item_name`, `description`, `score_standard`, `standard_score`, `sort`, `disabled_flag`, `deleted_flag`)
SELECT
  @work_item_type_study_id,
  CONCAT('学习', LPAD(`seq`, 2, '0')),
  CONCAT('学习工作项模拟数据', LPAD(`seq`, 2, '0')),
  '按完成情况计入标准分',
  1.00,
  `seq`,
  0,
  0
FROM `tmp_work_item_mock_seq`
WHERE NOT EXISTS (
  SELECT 1
  FROM `t_work_item`
  WHERE `work_item_type_id` = @work_item_type_study_id
    AND `work_item_name` = CONCAT('学习', LPAD(`tmp_work_item_mock_seq`.`seq`, 2, '0'))
    AND `deleted_flag` = 0
);

DROP TEMPORARY TABLE IF EXISTS `tmp_work_item_mock_seq`;
