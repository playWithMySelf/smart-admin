# Error Handling

> SmartAdmin 后端错误返回、错误码、异常处理契约。

---

## Response Contract

所有业务接口返回统一使用 `ResponseDTO<T>`：

```java
public class ResponseDTO<T> {
    private Integer code;    // 0 成功，非 0 失败
    private String level;    // system / unexpected / user，成功为空
    private String msg;      // 返回消息
    private Boolean ok;      // 是否成功
    private T data;          // 业务数据
    private Integer dataType;
}
```

常用方法：

- `ResponseDTO.ok()`
- `ResponseDTO.ok(data)`
- `ResponseDTO.okMsg(msg)`
- `ResponseDTO.error(errorCode)`
- `ResponseDTO.error(errorCode, msg)`
- `ResponseDTO.errorData(errorCode, data)`
- `ResponseDTO.userErrorParam()`
- `ResponseDTO.userErrorParam(msg)`

参考：`sa-base/src/main/java/net/lab1024/sa/base/common/domain/ResponseDTO.java`

---

## Error Levels

错误码分三类：

| Level | 含义 | 适用场景 | 示例 |
|-------|------|----------|------|
| `system` | 系统错误 | 核心配置缺失、菜单等系统核心数据异常、未捕获异常 | `SystemErrorCode.SYSTEM_ERROR` |
| `unexpected` | 未预期业务异常 | 业务状态不应发生但已发生，需要技术排查 | `UnexpectedErrorCode` |
| `user` | 用户级错误 | 参数错误、权限不足、数据不存在、重复提交、登录失效 | `UserErrorCode.PARAM_ERROR` |

用户操作或表单校验失败，不要返回系统错误；系统核心功能损坏，不要包装成普通用户错误。

参考：

- `sa-base/src/main/java/net/lab1024/sa/base/common/code/ErrorCode.java`
- `sa-base/src/main/java/net/lab1024/sa/base/common/code/SystemErrorCode.java`
- `sa-base/src/main/java/net/lab1024/sa/base/common/code/UnexpectedErrorCode.java`
- `sa-base/src/main/java/net/lab1024/sa/base/common/code/UserErrorCode.java`

---

## Error Code Registration

新增业务错误码时：

- 业务错误码枚举实现 `ErrorCode`。
- 为错误码分配明确范围，避免和已有错误码冲突。
- 在 `ErrorCodeRegister` 注册范围。
- 启动时通过 `ErrorCodeRegister.initialize()` 校验重复、越界和注册合法性。

错误码必须大于 `10000`，并按系统、未预期、用户或业务域范围维护。

参考：

- `sa-base/src/main/java/net/lab1024/sa/base/common/code/ErrorCodeRegister.java`
- `sa-base/src/main/java/net/lab1024/sa/base/common/code/ErrorCodeRangeContainer.java`

---

## Global Exception Handling

全局异常在 `GlobalExceptionHandler` 统一处理：

- JSON 格式错误、参数绑定错误返回 `UserErrorCode.PARAM_ERROR`。
- Sa-Token 权限异常返回 `UserErrorCode.NO_PERMISSION`；非生产环境可以带具体消息，生产环境隐藏细节。
- `BusinessException` 当前按系统错误返回。
- 兜底 `Throwable` 记录错误日志；生产环境不把异常详情返回给前端，非生产环境可返回 `e.toString()` 便于排查。

参考：`sa-base/src/main/java/net/lab1024/sa/base/handler/GlobalExceptionHandler.java`

---

## Service Return Pattern

Service 业务校验失败时直接返回 `ResponseDTO`，不要抛异常作为常规控制流：

```java
if (Objects.isNull(enterpriseDetail) || enterpriseDetail.getDeletedFlag()) {
    return ResponseDTO.userErrorParam("企业不存在");
}
```

成功返回：

```java
return ResponseDTO.ok();
return ResponseDTO.ok(pageResult);
```

参考：`EnterpriseService#createEnterprise`、`EnterpriseService#updateEnterprise`。

---

## Validation & Error Matrix

| 条件 | 返回 |
|------|------|
| 请求体 JSON 缺失或格式错误 | `ResponseDTO.error(UserErrorCode.PARAM_ERROR, "参数JSON格式错误")` |
| `@Valid` 参数校验失败 | `ResponseDTO.error(UserErrorCode.PARAM_ERROR, 校验消息)` |
| 用户未登录或登录失效 | `UserErrorCode.LOGIN_STATE_INVALID` |
| 无权限 | `UserErrorCode.NO_PERMISSION` |
| 用户输入导致数据不存在 | `ResponseDTO.userErrorParam("xxx不存在")` 或 `UserErrorCode.DATA_NOT_EXIST` |
| 系统核心数据异常 | `SystemErrorCode.SYSTEM_ERROR` |
| 重复提交 | `UserErrorCode.REPEAT_SUBMIT` |

---

## Wrong vs Correct

### Wrong

```java
return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "商品不存在");
```

普通业务数据不存在是用户级错误，不应标成系统错误。

### Correct

```java
return ResponseDTO.userErrorParam("商品不存在");
```

---

## Common Mistakes

- 直接返回字符串、Map 或裸对象，绕过 `ResponseDTO`。
- 在 Controller 中 try/catch 所有异常并吞掉日志。
- 用户错误、未预期错误、系统错误混用。
- 生产环境把堆栈、SQL、敏感路径返回给前端。
- 新增业务错误码但未注册范围，导致启动校验无法覆盖。
