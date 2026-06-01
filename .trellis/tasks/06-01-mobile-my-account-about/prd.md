# 移动端我的账号与关于我们

## Goal

完善 `smart-app` 移动端“我的”菜单：直接增加“个人信息”和“修改密码”入口，分别对接个人信息修改和密码修改功能；“关于我们”点击后展示简短的 App 功能介绍。

## What I already know

* 用户要求移动端“我的”菜单中接入个人信息修改、密码修改和关于我们介绍。
* 移动端入口位于 `smart-app/src/pages/mine/mine.vue`，菜单组件位于 `smart-app/src/pages/mine/components/mine-menu.vue`。
* 移动端页面路由集中配置在 `smart-app/src/pages.json`。
* 移动端已有请求封装 `smart-app/src/lib/smart-request.js`，支持普通 `postRequest` 和加密 `postEncryptRequest`。
* 后端已有个人中心更新接口：`POST /employee/update/center`。
* 后端已有密码修改接口：`POST /employee/update/password`，并标记 `@ApiDecrypt`，前端需要使用加密请求。
* 后端已有密码复杂度接口：`GET /employee/getPasswordComplexityEnabled`。
* Web 端已有可参考实现：`smart-admin-web-typescript/src/views/system/account/components/center/index.vue` 和 `password/index.vue`。

## Assumptions

* 本次仅改 `smart-app` 移动端，不新增后端接口。
* 个人信息修改沿用登录信息中的字段，优先支持姓名、性别、手机号、邮箱、备注等后端 `EmployeeUpdateCenterForm` 支持的基础字段；部门、登录账号等只展示不允许修改。
* 密码修改提交成功后提示成功并清空表单。
* “关于我们”使用弹窗或轻量页面展示简短介绍，不做富文本、版本更新详情或隐私协议扩展。

## Open Questions

* 已确认取消“账号管理”中间入口，直接在“我的”菜单展示“个人信息”和“修改密码”。

## Requirements

* “我的”菜单中直接展示“个人信息”入口，点击后进入个人信息修改。
* “我的”菜单中直接展示“修改密码”入口，并对接已有密码修改接口。
* 个人信息修改提交后刷新本地登录用户信息，保证“我的”页展示同步更新。
* 密码修改使用 `postEncryptRequest` 调用 `/employee/update/password`。
* 密码修改页面读取密码复杂度开关，并按现有 Web 端规则给出校验提示。
* “关于我们”点击后展示一段简短 App 功能介绍。

## Acceptance Criteria

* [ ] 点击“我的 > 个人信息”进入个人信息编辑界面。
* [ ] 个人信息表单可保存，并调用 `/employee/update/center`。
* [ ] 保存个人信息成功后重新拉取 `/login/getLoginInfo` 并更新 `userStore`。
* [ ] 点击“我的 > 修改密码”进入密码修改界面。
* [ ] 修改密码校验原密码、新密码、确认密码，并调用 `/employee/update/password` 的加密请求。
* [ ] 新密码和确认密码不一致时阻止提交并提示用户。
* [ ] 点击“关于我们”展示 SmartAdmin App 简短功能介绍。
* [ ] `smart-app` 构建或可用质量检查通过。

## Definition of Done

* 代码符合当前 `smart-app` 的 Vue/Uni-App 写法和请求封装风格。
* 页面路由、菜单入口、API 封装一致。
* 表单校验和错误处理走现有 `SmartToast`、`SmartLoading`、`smartSentry` 模式。
* 至少执行一次可行的移动端前端质量检查或构建命令。

## Out of Scope

* 不修改后端接口。
* 不增加头像上传能力，除非实现中发现移动端已有成熟上传模式可低风险复用。
* 不新增职务、部门选择器等复杂组织架构编辑能力。
* 不改 Web 管理端账号中心。

## Technical Notes

* 相关移动端文件：
  * `smart-app/src/pages/mine/components/mine-menu.vue`
  * `smart-app/src/pages.json`
  * `smart-app/src/api/system/login-api.js`
  * `smart-app/src/lib/smart-request.js`
  * `smart-app/src/store/modules/system/user.js`
* 相关后端契约：
  * `EmployeeController#updateCenter`
  * `EmployeeController#updatePassword`
  * `EmployeeUpdateCenterForm`
  * `EmployeeUpdatePasswordForm`
* 相关 Web 端参考：
  * `smart-admin-web-typescript/src/api/system/employee-api.ts`
  * `smart-admin-web-typescript/src/views/system/account/components/center/index.vue`
  * `smart-admin-web-typescript/src/views/system/account/components/password/index.vue`

## Proposed Approach

**最终方案：我的菜单直接展示两个入口**

* 删除“账号管理”中间入口。
* “我的”菜单直接新增“个人信息”，跳转 `pages/mine/account/account-profile.vue`。
* “我的”菜单直接新增“修改密码”，跳转 `pages/mine/account/account-password.vue`。
* “关于我们”在菜单点击后使用 `uni.showModal` 展示简短介绍。
* 优点：入口更短，符合用户最终要求；个人信息和密码修改仍由独立页面承载，表单职责清晰。

## Recommendation

采用最终方案。它减少一层跳转，菜单更直接，同时保留独立页面承载表单，后续维护成本低。

## Confirmed Decision

用户先选择方案 1，后续调整为去掉“账号管理”中间入口：

* “我的”菜单直接展示“个人信息”和“修改密码”。
* 个人信息、修改密码分别使用独立页面承载表单。
* “关于我们”仍在“我的”菜单中点击后直接展示简短 App 功能介绍。
