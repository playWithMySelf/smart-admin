/*
 * 待办工作
 *
 * @Author:    jinwei
 * @Date:      2026-06-19
 */
import { getRequest, postRequest } from '@/lib/smart-request';

export const toBeDoneApi = {
  // 查询我的待办工作 @author jinwei
  queryList() {
    return getRequest('/to-be-done/list');
  },

  // 查询我的未完成待办数量 @author jinwei
  queryCount() {
    return getRequest('/to-be-done/count');
  },

  // 新增待办工作 @author jinwei
  add(param) {
    return postRequest('/to-be-done/add', param);
  },

  // 更新待办工作 @author jinwei
  update(param) {
    return postRequest('/to-be-done/update', param);
  },

  // 删除待办工作 @author jinwei
  deleteToBeDone(toBeDoneId) {
    return getRequest(`/to-be-done/delete/${toBeDoneId}`);
  },
};
