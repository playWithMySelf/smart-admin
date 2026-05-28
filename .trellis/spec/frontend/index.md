# Frontend Development Guidelines

> SmartAdmin 前端开发规范。来源包括 `docs/SmartAdmin开发文档.md` 以及当前仓库 `smart-admin-web-typescript` 的真实代码。

---

## Overview

前端主工程位于 `smart-admin-web-typescript`，采用 Vue 3、Vite 5、TypeScript、Ant Design Vue 4、Pinia、Vue Router、Axios、Less。仓库另有 `smart-app` UniApp 移动端，本目录规范优先覆盖管理端 Web。

开发原则：

- Vue 单文件组件统一使用 `<script setup lang="ts">`。
- 目录、文件、路由 path 使用小写和中划线。
- 同一业务命名前后端统一，例如企业统一 `enterprise`，通知统一 `notice`。
- 页面逻辑按 Composition API 的业务关注点分块，不按 Vue2 的 data/methods 思维堆叠。

---

## Pre-Development Checklist

- [ ] 确认新代码属于 `api`、`views`、`components`、`constants`、`store`、`router`、`lib` 还是 `utils`。
- [ ] 新增页面时同步检查后端接口路径、前端 API 文件、路由 path/name、权限点和菜单配置命名。
- [ ] 新增枚举或常量时，优先放入 `src/constants/**`，需要模板展示时接入 `smart-enums-plugin`。
- [ ] 新增公共组件时放入独立目录并使用 `index.vue`。
- [ ] 涉及 API 调用时，使用 `src/lib/axios.ts` 封装方法，错误捕获交给 `smartSentry.captureError` 或全局 axios 处理。

---

## Guidelines Index

| Guide | Description | Status |
|-------|-------------|--------|
| [Directory Structure](./directory-structure.md) | Web 目录、API、assets、constants、router/store、views 命名 | Filled |
| [Component Guidelines](./component-guidelines.md) | SFC、Composition API、组件命名、模板表达式、样式 | Filled |
| [Hook Guidelines](./hook-guidelines.md) | 可复用组合逻辑、lib/utils、业务分块约定 | Filled |
| [State Management](./state-management.md) | Pinia、默认配置、全局状态边界 | Filled |
| [Quality Guidelines](./quality-guidelines.md) | 前端质量、注释、错误处理、提交前检查 | Filled |
| [Type Safety](./type-safety.md) | TypeScript、类型位置、API 响应、枚举类型 | Filled |
| [Built-in Components](./built-in-components.md) | SmartLoading、枚举、字典、文件、表格列、布局主题等内置能力 | Filled |

---

## Quality Check

- [ ] 没有模板占位文本。
- [ ] 新页面文件名、路由 path、API 文件、常量文件均符合命名规则。
- [ ] `<script setup lang="ts">` 中相关变量和方法按业务块放在一起。
- [ ] 模板表达式保持简单，复杂计算移入 `computed` 或函数。
- [ ] API 注释和后端 OpenAPI 描述、作者一致。
