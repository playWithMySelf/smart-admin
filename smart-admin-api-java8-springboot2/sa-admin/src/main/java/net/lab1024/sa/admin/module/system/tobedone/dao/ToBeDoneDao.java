package net.lab1024.sa.admin.module.system.tobedone.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.system.tobedone.domain.entity.ToBeDoneEntity;
import net.lab1024.sa.admin.module.system.tobedone.domain.vo.ToBeDoneVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 待办工作
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-06-19
 */
@Mapper
public interface ToBeDoneDao extends BaseMapper<ToBeDoneEntity> {

    /**
     * 查询我的待办工作
     */
    List<ToBeDoneVO> queryList(@Param("employeeId") Long employeeId, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 查询我的未完成待办数量
     */
    Integer countUndone(@Param("employeeId") Long employeeId, @Param("doneFlag") Boolean doneFlag, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 查询我的待办详情
     */
    ToBeDoneEntity selectByIdAndEmployee(@Param("toBeDoneId") Long toBeDoneId, @Param("employeeId") Long employeeId, @Param("deletedFlag") Boolean deletedFlag);

    /**
     * 软删除我的待办
     */
    void deleteByIdAndEmployee(@Param("toBeDoneId") Long toBeDoneId, @Param("employeeId") Long employeeId, @Param("deletedFlag") Boolean deletedFlag);
}
