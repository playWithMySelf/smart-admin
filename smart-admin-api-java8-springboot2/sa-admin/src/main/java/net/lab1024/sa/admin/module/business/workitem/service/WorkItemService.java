package net.lab1024.sa.admin.module.business.workitem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.workitem.dao.WorkItemDao;
import net.lab1024.sa.admin.module.business.workitem.dao.WorkItemTypeDao;
import net.lab1024.sa.admin.module.business.workitem.domain.entity.WorkItemEntity;
import net.lab1024.sa.admin.module.business.workitem.domain.entity.WorkItemTypeEntity;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkItemAddForm;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkItemQueryForm;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkItemTypeAddForm;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkItemTypeUpdateForm;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkItemUpdateForm;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkItemTypeVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkItemVO;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 工作项维护
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Service
public class WorkItemService {

    @Resource
    private WorkItemTypeDao workItemTypeDao;

    @Resource
    private WorkItemDao workItemDao;

    /**
     * 查询类型
     */
    public ResponseDTO<List<WorkItemTypeVO>> queryTypeList(Boolean disabledFlag) {
        return ResponseDTO.ok(workItemTypeDao.queryList(disabledFlag, Boolean.FALSE));
    }

    /**
     * 添加类型
     */
    public ResponseDTO<String> addType(WorkItemTypeAddForm addForm) {
        WorkItemTypeEntity existEntity = workItemTypeDao.queryByName(addForm.getTypeName(), null, Boolean.FALSE);
        if (Objects.nonNull(existEntity)) {
            return ResponseDTO.userErrorParam("工作项类型名称重复");
        }
        WorkItemTypeEntity entity = SmartBeanUtil.copy(addForm, WorkItemTypeEntity.class);
        entity.setSort(entity.getSort() == null ? 0 : entity.getSort());
        entity.setDisabledFlag(Boolean.TRUE.equals(entity.getDisabledFlag()));
        entity.setDeletedFlag(Boolean.FALSE);
        workItemTypeDao.insert(entity);
        return ResponseDTO.ok();
    }

    /**
     * 更新类型
     */
    public ResponseDTO<String> updateType(WorkItemTypeUpdateForm updateForm) {
        WorkItemTypeEntity dbEntity = workItemTypeDao.selectById(updateForm.getWorkItemTypeId());
        if (Objects.isNull(dbEntity) || Boolean.TRUE.equals(dbEntity.getDeletedFlag())) {
            return ResponseDTO.userErrorParam("工作项类型不存在");
        }
        WorkItemTypeEntity existEntity = workItemTypeDao.queryByName(updateForm.getTypeName(), updateForm.getWorkItemTypeId(), Boolean.FALSE);
        if (Objects.nonNull(existEntity)) {
            return ResponseDTO.userErrorParam("工作项类型名称重复");
        }
        WorkItemTypeEntity updateEntity = SmartBeanUtil.copy(updateForm, WorkItemTypeEntity.class);
        updateEntity.setSort(updateEntity.getSort() == null ? 0 : updateEntity.getSort());
        updateEntity.setDisabledFlag(Boolean.TRUE.equals(updateEntity.getDisabledFlag()));
        workItemTypeDao.updateById(updateEntity);
        return ResponseDTO.ok();
    }

    /**
     * 删除类型
     */
    public ResponseDTO<String> deleteType(Long workItemTypeId) {
        WorkItemTypeEntity dbEntity = workItemTypeDao.selectById(workItemTypeId);
        if (Objects.isNull(dbEntity) || Boolean.TRUE.equals(dbEntity.getDeletedFlag())) {
            return ResponseDTO.userErrorParam("工作项类型不存在");
        }
        Integer itemCount = workItemDao.countByType(workItemTypeId, Boolean.FALSE);
        if (itemCount != null && itemCount > 0) {
            return ResponseDTO.userErrorParam("请先删除该类型下的工作项");
        }
        workItemTypeDao.updateDeletedFlag(workItemTypeId, Boolean.TRUE);
        return ResponseDTO.ok();
    }

