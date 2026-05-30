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

### Scenario: Employee Snapshot Data Scope

#### 1. Scope / Trigger

- Trigger: 查询数据的可见主体不是 `create_user_id`，而是业务快照字段，例如 `t_work_daily_report.employee_id`。
- Use case: 日报审核、积分报表这类按员工归属展示的数据。

#### 2. Signatures

- Enum: `DataScopeTypeEnum.<MODULE>(value, sort, name, desc)`。
- Service dependency: `DataScopeViewService#getEmployeeDataScopeViewType(dataScopeType, employeeId)`。
- Service dependency: `DataScopeViewService#getCanViewEmployeeId(viewType, employeeId)`。
- Query form hidden field: `List<Long> dataScopeEmployeeIdList`。
- Mapper condition: `employee_id IN (#{item}...)` only when `dataScopeEmployeeIdList` is not empty.

#### 3. Contracts

- `DataScopeViewTypeEnum.ALL` returns an empty employee list; XML must treat empty as no additional filter.
- Non-admin users with no role data-scope setting default to `ME`, so the employee list contains only the current employee.
- Controller must pass the current `RequestEmployee` into Service for scoped queries.
- ID-based detail or write actions must check visibility in Service, not only rely on list-query filtering.

#### 4. Validation & Error Matrix

| 条件 | 正确处理 |
|------|----------|
| 用户仅本人可见 | 查询 SQL 附加当前员工 ID 过滤 |
| 用户本部门可见 | 查询 SQL 附加本部门员工 ID 过滤 |
| 用户全部可见或管理员 | 不附加员工 ID 过滤 |
| 通过 ID 访问不可见详情 | 返回业务层“数据不存在/无权限”等用户级错误 |

#### 5. Good/Base/Bad Cases

- Good: Service 统一计算 `dataScopeEmployeeIdList`，列表、详情、审核动作共用同一可见性口径。
- Base: 普通列表查询只在 XML 中追加 `employee_id IN (...)`。
- Bad: 只在前端隐藏数据，或只过滤列表但详情/提交接口仍可按 ID 越权访问。

#### 6. Tests Required

- 仅本人可见角色查询列表，只返回本人数据。
- 本部门可见角色查询列表，只返回本部门员工数据。
- 全部可见角色查询列表，不被额外员工 ID 限制。
- 不可见日报 ID 调详情或审核接口，断言不能返回或修改该日报。

#### 7. Wrong vs Correct

#### Wrong

```java
public ResponseDTO<VO> detail(Long id) {
    return ResponseDTO.ok(buildDetail(dao.selectById(id)));
}
```

#### Correct

```java
public ResponseDTO<VO> detail(RequestEmployee user, Long id) {
    Entity entity = dao.selectById(id);
    if (entity == null || !canView(user, entity.getEmployeeId())) {
        return ResponseDTO.userErrorParam("数据不存在");
    }
    return ResponseDTO.ok(buildDetail(entity));
}
```

### Scenario: Scoped Department Tree

#### 1. Scope / Trigger

- Trigger: 前端筛选项展示机构树，但业务数据受 `DataScopeTypeEnum` 控制。
- Use case: 积分报表、按机构筛选的审核/统计页面。

#### 2. Contracts

- Service 使用 `DataScopeViewService#getEmployeeDataScopeViewType(dataScopeType, employeeId)` 获取当前用户视图范围。
- Service 使用 `DataScopeViewService#getCanViewDepartmentId(viewType, employeeId)` 获取可选机构 ID。
- `DataScopeViewTypeEnum.ALL` 返回空机构列表，业务接口需解释为“不限制机构树”，返回全量部门树。
- `DataScopeViewTypeEnum.ME` 返回 `0L`，业务接口需解释为“无可选机构”，返回空树。
- 返回局部机构树时，不能直接修改 `DepartmentCacheManager#getDepartmentList()` 的缓存对象；先复制 `DepartmentVO`，再重置缺失父级为根节点并构建树。

#### 3. Good/Base/Bad Cases

- Good: 业务接口提供当前用户可见机构树，查询 SQL 仍在后端按员工数据范围兜底过滤。
- Base: 使用 `DepartmentService#departmentTreeByIdList` 从可见机构 ID 构建局部树。
- Bad: 前端使用全量 `/department/treeList`，只依赖查询结果过滤，导致普通用户可选择不可见机构。

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

## Scenario: Web 消息实时推送

### 1. Scope / Trigger
- Trigger: 后台写入站内信后，需要让 Web 端尽快刷新未读数或消息列表。
- Use case: 仅做单向通知，不承载双向聊天或大体量协同编辑。
- Boundary: 消息数据库仍是事实来源，实时通道只负责通知刷新。

### 2. Signatures
- `GET /support/message/stream` -> `SseEmitter`
- `MessageService#sendMessage(MessageSendForm...)`
- `MessageService#sendMessage(List<MessageSendForm>)`
- `MessageService#sendTemplateMessage(MessageTemplateSendForm...)`
- `MessageStreamService#connect(UserTypeEnum userType, Long userId)`
- `MessageStreamService#notifyAfterCommit(List<MessageEntity> messageEntityList)`

