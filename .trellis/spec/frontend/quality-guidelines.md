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

## Common Mistakes

- 在 `mescroll-body` 页面里既保留 `up.auto` 的默认自动加载，又在 `onShow` 手动触发列表刷新，导致首屏重复请求和重复数据。
- 页面从 `onShow` 刷新后没有把列表滚回顶部，用户会误以为内容没有变化。
- `up.auto=false` 后只在 `onShow` 中直接调接口；如果 `mescroll` 的 `@init` 晚于 `onShow`，首屏会空白到用户下拉才加载。
- `mescroll-body` 内部的吸顶搜索区只写 `position: sticky` 可能不生效；组件默认 `overflow: hidden` 会限制 sticky，需要在 `mescroll-body` 上同步开启 `sticky` prop。
- UniApp 小程序表单页在 `onShow` 中无条件重新加载详情；用户调用 `uni.chooseImage`、文件选择器或从预览返回页面时，可能触发 `onShow` 并覆盖未保存的工作项、备注、附件等本地编辑态。
- UniApp 小程序图片缩略图只在模拟器可见、真机不可见；真机 `<image>` 对远程地址的域名、证书、重定向、鉴权限制更严格，`uni.previewImage` 能打开不代表缩略图能直接绑定同一个 `fileUrl`。
- UniApp 微信小程序体验版不应依赖 H5 `<img>` 或 `data:image/*;base64,...` 直接展示验证码图片；开发者工具可能能显示，但体验版真机更严格，容易空白。
- UniApp 微信小程序体验版不应在 WXSS 中用 `url(...)` 引用本地图片；开发者工具可能能显示，上传或体验版会报“本地资源图片无法通过 WXSS 获取”。
- UniApp 小程序登录成功后不要只依赖 `uni.getStorageSync(tokenKey)` 做首页守卫；刚登录后的运行时状态应优先读 Pinia 内存 token，并在跳首页前用 `/login/getLoginInfo` 做一次登录态自检，避免首页闪回登录页时无法定位是 token 未写入还是后端未识别请求头。

**Fix**: 在需要手动刷新首屏的页面里，显式写 `:up="{ auto: false }"`，`onShow` 只保留一次刷新入口；如果 `getMescroll()` 为空，记录 pending 并在 `@init` 后补触发。刷新应调用 `mescroll.resetUpScroll()` 重置内部页码，`onUp` 在 `mescroll.num === 1` 时覆盖列表、后续页追加，并在必要时调用 `uni.pageScrollTo({ scrollTop: 0 })`。如果 `mescroll-body` 内部有 `position: sticky` 的搜索区或分类区，模板应写成 `<mescroll-body sticky ...>`，让组件取消默认 overflow 限制。

**Fix**: 对编辑态表单页，优先在 `onLoad` 或带初始化标记的 `onShow` 中做首次加载；日期切换、刷新按钮、保存成功、提交成功等用户明确操作再显式重载详情。文件上传返回对象需要标准化展示字段，例如补齐 `url/name` 或使用统一函数从 `fileUrl/url/tempFilePath` 中取预览地址。真机缩略图若由小程序域名、证书、重定向或鉴权限制导致，应优先修正合法域名配置或后端文件 URL 生成策略；不要在业务页面里默认加入下载/本地缓存绕过，除非需求明确接受这层复杂度。

**Fix**: 对微信小程序验证码这类后端返回 base64 的图片，模板使用原生 `<image>`；在 `MP-WEIXIN` 条件编译中用 `wx.base64ToArrayBuffer` 和 `wx.getFileSystemManager().writeFile` 写入 `wx.env.USER_DATA_PATH`，再把本地文件路径绑定给 `<image>`。同时兼容图片真实格式和 data URL MIME 不一致的情况，必要时通过文件头识别 `jpg/png`。

**Fix**: 对登录页、我的页等装饰背景，模板中放 `<image mode="aspectFill">` 并用绝对定位放在底层；不要写 `background-image: url('@/static/...')`。输入框图标选择器要使用 `.input-icon` 这类专用 class，避免 `.input-view image` 覆盖验证码 `<image>` 的宽高。

**Fix**: 登录成功后先 `setUserLoginInfo(res.data)`，再 `await userStore.getLoginInfo()`；成功后才 `switchTab`。`getToken` 和请求头 token 读取应优先使用 Pinia state，再回退到 `uni.getStorageSync(USER_TOKEN)`。登录失效跳转使用 `uni.reLaunch({ url: '/pages/login/login' })`，不要在 tabBar 页上 `navigateTo` 叠登录页。

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
