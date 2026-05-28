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
| 新增枚举字段 | 数据库注释、Java 枚举、前端常量同步 | 只改一端 |

---

## Tests Required

- DAO XML 新增复杂条件时，至少本地跑对应接口或 Mapper 查询，确认 SQL 可执行。
- 涉及事务的写操作，要验证成功写入、校验失败不写入、异常回滚。
- 跨层新增字段，要验证数据库字段、Entity、Form/VO、Mapper XML、前端 API/页面均已同步。
