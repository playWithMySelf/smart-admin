/*
 * 登录用户
 *
 * @Author: jw
 * @Date:      2022-09-06 20:55:09
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
 */
import _ from 'lodash';
import { defineStore } from 'pinia';
import { USER_TOKEN } from '@/constants/local-storage-key-const';
import { loginApi } from '@/api/system/login-api';
import { smartSentry } from '@/lib/smart-sentry';
import { messageApi } from '@/api/support/message-api';

const MESSAGE_TAB_BAR_INDEX = 3;

const defaultUserInfo = {
  token: '',
  //员工id
  employeeId: '',
  // 头像
  avatar: '',
  //登录名
  loginName: '',
  //姓名
  actualName: '',
  //手机号
  phone: '',
  //部门id
  departmentId: '',
  //部门名词
  departmentName: '',
  //是否需要修改密码
  needUpdatePwdFlag: false,
  //是否为超级管理员
  administratorFlag: false,
  // 菜单和功能点权限
  menuList: [],
  permissionList: [],
  //上次登录ip
  lastLoginIp: '',
  //上次登录ip地区
  lastLoginIpRegion: '',
  //上次登录 设备
  lastLoginUserAgent: '',
  //上次登录时间
  lastLoginTime: '',
  // 未读消息数量
  unreadMessageCount: 0,
};

export const useUserStore = defineStore({
  id: 'userStore',
  state: () => ({
    ...defaultUserInfo,
  }),
  getters: {
    getToken(state) {
      return uni.getStorageSync(USER_TOKEN);
    },
  },

  actions: {
    logout() {
      this.token = null;
      this.setUserLoginInfo(defaultUserInfo);
      this.syncUnreadMessageBadge(0);
      uni.removeStorage(USER_TOKEN);
    },
    clearUserLoginInfo() {
      this.setUserLoginInfo(defaultUserInfo);
      this.syncUnreadMessageBadge(0);
      uni.removeStorage(USER_TOKEN);
    },
    async getLoginInfo() {
      let token = uni.getStorageSync(USER_TOKEN);
      if (!token) {
        return;
      }
      let res = await loginApi.getLoginInfo();
      this.setUserLoginInfo(res.data);
    },
    syncUnreadMessageBadge(count = this.unreadMessageCount) {
      const unreadCount = Number(count) || 0;
      try {
        if (unreadCount > 0) {
          uni.setTabBarBadge({
            index: MESSAGE_TAB_BAR_INDEX,
            text: unreadCount > 99 ? '99+' : String(unreadCount),
          });
        } else {
          uni.removeTabBarBadge({
            index: MESSAGE_TAB_BAR_INDEX,
          });
        }
      } catch (e) {
        // 部分运行端不支持 tabBar 角标，静默兜底即可
      }
    },
    // 查询未读消息数量
    async queryUnreadMessageCount() {
      try {
        let result = await messageApi.queryUnreadCount();
        this.unreadMessageCount = Number(result.data) || 0;
        this.syncUnreadMessageBadge();
      } catch (e) {
        smartSentry.captureError(e);
      }
    },
    hasPermission(permission) {
      if (this.administratorFlag) {
        return true;
      }
      return this.permissionList.includes(permission);
    },
    //设置登录信息
    setUserLoginInfo(data) {
      // 用户基本信息
      this.token = data.token;
      this.employeeId = data.employeeId;
      this.loginName = data.loginName;
      this.actualName = data.actualName;
      this.phone = data.phone;
      this.departmentId = data.departmentId;
      this.departmentName = data.departmentName;
      this.administratorFlag = data.administratorFlag;
      this.lastLoginIp = data.lastLoginIp;
      this.lastLoginIpRegion = data.lastLoginIpRegion;
      this.lastLoginUserAgent = data.lastLoginUserAgent;
      this.lastLoginTime = data.lastLoginTime;
      this.menuList = data.menuList || [];
      this.permissionList = this.menuList
        .filter((menu) => menu.webPerms && menu.visibleFlag && !menu.disabledFlag)
        .map((menu) => menu.webPerms);
      this.unreadMessageCount = Number(data.unreadMessageCount) || 0;
      this.syncUnreadMessageBadge();

      uni.setStorageSync(USER_TOKEN, data.token);

      // 获取用户未读消息
      if (this.token) {
        this.queryUnreadMessageCount();
      }
    },
  },
});
