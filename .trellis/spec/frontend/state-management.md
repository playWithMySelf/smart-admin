# State Management

> Pinia、局部状态、全局配置和服务器数据状态规范。

---

## Store Library

当前项目使用 Pinia。

参考：

- `src/store/index.ts`
- `src/store/modules/system/user.ts`
- `src/store/modules/system/app-config.ts`
- `src/store/modules/system/dict.ts`
- `src/store/modules/system/role.ts`

Store 文件按业务拆分，不能把所有状态堆到一个文件。

---

## State Categories

| 状态类型 | 放置位置 | 示例 |
|----------|----------|------|
| 组件局部状态 | 当前 `.vue` 的 `ref` / `reactive` | 查询表单、表格 loading、弹窗 visible |
| 页面 URL 状态 | Vue Router `query` | 详情页 `enterpriseId` |
| 全局用户/权限状态 | Pinia system store | `useUserStore`、`useRoleStore` |
| 全局应用配置 | `app-config` store | 布局、主题、页签、帮助文档、水印 |
| 字典/枚举缓存 | Pinia 或插件 | `useDictStore`、`smart-enums-plugin` |
| 服务器列表数据 | 组件内请求后保存 | 表格 `tableData`、`total` |

---

## App Default Config

前端默认配置在 `src/config/app-config.ts`，由 Pinia `app-config` 模块读取作为初始值。

常见配置：

- `language`
- `layout`
- `sideMenuWidth`
- `sideMenuTheme`
- `pageWidth`
- `pageTagFlag`
- `breadCrumbFlag`
- `footerFlag`
- `helpDocFlag`
- `watermarkFlag`
- `websiteName`

修改默认行为时，优先改默认配置和对应 store，不要在布局组件中写死。

---

## When To Use Global State

使用 Pinia 的条件：

- 多个布局或页面需要共享。
- 状态需要跨路由保留。
- 状态来自登录用户、权限、菜单、字典、系统配置。
- 状态需要被插件或指令读取。

保持局部状态的条件：

- 只服务于一个页面或组件。
- 关闭页面后不需要保留。
- 如查询表单、分页、表格数据、弹窗显示状态。

---

## App Badges And Session Sync

### Convention: Unread Badge Follows User Store

**What**: 未读消息数量由 `useUserStore` 统一维护，并在数量变化时同步到 `uni.setTabBarBadge` / `uni.removeTabBarBadge`。

**Why**: 避免页面只更新本地数字、tabBar 角标却不变的分裂状态。

**Example**:
```js
// store action
this.unreadMessageCount = Number(result.data) || 0;
this.syncUnreadMessageBadge();
```

> **Warning**: `uni.setTabBarBadge` / `uni.removeTabBarBadge` 只能作用在真实 tabBar 项上。
>
> 修改 `pages.json` 的 tabBar 顺序或删减 tabBar 页面后，必须同步检查 `smart-app` 里的 badge index；在非 tabBar 页面调用 `removeTabBarBadge` 会触发 `not TabBar page`，在不存在的 index 上调用 `setTabBarBadge` 会触发 `tabbar item not found`。同步角标前应确认当前页面属于 tabBar，并给 uni badge API 提供静默 `fail` 兜底。

**Related**: 登录后、App 显示时、消息页刷新后都应重新拉取未读数。移动端消息列表加载不能直接把当前页全部设为已读；用户点击具体消息后，才标记该条已读，并刷新未读数和 tabBar 角标。

### Scenario: App 登录态持久化与自动恢复

### 1. Scope / Trigger
- Trigger: `smart-app` 需要在 App、小程序、H5 运行端复用本地 token，让用户在后台 token 仍有效时再次打开应用可直接进入首页。
- Boundary: 前端只负责保存 token 和恢复用户信息，不延长后台 token 总有效期，也不绕过后台活跃超时。

### 2. Signatures
- `useUserStore#getLoginInfo(): Promise<boolean>`：本地无 token 或恢复失败返回 `false`，恢复成功返回 `true`。
- `useUserStore#setUserLoginInfo(data)`：写入 Pinia 用户信息，并将 `data.token` 写入 `USER_TOKEN`。
- `useUserStore#logout()` / `clearUserLoginInfo()`：关闭消息流、重置用户信息、清理 `USER_TOKEN`。
- `loginApi.getLoginInfo()`：调用 `/login/getLoginInfo`，依赖请求头 `Authorization: Bearer <token>`。

### 3. Contracts
- 本地存储 key 统一使用 `src/constants/local-storage-key-const.js#USER_TOKEN`。
- 登录页进入时先检查 `useUserStore().getToken`；有 token 时先调用 `getLoginInfo()`，成功后 `uni.switchTab({ url: '/pages/home/index' })`。
- token 失效错误码沿用请求层约定：`30007`、`30008`、`30012`，这些错误是预期登录失效分支，不应作为异常噪声重复上报。
- `loginDevice` 必须与后端 `LoginDeviceEnum` 对齐：`PC=1`、`ANDROID=2`、`APPLE=3`、`H5=4`、`WEIXIN_MP=5`。App 端按 `uni.getSystemInfoSync().platform` 区分 iOS/Android，小程序端使用小程序枚举。

