package net.lab1024.sa.admin.module.business.workitem.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 工作项积分明细
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkScoreItemVO {

    private LocalDate reportDate;

    private Long workDailyReportItemId;

    private Long workItemId;

    private String workItemName;

    private Long workItemTypeId;

    private String workItemTypeName;

    private BigDecimal standardScore;

    private BigDecimal finalScore;

    private String deductReason;

    private String finishRemark;

    private List<WorkDailyReportFileVO> fileList;
}
