/*
 * 工作项积分
 *
 * @Author:    jinwei
 * @Date:      2026-05-28
 */
import { getRequest, postRequest } from '@/lib/smart-request';

function buildQuery(params) {
  const queryList = Object.keys(params || {})
    .filter((key) => params[key] !== undefined && params[key] !== null && params[key] !== '')
    .map((key) => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`);
  return queryList.length ? `?${queryList.join('&')}` : '';
}

export const workitemApi = {
  // 查询工作项类型 @author jinwei
  queryTypeList(disabledFlag) {
    return getRequest(`/workitem/type/query${buildQuery({ disabledFlag })}`);
  },

  // 查询工作项详情 @author jinwei
  getItemDetail(workItemId) {
    return getRequest(`/workitem/item/detail/${workItemId}`);
  },

  // 查询可选工作项 @author jinwei
  queryItemList(workItemTypeId, keywords) {
    return getRequest(`/workitem/item/list${buildQuery({ workItemTypeId, keywords })}`);
  },

  // 分页查询工作项 @author jinwei
  queryItemPage(param) {
    return postRequest('/workitem/item/page/query', param);
  },

  // 分页查询我的日报 @author jinwei
  queryMyDailyPage(param) {
    return postRequest('/workitem/daily/my/page/query', param);
  },

  // 查询我的日报详情 @author jinwei
  getMyDailyDetail(workDailyReportId) {
    return getRequest(`/workitem/daily/my/detail/${workDailyReportId}`);
  },

  // 保存我的日报草稿 @author jinwei
  saveDailyDraft(param) {
    return postRequest('/workitem/daily/my/save', param);
  },

  // 提交我的日报 @author jinwei
  submitDaily(workDailyReportId) {
    return getRequest(`/workitem/daily/my/submit/${workDailyReportId}`);
  },

  // 分页查询待审核日报 @author jinwei
  queryReviewPage(param) {
    return postRequest('/workitem/daily/review/page/query', param);
  },

  // 查询审核日报详情 @author jinwei
  getReviewDetail(workDailyReportId) {
    return getRequest(`/workitem/daily/review/detail/${workDailyReportId}`);
  },

  // 审核日报 @author jinwei
  auditDaily(param) {
    return postRequest('/workitem/daily/review/audit', param);
  },

  // 查询员工积分总分 @author jinwei
  queryEmployeeScore(param) {
    return postRequest('/workitem/score/report/employee', param);
  },

  // 查询日期积分明细 @author jinwei
  queryDateScore(param) {
    return postRequest('/workitem/score/report/date', param);
  },

  // 查询工作项积分明细 @author jinwei
  queryItemScore(param) {
    return postRequest('/workitem/score/report/item', param);
  },
};
