# 前端日报图片上传压缩

## Goal

日报佐证图片在管理端和移动端上传前进行前端压缩，减少手机相册原图带来的上传耗时、流量消耗和服务器存储压力，同时保持日报查看场景下的视觉清晰度。

## What I Already Know

* 管理端位于 `smart-admin-web-typescript`，技术栈为 Vue 3、Vite、ant-design-vue。
* 移动端位于 `smart-app`，技术栈为 uni-app、Vue 3。
* 管理端日报页面 `smart-admin-web-typescript/src/views/business/workitem/my-daily-report.vue` 使用通用 `Upload` 组件上传佐证图片。
* 管理端通用上传组件位于 `smart-admin-web-typescript/src/components/support/file-upload/index.vue`，当前在 `customRequest` 中直接把原始 `options.file` 追加到 `FormData` 上传。
* 移动端日报页面 `smart-app/src/pages/workitem/my-daily-report.vue` 当前使用 `uni.chooseImage` 选择图片，然后通过 `fileApi.upload` 直接上传 `tempFilePath`。
* 用户确认 PC 管理端和移动端都需要处理，压缩希望在前端完成。

## Requirements

* 管理端日报佐证图片上传前自动压缩图片。
* 移动端日报佐证图片上传前自动压缩图片。
* 管理端优先使用 `browser-image-compression` 压缩 `File`。
* 移动端 App / 小程序优先使用 `uni.compressImage` 压缩临时图片路径。
* 移动端 H5 场景需要具备可用兜底，不应因为 `uni.compressImage` 不支持 H5 而上传失败。
* 压缩仅针对图片文件；非图片上传流程保持原样。
* 小图片不重复压缩，避免无意义损耗。
* 压缩失败时不应阻断用户上传，可记录错误并回退上传原文件。
* 后端接口协议不变，仍然走现有文件上传接口。

## Recommended Compression Defaults

* 小于等于 800KB 的图片不压缩。
* 管理端普通日报照片目标体积不超过 0.8MB，最长边不超过 1600px。
* 移动端 `uni.compressImage` 使用 `quality: 80`，最长边约束按平台能力尽量压到 1600px。
* 不保留 EXIF 元数据。

## Acceptance Criteria

* [ ] 管理端日报上传 `.jpg/.jpeg/.png` 佐证图片时，上传前会经过压缩处理。
* [ ] 管理端通用上传组件默认行为兼容原有附件上传，非图片不被压缩。
* [ ] 移动端日报上传图片时，上传前会优先得到压缩后的临时路径或压缩文件。
* [ ] 压缩失败时仍可继续上传原图，并通过现有错误上报记录异常。
* [ ] 现有日报保存、提交、预览、删除附件流程不受影响。
* [ ] 管理端和移动端构建或类型检查通过。

## Out Of Scope

* 不修改后端上传接口。
* 不新增服务端图片压缩。
* 不改造对象存储或缩略图服务。
* 不处理视频、音频、PDF 等非图片附件压缩。
* 不做用户可配置的压缩质量 UI。

## Technical Notes

* 管理端适合新增图片压缩工具函数，再由上传组件按 props 控制是否启用。
* 日报页面可以显式开启图片压缩，避免影响所有业务附件上传。
* 移动端适合新增图片路径压缩工具函数，由日报 `chooseImage` 上传前调用。
* `browser-image-compression` 支持 `maxSizeMB`、`maxWidthOrHeight`、`useWebWorker`、`preserveExif` 等选项。
* `uni.compressImage` 在 App / 多个小程序端可用，H5 需另行兜底。

## Research References

* `research/frontend-image-compression.md` — 前端图片压缩库与 uni-app 压缩能力选型结论。

## Definition Of Done

* 代码改动保持局部，优先复用现有上传接口和错误上报。
* 管理端新增依赖后锁文件同步更新。
* 相关构建或类型检查通过。
* 如产生可复用约定，更新 Trellis spec 或记录无需更新的判断。
