# uni-app 微信小程序包体积与按需组件配置

## 来源

`ctx7` 查询 `/dcloudio/unidocs-zh`，主题为微信小程序主包体积、分包优化、组件按需注册。

## 结论

uni-app 官方文档给出的微信小程序优化配置如下：

```json
"mp-weixin": {
  "optimization": {
    "subPackages": true
  },
  "lazyCodeLoading": "requiredComponents"
}
```

其中：

* `optimization.subPackages` 用于开启分包优化，配置名必须是 `subPackages`。
* `lazyCodeLoading: "requiredComponents"` 用于开启微信小程序按需注入特性，对应微信小程序上传提示中的组件按需注册/按需注入要求。
* 分包优化会让仅被某个分包引用的 JS 和分包目录内静态资源进入对应分包，减少主包压力。

## 对本项目的影响

`smart-app/src/pages.json` 当前没有 `subPackages`，所有页面都进入主包。可保留 TabBar 页面与登录页在主包，其余业务页按目录拆到分包，再通过构建产物验证主包体积。
