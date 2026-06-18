-- 允许同一日报重复添加同一工作项，每条明细独立计分
SELECT COUNT(1) INTO @uk_report_work_item_count
FROM INFORMATION_SCHEMA.STATISTICS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 't_work_daily_report_item'
  AND INDEX_NAME = 'uk_report_work_item';

SET @drop_uk_report_work_item_sql = IF(@uk_report_work_item_count > 0,
  'ALTER TABLE `t_work_daily_report_item` DROP INDEX `uk_report_work_item`',
  'SELECT 1');
PREPARE drop_uk_report_work_item_stmt FROM @drop_uk_report_work_item_sql;
EXECUTE drop_uk_report_work_item_stmt;
DEALLOCATE PREPARE drop_uk_report_work_item_stmt;

SELECT COUNT(1) INTO @idx_report_work_item_count
FROM INFORMATION_SCHEMA.STATISTICS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 't_work_daily_report_item'
  AND INDEX_NAME = 'idx_report_work_item';

SET @add_idx_report_work_item_sql = IF(@idx_report_work_item_count = 0,
  'ALTER TABLE `t_work_daily_report_item` ADD INDEX `idx_report_work_item` (`work_daily_report_id`, `work_item_id`)',
  'SELECT 1');
PREPARE add_idx_report_work_item_stmt FROM @add_idx_report_work_item_sql;
EXECUTE add_idx_report_work_item_stmt;
DEALLOCATE PREPARE add_idx_report_work_item_stmt;