### 4. Validation & Error Matrix
| 条件 | 正确处理 |
|------|----------|
| 本地无 token | 初始化登录页验证码和双因子配置 |
| 本地 token 有效 | 拉取用户信息，恢复 Pinia 状态，跳转首页 |
| token 失效 / 活跃超时 / 异地登录 | 请求层清理登录态，登录页停留并允许重新登录 |
| 网络异常 | 不清理本地 token，记录异常，留在登录页 |
| 用户主动退出 | 清理本地 token，下次进入不自动跳首页 |

### 5. Good/Base/Bad Cases
- Good: 登录页先恢复登录态，成功后不再拉验证码；失败才展示登录流程。
- Base: `App.onLaunch()` 也可以调用 `getLoginInfo()` 预热用户信息，但登录页仍需自检以覆盖直接进入登录页的场景。
- Bad: 只判断本地 token 字符串就直接跳首页，或者前端自行假设 token 永不过期。

### 6. Tests Required
- App/H5/小程序构建至少覆盖改动过的条件编译分支。
- 模拟本地有 token 且 `/login/getLoginInfo` 成功，断言进入首页并恢复用户信息。
- 模拟 `30007` / `30008` / `30012`，断言清理 token 且不产生自动跳转循环。
- 主动退出后断言 `USER_TOKEN` 被删除。

### 7. Wrong vs Correct
#### Wrong
```js
if (uni.getStorageSync(USER_TOKEN)) {
  uni.switchTab({ url: '/pages/home/index' });
}
```

只看本地 token 会把后台已失效的登录态当成有效。

#### Correct
```js
const loginInfoReady = await useUserStore().getLoginInfo();
if (loginInfoReady) {
  uni.switchTab({ url: '/pages/home/index' });
}
```

用后台 `/login/getLoginInfo` 作为登录态有效性的最终判断。

---

## Scenario: Web 端消息实时同步

### 1. Scope / Trigger
- Trigger: Web 端接入 SSE 消息推送后，需要同步未读数和消息列表，但不能把长连接拆成多份状态源。
- Use case: 顶部消息角标、消息气泡和账号页消息列表都要保持一致。
- Boundary: `useUserStore` 仍然是未读数的唯一来源，推送事件只负责触发刷新。

### 2. Signatures
- `useUserStore#queryUnreadMessageCount()`
- `useUserStore#setUserLoginInfo(data)`
- `useUserStore#logout()`
- `src/lib/message-stream.ts#startMessageStream()`
- `src/lib/message-stream.ts#stopMessageStream()`
- `messageStreamEmitter.on('message-refresh', handler)`

### 3. Contracts
- 登录成功后建立 SSE 连接，退出登录后关闭连接并清理监听器。
- 推送到达后先刷新 `useUserStore().unreadMessageCount`，再按需刷新当前页面消息列表。
- 页面若需要感知“推送到达”，优先用轻量事件总线，不要把长连接状态拆成多个 store。
- 连接断开、鉴权失效或后端重连时，页面逻辑必须幂等，不依赖单次推送保证最终一致性。

### 4. Validation & Error Matrix
| 条件 | 正确处理 |
|------|----------|
| 登录成功 | 建立单条 SSE 连接并注册刷新监听 |
| 退出登录 | 关闭 SSE 连接并移除监听 |
| 连接断开 | 自动重连，不阻塞页面主流程 |
| 鉴权失效 | 停止重连并交给退出登录流程处理 |

### 5. Good/Base/Bad Cases
- Good: 用户 store 持有 SSE 连接和重连逻辑，消息气泡组件只订阅刷新事件。
- Base: 单页临时状态仍按需调用 API 拉取，不额外放进推送通道。
- Bad: 每个组件各建一条实时连接，或者把未读数和消息列表分别用不同状态源维护。

### 6. Tests Required
- 登录后断言 SSE 连接被创建。
- 退出登录后断言 SSE 连接被关闭且监听器被移除。
- 收到 `message-refresh` 后断言未读数刷新。
- 消息气泡打开时收到推送，断言消息列表也刷新。

### 7. Wrong vs Correct
#### Wrong
```ts
// 每个组件自己维护一条连接
const source = new EventSource('/support/message/stream');
```

状态分散，难以统一关闭和重连。

#### Correct
```ts
// 连接和未读数放在 user store，组件只订阅刷新事件
startMessageStream();
useUserStore().queryUnreadMessageCount();
```

统一管理连接生命周期，页面只做局部刷新。

---

## Scenario: App 端消息实时同步与本地通知

