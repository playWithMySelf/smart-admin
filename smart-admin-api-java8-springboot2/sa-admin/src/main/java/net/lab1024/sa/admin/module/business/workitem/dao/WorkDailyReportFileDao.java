package net.lab1024.sa.admin.module.business.workitem.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.business.workitem.domain.entity.WorkDailyReportFileEntity;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkDailyReportFileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 工作项日报明细图片 Dao
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Mapper
public interface WorkDailyReportFileDao extends BaseMapper<WorkDailyReportFileEntity> {

    List<WorkDailyReportFileVO> queryByItemIds(@Param("itemIdList") Collection<Long> itemIdList);

    void deleteByReportId(@Param("workDailyReportId") Long workDailyReportId);
}
