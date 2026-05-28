package net.lab1024.sa.admin.module.business.workitem.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 工作项积分报表查询
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkScoreReportQueryForm {

    @Schema(description = "开始日期")
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @Schema(description = "员工ID")
    private Long employeeId;

    @Schema(description = "部门ID")
    private Long departmentId;

    @Schema(description = "日报日期")
    private LocalDate reportDate;

    @Schema(description = "工作项类型ID")
    private Long workItemTypeId;
}
