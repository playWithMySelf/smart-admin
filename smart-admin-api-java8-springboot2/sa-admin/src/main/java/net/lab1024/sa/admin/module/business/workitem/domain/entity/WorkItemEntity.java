package net.lab1024.sa.admin.module.business.workitem.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("t_work_item")
public class WorkItemEntity {

    @TableId(type = IdType.AUTO)
    private Long workItemId;

    /**
     * 工作项类型ID
     */
    private Long workItemTypeId;

    /**
     * 工作项名称
     */
    private String workItemName;

    /**
     * 工作项描述
     */
    private String description;

    /**
     * 评分标准
     */
    private String scoreStandard;

    /**
     * 标准分
     */
    private BigDecimal standardScore;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 禁用状态
     */
    private Boolean disabledFlag;

    /**
     * 删除状态
     */
    private Boolean deletedFlag;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;
}
