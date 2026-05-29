# 日报审核后发送站内信

## Goal

日报审核完成后，无论审核通过还是审核失败，都给该日报所属员工发送站内信，让提交人能及时知道审核结果。

## What I already know

* 用户明确要求“日报审核完后，不管成功与否，需要给这个人发站内信（已有功能）”。
* 日报审核入口位于 `smart-admin-api-java8-springboot2/sa-admin/src/main/java/net/lab1024/sa/admin/module/business/workitem/service/WorkDailyReportService.java` 的 `audit` 方法。
* 已有站内信能力位于 `smart-admin-api-java8-springboot2/sa-base/src/main/java/net/lab1024/sa/base/module/support/message/service/MessageService.java`。
* 站内信发送表单为 `MessageSendForm`，消息类型使用 `MessageTypeEnum.MAIL`，接收人类型使用 `UserTypeEnum.ADMIN_EMPLOYEE`。

## Requirements

* 审核通过后，给日报所属员工发送一条站内信。
* 审核失败后，也给日报所属员工发送一条站内信。
* 站内信标题和内容要能区分审核通过、审核失败，并包含日报日期。
* 审核失败时，站内信内容包含失败原因。
* 站内信与审核主流程弱关联，发送失败不能影响审核结果。
* 复用已有站内信能力，不新增消息表或额外通知通道。

## Acceptance Criteria

* [ ] 审核通过保存日报状态和审核历史后，尽力生成接收人为日报员工的站内信。
* [ ] 审核失败保存日报状态和失败原因后，尽力生成接收人为日报员工的站内信。
* [ ] 原有审核校验失败时不发送站内信。
* [ ] 站内信发送异常时，审核接口仍返回成功，日报状态和审核历史不回滚。
* [ ] 后端编译或可用的质量检查通过。

## Definition of Done

* 代码改动尽量收敛在日报审核服务。
* 复用 `MessageService`、`MessageSendForm`、`MessageTypeEnum`、`UserTypeEnum`。
* 不改变前端接口入参和返回结构。
* 不新增数据库结构。

## Technical Approach

推荐方案：在 `WorkDailyReportService#audit` 的事务成功路径中，完成日报状态更新和审核历史插入后，调用私有方法构造并发送站内信。站内信是弱关联通知，发送异常只记录日志，不向外抛出，避免通知失败影响审核结果。

## Out of Scope

* 不做短信、邮件、即时推送等额外通知方式。
* 不新增可配置通知模板。
* 不改前端页面展示。

## Technical Notes

* `MessageService#sendMessage` 会校验 `MessageSendForm`，然后批量写入 `t_message`。
* `MessageSendForm#dataId` 可关联业务 ID，本任务使用 `workDailyReportId`。
* 当前修改是后端单点增强，用户已确认站内信与审核不强关联，通知失败不应导致审核回滚。
