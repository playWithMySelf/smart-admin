package net.lab1024.sa.admin.module.business.workitem.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.business.workitem.domain.entity.WorkDailyReportEntity;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkDailyReportQueryForm;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkScoreReportQueryForm;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkDailyReportVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkScoreDateVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkScoreEmployeeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 工作项日报 Dao
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Mapper
public interface WorkDailyReportDao extends BaseMapper<WorkDailyReportEntity> {

    WorkDailyReportEntity getByEmployeeAndDate(@Param("employeeId") Long employeeId, @Param("reportDate") LocalDate reportDate);

    List<WorkDailyReportVO> queryPage(Page page, @Param("queryForm") WorkDailyReportQueryForm queryForm);

    List<WorkScoreEmployeeVO> queryEmployeeScore(@Param("queryForm") WorkScoreReportQueryForm queryForm, @Param("auditPassStatus") Integer auditPassStatus);

    List<WorkScoreDateVO> queryDateScore(@Param("queryForm") WorkScoreReportQueryForm queryForm, @Param("auditPassStatus") Integer auditPassStatus);
}
