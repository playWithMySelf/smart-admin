# Directory Structure

> 前端目录结构、文件命名、路由/API/常量组织规范。

---

## Project Layout

Web 管理端主工程：

```text
smart-admin-web-typescript/
|-- package.json
|-- vite.config.ts
|-- src/
```

技术栈以 `package.json` 为准：Vue 3、Vite 5、TypeScript、Ant Design Vue、Pinia、Vue Router、Axios、Less。

---

## Src Layout

`src` 目录职责：

```text
src
|-- api             所有接口封装
|-- assets          静态资源，images、icons、styles 等
|-- components      公共组件
|-- config          应用默认配置
|-- constants       常量和枚举
|-- directives      自定义指令
|-- i18n            国际化
|-- layout          布局入口和布局公共组件
|-- lib             外部插件封装、项目级运行库
|-- plugins         全局插件
|-- router          路由
|-- store           Pinia 状态
|-- theme           主题与全局样式
|-- types           全局类型声明
|-- utils           工具函数
|-- views           页面视图
```

参考目录：

- `smart-admin-web-typescript/src/api/business/oa/enterprise-api.ts`
- `smart-admin-web-typescript/src/views/business/oa/enterprise/enterprise-list.vue`
- `smart-admin-web-typescript/src/components/framework/smart-enum-select/index.vue`
- `smart-admin-web-typescript/src/store/modules/system/user.ts`
- `smart-admin-web-typescript/src/layout/index.vue`

---

## Naming Conventions

- 项目名、目录名、文件名使用小写中划线：`smart-admin`、`enterprise-list.vue`、`role-form-modal.vue`。
- 静态资源文件使用 kebab-case：`background-color.png`、`upload-header.png`。
- API 文件以 `api` 结尾：`enterprise-api.ts`、`login-api.ts`。
- API 导出对象以 `Api` 结尾：`enterpriseApi`、`roleApi`。
- 常量文件以 `const` 结尾：`enterprise-const.ts`、`table-id-const.ts`。
- 常量变量使用大写下划线：`ENTERPRISE_TYPE_ENUM`、`PAGE_SIZE_OPTIONS`。
- 枚举常量以 `ENUM` 结尾。
- 页面文件按用途结尾：
  - 列表页：`*-list.vue`
  - 表单页：`*-form.vue`
  - 弹窗：`*-modal.vue`
  - 抽屉：`*-drawer.vue`

---

## Api Directory

API 文件必须：

- 从 `/@/lib/axios` 引入 `getRequest`、`postRequest`、`postDownload` 等封装。
- 导出一个对象，所有方法包在对象内。
- 方法注释和后端 OpenAPI 描述保持一致，并保留作者。
- 请求路径和后端 Controller 完整路径一致。

示例：`src/api/business/oa/enterprise-api.ts`

```ts
export const enterpriseApi = {
  // 新建企业 @author 开云
  create: (param) => postRequest('/oa/enterprise/create', param),
};
```

### Scenario: smart-app UniApp API Wrappers

#### 1. Scope / Trigger
- Trigger: 在 `smart-app` 新增业务 API 封装时，不能直接照搬 Web 端 `getRequest(url, params)` 写法。

#### 2. Signatures
- `smart-app/src/lib/smart-request.js`
  - `getRequest(url: string): Promise<ResponseDTO>`
  - `postRequest(url: string, data?: object): Promise<ResponseDTO>`
- `smart-admin-web-typescript/src/lib/axios.ts`
  - Web 端 `getRequest` 支持第二个参数对象，移动端当前不支持。

#### 3. Contracts
- 移动端 GET 接口的查询参数必须拼进 URL，例如 `/workitem/type/query?disabledFlag=false`。
- 移动端 POST 接口仍通过第二个参数传 JSON body。
- API 封装文件放在 `smart-app/src/api/<module>/**`，导出对象以 `Api` 结尾，例如 `workitemApi`。

#### 4. Validation & Error Matrix
- GET 参数误传为第二个参数 -> 参数不会进入请求，后端按空参数处理。
- 查询值包含中文或特殊字符 -> 必须 `encodeURIComponent`，避免 URL 截断或乱码。
- 后端返回非 `code === 1` -> `smart-request` 统一 toast 并 reject，页面只需 `smartSentry.captureError(err)`。

#### 5. Good/Base/Bad Cases
- Good: `getRequest('/workitem/item/list?keywords=' + encodeURIComponent(keywords))`
- Base: `postRequest('/workitem/daily/my/save', param)`
- Bad: `getRequest('/workitem/item/list', { keywords })`

#### 6. Tests Required
- H5 构建需覆盖新增移动端页面，确认 API 封装能被 Vite/UniApp 正常解析。
- 涉及 GET 查询参数时，至少检查生成 URL 包含预期 query key。

#### 7. Wrong vs Correct
Wrong:

```js
queryItemList(workItemTypeId, keywords) {
  return getRequest('/workitem/item/list', { workItemTypeId, keywords });
}
```

Correct:

```js
queryItemList(workItemTypeId, keywords) {
  return getRequest(`/workitem/item/list${buildQuery({ workItemTypeId, keywords })}`);
}
```

---

## Constants Directory

常量和枚举集中放在 `src/constants/**`。

示例：`src/constants/business/oa/enterprise-const.ts`

```ts
export const ENTERPRISE_TYPE_ENUM: SmartEnum<number> = {
  NORMAL: { value: 1, desc: '有限企业' },
  FOREIGN: { value: 2, desc: '外资企业' },
};
```

需要在模板中按枚举值展示中文时，使用 `$smartEnumPlugin.getDescByValue(...)`。

---

## Router Directory

- `router` 按 `views` 结构拆分，不要堆在单个巨大文件。
- `path` 使用 kebab-case，并以 `/` 开头，即使是 children 也写完整路径。
- `name` 使用组件名风格，并与组件缓存名保持一致，避免 keep-alive 失效。
- 页面跳转传参优先使用 `query`，例如：

```ts
router.push({ path: '/oa/enterprise/enterprise-detail', query: { enterpriseId } });
```

参考：`src/router/routers.ts`、`src/router/support/help-doc.ts`。

---

## Views Directory

`views` 按业务模块划分。一个业务模块中页面、弹窗、抽屉和模块内组件分开：

```text
views/business/oa/enterprise/
|-- enterprise-list.vue
|-- enterprise-detail.vue
|-- components/
|   |-- enterprise-operate-modal.vue
|   |-- enterprise-bank-list.vue
```

页面内的模块专用组件放在当前模块 `components` 目录；跨模块复用组件放到 `src/components`。

---

## Assets And Theme

- `assets` 存放图片、图标、样式等静态资源，文件名 kebab-case。
- `theme` 存放项目主题和全局样式。
- `src/theme/smart-admin.less` 中项目自有样式以 `smart` 开头，便于和 Ant Design Vue 样式区分。
- `src/theme/index.less` 是样式入口，当前项目选择导入 `ant-design-vue/dist/antd.css`，不要轻易改为 less 全量导入，避免 Vite 启动明显变慢。

---

## Common Mistakes

- API 文件没有 `api` 后缀，导出对象没有 `Api` 后缀。
- 页面文件用大驼峰或小驼峰命名。
- 路由 children path 不以 `/` 开头，导致搜索路径时需要多次拼接。
- 一个公共组件散落多个文件但没有独立目录和 `index.vue`。
- 新增业务常量直接写在页面里，未放入 `constants`。
