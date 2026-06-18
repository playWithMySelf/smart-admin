/*
 *  员工
 *
 * @Author: jw
 * @Date:      2022-09-03 21:59:15
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
 */
import { getRequest, postEncryptRequest, postRequest } from '@/lib/smart-request';

export const employeeApi = {
  /**
   * 更新员工个人中心信息 @author 善逸
   */
  updateCenter: (params) => {
    return postRequest('/employee/update/center', params);
  },

  /**
   * 修改密码 @author 卓大
   */
  updateEmployeePassword: (param) => {
    return postEncryptRequest('/employee/update/password', param);
  },

  /**
   * 获取密码复杂度 @author 卓大
   */
  getPasswordComplexityEnabled: () => {
    return getRequest('/employee/getPasswordComplexityEnabled');
  },
};