    /**
     * 分页查询工作项
     */
    public ResponseDTO<PageResult<WorkItemVO>> queryItemPage(WorkItemQueryForm queryForm) {
        queryForm.setDeletedFlag(Boolean.FALSE);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<WorkItemVO> list = workItemDao.queryPage(page, queryForm);
        return ResponseDTO.ok(SmartPageUtil.convert2PageResult(page, list));
    }

    /**
     * 查询可选工作项
     */
    public ResponseDTO<List<WorkItemVO>> queryItemList(Long workItemTypeId, String keywords) {
        return ResponseDTO.ok(workItemDao.queryList(workItemTypeId, keywords, Boolean.FALSE, Boolean.FALSE));
    }

    /**
     * 工作项详情
     */
    public ResponseDTO<WorkItemVO> getItemDetail(Long workItemId) {
        WorkItemVO detail = workItemDao.getDetail(workItemId, Boolean.FALSE);
        if (Objects.isNull(detail)) {
            return ResponseDTO.userErrorParam("工作项不存在");
        }
        return ResponseDTO.ok(detail);
    }

    /**
     * 添加工作项
     */
    public ResponseDTO<String> addItem(WorkItemAddForm addForm) {
        ResponseDTO<String> checkResult = this.checkItem(addForm, null);
        if (!checkResult.getOk()) {
            return checkResult;
        }
        WorkItemEntity entity = SmartBeanUtil.copy(addForm, WorkItemEntity.class);
        entity.setSort(entity.getSort() == null ? 0 : entity.getSort());
        entity.setDisabledFlag(Boolean.TRUE.equals(entity.getDisabledFlag()));
        entity.setDeletedFlag(Boolean.FALSE);
        workItemDao.insert(entity);
        return ResponseDTO.ok();
    }

    /**
     * 更新工作项
     */
    public ResponseDTO<String> updateItem(WorkItemUpdateForm updateForm) {
        WorkItemEntity dbEntity = workItemDao.selectById(updateForm.getWorkItemId());
        if (Objects.isNull(dbEntity) || Boolean.TRUE.equals(dbEntity.getDeletedFlag())) {
            return ResponseDTO.userErrorParam("工作项不存在");
        }
        ResponseDTO<String> checkResult = this.checkItem(updateForm, updateForm.getWorkItemId());
        if (!checkResult.getOk()) {
            return checkResult;
        }
        WorkItemEntity entity = SmartBeanUtil.copy(updateForm, WorkItemEntity.class);
        entity.setSort(entity.getSort() == null ? 0 : entity.getSort());
        entity.setDisabledFlag(Boolean.TRUE.equals(entity.getDisabledFlag()));
        workItemDao.updateById(entity);
        return ResponseDTO.ok();
    }

    /**
     * 删除工作项
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> deleteItem(Long workItemId) {
        WorkItemEntity dbEntity = workItemDao.selectById(workItemId);
        if (Objects.isNull(dbEntity) || Boolean.TRUE.equals(dbEntity.getDeletedFlag())) {
            return ResponseDTO.userErrorParam("工作项不存在");
        }
        workItemDao.updateDeletedFlag(workItemId, Boolean.TRUE);
        return ResponseDTO.ok();
    }

    private ResponseDTO<String> checkItem(WorkItemAddForm form, Long excludeItemId) {
        WorkItemTypeEntity typeEntity = workItemTypeDao.selectById(form.getWorkItemTypeId());
        if (Objects.isNull(typeEntity) || Boolean.TRUE.equals(typeEntity.getDeletedFlag())) {
            return ResponseDTO.userErrorParam("工作项类型不存在");
        }
        if (Boolean.TRUE.equals(typeEntity.getDisabledFlag())) {
            return ResponseDTO.userErrorParam("工作项类型已禁用");
        }
        if (form.getStandardScore().compareTo(BigDecimal.ZERO) < 0) {
            return ResponseDTO.userErrorParam("标准分不能小于0");
        }
        WorkItemEntity existEntity = workItemDao.queryByName(form.getWorkItemTypeId(), form.getWorkItemName(), excludeItemId, Boolean.FALSE);
        if (Objects.nonNull(existEntity)) {
            return ResponseDTO.userErrorParam("同类型下工作项名称重复");
        }
        return ResponseDTO.ok();
    }
}
