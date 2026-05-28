/*
 * 工作项积分
 *
 * @Author:    jinwei
 * @Date:      2026-05-28
 */
import { SmartEnum } from '/@/types/smart-enum';

export const WORK_DAILY_REPORT_STATUS_ENUM: SmartEnum<number> = {
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

export default {
  WORK_DAILY_REPORT_STATUS_ENUM,
};
