# Database Guidelines

> 数据库、MyBatis-Plus、XML SQL、事务和多环境配置规范。

---

## Stack

- 持久层优先使用 MyBatis-Plus。
- DAO 接口继承 `BaseMapper<XxxEntity>`。
- 复杂查询、分页查询和动态条件写在 XML Mapper 中。
- 多数据源场景可参考官方文档中 SmartDb，但当前主代码以单数据源 + Druid + MyBatis-Plus 为准。

参考文件：

- `sa-admin/src/main/java/net/lab1024/sa/admin/module/business/oa/enterprise/dao/EnterpriseDao.java`
- `sa-admin/src/main/resources/mapper/business/oa/enterprise/EnterpriseMapper.xml`
- `sa-base/src/main/java/net/lab1024/sa/base/config/DataSourceConfig.java`

---

## DAO And XML Contracts

DAO 方法签名要让 XML 可读、可搜索：

```java
List<EnterpriseVO> queryPage(Page page, @Param("queryForm") EnterpriseQueryForm queryForm);
```

XML 规则：

- `mapper namespace` 必须等于 DAO 全限定名。
- SQL 中不要写死业务常量，应从 DAO 参数传入。
- 查询条件对象统一命名为 `queryForm` 时，XML 使用 `#{queryForm.xxx}`。
- 同一个 `Form` 被多个报表或聚合 SQL 复用时，新增或既有筛选字段必须逐个同步到相关 XML；例如 `departmentId` 不能只在员工汇总查询生效，也要在日期趋势、类型明细等同范围查询中保持一致。若部门筛选语义是“自身及下级部门”，Service 层先填充隐藏的 `departmentIdList`，XML 使用 `department_id IN (...)`。
- 分页用 MyBatis-Plus `Page<?> page = SmartPageUtil.convert2PageQuery(queryForm)`，返回用 `SmartPageUtil.convert2PageResult(page, list)`。
- 默认排序只在前端未传排序项时生效：

```xml
<if test="queryForm.sortItemList == null or queryForm.sortItemList.size == 0">
    ORDER BY create_time DESC
</if>
```

---

## Forbidden Query Patterns

- 禁止使用 MyBatis-Plus `Wrapper` / `LambdaQueryWrapper` 组织复杂条件。原因：SQL 不易复用，慢 SQL 或线上问题无法按 SQL 片段快速定位。
- 禁止在 XML 中硬编码枚举值、状态值。将常量作为 DAO 参数传入。
- Join 较多时不要使用难懂短别名；官方规范推荐使用表全名，以便阅读和搜索。

正确方向：

```xml
FROM t_oa_enterprise
WHERE deleted_flag = #{queryForm.deletedFlag}
```

需要 Join 时优先：

```xml
FROM t_notice
LEFT JOIN t_employee ON t_notice.create_user_id = t_employee.employee_id
```

---

## Transaction Boundaries

`@Transactional` 要谨慎使用：

- 必须显式设置 `rollbackFor`，常规业务使用 `Exception.class`，部分支撑任务或历史代码使用 `Throwable.class` 时要有明确理由。
- 不要把大量查询校验放进事务方法，避免过早占用数据库连接。
- 复杂业务建议：Service 做查询和校验，Manager 承担最小必要的事务写入。
- 同一个类内部 `this.xxx()` 调用带事务的方法不会触发 Spring AOP 事务。需要跨 Bean 调用、下沉到 Manager，或使用明确的代理方案。
- `ResponseDTO.userErrorParam(...)` 这类普通返回值不会触发事务回滚；事务方法里如果已经执行写操作，后续校验失败会提交之前的写入。正确做法是：先完成全部可预判校验，再进入删除/插入/更新；或在必须写后校验的场景抛出可回滚异常。

参考：

- `EnterpriseService#createEnterprise`
- `EnterpriseService#updateEnterprise`
- `sa-admin/src/main/java/net/lab1024/sa/admin/module/system/employee/manager/EmployeeManager.java`

---

## Database Naming

- 数据库名使用小写下划线，并区分环境：`smart_admin_v2_dev`、`smart_admin_v2_test`、`smart_admin_v2_prod`。
- 表名使用小写下划线，并以 `t_` 开头，例如 `t_employee`、`t_department`、`t_config`、`t_oa_enterprise`。
- 主键字段使用 `[module]_id`，例如 `enterprise_id`、`employee_id`。
- Boolean 字段使用 `_flag` 后缀，例如 `deleted_flag`、`disabled_flag`。

---

## Table Design

业务表通常至少包含：

