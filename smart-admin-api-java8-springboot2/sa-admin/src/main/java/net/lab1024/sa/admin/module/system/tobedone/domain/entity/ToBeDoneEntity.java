package net.lab1024.sa.admin.module.system.tobedone.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 待办工作
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-06-19
 */
@Data
@TableName("t_to_be_done")
public class ToBeDoneEntity {

    /**
     * 待办ID
     */
    @TableId(type = IdType.AUTO)
    private Long toBeDoneId;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 待办标题
     */
    private String title;

    /**
     * 完成状态
     */
    private Boolean doneFlag;

    /**
     * 星标状态
     */
    private Boolean starFlag;

    /**
     * 删除状态
     */
    private Boolean deletedFlag;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
