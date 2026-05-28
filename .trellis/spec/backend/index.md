# Backend Development Guidelines

> SmartAdmin 后端开发规范。来源包括 `docs/SmartAdmin开发文档.md` 以及当前仓库 `smart-admin-api-java8-springboot2` 的真实代码。

---

## Overview

后端当前主实现位于 `smart-admin-api-java8-springboot2`，采用 Java 8、Spring Boot 2.7、Sa-Token、MyBatis-Plus、MySQL、Redis/Caffeine、Knife4j/OpenAPI。仓库也保留官方文档中提到的 Java 17 + Spring Boot 3 升级路线，但当前代码示例以 Java 8 目录为准。

开发原则按优先级排序：

1. 满足业务需要。
2. 代码清晰明了，命名与业务一致。
3. 在清晰前提下尽量少写代码、少加类、少加冗余注释。
4. 在前 3 条成立时再考虑复用和模块化。

---

## Pre-Development Checklist

- [ ] 新业务先确认放在 `sa-admin` 业务模块还是 `sa-base` 支撑模块。
- [ ] 若涉及接口、Service、DAO、数据库、配置或支撑能力，阅读对应专题规范。
- [ ] 跨层新增字段时，确认后端 Form/VO/Entity、Mapper SQL、前端 API/页面/常量命名一致。
- [ ] 接口返回统一使用 `ResponseDTO<T>`；错误码优先从三类错误码或业务错误码枚举中选择。
- [ ] 数据库操作优先使用 MyBatis-Plus `BaseMapper` + XML SQL，不使用 Wrapper 拼条件。
- [ ] 写事务前先拆分校验和数据库写入范围，避免长事务。

---

## Guidelines Index

| Guide | Description | Status |
|-------|-------------|--------|
| [Directory Structure](./directory-structure.md) | Maven 模块、Java 包、MVC 分层、命名规则 | Filled |
| [Database Guidelines](./database-guidelines.md) | MyBatis-Plus、XML SQL、事务、表字段规范 | Filled |
| [Error Handling](./error-handling.md) | `ResponseDTO`、错误码分类、全局异常 | Filled |
| [Quality Guidelines](./quality-guidelines.md) | 命名、参数、注释、提交前检查、禁止模式 | Filled |
| [Logging Guidelines](./logging-guidelines.md) | `Slf4j`、全局异常日志、任务日志、安全边界 | Filled |
| [Support Modules](./support-modules.md) | SmartJob、DataScope、文件上传、重复提交、数据变更记录等内置能力 | Filled |

---

## Quality Check

- [ ] 没有模板占位文本。
- [ ] 新增后端代码能在现有包结构中找到同类示例。
- [ ] Controller 保持薄层；Service/Manager/DAO 职责边界清晰。
- [ ] `ResponseDTO`、错误码、日志、事务、Mapper XML 与本目录规范一致。
