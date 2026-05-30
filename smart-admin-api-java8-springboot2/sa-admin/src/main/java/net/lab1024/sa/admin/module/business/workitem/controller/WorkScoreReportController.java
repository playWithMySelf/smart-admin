package net.lab1024.sa.admin.module.business.workitem.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.admin.constant.AdminSwaggerTagConst;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkScoreReportQueryForm;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkScoreDateVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkScoreEmployeeVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkScoreEmployeeDateVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkScoreItemVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkScoreTypeVO;
import net.lab1024.sa.admin.module.business.workitem.service.WorkScoreReportService;
import net.lab1024.sa.admin.module.system.department.domain.vo.DepartmentTreeVO;
import net.lab1024.sa.admin.util.AdminRequestUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.module.support.operatelog.annotation.OperateLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 工作项积分报表
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@RestController
@Tag(name = AdminSwaggerTagConst.Business.WORK_ITEM)
@OperateLog
public class WorkScoreReportController {

    @Resource
    private WorkScoreReportService workScoreReportService;

    @Operation(summary = "查询员工积分总分 @author jinwei")
    @PostMapping("/workitem/score/report/employee")
    @SaCheckPermission("workitem:score:report")
    public ResponseDTO<List<WorkScoreEmployeeVO>> queryEmployeeScore(@RequestBody @Valid WorkScoreReportQueryForm queryForm) {
        return workScoreReportService.queryEmployeeScore(AdminRequestUtil.getRequestUser(), queryForm);
    }

    @Operation(summary = "查询积分报表可见机构树 @author jinwei")
    @GetMapping("/workitem/score/report/department/tree")
    @SaCheckPermission("workitem:score:report")
    public ResponseDTO<List<DepartmentTreeVO>> queryDepartmentTree() {
        return workScoreReportService.queryDepartmentTree(AdminRequestUtil.getRequestUser());
    }

    @Operation(summary = "查询员工日期积分明细 @author jinwei")
    @PostMapping("/workitem/score/report/date")
    @SaCheckPermission("workitem:score:report")
    public ResponseDTO<List<WorkScoreDateVO>> queryDateScore(@RequestBody @Valid WorkScoreReportQueryForm queryForm) {
        return workScoreReportService.queryDateScore(AdminRequestUtil.getRequestUser(), queryForm);
    }

    @Operation(summary = "查询员工日期积分汇总 @author jinwei")
    @PostMapping("/workitem/score/report/employee-date")
    @SaCheckPermission("workitem:score:report")
    public ResponseDTO<List<WorkScoreEmployeeDateVO>> queryEmployeeDateScore(@RequestBody @Valid WorkScoreReportQueryForm queryForm) {
        return workScoreReportService.queryEmployeeDateScore(AdminRequestUtil.getRequestUser(), queryForm);
    }

    @Operation(summary = "查询工作项类型积分明细 @author jinwei")
    @PostMapping("/workitem/score/report/type")
    @SaCheckPermission("workitem:score:report")
    public ResponseDTO<List<WorkScoreTypeVO>> queryTypeScore(@RequestBody @Valid WorkScoreReportQueryForm queryForm) {
        return workScoreReportService.queryTypeScore(AdminRequestUtil.getRequestUser(), queryForm);
    }

    @Operation(summary = "查询工作项积分明细 @author jinwei")
    @PostMapping("/workitem/score/report/item")
    @SaCheckPermission("workitem:score:report")
    public ResponseDTO<List<WorkScoreItemVO>> queryItemScore(@RequestBody @Valid WorkScoreReportQueryForm queryForm) {
        return workScoreReportService.queryItemScore(AdminRequestUtil.getRequestUser(), queryForm);
    }
}
