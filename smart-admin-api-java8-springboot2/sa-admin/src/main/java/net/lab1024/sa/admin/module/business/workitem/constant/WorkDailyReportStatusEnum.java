package net.lab1024.sa.admin.module.business.workitem.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.enumeration.BaseEnum;

/**
 * 工作项日报状态
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Getter
@AllArgsConstructor
public enum WorkDailyReportStatusEnum implements BaseEnum {

    DRAFT(1, "草稿"),

    WAIT_AUDIT(2, "待审核"),

    AUDIT_PASS(3, "审核通过"),

    AUDIT_FAIL(4, "审核失败"),

    ;

    private final Integer value;

    private final String desc;
}
