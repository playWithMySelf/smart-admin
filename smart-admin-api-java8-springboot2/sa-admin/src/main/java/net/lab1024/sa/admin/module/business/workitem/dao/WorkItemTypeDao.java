package net.lab1024.sa.admin.module.business.workitem.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.business.workitem.domain.entity.WorkItemTypeEntity;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkItemTypeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工作项类型 Dao
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Mapper
public interface WorkItemTypeDao extends BaseMapper<WorkItemTypeEntity> {

    List<WorkItemTypeVO> queryList(@Param("disabledFlag") Boolean disabledFlag, @Param("deletedFlag") Boolean deletedFlag);

    WorkItemTypeEntity queryByName(@Param("typeName") String typeName, @Param("excludeId") Long excludeId, @Param("deletedFlag") Boolean deletedFlag);

    void updateDeletedFlag(@Param("workItemTypeId") Long workItemTypeId, @Param("deletedFlag") Boolean deletedFlag);
}
