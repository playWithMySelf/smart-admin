package net.lab1024.sa.admin.module.business.workitem.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 工作项
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkItemVO {

    private Long workItemId;

    private Long workItemTypeId;

    private String workItemTypeName;

    private String workItemName;

    private String description;

    private String scoreStandard;

    private BigDecimal standardScore;

    private Integer sort;

    private Boolean disabledFlag;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
