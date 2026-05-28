# Quality Guidelines

> 后端质量、命名、注释、参数、代码整洁规范。

---

## Good Code Principles

SmartAdmin 官方文档给出的优先级：

1. 先满足业务。
2. 尽可能清晰明了。
3. 在清晰前提下尽可能少。
4. 在前三者成立后再追求复用和模块化。

不要为了抽象而抽象。比如前端 Layout 官方选择为每种布局保留独立入口，牺牲少量重复来降低复杂度；后端同样适用。

---

## Naming

- 必须使用正确、常用、符合业务语义的英文单词，禁止拼音式命名。
- 名词用于类、接口、枚举、模块、数据库表等较大范围。
- 动词或动宾短语用于方法名。
- 前后端、移动端、数据库、Redis、Docker 等对同一业务单元命名必须一致。

示例：

- 正确：`NoticeController`、`t_notice`、`notice-list.vue`
- 错误：后端 `notice`，前端 `news`，移动端 `message`

---

## Method Parameters

所有方法，包括 Controller、Service、Manager、DAO 和工具类，参数最多 5 个。超过 5 个时必须封装成 JavaBean，例如 `XxxForm`、`XxxDTO`。

原因：

- 同类型参数靠顺序区分容易出错。
- 调用方更易理解。
- 后续扩展字段更安全。

---

## Comments

注释和代码必须保持一致，但不要写空洞模板注释。

必须注释：

- 复杂业务步骤。
- 关键算法、解析规则、特殊边界。
- 对外接口、公共工具方法的重要参数要求和返回约定。
- JavaBean 字段含义。

不要注释：

- 已经由方法名表达清楚的简单逻辑。
- 空有 `@param` / `@return` 但没有信息量的方法头。
- 被注释掉的旧代码。

TODO/FIXME 格式：

```java
// TODO <author-name>: 补充XX处理
// FIXME <author-name>: XX缺陷
```

无用代码直接删除，不要注释保留。Git 会保存历史。

---

## Required Patterns

- Controller 方法简洁，只做路由、权限、`@Valid`、当前用户获取和 Service 调用。
- Service 返回 `ResponseDTO`。
- DAO 继承 `BaseMapper`，复杂 SQL 写 XML。
- Entity/Form/VO/DTO/BO 后缀含义固定。
- Boolean 字段使用 `Flag` 后缀。
- 涉及数据变更的重要业务接入 `DataTracerService`。
- 防重复提交接口使用 `@RepeatSubmit`。
- 需要当前请求用户时，只在 Controller 层获取并向下传递。
- 文件字段使用项目内 FileKey 序列化/反序列化能力。

---

## Forbidden Patterns

- Controller 中写业务逻辑、业务校验或数据库操作。
- DAO XML 写死枚举状态常量。
- 使用 MyBatis-Plus Wrapper 组织复杂业务查询。
- Form/VO 继承 Entity。
- Boolean 字段使用 `isXxx`。
- `@Transactional` 包住大量查询校验或依赖同类内部调用生效。
- 大段复制代码但不核对注释。
- 提交编译不过、未格式化、带无用 import、带本地配置或临时文件的代码。

---

## Commit Checklist

提交前按官方文档检查：

- [ ] 确认没有漏加版本控制，也没有提交不该提交的文件。
- [ ] 至少编译一次，避免基础编译错误。
- [ ] 提交前先更新代码并解决冲突。
- [ ] 检查格式化、无用 import、无用变量。
- [ ] 检查注释与代码一致。
- [ ] 提交信息描述具体事项；如对应禅道任务或 bug，使用 `task#id 标题 事项` 或 `bug#id 标题 事项`。

示例：

```bash
git commit -m "task#1101 开发smartreload功能 完成线程池的编码"
git commit -m "bug#1102 smartreload时间不正确 线程池的大小问题"
```

---

## Tests Required

- Controller/Service/DAO 跨层变更：至少验证接口正常、参数错误、无权限或无数据分支。
- 事务写入：验证成功、校验失败、异常回滚。
- 错误码：新增枚举时验证 `ErrorCodeRegister.initialize()` 不报错。
- DataScope：验证有权限范围和无权限范围的查询结果。
- 文件上传：验证 local/cloud 对应配置、公开/私有文件 URL。
- SmartJob：验证任务可注册、可执行、日志可查询，多实例场景不重复执行。