### 1. Scope / Trigger
- Trigger: `smart-app` 接入消息 SSE 后，需要让 tabBar 未读角标、消息列表、本地通知保持同一个状态来源。
- Use case: 用户登录 app 后收到后端 `/support/message/stream` 的 `refresh` 事件，app 前台刷新角标和列表；app 处于 `onHide` 不可见状态但运行时仍收到事件时，创建本地通知栏消息。
- Boundary: 这不是离线推送。锁屏、进程被系统挂起或杀掉时，可靠通知必须走 Uni Push/厂商服务端推送通道。

### 2. Signatures
- `smart-app/src/lib/message-stream.js#startMessageStream()`
- `smart-app/src/lib/message-stream.js#stopMessageStream()`
- `messageStreamEmitter.on(MESSAGE_STREAM_EVENT.REFRESH, handler)`
- `useUserStore#startUserMessageStream()`
- `useUserStore#stopUserMessageStream()`
- `useUserStore#queryUnreadMessageCount()`
- `useUserStore#showUnreadMessageNotificationIfHidden()`
- `setAppVisible(visible: boolean)`
- `showMessageLocalNotification(message)`

### 3. Contracts
- SSE URL: `${VITE_APP_API_URL}/support/message/stream`
- Request headers: `Authorization: Bearer <token>` and `Accept: text/event-stream`
- Expected SSE frame: `event: refresh` with optional `data` content. The payload is only a refresh trigger; the app must query APIs for counts/details.
- Hidden notification lookup: call `messageApi.queryMessage({ pageNum: 1, pageSize: 1, readFlag: false, searchCount: false })`, then use the first unread message as local notification content.
- App-Plus manifest must enable `"Push": {}` before using `plus.push.createMessage`.

### 4. Validation & Error Matrix
| 条件 | 正确处理 |
|------|----------|
| 无 token | 不启动消息流 |
| 运行端支持 `fetch` streaming | 优先用 `fetch` + `ReadableStream`，便于 H5 携带 Authorization |
| 运行端不支持 `fetch` streaming | 降级到 `uni.request({ enableChunked: true, responseType: 'arraybuffer' })` |
| 不支持 chunk 接收 | 标记为不支持，不重复重连，不影响 onShow 主动刷新 |
| `401/403/30007/30008/30012` | 触发 auth error，清理登录态 |
| app 不可见且收到 `refresh` | 查询最新未读消息并创建本地通知 |
| app 已不可见但运行时被系统挂起 | 不保证本地通知；应使用服务端推送 |

### 5. Good/Base/Bad Cases
- Good: `userStore` 统一管理消息流生命周期、未读数和隐藏态通知；页面只订阅 `message-refresh` 做局部列表刷新。
- Base: 运行端不支持消息流时，继续依赖登录、`onShow`、消息页刷新主动拉取未读数。
- Bad: 每个页面各开一条 SSE；或者在 `onHide` 后承诺“离线也能收到”但没有服务端推送通道。

### 6. Tests Required
- 登录后断言 `startUserMessageStream()` 会绑定刷新/auth 事件并启动消息流。
- 退出登录或清空登录信息后断言 `stopUserMessageStream()` 关闭消息流并解绑事件。
- 模拟 `MESSAGE_STREAM_EVENT.REFRESH`，断言未读角标刷新；消息页可见时列表刷新。
- 设置 app 不可见后模拟刷新，断言会查询最新未读消息并调用本地通知创建函数。
- App 构建需覆盖 App-Plus 条件编译和 manifest Push 模块。

### 7. Wrong vs Correct
#### Wrong
```js
onHide() {
  stopMessageStream();
}
```

如果需求包含“不可见时收到消息给本地通知”，隐藏时直接断开消息流会让本地通知永远没有触发机会。

#### Correct
```js
onHide() {
  setAppVisible(false);
}

messageStreamEmitter.on(MESSAGE_STREAM_EVENT.REFRESH, () => {
  userStore.queryUnreadMessageCount();
  userStore.showUnreadMessageNotificationIfHidden();
});
```

保留运行时可收到消息的机会，同时明确这只是 best-effort，本地通知不能替代服务端离线推送。

---

## Layout State

Layout 有多种形态：side、side-expand、top 等。官方设计选择每种布局一个入口文件，公共组件放 `layout/components`，少量重复换可读性。

`layout/index.vue` 根据 `useAppConfigStore().$state.layout` 选择具体布局。不要把所有布局分支塞到一个巨大组件中。

参考：

- `src/layout/index.vue`
- `src/layout/side-layout.vue`
- `src/layout/top-layout.vue`
- `src/layout/components/`

---

## Server State

当前项目不使用前端服务器状态缓存库。列表页常用模式：

- `queryForm` 存查询条件。
- `tableLoading` 存 loading。
- `tableData` 和 `total` 存接口结果。
- 分页变化直接调用 `ajaxQuery`。

刷新策略由页面方法控制，例如新增/编辑弹窗成功后 emit `refresh` 并调用列表查询。

---

## Common Mistakes

- 把页面表格数据塞入全局 store。
- 在多个组件中各自维护一份用户、权限或菜单状态。
- 修改布局默认值时绕过 `app-config`。
- 新增 store 文件没有按业务拆分，或命名无法反映业务。
