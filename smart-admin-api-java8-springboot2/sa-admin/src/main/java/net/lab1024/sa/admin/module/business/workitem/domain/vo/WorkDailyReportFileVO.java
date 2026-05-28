package net.lab1024.sa.admin.module.business.workitem.domain.vo;

import lombok.Data;

/**
 * 工作项日报明细图片
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkDailyReportFileVO {

    private Long workDailyReportFileId;

    private Long workDailyReportItemId;

    private Long fileId;

    private String fileKey;

    private String fileName;

    private String fileUrl;
}
