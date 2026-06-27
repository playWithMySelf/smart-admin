# smart-admin-web-typescript 目录结构说明

> 面向脚手架复用整理：保留通用底座能力，替换业务模块、业务接口、业务常量和品牌资源后，可用于快速对接其他中台接口。

## 1. 项目定位

`smart-admin-web-typescript` 是 SmartAdmin 的 Web 管理端工程，技术栈以 `package.json` 为准：

- Vue 3
- Vite 5
- TypeScript
- Ant Design Vue 4
- Pinia
- Vue Router
- Axios
- Less

脚手架抽取时建议把它拆成两类：

- **底座层**：启动入口、环境配置、请求封装、路由权限、布局、主题、全局状态、通用组件、字典/枚举/权限插件等。
- **业务层**：具体业务接口、业务页面、业务常量、业务组件、业务图片资源等。

## 2. 根目录结构

```text
smart-admin-web-typescript/
|-- public/                 静态公开资源，Vite 会原样复制
|-- src/                    前端源码主目录
|-- .env.development        开发环境变量
|-- .env.localhost          本地环境变量
|-- .env.pre                预发布环境变量
|-- .env.production         生产环境变量
|-- .env.test               测试环境变量
|-- .eslintrc.cjs           ESLint 配置
|-- .prettierrc.cjs         Prettier 配置
|-- .stylelintrc.js         Stylelint 配置
|-- index.html              Vite 入口 HTML
|-- package.json            依赖、脚本、Node 版本要求
|-- package-lock.json       npm 锁定文件
|-- tsconfig.json           TypeScript 配置
|-- vite.config.ts          Vite 构建配置
```

### 脚手架建议

- `package.json`、`vite.config.ts`、`tsconfig.json`、代码规范配置建议保留。
- `.env.*` 建议保留文件结构，但替换接口地址、应用标识、部署路径等环境值。
- `public/` 和 `src/assets/images/logo`、`src/assets/images/login` 中的品牌资源需要按新中台替换。
- `dist/`、`node_modules/` 属于构建产物和依赖目录，不进入脚手架源码模板。

## 3. src 总览

```text
src/
|-- api/                    接口封装和接口通用模型
|-- assets/                 图片、图标等静态资源
|-- components/             公共组件、支撑组件、系统组件、业务组件
|-- config/                 应用默认配置
|-- constants/              全局常量、系统常量、支撑功能常量、业务枚举
|-- directives/             Vue 自定义指令
|-- i18n/                   国际化
|-- layout/                 管理端整体布局、菜单、页签、顶部用户区
|-- lib/                    项目级运行库和外部能力封装
|-- plugins/                全局插件
|-- router/                 路由实例、动态路由构建、静态路由
|-- store/                  Pinia 状态管理
|-- theme/                  全局样式、主题变量、颜色工具
|-- types/                  全局类型声明
|-- utils/                  通用工具函数
|-- views/                  页面视图
|-- App.vue                 根组件
|-- main.ts                 应用启动入口
|-- shims-vue.d.ts          Vue 文件声明
|-- vite-env.d.ts           Vite 环境类型声明
```

## 4. 启动入口

```text
src/
|-- main.ts
|-- App.vue
```

### `main.ts`

项目启动核心，适合作为脚手架底座保留。它负责：

- 创建 Vue 应用。
- 注册 `router`、`store`、`i18n`、Ant Design Vue、JsonViewer。
- 注册权限、字典、枚举等全局插件。
- 注册 Ant Design 图标组件。
- 读取本地 token。
- 如果已登录，先请求登录用户信息、菜单路由、字典数据，再构建动态路由并挂载应用。
- 如果未登录，直接初始化只包含登录等基础路由的应用。

对接新中台时通常需要关注：

- `loginApi.getLoginInfo()` 返回结构是否兼容。
- `res.data.menuList` 的菜单字段是否兼容动态路由构建逻辑。
- `dictApi.getAllDictData()` 的字典结构是否兼容当前 `dict` store 和 `dict-plugin.ts`。
- token 的存储 key、请求头名称是否需要调整。

### `App.vue`

根组件，通常保留。页面骨架主要由 `router-view` 和全局样式承载。

## 5. api：接口封装

