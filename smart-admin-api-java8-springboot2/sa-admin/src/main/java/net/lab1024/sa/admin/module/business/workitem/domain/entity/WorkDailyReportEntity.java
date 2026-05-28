package net.lab1024.sa.admin.module.business.workitem.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工作项日报
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
@TableName("t_work_daily_report")
public class WorkDailyReportEntity {

    @TableId(type = IdType.AUTO)
    private Long workDailyReportId;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 日报日期
     */
    private LocalDate reportDate;

    /**
     * 日报状态
     */
    private Integer status;

    /**
     * 总分
     */
    private BigDecimal totalScore;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 最新审核人ID
     */
    private Long latestAuditEmployeeId;

    /**
     * 最新审核人姓名
     */
    private String latestAuditEmployeeName;

    /**
     * 最新审核时间
     */
    private LocalDateTime latestAuditTime;

    /**
     * 最新审核失败原因
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String latestFailReason;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;
}
