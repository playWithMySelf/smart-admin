# Built-in Components

> SmartAdmin 前端内置组件、布局、主题和项目默认配置规范。

---

## SmartLoading

全局 Loading 组件位于 `src/components/framework/smart-loading`。

使用规则：

- 全局长操作使用 `SmartLoading.show()` 和 `SmartLoading.hide()`。
- 必须放在 `try/finally` 中，确保异常时也关闭。
- 表格查询优先使用局部 `tableLoading`，不要所有查询都打开全局遮罩。

```ts
try {
  SmartLoading.show();
  await enterpriseApi.delete(enterpriseId);
} catch (e) {
  smartSentry.captureError(e);
} finally {
  SmartLoading.hide();
}
```

---

## Enum Components

枚举组件位于 `src/components/framework`：

- `smart-enum-select`
- `smart-enum-radio`
- `smart-enum-checkbox`

使用前在 `src/constants/**` 定义 `SmartEnum<T>`，模板中传 `enum-name`：

```vue
<SmartEnumSelect enum-name="GOODS_STATUS_ENUM" v-model:value="queryForm.goodsStatus" width="160px" />
```

展示枚举值时使用：

```vue
{{ $smartEnumPlugin.getDescByValue('ENTERPRISE_TYPE_ENUM', text) }}
```

---

## Dict Components

字典下拉框位于 `src/components/support/dict-select`。需要后端字典支持时优先使用字典组件，不要在页面硬编码字典项。

相关能力：

- `src/store/modules/system/dict.ts`
- `src/plugins/dict-plugin.ts`
- `src/components/support/dict-label`

---

## File Components

文件组件：

- `src/components/support/file-upload`
- `src/components/support/file-preview`
- `src/components/support/file-preview-modal`

后端文件字段通常通过 FileKey 序列化/反序列化和前端交换 `fileVO` 数组。前端不要自己拼接云存储 URL；通过后端返回和文件组件展示。

---

## Table Operator

用户自定义表格列使用 `TableOperator`：

```vue
<TableOperator v-model="columns" :tableId="TABLE_ID_CONST.BUSINESS.OA.ENTERPRISE" :refresh="ajaxQuery" />
```

规则：

- 每个业务表格需要稳定唯一的 `tableId`，定义在 `TABLE_ID_CONST`。
- 表格列数组放在页面业务块中，字段 `dataIndex` 与后端 VO 字段一致。
- 自定义列操作后通过 `refresh` 重新查询。

参考：`src/views/business/oa/enterprise/enterprise-list.vue`。

---

## Layout

布局目录：

```text
src/layout/
|-- index.vue
|-- side-layout.vue
|-- side-expand-layout.vue
|-- top-layout.vue
|-- components/
```

设计原则：

- 每种布局一个入口组件。
- 公共头部、菜单、页签、帮助文档等放 `layout/components`。
- 少量重复优先于过度抽象，保持布局文件直接可读。
- 当前布局由 `useAppConfigStore().$state.layout` 决定。

---

## Theme

主题文件：

- `src/theme/smart-admin.less`：项目自有样式，类名以 `smart` 开头。
- `src/theme/index.less`：全局样式入口。
- `src/theme/color.ts`、`custom-variables.ts`：主题颜色和变量。

项目选择导入 `ant-design-vue/dist/antd.css`，原因是中后台页面复杂、组件使用广，按需样式收益小；导入 less 会显著影响 Vite 启动速度。不要轻易改动此决策。

---

## App Default Config

默认配置在 `src/config/app-config.ts`，由 `app-config` store 初始化。

常见字段：

- `language`
- `layout`
- `sideMenuWidth`
- `sideMenuTheme`
- `pageWidth`
- `pageTagFlag`
- `breadCrumbFlag`
- `footerFlag`
- `helpDocFlag`
- `watermarkFlag`
- `websiteName`

修改项目默认体验时先改配置，不要在多个布局组件中写死。

---

## Watermark

水印能力由 `src/lib/smart-watermark.ts` 提供，布局挂载时根据当前用户设置。涉及页面内容区域 id 或布局结构时，确认水印目标仍然正确。

参考：`src/layout/side-layout.vue`。

---

## Common Mistakes

- 全局 loading 打开后异常分支没关闭。
- 枚举组件使用了不存在的 `enum-name`。
- 表格 `tableId` 复用，导致用户列配置串表。
- 文件上传后前端直接拼 URL，绕过后端私有文件缓存和签名逻辑。
- 修改布局时破坏 keep-alive、iframe 或水印目标。
