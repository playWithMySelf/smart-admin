package net.lab1024.sa.admin.module.business.workitem.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作项类型
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
@TableName("t_work_item_type")
public class WorkItemTypeEntity {

    @TableId(type = IdType.AUTO)
    private Long workItemTypeId;

    /**
     * 类型名称
     */
    private String typeName;

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
