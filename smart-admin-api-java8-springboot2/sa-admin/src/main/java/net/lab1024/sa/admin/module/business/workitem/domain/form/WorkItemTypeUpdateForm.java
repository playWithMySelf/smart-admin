package net.lab1024.sa.admin.module.business.workitem.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 工作项类型更新
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkItemTypeUpdateForm {

    @Schema(description = "工作项类型ID")
    @NotNull(message = "工作项类型ID不能为空")
    private Long workItemTypeId;

    @Schema(description = "类型名称")
    @NotBlank(message = "类型名称不能为空")
    @Length(max = 50, message = "类型名称最多50字符")
    private String typeName;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "禁用状态")
    private Boolean disabledFlag;
}