- `[module]_id`：`BIGINT`，单表自增。
- `create_time`：`datetime`，默认 `CURRENT_TIMESTAMP`。
- `update_time`：`datetime`，默认 `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`。

简单日志表可按实际情况省略 `update_time`。

枚举或状态字段的数据库注释必须列出所有枚举含义。新增或修改枚举时，同步更新表字段注释、Java 枚举、前端常量。

---

## Multi-Environment Configuration

后端通过 Maven profile 管理环境：

- `dev`：开发环境，默认激活。
- `test`：测试环境。
- `pre`：预发布环境。
- `prod`：生产环境。

配置目录必须保持：

```text
src/main/resources/dev
src/main/resources/test
src/main/resources/pre
src/main/resources/prod
```

`application.yaml` 中 `spring.profiles.active` 由 Maven profile 注入。运行时环境判断通过 `SystemEnvironment`，不要手写字符串判断环境。

参考：

- `smart-admin-api-java8-springboot2/pom.xml`
- `sa-base/src/main/java/net/lab1024/sa/base/common/domain/SystemEnvironment.java`
- `sa-base/src/main/java/net/lab1024/sa/base/config/SystemEnvironmentConfig.java`

---

## Validation & Error Matrix

| 场景 | 正确处理 | 禁止处理 |
|------|----------|----------|
| XML 条件需要状态值 | DAO 参数传入状态值 | 在 XML 中写死 `status = 1` |
| 复杂查询 | DAO + XML Mapper | Wrapper 拼复杂 SQL |
| 需要事务 | 缩短事务，只包写入关键段 | 方法一开始就开事务并做大量查询校验 |
| 内部方法事务 | 跨 Bean 调用或下沉 Manager | `this.saveData()` 期望事务生效 |
| 写入后业务校验失败 | 写入前预校验，或抛异常触发回滚 | 已经 `delete/update/insert` 后直接 `return ResponseDTO.userErrorParam(...)` |
| 新增枚举字段 | 数据库注释、Java 枚举、前端常量同步 | 只改一端 |

---

## Tests Required

- DAO XML 新增复杂条件时，至少本地跑对应接口或 Mapper 查询，确认 SQL 可执行。
- 涉及事务的写操作，要验证成功写入、校验失败不写入、异常回滚。
- 跨层新增字段，要验证数据库字段、Entity、Form/VO、Mapper XML、前端 API/页面均已同步。

---

## Scenario: 工作项积分报表部门口径

### 1. Scope / Trigger

- Trigger: 工作项积分报表新增或调整 API、Mapper SQL、首页图表时，必须保持“本部门及以下部门”的部门筛选口径一致。

### 2. Signatures

- 排行榜/汇总类接口：`POST /workitem/score/report/employee`，使用 `WorkScoreReportQueryForm.departmentId` 输入，Service 调用 `fillQueryScope(...)` 后由 XML 使用 `departmentIdList`。
- 日期汇总接口：`POST /workitem/score/report/date`，使用同样的“本部门及以下部门”部门口径。
- 个人趋势接口：`POST /workitem/score/report/employee-date`，使用同样的“本部门及以下部门”部门口径，返回 `List<WorkScoreEmployeeDateVO>`，字段为 `employeeId`、`employeeName`、`reportDate`、`totalScore`。

### 3. Contracts

- `departmentId` 是前端选中或首页当前用户所属部门。
- 积分报表部门筛选接口必须在 Service 层调用 `fillQueryScope(...)` 或等价地同时调用 `fillDataScopeEmployeeIdList(...)` 与 `fillDepartmentIdList(queryForm)`，XML 只读取隐藏字段 `departmentIdList` 做 `department_id IN (...)`。
- 首页个人趋势接口也必须包含下级部门员工，不能使用 `department_id = #{queryForm.departmentId}` 只查当前部门。
- 所有接口都必须叠加 `dataScopeEmployeeIdList`，空列表表示当前用户可查看全部员工数据，按现有 DataScope 约定不拼接员工 `IN` 条件。

### 4. Validation & Error Matrix

| 条件 | 正确处理 |
|------|----------|
| `startDate` 或 `endDate` 为空 | 由 `@Valid` 拦截 `@NotNull` |
| 当前用户仅可查看本人 | `dataScopeEmployeeIdList` 只包含本人，XML 限定员工 ID |
| 首页个人趋势传入当前部门 | 查询该部门及所有下级部门员工 |
| 首页排行榜传入当前部门 | 查询该部门及所有下级部门员工 |

### 5. Good/Base/Bad Cases

