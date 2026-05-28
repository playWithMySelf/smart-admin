package net.lab1024.sa.admin.module.business.workitem.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;

import java.time.LocalDate;

/**
 * 工作项日报查询
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkDailyReportQueryForm extends PageParam {

    @Schema(description = "员工ID")
    private Long employeeId;

    @Schema(description = "部门ID")
    private Long departmentId;

    @Schema(description = "关键字")
    private String keywords;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;
}