```text
src/api/
|-- base-model/
|   |-- response-model.ts
|   |-- page-result-model.ts
|   |-- sort-item-model.ts
|   |-- page-param-model.ts
|-- system/
|-- support/
|-- business/
```

### `base-model`

接口通用模型目录，适合作为脚手架底座保留。

- `response-model.ts`：统一响应结构。
- `page-result-model.ts`：分页返回结构。
- `sort-item-model.ts`：排序项结构。
- `page-param-model.ts`：分页查询参数结构。它虽然常被业务列表使用，但本质是通用分页模型，是否保留取决于新中台分页协议。如果新接口分页字段不同，可替换为新的通用分页模型。

### `system`

系统能力接口，偏底座但强依赖后端协议。常见内容包括：

- 登录、获取登录用户信息。
- 菜单、角色、部门、员工、岗位。
- 首页待办、首页数据。

脚手架中可以保留目录和调用模式，但实际 API 路径、请求参数、返回字段要按新中台接口重写。

### `support`

支撑能力接口，部分适合沉淀到脚手架：

- `dict-api.ts`：数据字典，建议保留能力模型。
- `file-api.ts`：文件上传下载，建议保留或按新文件服务调整。
- `table-column-api.ts`：表格列配置，若新中台也需要用户自定义列则保留。
- `data-tracer-api.ts`：数据变更记录，按新后端是否提供决定。
- `operate-log-api.ts`、`login-log-api.ts`：日志类接口，按中台能力决定。
- `code-generator-api.ts`、`reload-api.ts`、`cache-api.ts` 等偏平台运维能力，脚手架可选。

### `business`

业务接口目录，主要是替换区：

```text
src/api/business/
|-- category/
|-- goods/
|-- oa/
|-- workitem/
```

对接其他中台时，一般清空或按新业务域重建。例如：

```text
src/api/business/
|-- customer/customer-api.ts
|-- order/order-api.ts
|-- settlement/settlement-api.ts
```

接口文件建议继续遵循：

- 文件名以 `api` 结尾：`customer-api.ts`。
- 导出对象以 `Api` 结尾：`customerApi`。
- 统一从 `/@/lib/axios` 引入 `getRequest`、`postRequest`、`postDownload` 等方法。

## 6. lib：项目级运行库

```text
src/lib/
|-- api-base-url.ts
|-- axios.ts
|-- default-time-ranges.ts
|-- encrypt.ts
|-- highlight-line-number.ts
|-- image-compress.ts
|-- message-stream.ts
|-- smart-sentry.ts
|-- smart-watermark.ts
|-- table-auto-height.ts
```

这是脚手架核心目录，建议重点保留。

- `axios.ts`：统一请求实例、token 注入、响应处理、错误提示、下载处理、加密请求入口。对接新中台时优先改这里，避免页面和 API 文件散落处理错误。
- `api-base-url.ts`：接口基础地址计算。通常和 `.env.*` 配合使用。
- `encrypt.ts`：接口加密/解密能力。如果新中台没有加密协议，可保留但不启用，或删掉加密请求入口。
- `smart-sentry.ts`：前端异常捕获封装。不是 Sentry 官方 SDK 的强绑定名，而是项目内统一错误上报入口，可按新平台替换实现。
- `smart-watermark.ts`：水印能力，管理后台常用，建议保留。
- `table-auto-height.ts`：表格高度自适应能力，适合后台列表页复用。
- `default-time-ranges.ts`：常用时间范围，适合查询表单复用。
- `image-compress.ts`：图片压缩，文件上传场景可复用。
- `message-stream.ts`：流式消息读取，只有新中台存在 SSE/流式接口时再保留。
- `highlight-line-number.ts`：代码高亮行号处理，偏展示工具，按页面需求决定。

## 7. plugins：全局插件

```text
src/plugins/
|-- dict-plugin.ts
|-- privilege-plugin.ts
|-- smart-enums-plugin.ts
```

这三个文件很适合作为脚手架通用能力。

- `dict-plugin.ts`：数据字典插件，配合字典 store 和字典组件使用，用于通过字典 code 显示文本、构建下拉选项等。
- `privilege-plugin.ts`：权限插件，通常用于按钮级权限判断。
- `smart-enums-plugin.ts`：前端枚举插件，配合 `src/constants/**` 中的 `SmartEnum` 常量使用，负责按枚举值展示描述、生成选项等。