- Good: 首页个人积分趋势图标题使用“部门个人积分趋势图”，后端 SQL 使用 `department_id IN departmentIdList`，每个员工每天聚合一条记录。
- Base: 管理员可查看全部数据时，`dataScopeEmployeeIdList` 为空，只由部门条件限定结果。
- Bad: 个人趋势接口只调用 `fillDataScopeEmployeeIdList(...)`，XML 使用 `department_id = departmentId`，导致下级部门员工丢失。

### 6. Tests Required

- 构造同一部门和下级部门员工数据，断言 `/employee-date` 和 `/employee` 都返回本部门及下级部门员工。
- 构造同一员工同一天多条日报明细，断言 `totalScore` 按 `employee_id + report_date` 正确汇总。
- 构造无积分数据月份，断言接口返回空列表，前端图表清空旧 series。

### 7. Wrong vs Correct

#### Wrong

```java
public ResponseDTO<List<WorkScoreEmployeeDateVO>> queryEmployeeDateScore(RequestEmployee requestEmployee, WorkScoreReportQueryForm queryForm) {
    this.fillDataScopeEmployeeIdList(requestEmployee, queryForm);
    return ResponseDTO.ok(workDailyReportDao.queryEmployeeDateScore(queryForm, WorkDailyReportStatusEnum.AUDIT_PASS.getValue()));
}
```

#### Correct

```java
public ResponseDTO<List<WorkScoreEmployeeDateVO>> queryEmployeeDateScore(RequestEmployee requestEmployee, WorkScoreReportQueryForm queryForm) {
    this.fillQueryScope(requestEmployee, queryForm);
    return ResponseDTO.ok(workDailyReportDao.queryEmployeeDateScore(queryForm, WorkDailyReportStatusEnum.AUDIT_PASS.getValue()));
}
```

---

## Scenario: 工作项积分报表工作项详情字段

### 1. Scope / Trigger

- Trigger: 调整 `POST /workitem/score/report/item` 工作项详情接口、`WorkScoreItemVO`、日报明细图片表或前端积分报表日期明细展示。

### 2. Signatures

- API: `POST /workitem/score/report/item`
- Backend VO: `WorkScoreItemVO`
- Required response fields: `reportDate`、`workDailyReportItemId`、`workItemId`、`workItemName`、`workItemTypeId`、`workItemTypeName`、`standardScore`、`finalScore`、`deductReason`、`finishRemark`、`fileList`
- `fileList` item type: `WorkDailyReportFileVO`，至少包含 `fileId`、`fileKey`、`fileName`、`fileUrl`

### 3. Contracts

- `finishRemark` 是用户填报日报时填写的完成说明，积分报表的“日期明细 / 工作项详情”必须展示。
- `fileList` 是用户上传的佐证图，接口返回前必须通过 `FileService#getFileUrl(fileKey)` 补齐 `fileUrl`，前端用图片预览组件展示。
- `workDailyReportItemId` 是日报明细主键，查询佐证图和前端表格 `rowKey` 都使用它，不能用 `workItemId` 代替。
- 查询仍必须叠加 `fillQueryScope(...)` 的员工数据范围与部门范围，不因图片补充绕过报表权限。

### 4. Validation & Error Matrix

| 条件 | 正确处理 |
|------|----------|
| 明细没有完成说明 | 返回空值，前端展示 `-` |
| 明细没有佐证图 | `fileList` 返回空列表，前端展示 `-` |
| 文件 URL 获取失败 | 保留文件基础信息，不抛出报表接口异常 |
| 多个日报明细引用同一工作项 | 以 `workDailyReportItemId` 区分行和图片 |

### 5. Good/Base/Bad Cases

- Good: SQL 返回 `work_daily_report_item_id`，Service 按日报明细 ID 查询 `t_work_daily_report_file`，并补齐 `fileUrl`。
- Base: 只有文字说明没有图片时，详情表格仍展示说明内容。
- Bad: 只在 VO 里加 `fileList`，但 Service 不填充图片，导致前端永远没有佐证图。

### 6. Tests Required

- 构造带 `finishRemark` 和两张佐证图的审核通过日报，断言 `/workitem/score/report/item` 返回说明和两个带 `fileUrl` 的文件。
- 构造无图片日报，断言接口返回空 `fileList` 且前端工作项详情不报错。
- 构造同一员工同一天多个明细，断言每行 `workDailyReportItemId` 唯一且图片不串行。

### 7. Wrong vs Correct

#### Wrong

```xml
SELECT
    t_work_daily_report_item.work_item_id,
    t_work_daily_report_item.finish_remark
FROM t_work_daily_report_item
```

#### Correct

