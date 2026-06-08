# Journal - jinwei (Part 1)

> AI development session journal
> Started: 2026-05-27

---



## Session 1: 沉淀 SmartAdmin 开发规范

**Date**: 2026-05-28
**Task**: 沉淀 SmartAdmin 开发规范
**Branch**: `dev`

### Summary

根据官方 SmartAdmin 开发文档和现有代码，将后端、前端开发规范沉淀到 .trellis/spec，并归档 bootstrap guidelines 任务。

### Main Changes

- 为 `smart-app` 新增消息 SSE 客户端，复用后端 `/support/message/stream` 刷新未读数。
- 将消息流生命周期接入 `userStore` 和 `App.vue`，登录后启动、退出登录和鉴权失败时关闭。
- 消息 tab 页面订阅 `message-refresh`，页面可见时刷新列表并同步 tabBar 角标。
- 增加 App-Plus 隐藏态 best-effort 本地通知，启用 manifest Push 模块。
- 将 app 消息流、本地通知和离线推送边界写入前端状态管理 spec。

### Git Commits

| Hash | Message |
|------|---------|
| `e8be364` | (see git log) |

### Testing

- [OK] `npm run build:h5`
- [OK] `npm run build:app`

### Status

[OK] **Completed**

### Next Steps

- None - task complete


## Session 2: 工作项积分审核模块

**Date**: 2026-05-28
**Task**: 工作项积分审核模块
**Branch**: `dev`

### Summary

实现工作项维护、员工日报填报、审核打分与积分报表；接入图片佐证、补填天数参数配置，并完成后端构建、前端构建、真实接口链路与页面冒烟验证。

### Main Changes

- 修正 `smart-app` 消息 tabBar badge 索引，匹配当前 `pages.json` 中“消息”tab 的位置。
- 在同步 tabBar badge 前校验当前页面是否为 tabBar 页面，并给 uni badge API 增加静默 `fail` 兜底。
- 将首页“列表样式1/2”菜单从 `switchTab` 改为 `navigateTo`，避免跳转非 tabBar 页面时报错。
- 补充前端状态规范中关于 `uni.setTabBarBadge` / `uni.removeTabBarBadge` 的调用约束。

### Git Commits

| Hash | Message |
|------|---------|
| `572eb39` | (see git log) |

### Testing

- [OK] `npm run build:mp-weixin`
- [WARN] 定向 ESLint 暴露项目现有 uni-app 全局变量和历史组件规则问题，本次以微信小程序构建通过作为有效校验。

### Status

[OK] **Completed**

### Next Steps

- None - task complete


## Session 3: 工作项日报积分体验优化

**Date**: 2026-05-28
**Task**: 工作项日报积分体验优化
**Branch**: `dev`

### Summary

完成工作项批量删除、我的日报填报与审批历史体验、日报和积分报表数据权限、积分报表日期明细与权限机构筛选，并通过前后端构建验证。

### Main Changes

- 管理端日报佐证上传接入 `browser-image-compression`，支持上传前压缩并保持原有文件上传接口。
- `smart-app` 日报上传接入 `uni.compressImage` 路径压缩，失败时回退原路径上传。
- 通用文件上传组件的图片预览判断兼容 `fileType`、文件名和 URL 后缀，避免日报已填明细图片误走下载。
- 管理端开发环境改为 `/api` 同源请求，经 Vite 代理转发到 `VITE_APP_API_URL`，解决验证码跨域。
- 前端 spec 增加图片上传前压缩约定。

### Git Commits

| Hash | Message |
|------|---------|
| `6193fa9` | (see git log) |

### Testing

- [OK] `npm run build:prod` in `smart-admin-web-typescript`
- [OK] `npm run build:h5` in `smart-app`
- [OK] `git diff --check`
- [OK] local dev proxy request `http://localhost:8082/api/login/getCaptcha`

### Status

[OK] **Completed**

### Next Steps

- None - task complete


## Session 4: 日报消息通知与SSE收尾

**Date**: 2026-05-30
**Task**: 日报消息通知与SSE收尾
**Branch**: `dev`

### Summary

新增 Web 端 SSE 消息实时刷新，并扩展日报通知：审核结果通知提交人，日报提交后通知直属部门内拥有审核权限的员工。已补充后端消息通知规范并完成归档。

### Main Changes

