# app 对接消息通知

## Goal

让 `smart-app` 端接入现有消息通知实时刷新能力。后端和 Web 端已经具备消息 SSE 推送，app 端当前只有登录、进入前台、进入消息页时主动查询未读数；本任务补齐 app 端实时订阅，使新增消息后未读角标和消息列表能及时更新。

## What I Already Know

* 用户要求“把 app 也对接上消息通知”。
* `smart-app` 是 uni-app Vue 3 工程，已有消息 tabBar、消息列表页、`messageApi.queryUnreadCount()`、`messageApi.queryMessage()`、`messageApi.updateReadFlag()`。
* `smart-app/src/store/modules/system/user.js` 已维护 `unreadMessageCount`，并通过 `syncUnreadMessageBadge()` 同步消息 tabBar 角标。
* Web 端已通过 `smart-admin-web-typescript/src/lib/message-stream.ts` 连接 `/support/message/stream`，收到 `refresh` 事件后刷新未读数。
* 后端 `MessageStreamController` 暴露 `/support/message/stream`，`MessageStreamService` 在消息保存提交后推送 `refresh` 事件，并有心跳。
* uni-app 文档显示 `uni.request` 的 `RequestTask.onChunkReceived()` 可接收流式响应；App iOS 对 `Content-Type: text/event-stream` 这类 streaming response 有支持说明。但当前依赖是常规 uni-app 3.x，不是 uni-app x，需实现兼容兜底。

## Requirements

* app 登录成功或恢复登录信息后，启动消息实时订阅。
* app 退出登录、清空登录信息或鉴权失败时，停止消息实时订阅，避免连接泄漏。
* 收到服务端 SSE `refresh` 事件后，刷新当前用户未读消息数量并同步 tabBar 角标。
* 消息列表页打开期间收到刷新事件时，自动刷新列表，避免用户还要手动下拉。
* app 处于不可见状态但运行时仍收到消息刷新事件时，在 App-Plus 端尽力创建本地通知栏消息。
* 对不支持流式请求的运行端做降级：不报错影响业务，继续保留现有 onShow 主动刷新。
* 复用后端现有 `/support/message/stream`，不新增数据库表或后端业务接口。

## Acceptance Criteria

* [ ] 登录后 app 端会尝试建立消息通知订阅。
* [ ] 新消息产生并触发后端 `refresh` SSE 时，app 未读消息数量和消息 tabBar 角标会刷新。
* [ ] 用户停留在 `pages/message/message` 时收到 `refresh`，列表刷新到第一页。
* [ ] app 进入 `onHide` 后若仍能收到 `refresh`，会查询最新未读消息并创建本地通知栏消息；点击本地通知回到消息 tab。
* [ ] 退出登录或清空用户信息后，不再保持旧 token 的消息订阅。
* [ ] 不支持 SSE/流式请求的平台不会影响登录、消息列表、角标主动刷新等既有功能。

## Technical Approach

采用“app 侧轻量消息流适配器”方案：

* 在 `smart-app/src/lib/` 下新增 `message-stream.js`，提供 `startMessageStream()`、`stopMessageStream()`、事件总线和 SSE frame 解析。
* 优先使用 H5 环境的 `EventSource`，无法设置自定义 `Authorization` 时通过查询参数传递 token；后端如不支持查询 token，则 H5 使用 `uni.request` 流式方案或降级。
* App/其他端优先尝试 `uni.request({ responseType: 'arraybuffer', enableChunked: true })`，通过 `onChunkReceived` 解析 `text/event-stream` 帧。
* 解析到 `event: refresh` 后派发 `MESSAGE_STREAM_EVENT.REFRESH`，由 user store 刷新未读数量，消息页按需刷新列表。
* 使用 `App.vue` 记录 app 可见状态；不可见且收到刷新事件时，查询最新一条未读消息，在 App-Plus 端通过 `plus.push.createMessage` 创建本地通知，并在点击通知时跳转到消息 tab。
* 连接异常采用有限延迟重连；鉴权类错误停止连接并清理登录态。

## Decision (ADR-lite)

**Context**: Web 端已有 `fetch + ReadableStream` 版 SSE，但 app 端运行环境更复杂，直接照搬浏览器实现风险较高。现有 app 已有主动刷新和 tabBar 角标能力，实时订阅只需补齐“收到新消息后的刷新触发器”。

**Decision**: 在 app 端新增独立消息流适配器，保持与 Web 端事件语义一致，但实现上使用 uni-app 可用能力并做运行端降级。

**Consequences**: 这能最大限度复用后端能力，也能在不支持流式响应的平台保持现有体验。不可见时本地通知是尽力而为，依赖 app 运行时仍能收到 SSE；离线推送、锁屏长期可靠推送、厂商 Push 服务端通道不在本任务内。

## Out of Scope

* 不接入服务端 Uni Push、厂商推送或离线通知。
* 不新增消息详情页、消息分类筛选或弹窗提醒。
* 不改造后端消息模型和消息创建逻辑。
* 不调整 Web 端 SSE 已有行为。

## Technical Notes

* 相关 app 文件：
  * `smart-app/src/store/modules/system/user.js`
  * `smart-app/src/App.vue`
  * `smart-app/src/pages/message/message.vue`
  * `smart-app/src/api/support/message-api.js`
  * `smart-app/src/lib/smart-request.js`
* 相关 Web/后端参考：
  * `smart-admin-web-typescript/src/lib/message-stream.ts`
  * `smart-admin-web-typescript/src/store/modules/system/user.ts`
  * `smart-admin-api-java8-springboot2/sa-base/src/main/java/net/lab1024/sa/base/module/support/message/controller/MessageStreamController.java`
  * `smart-admin-api-java8-springboot2/sa-base/src/main/java/net/lab1024/sa/base/module/support/message/service/MessageStreamService.java`
* Context7 `/dcloudio/uni-app` 文档片段确认：`RequestTask.onChunkReceived()` 可监听 chunked received 事件，流式响应可配合 `arraybuffer` 解码；App iOS 说明对 `Content-Type: text/event-stream` 这类 streaming response 有支持条件。

## Definition of Done

* app 端代码实现并通过静态检查或构建验证。
* 关键流程具备错误捕获，不影响不支持平台的既有刷新逻辑。
* 如实现中发现新的项目约定或坑点，补充到 Trellis spec。
