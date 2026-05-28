package net.lab1024.sa.admin.module.business.workitem.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.business.workitem.domain.entity.WorkDailyReportAuditEntity;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkDailyReportAuditVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工作项日报审核历史 Dao
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Mapper
public interface WorkDailyReportAuditDao extends BaseMapper<WorkDailyReportAuditEntity> {

    List<WorkDailyReportAuditVO> queryByReportId(@Param("workDailyReportId") Long workDailyReportId);
}
