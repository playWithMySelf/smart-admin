package net.lab1024.sa.admin.module.business.workitem.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 工作项日报明细图片
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkDailyReportFileForm {

    @Schema(description = "文件ID")
    private Long fileId;

    @Schema(description = "文件key")
    @NotBlank(message = "文件key不能为空")
    private String fileKey;

    @Schema(description = "文件名称")
    private String fileName;
}
