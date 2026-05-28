package net.lab1024.sa.admin.module.business.workitem.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 工作项日报审核
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkDailyReportAuditForm {

    @Schema(description = "日报ID")
    @NotNull(message = "日报ID不能为空")
    private Long workDailyReportId;

    @Schema(description = "是否通过")
    @NotNull(message = "审核结果不能为空")
    private Boolean passFlag;

    @Schema(description = "失败原因")
    @Length(max = 1000, message = "失败原因最多1000字符")
    private String failReason;

    @Schema(description = "审核明细")
    @Valid
    private List<WorkDailyReportAuditItemForm> itemList;
}
