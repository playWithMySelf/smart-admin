package net.lab1024.sa.admin.module.system.tobedone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.admin.constant.AdminSwaggerTagConst;
import net.lab1024.sa.admin.module.system.tobedone.domain.form.ToBeDoneAddForm;
import net.lab1024.sa.admin.module.system.tobedone.domain.form.ToBeDoneUpdateForm;
import net.lab1024.sa.admin.module.system.tobedone.domain.vo.ToBeDoneVO;
import net.lab1024.sa.admin.module.system.tobedone.service.ToBeDoneService;
import net.lab1024.sa.admin.util.AdminRequestUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.module.support.operatelog.annotation.OperateLog;
import net.lab1024.sa.base.module.support.repeatsubmit.annoation.RepeatSubmit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 待办工作
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-06-19
 */
@RestController
@Tag(name = AdminSwaggerTagConst.System.SYSTEM_TO_BE_DONE)
@OperateLog
public class ToBeDoneController {

    @Resource
    private ToBeDoneService toBeDoneService;

    @Operation(summary = "查询我的待办工作 @author jinwei")
    @GetMapping("/to-be-done/list")
    public ResponseDTO<List<ToBeDoneVO>> queryList() {
        return toBeDoneService.queryList(AdminRequestUtil.getRequestUser());
    }

    @Operation(summary = "查询我的未完成待办数量 @author jinwei")
    @GetMapping("/to-be-done/count")
    public ResponseDTO<Integer> queryCount() {
        return toBeDoneService.queryCount(AdminRequestUtil.getRequestUser());
    }

    @Operation(summary = "新增待办工作 @author jinwei")
    @PostMapping("/to-be-done/add")
    @RepeatSubmit
    public ResponseDTO<String> add(@RequestBody @Valid ToBeDoneAddForm addForm) {
        return toBeDoneService.add(AdminRequestUtil.getRequestUser(), addForm);
    }

    @Operation(summary = "更新待办工作 @author jinwei")
    @PostMapping("/to-be-done/update")
    public ResponseDTO<String> update(@RequestBody @Valid ToBeDoneUpdateForm updateForm) {
        return toBeDoneService.update(AdminRequestUtil.getRequestUser(), updateForm);
    }

    @Operation(summary = "删除待办工作 @author jinwei")
    @GetMapping("/to-be-done/delete/{toBeDoneId}")
    @RepeatSubmit
    public ResponseDTO<String> delete(@PathVariable Long toBeDoneId) {
        return toBeDoneService.delete(AdminRequestUtil.getRequestUser(), toBeDoneId);
    }
}
