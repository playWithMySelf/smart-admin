# Support Modules

> SmartAdmin 后端内置支撑能力：SmartJob、DataScope、文件上传、重复提交、数据变更记录、动态加载、工具类。

---

## SmartJob

SmartJob 是项目内轻量定时任务方案，目标是简单、好用、够用。它基于 Spring `ThreadPoolTaskScheduler`，支持 cron、fixedDelay、任务参数、动态启停、执行记录和分布式环境下不重复执行。

### Enable

配置开关：

```yaml
smart:
  job:
    enabled: true
    core-pool-size: 2
    db-refresh-enabled: true
    db-refresh-interval: 60
```

完整配置还包括 `init-delay`、`db-refresh-enabled`、`db-refresh-interval`。

### Implement A Job

新增任务类：

- 实现 `SmartJob`。
- 注册为 Spring Bean，例如 `@Service`。
- 业务逻辑写在 `run(String param)`。
- 需要事务时在 `run` 上添加 `@Transactional(rollbackFor = Throwable.class)` 或按业务选择更窄异常。

参考：

- `sa-base/src/main/java/net/lab1024/sa/base/module/support/job/core/SmartJob.java`
- `sa-base/src/main/java/net/lab1024/sa/base/module/support/job/sample/SmartJobSample1.java`
- `sa-base/src/main/java/net/lab1024/sa/base/module/support/job/sample/SmartJobSample2.java`

### Runtime Contract

- 任务配置可通过前端页面或 `b_smart_job` 表维护。
- `job_class` 必须与实现类匹配。
- 任务执行记录写入 `b_smart_job_log`。
- 分布式实例无需额外处理；同一时间任务只触发一次，配置更新会同步到运行实例。
- cron 适合固定时间点；fixedDelay 适合固定间隔，首次执行受服务启动时间影响。

---

## DataScope

DataScope 用于通过 MyBatis 插件动态拼接 SQL，实现按角色、部门、员工或自定义策略的数据范围控制。

### Use Annotation

DAO 查询方法上添加 `@DataScope`：

```java
@DataScope(
    dataScopeType = DataScopeTypeEnum.ORDER,
    whereInType = DataScopeWhereInTypeEnum.EMPLOYEE,
    joinSql = "create_user_id in (#employeeIds)"
)
List<OrderVO> queryByPage(Page page, @Param("queryForm") OrderQueryForm queryForm);
```

### Parameters

| 参数 | 说明 |
|------|------|
| `dataScopeType` | 对应业务模块，例如订单、企业等 |
| `whereInType` | 按部门、员工或自定义策略拼接 |
| `joinSql` | 非自定义策略必填，支持 `#departmentIds`、`#employeeIds` |
| `joinSqlImplClazz` | `CUSTOM_STRATEGY` 时必填，自定义 SQL 拼接策略 |
| `paramName` | 自定义策略需要读取 DAO 参数时配置 |
| `whereIndex` | 拼接到第几个 `where` 后 |

自定义策略继承 `AbstractDataScopeStrategy` 并实现 `getCondition(...)`。

参考：

- `sa-admin/src/main/java/net/lab1024/sa/admin/module/system/datascope/DataScope.java`
- `sa-admin/src/main/java/net/lab1024/sa/admin/module/system/datascope/MyBatisPlugin.java`
- `sa-admin/src/main/java/net/lab1024/sa/admin/module/system/datascope/service/DataScopeSqlConfigService.java`

---

## File Upload

文件上传支持 `local` 和 `cloud` 两种模式：

- `local`：文件存储在服务器本地。
- `cloud`：通过 S3 协议支持阿里云、华为云、七牛云、MinIO 等。

### Configuration

本地存储：

```yaml
file:
  storage:
    mode: local
    local:
      upload-path: ${localPath:/home}/smart_admin_v3/upload/
      url-prefix:
```

云存储：

```yaml
file:
  storage:
    mode: cloud
    cloud:
      region: us-east-1
      endpoint: http://127.0.0.1:9000
      bucket-name: smart-admin
      access-key:
      secret-key:
      private-url-expire-seconds: 3600
      public-url-prefix:
```

### Development Contract

