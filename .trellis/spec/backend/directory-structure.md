# Directory Structure

> 后端目录、包结构和 MVC 分层规范。

---

## Repository Layout

当前后端主工程：

```text
smart-admin-api-java8-springboot2/
|-- pom.xml                         父 POM，统一版本、BOM、profile
|-- sa-base/                        基础能力和支撑模块
|-- sa-admin/                       管理端业务模块
```

父 POM 统一维护依赖版本、BOM 与多环境 profile。不要在子模块随意重复声明版本；新增依赖优先放到父 POM 的 `<properties>` 和 `<dependencyManagement>`。

参考文件：

- `smart-admin-api-java8-springboot2/pom.xml`
- `smart-admin-api-java8-springboot2/sa-base/pom.xml`
- `smart-admin-api-java8-springboot2/sa-admin/pom.xml`

---

## Java Package Layout

常规 Java 源码目录按官方文档约定组织：

```text
src/main/java
|-- common        各项目通用类库、返回对象、错误码、异常、校验、Swagger/OpenAPI 等
|-- config        项目配置类
|-- constant      全局公共常量
|-- handler       全局处理器，如异常处理
|-- interceptor   全局拦截器
|-- listener      全局监听器
|-- module        业务或支撑模块
|-- third         第三方服务封装，如 Redis、OSS、微信 SDK 等
|-- util          全局工具类
|-- Application   启动类
```

当前代码中的落地示例：

- `sa-base/src/main/java/net/lab1024/sa/base/common/domain/ResponseDTO.java`
- `sa-base/src/main/java/net/lab1024/sa/base/handler/GlobalExceptionHandler.java`
- `sa-base/src/main/java/net/lab1024/sa/base/module/support/job/`
- `sa-admin/src/main/java/net/lab1024/sa/admin/module/business/oa/enterprise/`

---

## Module Organization

`module` 下按业务域拆顶级目录。每个业务模块内部再按 MVC 和 domain 对象分包：

```text
module/business/oa/enterprise/
|-- controller/                 HTTP 接口入口
|-- service/                    业务编排、校验、返回 ResponseDTO
|-- manager/                    事务写入、DAO 组合、缓存/中间件通用能力下沉
|-- dao/                        MyBatis Mapper 接口
|-- constant/                   模块内枚举和常量
|-- domain/
|   |-- entity/                 数据库持久对象，XxxEntity
|   |-- form/                   前端/RPC 请求对象，XxxForm
|   |-- vo/                     返回前端/RPC 的对象，XxxVO
|   |-- dto/                    层间传输对象，XxxDTO
|   |-- bo/                     内部业务对象，XxxBO
```

参考文件：

- `sa-admin/src/main/java/net/lab1024/sa/admin/module/business/oa/enterprise/controller/EnterpriseController.java`
- `sa-admin/src/main/java/net/lab1024/sa/admin/module/business/oa/enterprise/service/EnterpriseService.java`
- `sa-admin/src/main/java/net/lab1024/sa/admin/module/business/oa/enterprise/manager/EnterpriseEmployeeManager.java`
- `sa-admin/src/main/java/net/lab1024/sa/admin/module/business/oa/enterprise/dao/EnterpriseDao.java`
- `sa-admin/src/main/java/net/lab1024/sa/admin/module/business/oa/enterprise/domain/entity/EnterpriseEntity.java`

---

## Layer Responsibilities

### Controller

- 只负责路由、权限、简单 `@Valid` 参数校验、获取当前请求用户、调用 Service。
- 不写业务校验、不做数据拼装、不做数据库操作。
- 当前请求用户只能在 Controller 层通过 `SmartRequestUtil.getRequestUser()` 或 `AdminRequestUtil.getRequestUser()` 获取，再显式传给 Service 或写入 Form。
- OpenAPI 注解使用 `@Operation(summary = "... @author 作者")`，作者信息必须保留。
- URL 使用 `GET`/`POST` + 动作式路径，不追求 RESTful。路径格式优先 `/业务模块/子模块/动作`。
- `@GetMapping` / `@PostMapping` 写完整路径，避免只在类上写公共 `@RequestMapping` 后方法只写短路径。

示例：`EnterpriseController#createEnterprise` 获取当前用户并调用 Service。

### Service

- 负责业务校验、业务编排、调用 DAO/Manager、返回 `ResponseDTO`。
- 业务过大时拆成多个 Service，例如 `OrderQueryService`、`OrderCreateService`。
- 可以使用 `@Transactional`，但复杂校验和事务写入建议拆开；长事务优先下沉到 Manager。
- 示例：`EnterpriseService#updateEnterprise` 做存在性校验、重复校验、数据写入和数据变更记录。

### Manager

Manager 用于：

- 封装第三方平台或中间件能力。
- 下沉 Service 层通用能力，如缓存、批量保存、事务性 DAO 组合。
- 与多个 DAO 交互，尤其是需要缩短事务边界时。

示例：`EnterpriseEmployeeManager`、`EmployeeManager`、`NoticeManager`。

### DAO

- DAO 接口继承 MyBatis-Plus `BaseMapper<XxxEntity>`。
- 复杂 SQL 写在 `resources/mapper/**/*.xml`，命名空间必须匹配 DAO 全路径。
- DAO 方法参数用 `@Param` 显式命名，供 XML 使用。

示例：

- `EnterpriseDao`
- `sa-admin/src/main/resources/mapper/business/oa/enterprise/EnterpriseMapper.xml`

---

## Naming Conventions

- 项目名、模块名、数据库名使用小写和分隔符；Java 类使用标准 `UpperCamelCase`。
- 同一业务在后端、前端、移动端、数据库、Redis key、接口路径中必须统一英文命名。比如“通知”统一使用 `notice`，不要前端叫 `news`、移动端叫 `message`。
- 类和大范围对象使用名词：`OrderService`、`EnterpriseEntity`。
- 方法名使用动词或动宾短语：`createEnterprise`、`queryByPage`、`deleteEmployee`。
- Boolean 字段和数据库列统一以 `Flag` / `_flag` 结尾，例如 `deletedFlag`、`deleted_flag`、`disabledFlag`。
- JavaBean 后缀含义固定：
  - `XxxEntity`：数据库持久对象，字段与表字段一致。
  - `XxxForm`：前端/RPC 请求对象，不继承 Entity。
  - `XxxVO`：返回前端/RPC 的对象，不继承 Entity。
  - `XxxDTO`：层间传输对象。
  - `XxxBO`：内部业务对象，只在 Service/Manager/DAO 层使用，不进 Controller。

---

## JavaBean Contracts

- JavaBean 不写业务逻辑或计算逻辑。
- 基本数据类型使用包装类型，如 `Integer`、`Long`、`Boolean`。
- 不设置默认值，避免默认值掩盖前端未传参。
- 每个属性使用多行注释说明含义；Entity 注释要与数据库字段注释一致。
- 使用 Lombok 简化 getter/setter，例如 `@Data`。
- Entity 使用 `@TableName`、主键使用 `@TableId`，日期类型统一使用 `LocalDateTime` 或 `LocalDate`。

参考：`EnterpriseEntity`。

---

## Common Mistakes

- 在 Controller 中写业务校验或拼装复杂对象。
- 在 Service/Manager/DAO 中直接从 ThreadLocal 获取当前请求用户。
- 新业务新增了后端 `enterprise`，但前端、SQL 或路由用了另一套词。
- `Form` 或 `VO` 继承 `Entity`，导致请求/响应对象与数据库对象耦合。
- Boolean 字段命名成 `isDeleted`，引起序列化和框架解析歧义。
