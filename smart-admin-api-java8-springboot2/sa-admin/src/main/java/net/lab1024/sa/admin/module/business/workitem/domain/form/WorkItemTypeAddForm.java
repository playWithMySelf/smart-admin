package net.lab1024.sa.admin.module.business.workitem.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 工作项类型添加
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkItemTypeAddForm {

    @Schema(description = "类型名称")
    @NotBlank(message = "类型名称不能为空")
    @Length(max = 50, message = "类型名称最多50字符")
    private String typeName;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "禁用状态")
    private Boolean disabledFlag;
}
