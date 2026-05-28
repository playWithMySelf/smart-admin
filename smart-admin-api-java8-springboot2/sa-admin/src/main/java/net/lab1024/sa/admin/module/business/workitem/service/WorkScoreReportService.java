package net.lab1024.sa.admin.module.business.workitem.service;

import net.lab1024.sa.admin.module.business.workitem.constant.WorkDailyReportStatusEnum;
import net.lab1024.sa.admin.module.business.workitem.dao.WorkDailyReportDao;
import net.lab1024.sa.admin.module.business.workitem.dao.WorkDailyReportItemDao;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkScoreReportQueryForm;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkScoreDateVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkScoreEmployeeVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkScoreItemVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkScoreTypeVO;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 工作项积分报表
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Service
public class WorkScoreReportService {

    @Resource
    private WorkDailyReportDao workDailyReportDao;

    @Resource
    private WorkDailyReportItemDao workDailyReportItemDao;

    /**
     * 员工总分
     */
    public ResponseDTO<List<WorkScoreEmployeeVO>> queryEmployeeScore(WorkScoreReportQueryForm queryForm) {
        return ResponseDTO.ok(workDailyReportDao.queryEmployeeScore(queryForm, WorkDailyReportStatusEnum.AUDIT_PASS.getValue()));
    }

    /**
     * 日期明细
     */
    public ResponseDTO<List<WorkScoreDateVO>> queryDateScore(WorkScoreReportQueryForm queryForm) {
        return ResponseDTO.ok(workDailyReportDao.queryDateScore(queryForm, WorkDailyReportStatusEnum.AUDIT_PASS.getValue()));
    }

    /**
     * 类型明细
     */
    public ResponseDTO<List<WorkScoreTypeVO>> queryTypeScore(WorkScoreReportQueryForm queryForm) {
        return ResponseDTO.ok(workDailyReportItemDao.queryTypeScore(queryForm, WorkDailyReportStatusEnum.AUDIT_PASS.getValue()));
    }

    /**
     * 工作项明细
     */
    public ResponseDTO<List<WorkScoreItemVO>> queryItemScore(WorkScoreReportQueryForm queryForm) {
        return ResponseDTO.ok(workDailyReportItemDao.queryItemScore(queryForm, WorkDailyReportStatusEnum.AUDIT_PASS.getValue()));
    }
}
