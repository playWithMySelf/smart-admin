# 前端图片上传压缩选型

## 结论

本任务采用组合方案：

* 管理端 Web 使用 `browser-image-compression`，因为它直接处理浏览器 `File`，支持目标体积、最大边长、Web Worker 和 EXIF 控制，适合接入 ant-design-vue 上传前流程。
* 移动端 uni-app 的 App / 小程序场景优先使用 `uni.compressImage`，因为日报上传拿到的是临时文件路径，原生 API 更符合多端运行环境。
* 移动端 H5 如无法使用 `uni.compressImage`，保留原路径上传兜底，避免跨端能力差异导致上传中断。

## 对比

### browser-image-compression

优势：

* 支持 `maxSizeMB` 和 `maxWidthOrHeight`，可以贴合“压到 800KB 左右、最长边 1600px”的业务目标。
* 支持 `useWebWorker`，降低大图压缩时卡住页面的概率。
* 支持 `preserveExif`，默认可不保留 EXIF，减少元数据体积和隐私信息。
* TypeScript / Vite / Vue 项目接入成本低。

注意：

* 依赖浏览器 Canvas 能力，压缩结果会受浏览器实现影响。
* GIF 动图不适合作为普通照片压缩处理，本任务先不对 GIF 做压缩。

### compressorjs

优势：

* API 简单，适合质量和尺寸压缩。
* 依赖轻，成熟度较高。

不足：

* 不如 `browser-image-compression` 直接支持按目标体积压缩。
* 对进度、取消、Web Worker 等上传体验能力支持较弱。

### uni.compressImage

优势：

* 适配 uni-app App / 小程序临时文件路径。
* 不需要把小程序或 App 图片路径转换成浏览器 `File`。
* 与现有 `uni.chooseImage -> uni.uploadFile` 流程匹配。

注意：

* H5 不稳定或不支持，应保留兜底。
* 不同平台对宽高参数支持程度可能不同，质量参数和压缩结果应以实测为准。

## 推荐默认参数

* Web：`maxSizeMB: 0.8`，`maxWidthOrHeight: 1600`，`initialQuality: 0.82`，`useWebWorker: true`，`preserveExif: false`。
* uni-app：`quality: 80`，尽量设置最长边约束为 1600px；失败时使用原始临时路径继续上传。
* 小于等于 800KB 的图片跳过压缩。
