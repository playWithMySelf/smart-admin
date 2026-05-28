package net.lab1024.sa.admin.module.business.workitem.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 日期积分汇总
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkScoreDateVO {

    private LocalDate reportDate;

    private Integer itemCount;

    private BigDecimal totalScore;
}
