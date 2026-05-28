package net.lab1024.sa.admin.module.business.workitem.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 工作项日报明细保存
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkDailyReportItemForm {

    @Schema(description = "工作项ID")
    @NotNull(message = "工作项不能为空")
    private Long workItemId;

    @Schema(description = "完成说明")
    @Length(max = 2000, message = "完成说明最多2000字符")
    private String finishRemark;

    @Schema(description = "图片列表")
    @Valid
    @Size(max = 20, message = "每条明细最多上传20张图片")
    private List<WorkDailyReportFileForm> fileList;
}
