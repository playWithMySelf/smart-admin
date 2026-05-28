package net.lab1024.sa.admin.module.business.workitem.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.workitem.domain.entity.WorkItemEntity;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkItemQueryForm;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工作项 Dao
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Mapper
public interface WorkItemDao extends BaseMapper<WorkItemEntity> {

    List<WorkItemVO> queryPage(Page page, @Param("queryForm") WorkItemQueryForm queryForm);

    List<WorkItemVO> queryList(@Param("workItemTypeId") Long workItemTypeId, @Param("keywords") String keywords, @Param("disabledFlag") Boolean disabledFlag, @Param("deletedFlag") Boolean deletedFlag);

    WorkItemVO getDetail(@Param("workItemId") Long workItemId, @Param("deletedFlag") Boolean deletedFlag);

    WorkItemEntity queryByName(@Param("workItemTypeId") Long workItemTypeId, @Param("workItemName") String workItemName, @Param("excludeId") Long excludeId, @Param("deletedFlag") Boolean deletedFlag);

    Integer countByType(@Param("workItemTypeId") Long workItemTypeId, @Param("deletedFlag") Boolean deletedFlag);

    void updateDeletedFlag(@Param("workItemId") Long workItemId, @Param("deletedFlag") Boolean deletedFlag);
}
