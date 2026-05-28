# Quality Guidelines

> 前端质量、命名、注释、错误处理和检查规范。

---

## Formatting Baseline

官方文档要求：

- HTML 和 Vue template 标签属性使用双引号。
- JS/TS 字符串使用单引号。
- JS/TS 语句换行使用分号。
- 目录和文件名使用小写中划线。

项目依赖 ESLint、Prettier、Stylelint，具体版本见 `package.json`。

---

## Required Patterns

- 所有新 Vue 组件使用 `<script setup lang="ts">`。
- Composition API 按业务块组织，每块变量和方法放在一起。
- 模板表达式保持简单。
- API 调用统一走 `src/api/**` 和 `src/lib/axios.ts`。
- 异步请求中页面级错误捕获使用 `smartSentry.captureError(e)`。
- 全局 loading 使用 `SmartLoading.show()` / `SmartLoading.hide()`，局部表格使用 `tableLoading`。
- 权限控制使用 `v-privilege` 和 `$privilege`，不要手写散落的权限判断。
- 常量、枚举、默认配置放到对应目录，不在页面写魔法值。

参考：`src/views/business/oa/enterprise/enterprise-list.vue`。

---

## Forbidden Patterns

- 页面、API、路由使用不同业务命名。
- 在 template 中写复杂表达式或大量计算。
- 复制后不改注释、作者、接口说明。
- 直接使用 axios 而不是项目封装请求。
- 页面内硬编码枚举中文。
- 把公共组件放成散文件，不建独立目录。
- 将布局多形态强行抽成一个复杂巨组件。
- 无用代码注释保留；应该删除。

---

## Error Handling

页面异步请求标准形态：

```ts
try {
  SmartLoading.show();
  await api.action(param);
  message.success('操作成功');
} catch (e) {
  smartSentry.captureError(e);
} finally {
  SmartLoading.hide();
}
```

表格查询可使用局部 loading：

```ts
try {
  tableLoading.value = true;
  const responseModel = await enterpriseApi.pageQuery(queryForm);
  tableData.value = responseModel.data.list;
} catch (e) {
  smartSentry.captureError(e);
} finally {
  tableLoading.value = false;
}
```

---

## Code Review Checklist

- [ ] 文件名、目录名、路由 path、API 文件、常量文件符合命名规则。
- [ ] 新页面或组件使用 `<script setup lang="ts">`。
- [ ] 相关变量和方法按业务块组织。
- [ ] API 注释与后端接口描述和作者一致。
- [ ] 新增枚举同步后端、数据库注释和前端常量。
- [ ] 异步操作有 loading 和异常处理。
- [ ] 权限点使用 `v-privilege` / `$privilege`。
- [ ] 无调试输出、无无用注释代码、无本地配置提交。

---

## Verification Commands

在 `smart-admin-web-typescript` 下按变更范围运行：

```bash
npm run dev
npm run build:prod
```

如果仅修改 `.trellis/spec`，不需要运行前端构建；但需要检查 spec 无占位符。
