/*
 * 系统配置
 *
 * @Author: 
 * @Date:      2026-05-29
 */
import { getRequest } from '@/lib/smart-request';

export const configApi = {
  // 根据key查询配置 @author 1024创新实验室
  queryByKey(configKey) {
    return getRequest(`/support/config/queryByKey?configKey=${encodeURIComponent(configKey)}`);
  },
};