对接新中台时主要确认：

- 字典接口返回结构。
- 权限点字段和按钮权限判断逻辑。
- 枚举数据是否继续使用前端常量维护，还是改为后端字典下发。

## 8. directives：自定义指令

```text
src/directives/
|-- privilege.ts
```

`privilege.ts` 是按钮/元素权限控制指令，适合保留在脚手架中。它通常和 `privilege-plugin.ts`、用户权限数据、菜单/按钮权限点一起工作。

新中台权限模型变更时，需要同步检查：

- 登录用户信息中的权限点字段。
- `store/modules/system/user.ts` 中权限数据保存方式。
- `privilege-plugin.ts` 和 `directives/privilege.ts` 的判断逻辑。

## 9. router：路由和动态菜单

```text
src/router/
|-- index.ts
|-- routers.ts
|-- system/
|   |-- home.ts
|   |-- login.ts
|-- support/
|   |-- help-doc.ts
```

### `index.ts`

脚手架核心文件，包含：

- `createRouter` 初始化。
- 登录校验。
- 路由进度条。
- 页签导航记录。
- keep-alive 缓存维护。
- `buildRoutes(menuRouterList)` 动态路由构建。
- 根据后端菜单中的 `component` 字段匹配 `src/views/**/**.vue` 页面组件。
- 外链菜单使用 `components/framework/iframe/iframe-index.vue`。

对接新中台时，动态菜单字段是最关键的适配点。当前逻辑主要依赖：

- `menuId`
- `path`
- `menuName`
- `icon`
- `visibleFlag`
- `cacheFlag`
- `frameFlag`
- `frameUrl`
- `component`
- `deletedFlag`

如果新中台菜单字段不同，建议在登录后增加一层菜单适配函数，把新接口返回值转换为当前路由构建所需结构，而不是在各处散改字段名。

### `routers.ts`

静态路由集合入口，适合保留。登录页、404、403、帮助文档等不依赖后端菜单的页面可放这里。

### `system` / `support`

静态路由模块。脚手架可以保留登录、首页、错误页等基础路由；帮助文档类路由按新项目需要决定。

## 10. store：Pinia 状态管理

```text
src/store/
|-- index.ts
|-- modules/
|   |-- model/
|   |   |-- UserTagNav.ts
|   |-- system/
|       |-- app-config.ts
|       |-- dict.ts
|       |-- role.ts
|       |-- spin.ts
|       |-- user.ts
```

脚手架建议保留：

- `index.ts`：Pinia 实例。
- `system/user.ts`：登录用户、菜单、权限、页签、keep-alive 等核心状态。对接新中台时重点改登录信息结构。
- `system/dict.ts`：字典缓存。与 `dict-plugin.ts`、字典组件配套。
- `system/app-config.ts`：应用配置状态。与 `config/app-config.ts` 配套。
- `system/spin.ts`：全局 loading 状态。
- `model/UserTagNav.ts`：页签导航模型。

`system/role.ts` 是否保留取决于角色相关页面和权限模型是否沿用。

## 11. layout：后台布局

```text
src/layout/
|-- index.vue
|-- side-layout.vue
|-- side-expand-layout.vue
|-- top-layout.vue
|-- top-expand-layout.vue
|-- help-doc-layout.vue
|-- layout-const.ts
|-- components/
|   |-- header-user-space/
|   |-- menu-location-breadcrumb/
|   |-- page-tag/
|   |-- side-menu/
|   |-- side-expand-menu/
|   |-- top-menu/
|   |-- top-expand-menu/
|   |-- side-help-doc/
|   |-- smart-footer/
|   |-- smart-keep-alive.ts
```

这是管理端脚手架的重要资产，建议保留。

- `index.vue`：布局入口，根据配置选择具体布局。
- `side-layout.vue`、`top-layout.vue` 等：不同菜单布局模式。
- `layout-const.ts`：布局相关常量。
- `header-user-space/`：右上角用户区域，含头像、消息、设置、重置密码等。
- `page-tag/`：页签导航。
- `side-menu/`、`top-menu/`：菜单渲染。
- `smart-keep-alive.ts`：页面缓存组件能力。
- `menu-location-breadcrumb/`：面包屑。
- `smart-footer/`：底部区域。
- `side-help-doc/`、`help-doc-layout.vue`：帮助文档能力，按新项目需要保留或删除。

