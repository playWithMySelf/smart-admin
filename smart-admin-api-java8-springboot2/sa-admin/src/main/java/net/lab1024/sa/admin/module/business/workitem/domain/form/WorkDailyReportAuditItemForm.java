package net.lab1024.sa.admin.module.business.workitem.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 工作项日报审核明细
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkDailyReportAuditItemForm {

    @Schema(description = "日报明细ID")
    @NotNull(message = "日报明细ID不能为空")
    private Long workDailyReportItemId;

    @Schema(description = "最终分")
    @NotNull(message = "最终分不能为空")
    private BigDecimal finalScore;

    @Schema(description = "扣分原因")
    @Length(max = 1000, message = "扣分原因最多1000字符")
    private String deductReason;
}