- `smart-app` 日报填写页只在首次进入时自动加载日报详情，避免选择佐证图返回页面时 `onShow` 覆盖未保存工作项。
- 新增 `file-display.js`，统一 `fileUrl/url/tempFilePath` 读取和上传返回对象标准化。
- 审核详情页复用统一图片 URL 读取逻辑，真机缩略图域名限制不做代码绕过，记录到前端规范。

### Git Commits

| Hash | Message |
|------|---------|
| `2cbd022` | (see git log) |
| `16f6e31` | (see git log) |

### Testing

- [OK] `npm run build:mp-weixin`

### Status

[OK] **Completed**

### Next Steps

- None - task complete


## Session 5: app 对接消息通知

**Date**: 2026-05-30
**Task**: app 对接消息通知
**Branch**: `dev`

### Summary

为 smart-app 接入消息 SSE 实时刷新、隐藏态本地通知、消息页刷新，并记录 app 消息通知实现契约。

### Main Changes

(Add details)

### Git Commits

| Hash | Message |
|------|---------|
| `0ce8d6e` | (see git log) |
| `941f746` | (see git log) |

### Testing

- [OK] (Add test results)

### Status

[OK] **Completed**

### Next Steps

- None - task complete


## Session 6: 移动端我的账号管理

**Date**: 2026-06-01
**Task**: 移动端我的账号管理
**Branch**: `dev`

### Summary

移动端我的菜单新增个人信息和修改密码入口，对接个人信息更新、加密修改密码和关于我们简介，并完成 H5 构建验证。

### Main Changes

(Add details)

### Git Commits

| Hash | Message |
|------|---------|
| `b5c2004` | (see git log) |

### Testing

- [OK] (Add test results)

### Status

[OK] **Completed**

### Next Steps

- None - task complete


## Session 7: 修复小程序 tabBar 角标报错

**Date**: 2026-06-03
**Task**: 修复小程序 tabBar 角标报错
**Branch**: `dev`

### Summary

修复 smart-app 微信小程序 tabBar badge 索引和非 tabBar 页面调用报错；将首页非 tabBar 菜单改为 navigateTo；补充 badge 调用规范并完成微信小程序构建验证。

### Main Changes

(Add details)

### Git Commits

| Hash | Message |
|------|---------|
| `de3bfcc` | (see git log) |

### Testing

- [OK] (Add test results)

### Status

[OK] **Completed**

### Next Steps

- None - task complete


## Session 8: smart-app 保持登录态

**Date**: 2026-06-03
**Task**: smart-app 保持登录态
**Branch**: `dev`

### Summary

实现 smart-app App/小程序/H5 登录态自动恢复：登录页优先校验本地 token 并通过 getLoginInfo 恢复用户信息，修正登录设备枚举与退出清 token 行为，并补充前端状态管理规范。验证通过 build:mp-weixin、build:h5、build:app。

### Main Changes

(Add details)

### Git Commits

| Hash | Message |
|------|---------|
| `0052505` | (see git log) |

### Testing

- [OK] (Add test results)

### Status

[OK] **Completed**

### Next Steps

- None - task complete


## Session 9: 前端日报图片压缩与开发代理修复

**Date**: 2026-06-03
**Task**: 前端日报图片压缩与开发代理修复
**Branch**: `dev`

### Summary

为日报佐证图片增加 Web 与 uni-app 上传前压缩；修复管理端开发环境远端 API 验证码跨域；增强上传组件图片预览识别并记录前端图片压缩规范。

### Main Changes

(Add details)

### Git Commits

| Hash | Message |
|------|---------|
| `665c70e` | (see git log) |
| `e2826d3` | (see git log) |
| `4dff677` | (see git log) |

### Testing

- [OK] (Add test results)

### Status

[OK] **Completed**

### Next Steps

- None - task complete


## Session 10: 修复小程序日报佐证图上传状态

**Date**: 2026-06-08
**Task**: 修复小程序日报佐证图上传状态
**Branch**: `dev`

### Summary

修复 smart-app 日报上传佐证图后 onShow 覆盖未保存工作项的问题，统一日报填写与审核详情的图片展示 URL 兜底，记录真机缩略图域名限制处理边界。

### Main Changes

(Add details)

### Git Commits

| Hash | Message |
|------|---------|
| `e0b170b` | (see git log) |
| `e0e83d6` | (see git log) |

### Testing

- [OK] (Add test results)

### Status

[OK] **Completed**

### Next Steps

- None - task complete
