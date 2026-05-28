package net.lab1024.sa.admin.module.business.workitem.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作项日报审核历史
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
@TableName("t_work_daily_report_audit")
public class WorkDailyReportAuditEntity {

    @TableId(type = IdType.AUTO)
    private Long workDailyReportAuditId;

    /**
     * 日报ID
     */
    private Long workDailyReportId;

    /**
     * 审核结果
     */
    private Integer auditResult;

    /**
     * 审核失败原因
     */
    private String failReason;

    /**
     * 审核人ID
     */
    private Long auditEmployeeId;

    /**
     * 审核人姓名
     */
    private String auditEmployeeName;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
}