对接其他中台时，通常要改：

- Logo、系统名、底部版权。
- 用户下拉菜单项。
- 消息中心入口。
- 帮助文档入口。
- 布局默认配置。

## 12. components：公共组件

```text
src/components/
|-- framework/
|-- support/
|-- system/
|-- business/
```

### `framework`

框架级组件，最适合进入脚手架。

```text
framework/
|-- area-cascader/
|-- boolean-select/
|-- icon-select/
|-- iframe/
|-- smart-copy-icon/
|-- smart-enum-checkbox/
|-- smart-enum-radio/
|-- smart-enum-select/
|-- smart-loading/
|-- text-ellipsis/
|-- wangeditor/
```

- `smart-enum-select`、`smart-enum-radio`、`smart-enum-checkbox`：前端枚举表单组件，和 `smart-enums-plugin.ts`、`constants` 配套。
- `smart-loading`：全局 loading 能力。
- `iframe`：外链菜单承载组件。
- `boolean-select`：布尔值下拉，后台查询表单常用。
- `icon-select`：菜单图标选择。
- `text-ellipsis`：文本省略展示。
- `smart-copy-icon`：复制能力。
- `area-cascader`：省市区选择。若新项目不需要地区数据，可删除。
- `wangeditor`：富文本编辑器。按业务需求决定。

### `support`

支撑功能组件，适合按能力保留。

```text
support/
|-- data-tracer/
|-- dict-code-select/
|-- dict-label/
|-- dict-select/
|-- file-preview/
|-- file-preview-modal/
|-- file-upload/
|-- table-header-cell/
|-- table-operator/
```

- `dict-select`、`dict-code-select`、`dict-label`：字典展示和选择组件，建议保留。
- `file-upload`、`file-preview`、`file-preview-modal`：文件能力，按新文件接口适配后保留。
- `table-operator`：表格列设置/操作能力，后台列表页高复用。
- `table-header-cell`：表头增强。
- `data-tracer`：数据变更追踪，按新中台是否提供变更记录接口决定。

### `system`

系统域公共组件。

```text
system/
|-- department-tree-select/
|-- employee-select/
|-- employee-table-select-modal/
|-- menu-tree-select/
|-- position-select/
```

这些组件和组织架构、人员、菜单、岗位强关联。若新中台也有类似系统管理能力，可以保留并适配接口；否则建议作为可选模块，不放进最小脚手架核心。

### `business`

业务组件替换区。

```text
business/
|-- category-tree-select/
|-- oa/
```

这些组件与当前业务域强关联，对接其他中台时通常删除或按新业务重建。

## 13. constants：常量和枚举

```text
src/constants/
|-- index.ts
|-- common-const.ts
|-- layout-const.ts
|-- local-storage-key-const.ts
|-- regular-const.ts
|-- system/
|-- support/
|-- business/
```

建议保留：

- `index.ts`：枚举插件注册入口。
- `common-const.ts`：通用状态、页面路径、数据类型等公共常量。
- `layout-const.ts`：布局常量。
- `local-storage-key-const.ts`：本地存储 key。对接新中台时重点检查 token、用户信息等 key。
- `regular-const.ts`：通用正则。
- `system/**`：登录设备、菜单、员工、首页等系统常量，按系统模块保留。
- `support/**`：字典、文件、表格列、日志等支撑模块常量，按功能保留。

建议替换：

- `business/**`：当前业务枚举，如 OA、ERP、workitem 等，接入新中台时按新业务域重建。

## 14. config：应用配置

```text
src/config/
|-- app-config.ts
```

`app-config.ts` 是应用默认配置，通常包含布局、主题、页面展示等默认项。适合保留为脚手架配置中心。

对接新中台时优先检查：

- 系统标题。
- Logo 展示。
- 默认布局模式。
- 页签、面包屑、帮助入口等开关。
- 是否启用水印、主题色等。

## 15. theme：主题和全局样式

