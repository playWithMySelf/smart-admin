package net.lab1024.sa.admin.module.business.workitem.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 工作项日报明细
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkDailyReportItemVO {

    private Long workDailyReportItemId;

    private Long workDailyReportId;

    private Long workItemId;

    private Long workItemTypeId;

    private String workItemTypeName;

    private String workItemName;

    private String description;

    private String scoreStandard;

    private BigDecimal standardScore;

    private BigDecimal finalScore;

    private String deductReason;

    private String finishRemark;

    private Integer sort;

    private LocalDateTime createTime;

    private List<WorkDailyReportFileVO> fileList;
}
