# 修复小程序上传失败

## Goal

修复 `smart-app` 打包后上传微信平台失败的问题，使微信小程序构建产物满足主包体积限制，并启用微信小程序组件按需注册/按需注入能力。

## What I Already Know

* 用户反馈上传失败原因包括：主包尺寸应小于 1.5M；需要启用组件按需注册。
* `smart-app` 是 uni-app + Vue 3 + Vite 项目，微信小程序构建脚本为 `npm run build:mp-weixin`。
* 当前 `smart-app/src/manifest.json` 的 `mp-weixin` 未配置 `lazyCodeLoading` 和 `optimization.subPackages`。
* 当前 `smart-app/src/pages.json` 将 27 个页面全部放在主包 `pages` 中，没有使用 `subPackages`。
* 当前 `smart-app/dist/build/mp-weixin/app.json` 未生成 `lazyCodeLoading`。
* 当前 `smart-app/dist/build/mp-weixin` 总体积约 2.16MB，所有页面和全量静态资源都在主包。

## Requirements

* 在微信小程序配置中启用组件按需注册/按需注入。
* 启用 uni-app 微信小程序分包优化。
* 将非 TabBar、非首屏必要页面拆入 `subPackages`，降低主包体积。
* 构建后验证 `dist/build/mp-weixin/app.json` 包含 `lazyCodeLoading: "requiredComponents"`。
* 构建后验证主包体积小于 1.5MB。

## Acceptance Criteria

* [ ] `npm run build:mp-weixin` 成功。
* [ ] `smart-app/dist/build/mp-weixin/app.json` 包含 `lazyCodeLoading: "requiredComponents"`。
* [ ] `smart-app/dist/build/mp-weixin/app.json` 包含分包配置。
* [ ] 构建后主包体积小于 1.5MB。
* [ ] 不改动已有未提交的环境配置和私钥文件。

## Definition of Done

* 代码以 UTF-8 无 BOM 保存。
* 构建通过。
* 体积和关键配置已用本地命令验证。
* 如发现必须压缩图片或移动资源，再最小范围处理并说明影响。

## Technical Approach

优先采用 uni-app 官方支持的微信小程序配置：

* `manifest.json > mp-weixin > optimization.subPackages = true`
* `manifest.json > mp-weixin > lazyCodeLoading = "requiredComponents"`
* `pages.json` 保留 `home`、`login`、`mine`、`message` 等首屏/TabBar 页面在主包，其他业务页面按目录拆为分包。

如果拆分页面后主包仍超过 1.5MB，再检查主包静态资源，优先处理体积最大的首屏图片，而不是调整业务逻辑。

## Decision

Context: 上传失败来自微信小程序平台限制，必须同时满足构建配置与体积门槛。

Decision: 先使用框架原生配置和页面分包，这是对路由和业务逻辑侵入最小的方式。

Consequences: 分包页面首次进入时可能有轻微加载成本；TabBar 页面和登录页仍在主包，保证首屏可用。

## Out of Scope

* 不调整业务接口、登录逻辑或页面功能。
* 不处理当前仓库中与本任务无关的未提交改动。
* 不引入新的图片压缩依赖，除非主包仍无法达标。

## Research References

* [`research/uniapp-weixin-package-optimization.md`](research/uniapp-weixin-package-optimization.md) — uni-app 文档确认 `optimization.subPackages` 和 `lazyCodeLoading` 的配置方式。

## Technical Notes

* 相关文件：`smart-app/src/manifest.json`、`smart-app/src/pages.json`。
* 参考构建目录：`smart-app/dist/build/mp-weixin`。