```text
src/theme/
|-- index.less
|-- smart-admin.less
|-- custom-variables.ts
|-- color.ts
```

脚手架建议保留。

- `index.less`：全局样式入口。
- `smart-admin.less`：项目自有样式。
- `custom-variables.ts`：主题变量。
- `color.ts`：颜色工具。

对接新中台时主要替换：

- 品牌色。
- 菜单主题。
- 登录页样式。
- Logo、背景图等视觉资源。

## 16. assets：静态资源

```text
src/assets/
|-- images/
    |-- 1024lab/
    |-- login/
    |-- logo/
    |-- nav/
    |-- notice/
```

建议分类处理：

- `logo/`：品牌资源，必须替换。
- `login/`：登录页背景和第三方登录图标，按新项目登录方式替换或删除。
- `nav/`：导航视觉资源，若布局沿用可保留。
- `notice/`：通知图标，按消息中心是否保留决定。
- `1024lab/`：原项目宣传/二维码资源，脚手架中通常删除。

## 17. utils：通用工具

```text
src/utils/
|-- local-util.ts
|-- ployfill.ts
|-- str-util.ts
```

建议保留：

- `local-util.ts`：本地存储读写工具。
- `str-util.ts`：字符串工具。
- `ployfill.ts`：兼容性补丁。文件名当前为 `ployfill`，如果未来整理脚手架，可以考虑统一改为 `polyfill`，但需要同步所有引用。

## 18. types：全局类型

```text
src/types/
|-- config.d.ts
|-- env.d.ts
|-- json-viewer.d.ts
|-- smart-enum.d.ts
|-- user.d.ts
```

建议保留并按新中台调整：

- `smart-enum.d.ts`：枚举结构类型，和 `smart-enums-plugin.ts`、枚举组件配套。
- `env.d.ts`：环境变量类型。
- `config.d.ts`：应用配置类型。
- `user.d.ts`：用户信息类型。新中台登录用户结构不同的话，这里要同步修改。
- `json-viewer.d.ts`：第三方组件类型声明，若不使用 JsonViewer 可删除。

## 19. i18n：国际化

```text
src/i18n/
|-- index.ts
|-- lang/
    |-- zh-CN/
    |-- en-US/
```

如果新中台需要中英文切换，建议保留；如果只做中文后台，可以保留最小结构，后续再扩展。

## 20. views：页面视图

```text
src/views/
|-- system/
|-- support/
|-- business/
```

### `system`

系统管理和基础页面，部分适合脚手架：

- `login/`：登录页，通常保留并替换品牌样式、登录协议。
- `home/`：首页，建议按新中台工作台需求改造。
- `40X/`：403、404 页面，建议保留。
- `menu/`、`role/`、`employee/`、`position/`：系统管理页面，取决于新中台是否沿用菜单/角色/用户/岗位模型。

### `support`

支撑功能页面，按平台能力选择：

- 字典管理。
- 文件/日志/消息/定时任务/缓存/代码生成等。
- 帮助文档、反馈、变更日志等。

如果目标是“最小可复用中台脚手架”，可以只保留字典、文件、日志、消息等常用模块；代码生成、等保、缓存重载等可以作为可选模块。

### `business`

业务页面替换区。当前包含 OA、ERP、workitem 等业务域。对接其他中台时建议按新业务域重建：

```text
src/views/business/
|-- customer/
|-- order/
|-- settlement/
```

推荐页面组织方式：

```text
views/business/<module>/<entity>/
|-- <entity>-list.vue
|-- <entity>-detail.vue
|-- components/
    |-- <entity>-form-modal.vue
    |-- <entity>-operate-drawer.vue
```

模块内专用组件放当前页面目录下的 `components/`；跨模块复用才放 `src/components/business/` 或更上层公共目录。

## 21. 最小脚手架保留清单

建议作为“干净脚手架”的核心保留：

