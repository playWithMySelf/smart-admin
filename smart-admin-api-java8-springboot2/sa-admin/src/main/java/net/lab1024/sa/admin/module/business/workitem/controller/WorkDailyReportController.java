package net.lab1024.sa.admin.module.business.workitem.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.admin.constant.AdminSwaggerTagConst;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkDailyReportAuditForm;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkDailyReportQueryForm;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkDailyReportSaveForm;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkDailyReportVO;
import net.lab1024.sa.admin.module.business.workitem.service.WorkDailyReportService;
import net.lab1024.sa.admin.module.system.login.domain.RequestEmployee;
import net.lab1024.sa.admin.util.AdminRequestUtil;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.module.support.operatelog.annotation.OperateLog;
import net.lab1024.sa.base.module.support.repeatsubmit.annoation.RepeatSubmit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 工作项日报
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@RestController
@Tag(name = AdminSwaggerTagConst.Business.WORK_ITEM)
@OperateLog
public class WorkDailyReportController {

    @Resource
    private WorkDailyReportService workDailyReportService;

    @Operation(summary = "分页查询我的日报 @author jinwei")
    @PostMapping("/workitem/daily/my/page/query")
    @SaCheckPermission("workitem:daily:my")
    public ResponseDTO<PageResult<WorkDailyReportVO>> queryMyPage(@RequestBody @Valid WorkDailyReportQueryForm queryForm) {
        return workDailyReportService.queryMyPage(AdminRequestUtil.getRequestUser(), queryForm);
    }

    @Operation(summary = "查询我的日报详情 @author jinwei")
    @GetMapping("/workitem/daily/my/detail/{workDailyReportId}")
    @SaCheckPermission("workitem:daily:my")
    public ResponseDTO<WorkDailyReportVO> getMyDetail(@PathVariable Long workDailyReportId) {
        return workDailyReportService.getMyDetail(AdminRequestUtil.getRequestUser(), workDailyReportId);
    }

    @Operation(summary = "保存我的日报草稿 @author jinwei")
    @PostMapping("/workitem/daily/my/save")
    @SaCheckPermission("workitem:daily:save")
    @RepeatSubmit
    public ResponseDTO<String> saveDraft(@RequestBody @Valid WorkDailyReportSaveForm saveForm) {
        RequestEmployee requestEmployee = AdminRequestUtil.getRequestUser();
        return workDailyReportService.saveDraft(requestEmployee, saveForm);
    }

    @Operation(summary = "提交我的日报 @author jinwei")
    @GetMapping("/workitem/daily/my/submit/{workDailyReportId}")
    @SaCheckPermission("workitem:daily:submit")
    @RepeatSubmit
    public ResponseDTO<String> submit(@PathVariable Long workDailyReportId) {
        return workDailyReportService.submit(AdminRequestUtil.getRequestUser(), workDailyReportId);
    }

    @Operation(summary = "分页查询待审核日报 @author jinwei")
    @PostMapping("/workitem/daily/review/page/query")
    @SaCheckPermission("workitem:daily:review")
    public ResponseDTO<PageResult<WorkDailyReportVO>> queryReviewPage(@RequestBody @Valid WorkDailyReportQueryForm queryForm) {
        return workDailyReportService.queryReviewPage(queryForm);
    }

    @Operation(summary = "查询审核日报详情 @author jinwei")
    @GetMapping("/workitem/daily/review/detail/{workDailyReportId}")
    @SaCheckPermission("workitem:daily:review")
    public ResponseDTO<WorkDailyReportVO> getReviewDetail(@PathVariable Long workDailyReportId) {
        return workDailyReportService.getReviewDetail(workDailyReportId);
    }

    @Operation(summary = "审核日报 @author jinwei")
    @PostMapping("/workitem/daily/review/audit")
    @SaCheckPermission("workitem:daily:audit")
    @RepeatSubmit
    public ResponseDTO<String> audit(@RequestBody @Valid WorkDailyReportAuditForm auditForm) {
        return workDailyReportService.audit(AdminRequestUtil.getRequestUser(), auditForm);
    }
}
