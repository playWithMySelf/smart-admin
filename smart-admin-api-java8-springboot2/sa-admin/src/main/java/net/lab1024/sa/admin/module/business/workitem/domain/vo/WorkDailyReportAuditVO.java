package net.lab1024.sa.admin.module.business.workitem.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作项日报审核历史
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkDailyReportAuditVO {

    private Long workDailyReportAuditId;

    private Long workDailyReportId;

    private Integer auditResult;

    private String failReason;

    private Long auditEmployeeId;

    private String auditEmployeeName;

    private LocalDateTime auditTime;
}
