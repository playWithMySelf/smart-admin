# Logging Guidelines

> 后端日志规范。当前代码使用 Lombok `@Slf4j` + Spring Boot 日志体系。

---

## Logger Pattern

业务类、配置类、切面、任务执行类需要日志时使用：

```java
@Slf4j
public class EnterpriseService {
}
```

不要手写重复的 `LoggerFactory.getLogger(...)`，除非已有类历史风格如此。

参考：

- `sa-base/src/main/java/net/lab1024/sa/base/handler/GlobalExceptionHandler.java`
- `sa-base/src/main/java/net/lab1024/sa/base/module/support/job/core/SmartJobExecutor.java`
- `sa-admin/src/main/java/net/lab1024/sa/admin/module/business/oa/enterprise/controller/EnterpriseController.java`

---

## Log Levels

- `info`：系统启动、支撑模块初始化、任务执行完成、关键配置数量等正常生命周期事件。
- `warn`：配置不完整但系统可降级继续运行，例如 DataScope 自定义策略 Bean 缺失。
- `error`：全局异常、任务执行异常、需要技术排查的失败。
- `debug`：默认少用；仅用于本地排查，避免提交大量调试日志。

---

## What To Log

- 全局异常必须记录当前请求 URL。
- SmartJob 执行日志要包含任务名称、耗时、结果或异常，方便管理端查询。
- ErrorCode 初始化可记录错误码数量。
- DataScope、SmartReload 等动态能力在配置缺失时记录足够的定位信息。
- 非生产环境可记录更多异常详情，生产环境前端响应必须隐藏细节，但服务端仍要保留错误日志。

参考：

- `GlobalExceptionHandler#errorHandler`
- `GlobalExceptionHandler#jsonFormatExceptionHandler`
- `docs/SmartAdmin开发文档.md` 中 SmartJob 启动和执行日志说明。

---

## What Not To Log

- 不要记录明文密码、万能密码 token、AccessKey、SecretKey、Sa-Token token。
- 文件上传配置中的 `access-key`、`secret-key` 不能出现在日志。
- 不要在生产接口响应中返回异常堆栈；日志可记录堆栈，响应只给通用错误消息。
- 不要长期保留 `console` 式临时输出或无上下文空日志。

---

## Environment-Aware Logging

`SystemEnvironment` 用于区分环境：

```java
if (!systemEnvironment.isProd()) {
    log.error("全局JSON格式错误异常,URL:{}", getCurrentRequestUrl(), e);
}
```

生产环境响应隐藏异常详情：

```java
return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, systemEnvironment.isProd() ? null : e.toString());
```

参考：`sa-base/src/main/java/net/lab1024/sa/base/common/domain/SystemEnvironment.java`

---

## Common Mistakes

- 日志只有异常对象，没有 URL、业务 id 或任务名，后续无法定位。
- 在业务失败分支既不返回明确错误码，也不写任何可追踪日志。
- 将密钥、token、文件临时 URL、用户敏感数据写入 info 日志。
- 为了调试把错误细节返回给生产前端。