### 3. Contracts
- 消息写入成功后，必须在事务提交后再触发实时推送。
- 推送事件保持轻量，只发送“refresh”类事件，不把整页消息列表绑到通道里。
- 连接注册键按 `userType + ":" + userId` 组织，允许同一用户多个标签页同时在线。
- 长连接需要心跳保活，并在连接失效、鉴权失败、发送异常时主动清理。
- 多实例部署时，推送事件需要跨实例广播层，优先复用 Redis。

### 4. Validation & Error Matrix
| 条件 | 正确处理 |
|------|----------|
| 消息事务回滚 | 不发送推送 |
| 消息发送成功但某个连接失效 | 清理失效连接，其余连接继续发送 |
| Web 端鉴权失效 | 服务端拒绝连接，前端停止重连并走退出登录流程 |
| 长连接空闲超时 | 心跳维持连接，若仍断开则允许前端重连 |

### 5. Good/Base/Bad Cases
- Good: 消息保存后，在 `afterCommit` 中发送一次刷新事件，前端据此重新拉取未读数。
- Base: 单实例部署使用内存连接注册表即可。
- Bad: 在事务内同步推送，或把完整业务数据直接塞进长连接通道。

### 6. Tests Required
- 消息保存成功后，断言推送发生在事务提交之后。
- 模拟连接失效，断言服务端清理连接且不会影响其他在线用户。
- 模拟鉴权失败，断言前端不进入无限重连。
- 心跳触发时，断言空闲连接没有被无故移除。

### 7. Wrong vs Correct
#### Wrong
```java
messageManager.saveBatch(messageEntityList);
messageStreamService.notifyNow(messageEntityList);
```

事务内同步通知，回滚会导致前端误刷新。

#### Correct
```java
messageManager.saveBatch(messageEntityList);
messageStreamService.notifyAfterCommit(messageEntityList);
```

在提交后再推送，保证消息事实和前端刷新一致。

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

## Message Notification

站内信通过 `MessageService` 写入 `t_message`，适合业务完成后的弱关联提醒。

### Scenario: Weakly Coupled Message After Transaction

#### 1. Scope / Trigger

- Trigger: 主业务操作完成后需要提醒用户，但通知失败不应影响主业务结果。
- Use case: 日报审核完成后提醒提交人、审批完成后提醒申请人。

#### 2. Signatures

- Service dependency: `MessageService#sendMessage(MessageSendForm... sendForms)`。
- Message type: `MessageTypeEnum.MAIL` 表示站内信。
- Receiver type: `UserTypeEnum.ADMIN_EMPLOYEE` 表示管理端员工。
- Async executor: `@Resource(name = AsyncConfig.ASYNC_EXECUTOR_THREAD_NAME) AsyncTaskExecutor asyncTaskExecutor`。

#### 3. Contracts

- `MessageSendForm` 必填字段：`messageType`、`receiverUserType`、`receiverUserId`、`title`、`content`。
- `dataId` 可选，用于关联业务 ID，推荐传主业务记录 ID。
- 弱关联通知应在主事务 `afterCommit` 后投递，避免主事务回滚后仍产生消息。
- 投递任务提交失败或消息写入失败都只记录日志，不向外抛出影响主流程。

#### 4. Validation & Error Matrix

| 条件 | 正确处理 |
|------|----------|
| 主业务校验失败 | 不发送站内信 |
| 主事务回滚 | 不发送站内信 |
| 线程池提交任务失败 | 记录包含业务 ID 和接收人 ID 的错误日志，主流程已提交结果不回滚 |
| 站内信写入失败 | 记录包含业务 ID 和接收人 ID 的错误日志，不影响主流程返回 |

#### 5. Good/Base/Bad Cases

- Good: 在事务成功路径注册 `TransactionSynchronization#afterCommit`，再通过 `AsyncTaskExecutor` 异步调用 `MessageService`，并捕获异常。
- Base: 主业务无事务时直接提交异步任务，并捕获异常。
- Bad: 在事务内同步发送弱关联站内信，或让通知异常导致主业务回滚。

#### 6. Tests Required

- 主业务成功后，断言消息接收人、标题、内容、`dataId` 正确。
- 主业务校验失败或事务回滚后，断言不产生消息。
- 模拟消息服务异常，断言主业务仍成功，且错误日志可定位。

#### 7. Wrong vs Correct

#### Wrong

```java
messageService.sendMessage(messageSendForm);
return ResponseDTO.ok();
```

弱关联通知直接同步发送，异常会影响主业务返回。

#### Correct

```java
TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
    @Override
    public void afterCommit() {
        asyncTaskExecutor.execute(() -> sendMessageSafely(messageSendForm));
    }
});
```

主事务提交后再投递消息，并在发送方法中捕获异常、记录日志。

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
