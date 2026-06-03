# 修复小程序 tabBar badge 报错

## Goal

修复 `smart-app` 在微信开发者工具中点击首页菜单、进入个人信息等场景出现的 tabBar badge API 报错，同时保持未读消息角标在 tabBar 页面上的正常展示。

## What I already know

* 当前报错包括 `setTabBarBadge:fail tabbar item not found` 和 `removeTabBarBadge:fail not TabBar page`。
* `smart-app/src/pages.json` 当前 tabBar 顺序为：首页 `0`、消息 `1`、我的 `2`。
* `smart-app/src/store/modules/system/user.js` 中 `MESSAGE_TAB_BAR_INDEX` 仍为 `3`，与当前 tabBar 不一致。
* `removeTabBarBadge` 在普通页面执行时会报 `not TabBar page`，例如进入个人信息页。
* 首页菜单中 `列表样式1`、`列表样式2` 使用 `switchTab` 跳转到非 tabBar 页面，后续需要一并校正为普通页面跳转，避免菜单点击路径继续触发 tabBar 相关异常。
* 用户要求提交时把已有改动一并带上。

## Requirements

* 将未读消息角标索引与当前 `pages.json` 的消息 tab 保持一致。
* 同步角标前判断当前页面是否为 tabBar 页面，非 tabBar 页面静默跳过。
* 首页菜单中非 tabBar 页面使用 `navigateTo`，不再使用 `switchTab`。
* 不回滚用户已有改动；最终提交计划包含用户当前已有改动和本次修复改动。

## Acceptance Criteria

* [x] 点击首页菜单不再出现 `setTabBarBadge:fail tabbar item not found`。
* [x] 点击“个人信息”后页面可正常展示，控制台不再出现 `removeTabBarBadge:fail not TabBar page`。
* [x] 未读消息数量大于 0 时，在“消息”tab 上仍能展示角标。
* [x] 未读消息数量为 0 时，在 tabBar 页面可正常移除角标。
* [x] 用户已有改动不会被覆盖，并纳入最终提交计划。

## Definition of Done

* 完成代码修复。
* 运行适合 `smart-app` 的 lint/build/typecheck（如项目脚本支持）。
* 检查 git diff，确认只包含用户已有改动、Trellis 任务文件和本次修复。
* 按用户要求提交改动。

## Technical Approach

优先做最小风险修复：沿用现有 `syncUnreadMessageBadge` 入口，不引入新依赖；把消息 tab 索引修正为 `1`，新增当前路由是否属于 tabBar 的轻量判断，避免在普通页面调用微信 tabBar badge API；把首页菜单里对非 tabBar 页面的跳转从 `switchTab` 改为 `navigateTo`。

## Out of Scope

* 不重构消息流、通知逻辑或登录流程。
* 不调整 tabBar 配置和页面结构。
* 不修改用户已有环境地址、manifest、首页 nav 区域改动的内容。

## Technical Notes

* 相关文件：
  * `smart-app/src/store/modules/system/user.js`
  * `smart-app/src/pages.json`
  * `smart-app/src/pages/home/components/menu.vue`
* 当前已有未提交用户改动：
  * `smart-app/.env.development`
  * `smart-app/.env.production`
  * `smart-app/src/manifest.json`
  * `smart-app/src/pages/home/index.vue`
* 验证：
  * `npm run build:mp-weixin` 通过。
  * `npx eslint src/store/modules/system/user.js src/pages/home/components/menu.vue` 未通过，原因是项目当前 ESLint 对 uni-app 全局变量和历史组件写法未适配，包含 `uni` / `getCurrentPages` 未声明、`menu.vue` 单词组件名、历史 `<image>` 非自闭合等问题。