```text
src/
|-- api/base-model/
|-- api/system/login-api.ts
|-- api/support/dict-api.ts
|-- assets/images/logo/
|-- components/framework/
|-- components/support/dict-label/
|-- components/support/dict-select/
|-- components/support/file-upload/
|-- components/support/file-preview/
|-- components/support/table-operator/
|-- config/app-config.ts
|-- constants/index.ts
|-- constants/common-const.ts
|-- constants/layout-const.ts
|-- constants/local-storage-key-const.ts
|-- constants/regular-const.ts
|-- directives/privilege.ts
|-- i18n/
|-- layout/
|-- lib/
|-- plugins/
|-- router/
|-- store/
|-- theme/
|-- types/
|-- utils/
|-- views/system/login/
|-- views/system/home/
|-- views/system/40X/
```

按项目能力选择保留：

```text
src/
|-- api/system/department-api.ts
|-- api/system/employee-api.ts
|-- api/system/menu-api.ts
|-- api/system/role-api.ts
|-- api/system/position-api.ts
|-- api/support/file-api.ts
|-- api/support/table-column-api.ts
|-- components/system/
|-- components/support/data-tracer/
|-- views/system/menu/
|-- views/system/role/
|-- views/system/employee/
|-- views/system/position/
|-- views/support/
```

通常作为业务替换区：

```text
src/
|-- api/business/
|-- components/business/
|-- constants/business/
|-- views/business/
```

通常不建议进入脚手架：

```text
smart-admin-web-typescript/
|-- dist/
|-- node_modules/

src/assets/images/1024lab/
```

## 22. 对接新中台接口的推荐流程

1. 修改 `.env.*` 和 `src/lib/api-base-url.ts`，确认基础接口地址。
2. 修改 `src/lib/axios.ts`，适配 token 请求头、响应 code、错误结构、登录过期 code、下载响应。
3. 修改 `src/constants/local-storage-key-const.ts`，确认 token、用户信息、布局配置等本地存储 key。
4. 修改 `src/api/system/login-api.ts`，接入登录、退出、获取登录用户信息接口。
5. 在 `main.ts` 或登录后处理逻辑中适配用户信息、菜单、字典初始化流程。
6. 适配 `src/router/index.ts` 的 `buildRoutes` 入参结构，建议新增菜单适配函数，不要让后端字段名污染所有前端逻辑。
7. 适配 `src/store/modules/system/user.ts`，统一保存用户、菜单、权限点、页签和 keep-alive 信息。
8. 适配 `src/store/modules/system/dict.ts`、`src/plugins/dict-plugin.ts`、`src/components/support/dict-*`，确认字典 code、label、value 结构。
9. 替换 `src/assets/images/logo`、登录页资源、主题色和系统标题。
10. 清理 `api/business`、`views/business`、`constants/business`，按新中台业务域重建。
11. 每新增一个业务模块，按 `api -> constants -> router/menu -> views -> components` 的顺序接入。

## 23. 新业务模块模板

假设新增客户管理模块：

```text
src/
|-- api/business/customer/
|   |-- customer-api.ts
|-- constants/business/customer/
|   |-- customer-const.ts
|-- views/business/customer/
|   |-- customer-list.vue
|   |-- customer-detail.vue
|   |-- components/
|       |-- customer-form-modal.vue
```

如果客户选择器会被多个模块复用，再提升到：

```text
src/components/business/customer-select/
|-- index.vue
```

## 24. 抽取脚手架时的判断标准

可以进脚手架：

- 和具体业务无关，但很多后台都会用。
- 能通过配置或少量适配接入不同后端。
- 修改点集中在 `lib`、`plugins`、`store`、`router`、`config` 这些底座层。
- 例如：`dict-plugin.ts`、`privilege-plugin.ts`、`smart-enums-plugin.ts`、`axios.ts`、`table-auto-height.ts`、`smart-watermark.ts`、布局和菜单组件。

不建议进最小脚手架：

- 和当前业务实体强绑定。
- 名称、字段、接口、页面流程都难以复用。
- 删除后不影响系统登录、菜单、权限、字典、布局、列表等底座能力。
- 例如：当前 `views/business/oa/**`、`api/business/oa/**`、`constants/business/oa/**`。

需要按项目判断：

- `page-param-model.ts`：如果新中台分页协议和当前一致，保留；否则替换为新分页模型。
- `components/system/**`：如果新中台沿用部门/员工/岗位/菜单模型，保留；否则作为可选系统模块。
- `views/support/**`：如果新中台提供对应平台能力，保留；否则拆成可选包。