```xml
SELECT
    t_work_daily_report_item.work_daily_report_item_id,
    t_work_daily_report_item.work_item_id,
    t_work_daily_report_item.finish_remark
FROM t_work_daily_report_item
```

---

## Scenario: ResponseDTO Failure Inside Transaction

### 1. Scope / Trigger

- Trigger: 事务方法中混合业务校验和数据库写入，且失败路径用 `ResponseDTO` 返回。

### 2. Signatures

- Typical service signature: `@Transactional(rollbackFor = Exception.class) public ResponseDTO<String> saveXxx(...)`
- Failure signature: `return ResponseDTO.userErrorParam("...")`

### 3. Contracts

- `ResponseDTO` 是普通返回值，不是异常。
- Spring 事务只会因为配置匹配的异常回滚；普通失败返回会正常提交事务。

### 4. Validation & Error Matrix

| 条件 | 正确处理 |
|------|----------|
| 工作项、附件、状态等可提前判断 | 全部校验通过后再写库 |
| 写入后才能发现的异常条件 | 抛出受 `rollbackFor` 覆盖的异常，或调整流程让校验前置 |
| 失败需要给前端友好提示 | 写入前返回 `ResponseDTO.userErrorParam(...)` |

### 5. Good/Base/Bad Cases

- Good: 先查询并校验所有明细有效，再删除旧明细、插入新明细。
- Base: 保存前检查日报状态、所属人、重复工作项。
- Bad: 先删除旧明细，再发现新明细里的工作项已停用，然后返回 `ResponseDTO.userErrorParam(...)`。

### 6. Tests Required

- 构造已有数据，提交包含无效明细的请求，断言旧数据仍保留。
- 构造审核分数无效的请求，断言所有明细分数都未被部分更新。

### 7. Wrong vs Correct

#### Wrong

```java
dao.deleteByReportId(reportId);
if (invalid) {
    return ResponseDTO.userErrorParam("数据无效");
}
dao.insert(entity);
```

---

## Scenario: Batch Soft Delete API

### 1. Scope / Trigger

- Trigger: 为已有单条软删除能力新增批量删除接口，且数据表使用 `deleted_flag` 标记删除。

### 2. Signatures

- Controller: `@PostMapping("/<module>/batch/delete") public ResponseDTO<String> batchDelete(@RequestBody List<Long> idList)`
- Service: `@Transactional(rollbackFor = Exception.class) public ResponseDTO<String> batchDelete(List<Long> idList)`
- DAO: `selectAvailableByIdList(Collection<Long> idList, Boolean deletedFlag)` + `updateDeletedFlagBatch(Collection<Long> idList, Boolean deletedFlag)`

### 3. Contracts

- Request body is a JSON array of primary-key IDs.
- Empty or null list returns `ResponseDTO.userErrorParam("请选择要删除的数据")`.
- Soft delete updates only existing rows whose `deleted_flag = false`.
- The batch endpoint should reuse the same permission point as single delete unless the product explicitly defines a separate batch permission.

### 4. Validation & Error Matrix

| Condition | Return |
|------|------|
| ID list is empty or null | `ResponseDTO.userErrorParam("请选择要删除的数据")` |
| Any ID does not exist or is already deleted | `ResponseDTO.userErrorParam("存在已删除或不存在的数据")` |
| All IDs valid | Batch `UPDATE ... SET deleted_flag = true WHERE id IN (...)` |

### 5. Good/Base/Bad Cases

- Good: Deduplicate IDs, query all valid rows first, compare counts, then batch update inside one transaction.
- Base: If the feature already has single delete, keep its behavior and permission semantics consistent.
- Bad: Loop over IDs and partially update before discovering a later invalid ID.

### 6. Tests Required

- Submit an empty list and assert no database writes.
- Submit a list containing an invalid or deleted ID and assert no rows are updated.
- Submit valid IDs and assert all target rows have `deleted_flag = true`.

### 7. Wrong vs Correct

#### Wrong

```java
for (Long id : idList) {
    dao.updateDeletedFlag(id, Boolean.TRUE);
}
```

#### Correct

```java
Set<Long> idSet = new LinkedHashSet<>(idList);
List<Entity> entityList = dao.selectAvailableByIdList(idSet, Boolean.FALSE);
if (entityList.size() != idSet.size()) {
    return ResponseDTO.userErrorParam("存在已删除或不存在的数据");
}
dao.updateDeletedFlagBatch(idSet, Boolean.TRUE);
```

#### Correct

```java
if (invalid) {
    return ResponseDTO.userErrorParam("数据无效");
}
dao.deleteByReportId(reportId);
dao.insert(entity);
```
