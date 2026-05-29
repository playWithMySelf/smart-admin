/*
 * 工作项积分
 *
 * @Author:    jinwei
 * @Date:      2026-05-28
 */

export const WORK_DAILY_REPORT_STATUS_ENUM = {
  DRAFT: {
    value: 1,
    desc: '草稿',
  },
  WAIT_AUDIT: {
    value: 2,
    desc: '待审核',
  },
  AUDIT_PASS: {
    value: 3,
    desc: '审核通过',
  },
  AUDIT_FAIL: {
    value: 4,
    desc: '审核失败',
  },
};

export const WORK_ITEM_CONFIG_KEY = {
  ALLOW_REPLENISH_DAYS: 'work_item_allow_replenish_days',
};

export function getWorkDailyReportStatusDesc(status) {
  const statusItem = Object.values(WORK_DAILY_REPORT_STATUS_ENUM).find((item) => item.value === status);
  return statusItem ? statusItem.desc : '未创建';
}

export default {
  WORK_DAILY_REPORT_STATUS_ENUM,
  WORK_ITEM_CONFIG_KEY,
};
