# State Management

> Pinia、局部状态、全局配置和服务器数据状态规范。

---

## Store Library

当前项目使用 Pinia。

参考：

- `src/store/index.ts`
- `src/store/modules/system/user.ts`
- `src/store/modules/system/app-config.ts`
- `src/store/modules/system/dict.ts`
- `src/store/modules/system/role.ts`

Store 文件按业务拆分，不能把所有状态堆到一个文件。

---

## State Categories

| 状态类型 | 放置位置 | 示例 |
|----------|----------|------|
| 组件局部状态 | 当前 `.vue` 的 `ref` / `reactive` | 查询表单、表格 loading、弹窗 visible |
| 页面 URL 状态 | Vue Router `query` | 详情页 `enterpriseId` |
| 全局用户/权限状态 | Pinia system store | `useUserStore`、`useRoleStore` |
| 全局应用配置 | `app-config` store | 布局、主题、页签、帮助文档、水印 |
| 字典/枚举缓存 | Pinia 或插件 | `useDictStore`、`smart-enums-plugin` |
| 服务器列表数据 | 组件内请求后保存 | 表格 `tableData`、`total` |

---

## App Default Config

前端默认配置在 `src/config/app-config.ts`，由 Pinia `app-config` 模块读取作为初始值。

常见配置：

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

修改默认行为时，优先改默认配置和对应 store，不要在布局组件中写死。

---

## When To Use Global State

使用 Pinia 的条件：

- 多个布局或页面需要共享。
- 状态需要跨路由保留。
- 状态来自登录用户、权限、菜单、字典、系统配置。
- 状态需要被插件或指令读取。

保持局部状态的条件：

- 只服务于一个页面或组件。
- 关闭页面后不需要保留。
- 如查询表单、分页、表格数据、弹窗显示状态。

---

## App Badges And Session Sync

### Convention: Unread Badge Follows User Store

**What**: 未读消息数量由 `useUserStore` 统一维护，并在数量变化时同步到 `uni.setTabBarBadge` / `uni.removeTabBarBadge`。

**Why**: 避免页面只更新本地数字、tabBar 角标却不变的分裂状态。

**Example**:
```js
// store action
this.unreadMessageCount = Number(result.data) || 0;
this.syncUnreadMessageBadge();
```

**Related**: 登录后、App 显示时、消息页刷新后都应重新拉取未读数。

---

## Layout State

Layout 有多种形态：side、side-expand、top 等。官方设计选择每种布局一个入口文件，公共组件放 `layout/components`，少量重复换可读性。

`layout/index.vue` 根据 `useAppConfigStore().$state.layout` 选择具体布局。不要把所有布局分支塞到一个巨大组件中。

参考：

- `src/layout/index.vue`
- `src/layout/side-layout.vue`
- `src/layout/top-layout.vue`
- `src/layout/components/`

---

## Server State

当前项目不使用前端服务器状态缓存库。列表页常用模式：

- `queryForm` 存查询条件。
- `tableLoading` 存 loading。
- `tableData` 和 `total` 存接口结果。
- 分页变化直接调用 `ajaxQuery`。

刷新策略由页面方法控制，例如新增/编辑弹窗成功后 emit `refresh` 并调用列表查询。

---

## Common Mistakes

- 把页面表格数据塞入全局 store。
- 在多个组件中各自维护一份用户、权限或菜单状态。
- 修改布局默认值时绕过 `app-config`。
- 新增 store 文件没有按业务拆分，或命名无法反映业务。
