package net.lab1024.sa.admin.module.business.workitem.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 工作项日报
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkDailyReportVO {

    private Long workDailyReportId;

    private Long employeeId;

    private String employeeName;

    private Long departmentId;

    private String departmentName;

    private LocalDate reportDate;

    private Integer status;

    private BigDecimal totalScore;

    private LocalDateTime submitTime;

    private Long latestAuditEmployeeId;

    private String latestAuditEmployeeName;

    private LocalDateTime latestAuditTime;

    private String latestFailReason;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<WorkDailyReportItemVO> itemList;

    private List<WorkDailyReportAuditVO> auditList;
}
