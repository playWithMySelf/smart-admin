# Type Safety

> TypeScript、类型组织、枚举类型和 API 响应类型规范。

---

## TypeScript Baseline

Web 工程使用 TypeScript 5.x，Vue 文件统一 `<script setup lang="ts">`。新增管理端代码默认使用 TS，不回退到纯 JS。

参考：`smart-admin-web-typescript/package.json`、`src/vite-env.d.ts`。

---

## Type Locations

| 类型 | 位置 | 示例 |
|------|------|------|
| 全局声明 | `src/types/*.d.ts` | `smart-enum.d.ts`、`user.d.ts`、`env.d.ts` |
| API 基础模型 | `src/api/base-model/*.ts` | `response-model.ts`、`page-result-model.ts` |
| 常量/枚举类型 | `src/constants/**/*.ts` | `ENTERPRISE_TYPE_ENUM: SmartEnum<number>` |
| 组件局部类型 | 当前组件或相邻文件 | 表格列、表单临时类型 |
| Store 类型 | 对应 store module | 用户、配置、字典状态 |

公共类型优先放到 `types` 或 `api/base-model`；只在单文件使用的临时类型留在组件内。

---

## API Response Shape

后端统一返回 `ResponseDTO<T>`，前端基础响应类型应与后端保持：

- `code`
- `level`
- `msg`
- `ok`
- `data`
- `dataType`

分页接口返回 `PageResult<T>`，页面读取 `responseModel.data.list`、`responseModel.data.total`。

参考：

- `src/api/base-model/response-model.ts`
- `src/api/base-model/page-result-model.ts`
- `src/views/business/oa/enterprise/enterprise-list.vue`

---

## Enums

前端枚举使用 `SmartEnum<T>`：

```ts
export const ENTERPRISE_TYPE_ENUM: SmartEnum<number> = {
  NORMAL: { value: 1, desc: '有限企业' },
};
```

规则：

- 枚举变量名大写下划线，且以 `ENUM` 结尾。
- 枚举值类型与后端字段类型一致，常见为 `number`。
- 后端新增或修改枚举时，同步数据库注释、Java 枚举、前端 `constants`。
- 模板展示优先使用 `$smartEnumPlugin.getDescByValue`，不要手写魔法数字判断。

---

## Validation

前端主要依赖 Ant Design Vue 表单规则做交互层校验；后端仍以 `@Valid` 作为最终校验。不要把前端校验当作安全边界。

表单对象常见模式：

```ts
const formDefault = { ... };
const form = reactive({ ...formDefault });
const rules = { ... };
```

重置时使用 `Object.assign(form, formDefault)`，避免替换 reactive 引用。

---

## Forbidden Patterns

- 新增 `.vue` 使用无类型 `<script setup>`。
- 常量枚举不加 `SmartEnum<T>` 类型。
- API 响应用 `any` 到处传递，导致页面不知道 `data` 结构。
- 表单重置直接 `form = { ... }` 破坏响应式。
- 前端和后端对同一字段类型不一致，例如后端 Boolean 前端当 number。

---

## Good/Base/Bad Cases

- Good：`ENTERPRISE_TYPE_ENUM: SmartEnum<number>` 与后端 `EnterpriseTypeEnum`、数据库注释同步。
- Base：页面内部临时表格列直接在组件内声明，保持简单。
- Bad：模板里写 `type === 1 ? '有限企业' : '外资企业'`，绕过枚举插件。
