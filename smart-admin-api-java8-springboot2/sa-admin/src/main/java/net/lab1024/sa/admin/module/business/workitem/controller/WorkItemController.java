package net.lab1024.sa.admin.module.business.workitem.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.admin.constant.AdminSwaggerTagConst;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkItemAddForm;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkItemQueryForm;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkItemTypeAddForm;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkItemTypeUpdateForm;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkItemUpdateForm;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkItemTypeVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkItemVO;
import net.lab1024.sa.admin.module.business.workitem.service.WorkItemService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.module.support.operatelog.annotation.OperateLog;
import net.lab1024.sa.base.module.support.repeatsubmit.annoation.RepeatSubmit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 工作项维护
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@RestController
@Tag(name = AdminSwaggerTagConst.Business.WORK_ITEM)
@OperateLog
public class WorkItemController {

    @Resource
    private WorkItemService workItemService;

    @Operation(summary = "查询工作项类型 @author jinwei")
    @GetMapping("/workitem/type/query")
    @SaCheckPermission("workitem:type:query")
    public ResponseDTO<List<WorkItemTypeVO>> queryTypeList(@RequestParam(value = "disabledFlag", required = false) Boolean disabledFlag) {
        return workItemService.queryTypeList(disabledFlag);
    }

    @Operation(summary = "添加工作项类型 @author jinwei")
    @PostMapping("/workitem/type/add")
    @SaCheckPermission("workitem:type:add")
    @RepeatSubmit
    public ResponseDTO<String> addType(@RequestBody @Valid WorkItemTypeAddForm addForm) {
        return workItemService.addType(addForm);
    }

    @Operation(summary = "更新工作项类型 @author jinwei")
    @PostMapping("/workitem/type/update")
    @SaCheckPermission("workitem:type:update")
    @RepeatSubmit
    public ResponseDTO<String> updateType(@RequestBody @Valid WorkItemTypeUpdateForm updateForm) {
        return workItemService.updateType(updateForm);
    }

    @Operation(summary = "删除工作项类型 @author jinwei")
    @GetMapping("/workitem/type/delete/{workItemTypeId}")
    @SaCheckPermission("workitem:type:delete")
    public ResponseDTO<String> deleteType(@PathVariable Long workItemTypeId) {
        return workItemService.deleteType(workItemTypeId);
    }

    @Operation(summary = "分页查询工作项 @author jinwei")
    @PostMapping("/workitem/item/page/query")
    @SaCheckPermission("workitem:item:query")
    public ResponseDTO<PageResult<WorkItemVO>> queryItemPage(@RequestBody @Valid WorkItemQueryForm queryForm) {
        return workItemService.queryItemPage(queryForm);
    }

    @Operation(summary = "查询可选工作项 @author jinwei")
    @GetMapping("/workitem/item/list")
    @SaCheckPermission("workitem:item:query")
    public ResponseDTO<List<WorkItemVO>> queryItemList(@RequestParam(value = "workItemTypeId", required = false) Long workItemTypeId,
                                                       @RequestParam(value = "keywords", required = false) String keywords) {
        return workItemService.queryItemList(workItemTypeId, keywords);
    }

    @Operation(summary = "查询工作项详情 @author jinwei")
    @GetMapping("/workitem/item/detail/{workItemId}")
    @SaCheckPermission("workitem:item:query")
    public ResponseDTO<WorkItemVO> getItemDetail(@PathVariable Long workItemId) {
        return workItemService.getItemDetail(workItemId);
    }

    @Operation(summary = "添加工作项 @author jinwei")
    @PostMapping("/workitem/item/add")
    @SaCheckPermission("workitem:item:add")
    @RepeatSubmit
    public ResponseDTO<String> addItem(@RequestBody @Valid WorkItemAddForm addForm) {
        return workItemService.addItem(addForm);
    }

    @Operation(summary = "更新工作项 @author jinwei")
    @PostMapping("/workitem/item/update")
    @SaCheckPermission("workitem:item:update")
    @RepeatSubmit
    public ResponseDTO<String> updateItem(@RequestBody @Valid WorkItemUpdateForm updateForm) {
        return workItemService.updateItem(updateForm);
    }

    @Operation(summary = "删除工作项 @author jinwei")
    @GetMapping("/workitem/item/delete/{workItemId}")
    @SaCheckPermission("workitem:item:delete")
    public ResponseDTO<String> deleteItem(@PathVariable Long workItemId) {
        return workItemService.deleteItem(workItemId);
    }
}