- 文件目录类型在 `FileFolderTypeEnum` 中维护，按业务大块划分。
- 文件实现通过 `IFileStorageService` 抽象，`FileStorageCloudServiceImpl` 和 `FileStorageLocalServiceImpl` 按配置条件装配。
- 私有文件 URL 可能需要远程生成，必须使用 Redis 缓存减少阻塞请求。
- 文件 key 规则为 32 位 UUID + 文件后缀。
- 业务表中多文件 key 可用逗号分隔存储，配合序列化器与前端文件组件交换 `fileVO` 数组。

参考：

- `sa-base/src/main/java/net/lab1024/sa/base/module/support/file/`
- `FileKeyVoSerializer`
- `FileKeyVoDeserializer`
- `FileKeySerializer`

---

## Repeat Submit

重复提交通过 `@RepeatSubmit` + AOP 限制接口在短时间内重复请求。

### Use

```java
@PostMapping("/tableColumn/update")
@RepeatSubmit
public ResponseDTO<String> updateTableColumn(@RequestBody @Valid TableColumnUpdateForm updateForm) {
    return tableColumnService.updateTableColumns(SmartRequestUtil.getRequestUser(), updateForm);
}
```

默认间隔 300ms，最大 30000ms。

### Storage Choice

- 集群部署使用 Redis ticket。
- 单体部署可使用 Caffeine 内存 ticket。
- ticket 通常由 `url + userId` 组成。

参考：

- `sa-base/src/main/java/net/lab1024/sa/base/module/support/repeatsubmit/annoation/RepeatSubmit.java`
- `sa-base/src/main/java/net/lab1024/sa/base/module/support/repeatsubmit/RepeatSubmitAspect.java`
- `RepeatSubmitRedisTicket`
- `RepeatSubmitCaffeineTicket`

---

## Data Tracer

数据变更记录用于中后台重要数据的新增、修改、删除和其他操作留痕。

### Use

- 在 `DataTracerTypeEnum` 新增业务类型。
- Entity 需要可读 diff 的字段添加注解：
  - `@DataTracerFieldLabel`
  - `@DataTracerFieldEnum`
  - `@DataTracerFieldDict`
  - `@DataTracerFieldBigDecimal`
  - `@DataTracerFieldSql`
- 新增调用 `dataTracerService.insert(dataId, type)`。
- 更新调用 `dataTracerService.addTrace(...)` 或 `dataTracerService.update(...)`，保留旧对象和新对象。
- 删除调用 `dataTracerService.delete(...)` 或 `batchDelete(...)`。

参考：

- `sa-base/src/main/java/net/lab1024/sa/base/module/support/datatracer/`
- `sa-admin/src/main/java/net/lab1024/sa/admin/module/business/oa/enterprise/service/EnterpriseService.java`
- `sa-admin/src/main/java/net/lab1024/sa/admin/module/business/oa/enterprise/domain/entity/EnterpriseEntity.java`

---

## SmartReload

SmartReload 用于运行时动态加载和刷新配置。

开发契约：

- reload 项定义在表 `t_reload_item`。
- 使用 `@SmartReload` 标记需要 reload 的方法。
- 通过轮询而非订阅作为核心策略。
- 刷新结果要能写入执行记录，便于前端查看。

参考：

- `sa-base/src/main/java/net/lab1024/sa/base/module/support/reload/`
- `sa-base/src/main/java/net/lab1024/sa/base/module/support/reload/core/annoation/SmartReload.java`

---

## Utility Classes

优先使用项目内封装工具，而不是重复造轮子：

- 当前用户：`SmartRequestUtil.getRequestUser()`、`getRequestUserId()`。
- Bean 复制：`SmartBeanUtil.copy(...)`、`copyProperties(...)`、`copyList(...)`。
- 字符串转换：`SmartStringUtil`。
- 枚举：`SmartEnumUtil`、`BaseEnum`。
- 时间：`SmartLocalDateUtil`。
- 分页：`SmartPageUtil`。

参考：`sa-base/src/main/java/net/lab1024/sa/base/common/util/`。

---

## Good/Base/Bad Cases

- Good：新增重要业务“企业”时，Controller 调 Service，Service 写业务校验，DAO XML 查询，更新后写 DataTracer。
- Base：普通查询只使用 DAO XML + `ResponseDTO.ok(data)`。
- Bad：Controller 直接拼 SQL、直接读写数据库、返回裸 Map、未接入错误码和数据变更记录。
