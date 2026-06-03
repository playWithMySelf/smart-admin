# smart-app 保持登录态

## Goal

优化 `smart-app` 在 App 和小程序运行端的登录体验：用户首次登录成功后，只要本地 token 仍有效，下次打开应用或再次进入登录页时应自动恢复用户信息并直接进入首页，不需要重复输入账号密码。

## Requirements

* 登录成功后继续将 token 持久化到 `uni` 本地存储。
* 应用启动时，如果本地存在 token，调用 `/login/getLoginInfo` 恢复用户信息。
* 登录页展示前，如果本地 token 有效，自动恢复用户信息并 `switchTab` 到首页。
* token 到达后台总有效期、超过后台活跃超时时间、账号在别处登录、接口返回认证错误时，沿用现有逻辑清理本地登录信息并留在或跳转到登录页。
* 当前项目后台已将“登录后无操作自动退出”调整为 `21600` 分钟（15 天）；前端不再按旧的 30 分钟做额外限制。
* 登录请求的 `loginDevice` 需要按实际运行端区分，避免 App 或小程序固定按 H5 登录。
* 保持现有退出登录行为：用户主动退出后清除 token，下次打开仍需要登录。

## Acceptance Criteria

* [ ] App 登录成功后关闭再打开，后台 token 仍有效时直接进入首页。
* [ ] 微信小程序登录成功后重新打开，后台 token 仍有效时直接进入首页。
* [ ] 访问登录页时，如果 token 有效，应自动跳首页，不显示重复登录流程。
* [ ] token 失效或超过后台活跃超时时间时不会卡在自动跳转循环，用户可正常重新登录。
* [ ] 主动退出登录后不会自动进入首页。
* [ ] H5、App、iOS App、小程序登录时传递的 `loginDevice` 与运行端匹配。

## Definition of Done

* 代码改动遵循现有 `smart-app` Vue 3 + UniApp 风格。
* 运行 lint 或可用的构建/静态检查，记录结果。
* 不引入新的依赖。
* 不改动无关业务页面。

## Technical Approach

推荐采用轻量登录态守卫：

* 在 `store/modules/system/user.js` 中增强 `getLoginInfo`，让调用方能拿到是否恢复成功的布尔结果，并在失败时清理过期 token。
* 在 `pages/login/login.vue` 的 `onShow` 中先检查本地 token；有 token 时尝试恢复登录信息，成功则直接 `switchTab` 到首页，失败再加载验证码并保留登录页。
* 在登录页新增运行端设备类型计算，替换当前固定 `LOGIN_DEVICE_ENUM.H5.value` 的写法。
* 保留 `App.vue` 现有启动恢复逻辑，用于首页首屏和消息流初始化。

## Decision (ADR-lite)

**Context**: 当前 store 已持久化 token，请求层也会从本地存储读取 token，但登录页没有“已登录跳首页”的入口逻辑；同时登录设备固定为 H5，不适合 App 和小程序。

**Decision**: 不引入全局路由守卫，先在登录页和用户 store 做局部增强，保持改动范围小且适配 App/小程序。

**Consequences**: 方案简单、风险低；后续如果需要保护所有业务页，可再新增统一页面级鉴权守卫。

## Out of Scope

* 不新增“记住我”开关。
* 不延长后端 token 总有效期，也不绕过后台活跃超时限制。
* 不改造所有页面的权限路由体系。
* 不调整后端登录接口。

## Technical Notes

* 已检查 `smart-app/src/store/modules/system/user.js`：`setUserLoginInfo` 已写入 `USER_TOKEN`，`getLoginInfo` 已调用 `/login/getLoginInfo` 但无成功/失败返回值。
* 已检查 `smart-app/src/lib/smart-request.js`：请求头从 `USER_TOKEN` 读取 token，认证错误码 `30007`、`30008`、`30012` 会清理登录态并跳登录页。
* 已检查 `smart-app/src/pages/login/login.vue`：登录成功后会写 store 并 `switchTab` 首页；`loginDevice` 当前固定为 H5。
* 已检查 `smart-app/src/pages.json`：首页是第一个页面且属于 tabBar，登录页不是 tabBar。
* 已检查后台 Sa-Token 配置：`sa-base.yaml` 的 `timeout` 为 `2592000` 秒（30 天），`active-timeout` 会被 `TokenConfig` 和 `Level3ProtectConfigService` 动态覆盖。
* 已检查初始化 SQL：`t_config.level3_protect_config` 默认 `loginActiveTimeoutMinutes` 为 `30`，即默认 30 分钟无请求后会触发 `LOGIN_ACTIVE_TIMEOUT(30012)`。
* 用户已确认当前后台“登录后无操作自动退出”已改为 `21600` 分钟（15 天）；若 `sa-token.timeout` 仍为 `2592000` 秒，则无操作 15 天会早于 token 总有效期 30 天触发。
