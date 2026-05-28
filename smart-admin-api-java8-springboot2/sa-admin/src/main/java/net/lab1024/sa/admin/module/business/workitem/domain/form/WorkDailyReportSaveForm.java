package net.lab1024.sa.admin.module.business.workitem.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * 工作项日报保存草稿
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkDailyReportSaveForm {

    @Schema(description = "日报ID")
    private Long workDailyReportId;

    @Schema(description = "日报日期")
    @NotNull(message = "日报日期不能为空")
    private LocalDate reportDate;

    @Schema(description = "日报明细")
    @Valid
    @NotEmpty(message = "请至少添加一条工作项")
    @Size(max = 100, message = "单张日报最多100条明细")
    private List<WorkDailyReportItemForm> itemList;
}
