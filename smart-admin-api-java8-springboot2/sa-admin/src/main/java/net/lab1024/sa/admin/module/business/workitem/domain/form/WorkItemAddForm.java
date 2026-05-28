package net.lab1024.sa.admin.module.business.workitem.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 工作项添加
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkItemAddForm {

    @Schema(description = "工作项类型ID")
    @NotNull(message = "工作项类型不能为空")
    private Long workItemTypeId;

    @Schema(description = "工作项名称")
    @NotBlank(message = "工作项名称不能为空")
    @Length(max = 100, message = "工作项名称最多100字符")
    private String workItemName;

    @Schema(description = "工作项描述")
    @Length(max = 1000, message = "工作项描述最多1000字符")
    private String description;

    @Schema(description = "评分标准")
    @Length(max = 2000, message = "评分标准最多2000字符")
    private String scoreStandard;

    @Schema(description = "标准分")
    @NotNull(message = "标准分不能为空")
    private BigDecimal standardScore;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "禁用状态")
    private Boolean disabledFlag;
}
