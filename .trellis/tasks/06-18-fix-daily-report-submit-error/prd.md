# fix daily report submit error

## Goal

修复日报页面点击“提交审核”时的报错，确保重复添加工作项后提交路径和保存草稿路径使用同一份最新明细数据。

## What I already know

- 用户已经执行数据库索引脚本，`t_work_daily_report_item` 不应再保留 `uk_report_work_item` 唯一索引。
- 小程序提交前会先调用 `saveDraft(true)`，保存成功并拿到 `detail.workDailyReportId` 后再提交。
- Web 端当前 `submitDaily()` 直接调用提交接口，不会先保存页面上新增或修改的明细。
- 如果页面上新增明细尚未保存，后端提交时只能读取数据库里的旧明细，可能返回“请至少添加一条工作项”或提交旧数据。
- 用户提供的服务端日志显示实际提交报错为 `NoSuchMethodError: WorkDailyReportService.access$000(...)`，发生在事务提交后的站内信回调 `WorkDailyReportService$1.afterCommit`。
- `access$000` 是 Java 为匿名内部类访问外部类私有方法生成的合成桥接方法；该错误通常说明外部类和内部类 class 文件版本不一致，或热替换/增量部署只刷新了一部分 class。

## Requirements

- Web 端点击“提交审核”前必须先保存当前页面明细。
- 保存失败时不得继续提交。
- 保存成功后必须使用刷新后的 `detail.workDailyReportId` 调用提交接口。
- 后端事务 afterCommit 回调不能依赖私有方法合成桥接方法，降低增量编译/部署时的 `NoSuchMethodError` 风险。
- 不改变小程序已存在的提交前保存逻辑。

## Acceptance Criteria

- [ ] Web 端新增重复工作项后直接点击提交，会先保存草稿，再提交审核。
- [ ] Web 端保存失败时不会继续提交。
- [ ] 服务端 `WorkDailyReportService$1/$2` 字节码不再调用 `access$000/access$100` 合成桥接方法。
- [ ] 后端编译通过，Web 构建通过。

## Technical Notes

- Web 页面：`smart-admin-web-typescript/src/views/business/workitem/my-daily-report.vue`
- 小程序页面：`smart-app/src/pages/workitem/my-daily-report.vue`
- 后端提交：`WorkDailyReportService.submit(...)`
