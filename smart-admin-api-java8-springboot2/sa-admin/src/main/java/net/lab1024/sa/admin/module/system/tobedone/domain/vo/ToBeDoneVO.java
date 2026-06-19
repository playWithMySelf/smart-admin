package net.lab1024.sa.admin.module.system.tobedone.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 待办工作
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-06-19
 */
@Data
public class ToBeDoneVO {

    private Long toBeDoneId;

    private String title;

    private Boolean doneFlag;

    private Boolean starFlag;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
