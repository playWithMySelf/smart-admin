package net.lab1024.sa.admin.module.system.tobedone.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 待办工作添加
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-06-19
 */
@Data
public class ToBeDoneAddForm {

    @Schema(description = "待办标题")
    @NotBlank(message = "待办标题不能为空")
    @Length(max = 100, message = "待办标题最多100字符")
    private String title;

    @Schema(description = "星标状态")
    private Boolean starFlag;
}
