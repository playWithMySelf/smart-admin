package net.lab1024.sa.admin.module.business.workitem.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 员工积分汇总
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkScoreEmployeeVO {

    private Long employeeId;

    private String employeeName;

    private Long departmentId;

    private String departmentName;

    private Integer reportCount;

    private Integer itemCount;

    private BigDecimal totalScore;
}
