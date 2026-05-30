/*
 * 工作项积分
 *
 * @Author:    jinwei
 * @Date:      2026-05-28
 */
import { getRequest, postRequest } from '/@/lib/axios';

type WorkitemParam = Record<string, any>;
type WorkitemId = number | string;

export const workitemApi = {
  // 查询工作项类型 @author jinwei
  queryTypeList: (disabledFlag?: boolean) => getRequest('/workitem/type/query', { disabledFlag }),
  // 添加工作项类型 @author jinwei
  addType: (param: WorkitemParam) => postRequest('/workitem/type/add', param),
  // 更新工作项类型 @author jinwei
  updateType: (param: WorkitemParam) => postRequest('/workitem/type/update', param),
  // 删除工作项类型 @author jinwei
  deleteType: (workItemTypeId: WorkitemId) => getRequest(`/workitem/type/delete/${workItemTypeId}`, {}),

  // 分页查询工作项 @author jinwei
  queryItemPage: (param: WorkitemParam) => postRequest('/workitem/item/page/query', param),
  // 查询可选工作项 @author jinwei
  queryItemList: (workItemTypeId?: WorkitemId, keywords?: string) => getRequest('/workitem/item/list', { workItemTypeId, keywords }),
  // 查询工作项详情 @author jinwei
  getItemDetail: (workItemId: WorkitemId) => getRequest(`/workitem/item/detail/${workItemId}`, {}),
  // 添加工作项 @author jinwei
  addItem: (param: WorkitemParam) => postRequest('/workitem/item/add', param),
  // 更新工作项 @author jinwei
  updateItem: (param: WorkitemParam) => postRequest('/workitem/item/update', param),
  // 删除工作项 @author jinwei
  deleteItem: (workItemId: WorkitemId) => getRequest(`/workitem/item/delete/${workItemId}`, {}),
  // 批量删除工作项 @author jinwei
  batchDeleteItem: (workItemIdList: WorkitemId[]) => postRequest('/workitem/item/batch/delete', workItemIdList),

  // 分页查询我的日报 @author jinwei
  queryMyDailyPage: (param: WorkitemParam) => postRequest('/workitem/daily/my/page/query', param),
  // 查询我的日报详情 @author jinwei
  getMyDailyDetail: (workDailyReportId: WorkitemId) => getRequest(`/workitem/daily/my/detail/${workDailyReportId}`, {}),
  // 保存我的日报草稿 @author jinwei
  saveDailyDraft: (param: WorkitemParam) => postRequest('/workitem/daily/my/save', param),
  // 提交我的日报 @author jinwei
  submitDaily: (workDailyReportId: WorkitemId) => getRequest(`/workitem/daily/my/submit/${workDailyReportId}`, {}),

  // 分页查询待审核日报 @author jinwei
  queryReviewPage: (param: WorkitemParam) => postRequest('/workitem/daily/review/page/query', param),
  // 查询审核日报详情 @author jinwei
  getReviewDetail: (workDailyReportId: WorkitemId) => getRequest(`/workitem/daily/review/detail/${workDailyReportId}`, {}),
  // 审核日报 @author jinwei
  auditDaily: (param: WorkitemParam) => postRequest('/workitem/daily/review/audit', param),

  // 查询员工积分总分 @author jinwei
  queryEmployeeScore: (param: WorkitemParam) => postRequest('/workitem/score/report/employee', param),
  // 查询积分报表可见机构树 @author jinwei
  queryScoreReportDepartmentTree: () => getRequest('/workitem/score/report/department/tree', {}),
  // 查询日期积分明细 @author jinwei
  queryDateScore: (param: WorkitemParam) => postRequest('/workitem/score/report/date', param),
  // 查询员工日期积分汇总 @author jinwei
  queryEmployeeDateScore: (param: WorkitemParam) => postRequest('/workitem/score/report/employee-date', param),
  // 查询类型积分明细 @author jinwei
  queryTypeScore: (param: WorkitemParam) => postRequest('/workitem/score/report/type', param),
  // 查询工作项积分明细 @author jinwei
  queryItemScore: (param: WorkitemParam) => postRequest('/workitem/score/report/item', param),
};
