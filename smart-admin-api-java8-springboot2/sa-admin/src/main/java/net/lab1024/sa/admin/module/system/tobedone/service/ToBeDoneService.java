package net.lab1024.sa.admin.module.system.tobedone.service;

import net.lab1024.sa.admin.module.system.login.domain.RequestEmployee;
import net.lab1024.sa.admin.module.system.tobedone.dao.ToBeDoneDao;
import net.lab1024.sa.admin.module.system.tobedone.domain.entity.ToBeDoneEntity;
import net.lab1024.sa.admin.module.system.tobedone.domain.form.ToBeDoneAddForm;
import net.lab1024.sa.admin.module.system.tobedone.domain.form.ToBeDoneUpdateForm;
import net.lab1024.sa.admin.module.system.tobedone.domain.vo.ToBeDoneVO;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 待办工作
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-06-19
 */
@Service
public class ToBeDoneService {

    @Resource
    private ToBeDoneDao toBeDoneDao;

    public ResponseDTO<List<ToBeDoneVO>> queryList(RequestEmployee requestEmployee) {
        return ResponseDTO.ok(toBeDoneDao.queryList(requestEmployee.getEmployeeId(), Boolean.FALSE));
    }

    public ResponseDTO<Integer> queryCount(RequestEmployee requestEmployee) {
        return ResponseDTO.ok(toBeDoneDao.countUndone(requestEmployee.getEmployeeId(), Boolean.FALSE, Boolean.FALSE));
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> add(RequestEmployee requestEmployee, ToBeDoneAddForm addForm) {
        ToBeDoneEntity entity = SmartBeanUtil.copy(addForm, ToBeDoneEntity.class);
        entity.setEmployeeId(requestEmployee.getEmployeeId());
        entity.setDoneFlag(Boolean.FALSE);
        entity.setStarFlag(Boolean.TRUE.equals(addForm.getStarFlag()));
        entity.setDeletedFlag(Boolean.FALSE);
        toBeDoneDao.insert(entity);
        return ResponseDTO.ok();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> update(RequestEmployee requestEmployee, ToBeDoneUpdateForm updateForm) {
        ToBeDoneEntity entity = toBeDoneDao.selectByIdAndEmployee(updateForm.getToBeDoneId(), requestEmployee.getEmployeeId(), Boolean.FALSE);
        if (Objects.isNull(entity)) {
            return ResponseDTO.userErrorParam("待办不存在");
        }

        if (StringUtils.isNotBlank(updateForm.getTitle())) {
            entity.setTitle(updateForm.getTitle());
        }
        if (updateForm.getDoneFlag() != null) {
            entity.setDoneFlag(updateForm.getDoneFlag());
        }
        if (updateForm.getStarFlag() != null) {
            entity.setStarFlag(updateForm.getStarFlag());
        }
        toBeDoneDao.updateById(entity);
        return ResponseDTO.ok();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> delete(RequestEmployee requestEmployee, Long toBeDoneId) {
        ToBeDoneEntity entity = toBeDoneDao.selectByIdAndEmployee(toBeDoneId, requestEmployee.getEmployeeId(), Boolean.FALSE);
        if (Objects.isNull(entity)) {
            return ResponseDTO.userErrorParam("待办不存在");
        }
        toBeDoneDao.deleteByIdAndEmployee(toBeDoneId, requestEmployee.getEmployeeId(), Boolean.TRUE);
        return ResponseDTO.ok();
    }
}
