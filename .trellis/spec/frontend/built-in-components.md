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

### Scenario: 图片上传前压缩

#### 1. Scope / Trigger
- Trigger: 业务上传手机相册照片、日报佐证、反馈图片等用户拍摄图片，且只需要页面查看级清晰度。
- Web 管理端优先在 `src/components/support/file-upload` 的上传前流程中开启图片压缩；不要绕过通用文件上传接口。
- `smart-app` UniApp 端优先压缩 `uni.chooseImage` 返回的临时路径，再传给 `uni.uploadFile` 封装。

#### 2. Signatures
- Web: `compressImageFileBeforeUpload(file: File, options?: ImageCompressOptions): Promise<File>`
- UniApp: `compressImagePathBeforeUpload(filePath: string, options?: object): Promise<string>`

#### 3. Contracts
- 只压缩图片文件，非图片保持原上传流程。
- 小图片默认跳过压缩，避免重复损耗。
- 压缩失败时记录错误并回退原文件或原路径，不能阻断用户上传。
- 后端上传接口和返回的 `fileVO` 结构不变。

#### 4. Validation & Error Matrix
- 原图超过允许选择大小 -> 前端提示并阻止选择。
- 压缩后仍超过业务上传大小 -> 前端提示并阻止上传。
- 浏览器或平台不支持压缩 API -> 回退原文件或原路径上传。
- 压缩 API 抛错 -> `smartSentry.captureError` 记录，回退原文件或原路径上传。

#### 5. Good/Base/Bad Cases
- Good: 日报佐证图片在 `Upload` 组件上显式开启压缩，只影响该业务上传。
- Base: 普通附件上传不传压缩开关，沿用原有大小限制和上传流程。
- Bad: 页面内直接新写一套上传接口，压缩后自己拼接文件 URL。

#### 6. Tests Required
- Web 管理端至少运行对应构建，确认压缩依赖和上传组件能被 Vite 正常打包。
- UniApp 修改图片上传路径时至少运行 H5 构建；涉及 App / 小程序能力时还需真机或开发者工具验证 `uni.compressImage`。

#### 7. Wrong vs Correct
Wrong:

```ts
formData.append('file', rawFile);
```

Correct:

```ts
const uploadFile = await compressImageFileBeforeUpload(rawFile);
formData.append('file', uploadFile);
```

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
