package net.lab1024.sa.admin.module.business.workitem.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 员工日期积分汇总
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-30
 */
@Data
public class WorkScoreEmployeeDateVO {

    private Long employeeId;

    private String employeeName;

    private LocalDate reportDate;

    private BigDecimal totalScore;
}
