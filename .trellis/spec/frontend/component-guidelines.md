# Component Guidelines

> Vue3 单文件组件、Composition API、Props、模板和样式规范。

---

## SFC Structure

单文件组件标签顺序固定：

```vue
<template>...</template>
<script setup lang="ts">...</script>
<style scoped lang="less">...</style>
```

当前项目大量组件使用 `<script setup lang="ts">`，新增 Web 代码必须保持一致。

参考：

- `src/views/business/oa/enterprise/enterprise-list.vue`
- `src/components/framework/smart-enum-select/index.vue`
- `src/layout/side-layout.vue`

---

## Composition API Organization

`<script setup>` 内代码按业务关注点分块，而不是按“所有变量一块、所有方法一块”的 Vue2 思维组织。

推荐结构：

```ts
import ...

// --------------------------- 企业表格 列 ---------------------------
const columns = ref([...]);

// --------------------------- 查询 ---------------------------
const queryFormState = { ... };
const queryForm = reactive({ ...queryFormState });

function onSearch() { ... }
async function ajaxQuery() { ... }

// --------------------------- 删除 ---------------------------
function confirmDelete(id) { ... }
async function del(id) { ... }
```

参考：`enterprise-list.vue` 中“企业表格列 / 查询 / 导出 / 删除 / 增加、修改、详情”的分块。

---

## Comments

Composition API 中变量和方法会交错出现，因此注释必须帮读者定位业务块：

- 业务块用分隔注释。
- 关键变量需要说明含义。
- 方法需要说明用途，尤其是异步请求、弹窗暴露方法、复杂表单逻辑。
- 修改复制代码时必须同步更新注释。

不要写没有信息量的注释，例如“定义变量”“调用方法”。

---

## Template Refs

模板引用变量统一以 `Ref` 结尾：

```vue
<EnterpriseOperate ref="operateRef" />
```

```ts
const operateRef = ref();
```

如果模板中是 `inputRef`，脚本中也必须是 `inputRef`。

---

## Props And Emits

- Props 多时主动换行，不把一长串属性写在一行。
- `defineProps` 结果命名为 `props`，需要暴露给父组件时使用 `defineExpose`。
- 事件使用 `defineEmits`，事件名应表达业务含义，如 `refresh`、`reloadList`。
- 弹窗/抽屉组件常见模式：父组件 `ref` 调用子组件 `showModal(...)`，子组件成功后 emit 刷新事件。

参考：

- `src/views/business/oa/enterprise/components/enterprise-operate-modal.vue`
- `src/components/support/table-operator/index.vue`

---

## Template Expressions

模板中只放简单表达式。复杂计算移动到 `computed` 或函数中。

允许：

```vue
<span>{{ $smartEnumPlugin.getDescByValue('ENTERPRISE_TYPE_ENUM', text) }}</span>
```

不建议：

```vue
{{ fullName.split(' ').map(...).join(' ') }}
```

复杂表达式会降低声明式模板可读性，也无法复用。

---

## Component Naming

- 组件文件名使用 kebab-case：`enterprise-operate-modal.vue`。
- 与父组件强耦合的子组件以前缀命名：`todo-list.vue`、`todo-list-item.vue`、`todo-list-item-button.vue`。
- 公共组件一个组件一个目录，入口 `index.vue`：

```text
components/framework/smart-enum-select/index.vue
components/support/file-upload/index.vue
```

---

## Styling

- 全局项目样式写在 `src/theme/smart-admin.less`，类名以 `smart` 开头。
- 组件私有样式优先使用当前组件 `<style scoped lang="less">`。
- 不要在业务页面随意覆盖 Ant Design Vue 全局类；确需全局规范时放到 theme。
- 查询表单、表格按钮等通用样式优先复用 `smart-query-form`、`smart-table-operate`、`smart-query-table-page` 等现有类。

---

## Common Mistakes

- `<script>` 放在 `<style>` 后面。
- 新组件没有使用 `<script setup lang="ts">`。
- 按 data/methods 思维把所有变量和所有函数分开放，导致一个业务逻辑上下滚动查找。
- 模板中写复杂表达式。
- 公共组件直接放一个散文件，没有独立目录和 `index.vue`。
