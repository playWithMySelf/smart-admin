package net.lab1024.sa.admin.module.business.workitem.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 工作项日报明细
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
@TableName("t_work_daily_report_item")
public class WorkDailyReportItemEntity {

    @TableId(type = IdType.AUTO)
    private Long workDailyReportItemId;

    /**
     * 日报ID
     */
    private Long workDailyReportId;

    /**
     * 工作项ID
     */
    private Long workItemId;

    /**
     * 工作项类型ID
     */
    private Long workItemTypeId;

    /**
     * 工作项类型名称快照
     */
    private String workItemTypeName;

    /**
     * 工作项名称快照
     */
    private String workItemName;

    /**
     * 工作项描述快照
     */
    private String description;

    /**
     * 评分标准快照
     */
    private String scoreStandard;

    /**
     * 标准分快照
     */
    private BigDecimal standardScore;

    /**
     * 最终分
     */
    private BigDecimal finalScore;

    /**
     * 扣分原因
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String deductReason;

    /**
     * 完成说明
     */
    private String finishRemark;

    /**
     * 排序
     */
    private Integer sort;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;
}
