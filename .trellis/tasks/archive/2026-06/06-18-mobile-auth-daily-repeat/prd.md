# 修复移动端登录态并允许日报重复选项

## Goal

解决移动端小程序登录后过一段时间再次进入仍被要求登录、登录页未回填上次账号密码的问题；同时调整日报填报规则，使同一个工作项允许重复添加，每添加一次都作为一条独立日报明细计分。改动覆盖 Web 端、移动端和后端受影响模块。

## What I Already Know

* 用户已将“登录后无操作自动退出的分钟”设置为 `43200`，期望移动端小程序在未真正超时前不应频繁重新登录。
* 移动端登录页 `smart-app/src/pages/login/login.vue` 当前没有读取或保存上次登录账号、密码。
* 移动端用户 store `smart-app/src/store/modules/system/user.js` 当前只持久化 `USER_TOKEN`，未持久化登录表单信息。
* 移动端启动时 `smart-app/src/App.vue` 会调用 `getLoginInfo()` 校验 token；公共请求层遇到 `30007`、`30008`、`30012` 会清除 token 并跳转登录页。
* Sa-Token v1.44.0 文档确认：`activeTimeout` 是 token 最低活跃频率，单位为秒；可通过全局配置或 `SaLoginParameter.setActiveTimeout(...)` 指定。
* 后端 `Level3ProtectConfigService` 已将三级等保配置中的 `loginActiveTimeoutMinutes` 转成秒，并通过 `TokenConfig` 写入 Sa-Token 全局配置。
* 后端当前 `sa-token.is-concurrent=false`，同一账号新登录可能顶掉旧 token，这是移动端“过一会儿要登录”的候选原因之一。
* 后端 `WorkDailyReportService.saveDraft()` 当前调用 `checkDuplicateWorkItem()`，明确拒绝同一张日报重复添加同一工作项。
* Web 端日报页 `smart-admin-web-typescript/src/views/business/workitem/my-daily-report.vue` 当前以 `workItemId` 判断“已添加”、滚动定位、删除和渲染 key/id，重复项会互相影响。
* 移动端日报页 `smart-app/src/pages/workitem/my-daily-report.vue` 当前 `isAdded()` 会阻止重复添加，删除也按 `workItemId` 删除全部同项。
* 审核模块已主要按 `workDailyReportItemId` 处理明细，基本符合重复项独立审核。
* 积分报表后端明细查询已按 `workDailyReportItemId` 返回明细行；移动端积分详情当前用 `workItemId` 作为 key，重复项下需要改为明细 ID。

## Requirements

* 移动端登录页在提示重新登录或用户再次进入登录页时，应回填上次登录的账号和密码。
* 移动端登录成功后，应保存本次用户输入的明文账号和密码到本地存储，用于下次登录页回填；发给后端时仍继续使用现有加密逻辑。
* 移动端 token 校验失败时，可以清除 token，但不能清除上次登录表单缓存。
* 若 token 仍有效，移动端进入登录页应继续自动跳转首页，不影响现有体验。
* 后端日报保存允许 `itemList` 中多个明细使用相同 `workItemId`，每条明细独立保存、独立排序、独立附件、独立审核和独立计分。
* Web 端日报添加同一工作项时不再显示“已添加”阻止操作，每次点击“添加”都新增一条独立明细。
* Web 端日报删除操作只删除当前明细，不删除同一 `workItemId` 的其他重复明细。
* Web 端日报渲染 key、DOM id、滚动定位等使用独立临时行 key 或后端明细 ID，避免重复工作项渲染冲突。
* 移动端日报添加同一工作项时不再阻止重复，每次点击“添加”新增一条独立明细。
* 移动端日报删除操作只删除当前明细，不删除同一 `workItemId` 的其他重复明细。
* 移动端和 Web 端从后端回显已有日报时，已有 `workDailyReportItemId` 的明细仍保持稳定；新建未保存明细使用前端临时 key。
* 受影响的审核、积分报表模块需要检查并修正重复项 key 或行标识问题，确保同一工作项多次出现时页面不丢行、不串行。

## Acceptance Criteria

