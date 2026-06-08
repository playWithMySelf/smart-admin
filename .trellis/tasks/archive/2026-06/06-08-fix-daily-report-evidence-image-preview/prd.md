# 修复日报上传佐证图后图片与工作项丢失

## Goal

修复 `smart-app` 在微信小程序开发助手预览日报页面时，给已选工作项上传佐证图后图片不显示、且已选工作项被清空的问题。意见反馈上传图片正常，说明通用上传接口可用，问题集中在日报页面的页面生命周期和文件对象处理。

## What I Already Know

* 用户反馈：`smart-app` 小程序开发助手预览中，日报上传佐证图后图片没显示，已选工作项消失；意见反馈上传图片正常。
* 用户补充：审核日报详情中的图片缩略图是白色框，看不见，但点击后能放大预览。
* 修复补充：单纯改 `aspectFit` 和 `fileUrl/url/tempFilePath` 兜底后，缩略图仍无法显示，说明问题不是裁剪，而是小程序 `<image>` 对该远程地址的缩略图渲染不稳定。
* 用户补充：模拟器可以显示，真机调试仍不行，说明真机端对远程图片地址的域名、证书、重定向或鉴权限制更严格。
* 用户决策：如果是真机域名限制导致的缩略图不可见，本任务先不做下载/本地缓存绕过。
* 日报页面：`smart-app/src/pages/workitem/my-daily-report.vue` 使用 `onShow` 同时执行 `queryAllowReplenishDays()` 和 `loadReportByDate()`。
* 日报上传：`chooseImage(item)` 选择图片后压缩并上传，再 `item.fileList.push(res.data)`。
* 意见反馈页面：`smart-app/src/pages/support/feedback/feedback-form.vue` 用 `uni-file-picker`，页面没有在 `onShow` 中刷新表单状态。
* 管理端上传组件会将上传返回对象标准化：`file.url = file.fileUrl`、`file.name = file.fileName`。
* 工作区已有用户未提交修改：`smart-app/src/manifest.json` 的微信 `appid` 变更，本任务不应覆盖或提交该改动。

## Requirements

* 日报页首次进入时仍需加载补填天数配置和当前日期日报详情。
* 上传佐证图时，不应因为小程序选择图片返回页面触发 `onShow` 而覆盖当前未保存的工作项、完成说明或文件列表。
* 上传成功后，当前工作项的佐证图应立即显示缩略图，并可预览。
* 审核日报详情中的佐证图缩略图应正常显示；点击预览仍保持可用。
* 保存草稿和提交审核时，文件列表仍按后端需要提交 `fileId`、`fileKey`、`fileName`。
* 日期切换和用户点击刷新时，仍允许主动从后端重新加载日报详情。

## Acceptance Criteria

* [ ] 在日报页添加工作项后，点击上传佐证图并选择图片，返回页面后工作项仍保留。
* [ ] 上传成功后，佐证图缩略图在当前工作项下立即显示。
* [ ] 图片预览使用上传后可访问的 URL，不因字段名差异为空。
* [ ] 点击保存草稿后重新加载详情，工作项和佐证图仍能展示。
* [ ] 审核日报详情中，佐证图不再显示为空白白框，缩略图和放大预览展示同一张图片。
* [ ] 意见反馈上传图片行为不受影响。

## Definition of Done

* 代码改动范围控制在 `smart-app` 日报上传相关逻辑内。
* 不覆盖用户已有的 `smart-app/src/manifest.json` 修改。
* 运行 `smart-app` 可用的 lint/build/typecheck 中至少一个质量检查；若项目缺少脚本或环境阻塞，记录原因。
* 判断是否有值得沉淀到 `.trellis/spec/` 的生命周期/上传规范。

## Technical Approach

推荐采用“页面加载只在首次进入自动执行，主动操作才刷新，并统一图片缩略图 URL 处理”的小范围修复：

1. 用一个已初始化标记保护 `onShow`，避免选择图片返回页面时再次执行 `loadReportByDate()`。
2. 保留日期切换、刷新按钮、保存成功、提交成功后的显式 `loadReportByDate()`。
3. 上传返回后对文件对象做标准化，补齐 `url/name` 或使用统一函数读取 `fileUrl/url/tempFilePath`，保证缩略图和预览有稳定来源。
4. 审核详情页复用同样的图片 URL 兜底逻辑，避免缩略图只绑定单一字段。
5. 真机域名、证书、重定向或鉴权限制导致的 `<image>` 缩略图不可见，本任务不做代码绕过；后续通过小程序合法域名配置或文件 URL 生成策略处理。

## Alternatives Considered

* 自动保存后再上传：可以规避本地状态丢失，但会改变用户语义，上传图片就落草稿，风险较大。
* 在每次 `onShow` 前做脏数据合并：可处理更多场景，但复杂度高，容易和后端详情合并出重复工作项。
* 推荐方案：只限制 `onShow` 自动刷新，并标准化上传文件对象，最贴近意见反馈正常工作的差异点，改动面最小。

## Out of Scope

* 不改后端上传接口或日报保存接口。
* 不重构日报页整体表单状态管理。
* 不修改意见反馈页面。
* 不处理 `manifest.json` 中已有的 `appid` 改动。
* 不处理真机环境下由小程序图片域名、证书、重定向或鉴权限制导致的缩略图不可见问题。

## Technical Notes

* 相关文件：
  * `smart-app/src/pages/workitem/my-daily-report.vue`
  * `smart-app/src/pages/workitem/daily-review-detail.vue`
  * `smart-app/src/pages/support/feedback/feedback-form.vue`
  * `smart-app/src/lib/smart-request.js`
  * `smart-app/src/lib/image-compress.js`
  * `smart-admin-web-typescript/src/components/support/file-upload/index.vue`
* 最近相关提交：`665c70e feat: add daily image compression`，小程序日报上传从直接上传原图改为先 `compressImagePathBeforeUpload(filePath)`。
