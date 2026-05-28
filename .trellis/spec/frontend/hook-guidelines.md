# Hook Guidelines

> Vue 组合逻辑、可复用函数、lib/utils 边界规范。

---

## Local First, Extract When Reused

SmartAdmin 官方 Vue3 规范强调“同一逻辑关注点的变量和方法放在一起”。默认先在组件内按业务块组织；当同一块逻辑在多个组件重复出现，或本身是框架级能力时，再抽到 `lib`、`utils` 或组件目录。

---

## Where Reusable Logic Lives

| 类型 | 位置 | 示例 |
|------|------|------|
| 项目级运行库、复杂浏览器能力 | `src/lib` | `smart-watermark.ts`、`smart-keep-alive.ts`、`axios.ts` |
| 通用纯工具函数 | `src/utils` | `str-util.ts`、`local-util.ts` |
| 全局状态逻辑 | `src/store/modules/**` | `user.ts`、`app-config.ts` |
| 组件内私有逻辑 | 当前 `.vue` 文件业务块 | `enterprise-list.vue` 查询、删除、导出 |
| 模块内组件共享逻辑 | 当前模块 `components` 或局部文件 | 企业模块弹窗/列表组件 |

---

## Naming

项目当前不强制所有组合函数使用 `use*` 命名，已有项目级逻辑有 `smartKeepAlive()` 等风格。新增时按现有目录风格：

- 和 Vue/Pinia 生态习惯强相关的组合函数可使用 `useXxx`。
- SmartAdmin 平台能力可使用 `smartXxx`。
- 纯工具函数使用动词短语，例如 `formatXxx`、`parseXxx`、`splitConvertToList`。

不要为了“hook 化”把只在一个组件使用的简单变量和方法抽出去。

---

## Data Fetching

数据请求不通过 React Query/SWR 这类外部缓存库；当前模式是：

1. `src/api/**` 封装接口。
2. 组件内 `async function ajaxQuery()` 调用 API。
3. `try/catch/finally` 控制 loading。
4. 异常交给 `smartSentry.captureError(e)`。

示例：`enterprise-list.vue#ajaxQuery`。

```ts
async function ajaxQuery() {
  try {
    tableLoading.value = true;
    const responseModel = await enterpriseApi.pageQuery(queryForm);
    tableData.value = responseModel.data.list;
  } catch (e) {
    smartSentry.captureError(e);
  } finally {
    tableLoading.value = false;
  }
}
```

---

## Extraction Rules

抽取组合逻辑前检查：

- 是否被 2 个以上组件复用。
- 是否可以用参数描述差异，而不是读写组件内部过多状态。
- 是否仍然保持类型清晰。
- 是否会让调用方比原组件内代码更难读。

如果抽取后需要在多个文件来回跳才能理解一个简单表单，保留在组件内。

---

## Common Mistakes

- 把每个页面的查询逻辑都抽成通用 hook，最后参数爆炸。
- 在 `utils` 里放带 UI、路由、store 副作用的函数。
- 在组件内复制大段异步逻辑却不抽公共 API、常量或错误处理。
- 抽取后丢失业务块注释，导致组件看不出数据流。
