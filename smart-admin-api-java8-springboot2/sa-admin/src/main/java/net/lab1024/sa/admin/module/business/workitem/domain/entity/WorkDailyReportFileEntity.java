package net.lab1024.sa.admin.module.business.workitem.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作项日报明细图片
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
@TableName("t_work_daily_report_file")
public class WorkDailyReportFileEntity {

    @TableId(type = IdType.AUTO)
    private Long workDailyReportFileId;

    /**
     * 日报明细ID
     */
    private Long workDailyReportItemId;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 文件key
     */
    private String fileKey;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 排序
     */
    private Integer sort;

    private LocalDateTime createTime;
}
