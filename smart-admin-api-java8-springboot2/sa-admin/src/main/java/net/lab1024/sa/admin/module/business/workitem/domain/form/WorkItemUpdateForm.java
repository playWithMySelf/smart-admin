package net.lab1024.sa.admin.module.business.workitem.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 工作项更新
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Data
public class WorkItemUpdateForm extends WorkItemAddForm {

    @Schema(description = "工作项ID")
    @NotNull(message = "工作项ID不能为空")
    private Long workItemId;
}