* [ ] 小程序登录成功后退出登录态或 token 失效跳回登录页，登录名和密码自动回填为上次成功登录输入。
* [ ] 小程序登录时提交给后端的密码仍为现有加密后的密码，本地缓存仅用于 UI 回填。
* [ ] 小程序已有有效 token 时进入登录页仍自动跳转首页。
* [ ] Web 端同一日报可连续添加同一工作项多次，已填明细数量每次增加 1。
* [ ] 移动端同一日报可连续添加同一工作项多次，已填明细数量每次增加 1。
* [ ] 删除重复工作项中的某一条时，只删除当前条，其他同名同 ID 工作项保留。
* [ ] 保存草稿后重新打开日报，重复明细按保存顺序全部回显。
* [ ] 提交审核、审核打分、审核失败/通过、积分统计均按重复明细逐条计算。
* [ ] Web 和移动端重复明细渲染无 key 冲突、附件串行、定位错误。

## Technical Approach

推荐方案是“前端用稳定行 key 表示明细行，后端移除业务去重校验”：

* 移动端登录缓存：新增移动端本地存储 key，例如 `LAST_LOGIN_FORM`，登录页 `onShow` 或初始化时读取回填；登录成功后保存用户输入的 `loginName` 和明文 `password`。`clearUserLoginInfo()`、`logout()` 仅清 token，不清该缓存，保证提示登录时能带出上次信息。
* 登录态排查：保留现有 `getLoginInfo()` 校验流程；如果后续仍出现 43200 分钟内 token 无效，应重点排查同账号互斥登录、服务端重启/Redis token 丢失、Sa-Token activeTimeout 是否已加载为 `2592000` 秒。本任务先修复明确的客户端回填问题，并避免误改全局并发策略。
* 日报保存：删除或跳过 `WorkDailyReportService.checkDuplicateWorkItem()` 调用，保留 `@Size(max = 100)` 上限；`replaceReportItems()` 当前按列表顺序逐条插入，天然支持重复工作项。
* Web 端日报：为每条前端明细生成 `rowKey`，优先用 `workDailyReportItemId`，新建项用临时 key；渲染、删除、滚动定位都改用该行 key。可选工作项按钮从“已添加/滚动”改为始终“添加”。
* 移动端日报：为每条明细生成同类 `rowKey`；列表 key 和删除逻辑按行 key 处理；可选工作项按钮始终允许添加。
* 受影响模块：Web 审核页已用 `workDailyReportItemId`，保留；Web 积分报表已用 `workDailyReportItemId`，保留；移动端审核详情已用 `workDailyReportItemId`，保留；移动端积分详情 key 从 `workItemId` 改为 `workDailyReportItemId || 临时组合 key`。

## Decision (ADR-lite)

**Context**: 业务需要同一个工作项在一张日报内出现多次，并且每次都独立计分。原实现用 `workItemId` 代表“是否已添加”，导致 UI 和后端都按工作项唯一处理。

**Decision**: 将日报明细的前端身份从 `workItemId` 切换到“明细行身份”，后端允许相同 `workItemId` 多条明细入库。

**Consequences**: 不需要改数据库结构；但所有列表 key、删除、滚动定位、附件绑定必须避免再使用 `workItemId` 作为唯一行标识。统计口径会自然按明细数量增长，这是本次需求期望的行为。

## Out of Scope

* 不在本任务中改变 Sa-Token 的同账号互斥策略 `is-concurrent=false`。
* 不新增“记住密码”开关；按用户要求默认回填上次登录信息。
* 不加密或脱敏本地保存的移动端密码缓存；如果后续需要安全强化，可另起任务做加密存储或开关控制。
* 不调整数据库表结构。

## Technical Notes

* Context7 文档：`/dromara/sa-token/v1.44.0`，`SaLoginParameter.setActiveTimeout(...)` 单位为秒，全局 `sa-token.active-timeout` 也是秒。
* 相关前端规范：`.trellis/spec/frontend/index.md`
* 相关后端规范：`.trellis/spec/backend/index.md`
* 相关思考指南：`.trellis/spec/guides/index.md`
* 主要影响文件预计包括：
  * `smart-app/src/pages/login/login.vue`
  * `smart-app/src/constants/local-storage-key-const.js`
  * `smart-app/src/pages/workitem/my-daily-report.vue`
  * `smart-app/src/pages/workitem/score-report-detail.vue`
  * `smart-admin-web-typescript/src/views/business/workitem/my-daily-report.vue`
  * `smart-admin-api-java8-springboot2/sa-admin/src/main/java/net/lab1024/sa/admin/module/business/workitem/service/WorkDailyReportService.java`
