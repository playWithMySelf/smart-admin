package net.lab1024.sa.admin.module.business.workitem.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.business.workitem.domain.entity.WorkDailyReportItemEntity;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkScoreReportQueryForm;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkDailyReportItemVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkScoreItemVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkScoreTypeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 工作项日报明细 Dao
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Mapper
public interface WorkDailyReportItemDao extends BaseMapper<WorkDailyReportItemEntity> {

    List<WorkDailyReportItemVO> queryByReportId(@Param("workDailyReportId") Long workDailyReportId);

    List<WorkDailyReportItemEntity> selectByReportId(@Param("workDailyReportId") Long workDailyReportId);

    void deleteByReportId(@Param("workDailyReportId") Long workDailyReportId);

    List<WorkScoreTypeVO> queryTypeScore(@Param("queryForm") WorkScoreReportQueryForm queryForm, @Param("auditPassStatus") Integer auditPassStatus);

    List<WorkScoreItemVO> queryItemScore(@Param("queryForm") WorkScoreReportQueryForm queryForm, @Param("auditPassStatus") Integer auditPassStatus);

    List<WorkDailyReportItemEntity> selectByReportIdAndItemIds(@Param("workDailyReportId") Long workDailyReportId, @Param("itemIdList") Collection<Long> itemIdList);
}
