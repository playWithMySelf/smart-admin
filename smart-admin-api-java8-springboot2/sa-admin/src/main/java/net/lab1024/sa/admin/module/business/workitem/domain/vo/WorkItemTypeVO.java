package net.lab1024.sa.admin.module.business.workitem.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作项类型
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkItemTypeVO {

    private Long workItemTypeId;

    private String typeName;

    private Integer sort;

    private Boolean disabledFlag;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
