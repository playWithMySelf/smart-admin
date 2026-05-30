package net.lab1024.sa.admin.module.business.workitem.service;

import net.lab1024.sa.admin.module.business.workitem.constant.WorkDailyReportStatusEnum;
import net.lab1024.sa.admin.module.business.workitem.dao.WorkDailyReportDao;
import net.lab1024.sa.admin.module.business.workitem.dao.WorkDailyReportFileDao;
import net.lab1024.sa.admin.module.business.workitem.dao.WorkDailyReportItemDao;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkScoreReportQueryForm;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkDailyReportFileVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkScoreDateVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkScoreEmployeeVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkScoreEmployeeDateVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkScoreItemVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkScoreTypeVO;
import net.lab1024.sa.admin.module.system.datascope.constant.DataScopeTypeEnum;
import net.lab1024.sa.admin.module.system.datascope.constant.DataScopeViewTypeEnum;
import net.lab1024.sa.admin.module.system.datascope.service.DataScopeViewService;
import net.lab1024.sa.admin.module.system.department.domain.vo.DepartmentTreeVO;
import net.lab1024.sa.admin.module.system.department.service.DepartmentService;
import net.lab1024.sa.admin.module.system.login.domain.RequestEmployee;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.module.support.file.service.FileService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Resource
    private WorkDailyReportFileDao workDailyReportFileDao;

    @Resource
    private DataScopeViewService dataScopeViewService;

    @Resource
    private DepartmentService departmentService;

    @Resource
    private FileService fileService;

    /**
     * 员工总分
     */
    public ResponseDTO<List<WorkScoreEmployeeVO>> queryEmployeeScore(RequestEmployee requestEmployee, WorkScoreReportQueryForm queryForm) {
        this.fillQueryScope(requestEmployee, queryForm);
        return ResponseDTO.ok(workDailyReportDao.queryEmployeeScore(queryForm, WorkDailyReportStatusEnum.AUDIT_PASS.getValue()));
    }

    /**
     * 可见机构树
     */
    public ResponseDTO<List<DepartmentTreeVO>> queryDepartmentTree(RequestEmployee requestEmployee) {
        DataScopeViewTypeEnum viewType = dataScopeViewService.getEmployeeDataScopeViewType(DataScopeTypeEnum.WORK_DAILY_REPORT, requestEmployee.getEmployeeId());
        List<Long> departmentIdList = dataScopeViewService.getCanViewDepartmentId(viewType, requestEmployee.getEmployeeId());
        if (CollectionUtils.isEmpty(departmentIdList)) {
            return departmentService.departmentTree();
        }
        return ResponseDTO.ok(departmentService.departmentTreeByIdList(departmentIdList));
    }

    /**
     * 日期明细
     */
    public ResponseDTO<List<WorkScoreDateVO>> queryDateScore(RequestEmployee requestEmployee, WorkScoreReportQueryForm queryForm) {
        this.fillQueryScope(requestEmployee, queryForm);
        return ResponseDTO.ok(workDailyReportDao.queryDateScore(queryForm, WorkDailyReportStatusEnum.AUDIT_PASS.getValue()));
    }

    /**
     * 员工日期积分汇总
     */
    public ResponseDTO<List<WorkScoreEmployeeDateVO>> queryEmployeeDateScore(RequestEmployee requestEmployee, WorkScoreReportQueryForm queryForm) {
        this.fillDataScopeEmployeeIdList(requestEmployee, queryForm);
        return ResponseDTO.ok(workDailyReportDao.queryEmployeeDateScore(queryForm, WorkDailyReportStatusEnum.AUDIT_PASS.getValue()));
    }

    /**
     * 类型明细
     */
    public ResponseDTO<List<WorkScoreTypeVO>> queryTypeScore(RequestEmployee requestEmployee, WorkScoreReportQueryForm queryForm) {
        this.fillQueryScope(requestEmployee, queryForm);
        return ResponseDTO.ok(workDailyReportItemDao.queryTypeScore(queryForm, WorkDailyReportStatusEnum.AUDIT_PASS.getValue()));
    }

    /**
     * 工作项明细
     */
    public ResponseDTO<List<WorkScoreItemVO>> queryItemScore(RequestEmployee requestEmployee, WorkScoreReportQueryForm queryForm) {
        this.fillQueryScope(requestEmployee, queryForm);
        List<WorkScoreItemVO> itemList = workDailyReportItemDao.queryItemScore(queryForm, WorkDailyReportStatusEnum.AUDIT_PASS.getValue());
        this.fillItemFileList(itemList);
        return ResponseDTO.ok(itemList);
    }

    private void fillQueryScope(RequestEmployee requestEmployee, WorkScoreReportQueryForm queryForm) {
        this.fillDataScopeEmployeeIdList(requestEmployee, queryForm);
        this.fillDepartmentIdList(queryForm);
    }

    private void fillDataScopeEmployeeIdList(RequestEmployee requestEmployee, WorkScoreReportQueryForm queryForm) {
        DataScopeViewTypeEnum viewType = dataScopeViewService.getEmployeeDataScopeViewType(DataScopeTypeEnum.WORK_DAILY_REPORT, requestEmployee.getEmployeeId());
        List<Long> employeeIdList = dataScopeViewService.getCanViewEmployeeId(viewType, requestEmployee.getEmployeeId());
        queryForm.setDataScopeEmployeeIdList(employeeIdList);
    }

    private void fillDepartmentIdList(WorkScoreReportQueryForm queryForm) {
        if (queryForm.getDepartmentId() == null) {
            return;
        }
        queryForm.setDepartmentIdList(departmentService.selfAndChildrenIdList(queryForm.getDepartmentId()));
    }

    private void fillItemFileList(List<WorkScoreItemVO> itemList) {
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }
        List<Long> itemIdList = itemList.stream().map(WorkScoreItemVO::getWorkDailyReportItemId).collect(Collectors.toList());
        List<WorkDailyReportFileVO> fileList = workDailyReportFileDao.queryByItemIds(itemIdList);
        if (CollectionUtils.isEmpty(fileList)) {
            itemList.forEach(e -> e.setFileList(Collections.emptyList()));
            return;
        }
        for (WorkDailyReportFileVO fileVO : fileList) {
            ResponseDTO<String> fileUrlResponse = fileService.getFileUrl(fileVO.getFileKey());
            if (fileUrlResponse.getOk()) {
                fileVO.setFileUrl(fileUrlResponse.getData());
            }
        }
        Map<Long, List<WorkDailyReportFileVO>> fileMap = fileList.stream().collect(Collectors.groupingBy(WorkDailyReportFileVO::getWorkDailyReportItemId));
        for (WorkScoreItemVO itemVO : itemList) {
            itemVO.setFileList(fileMap.getOrDefault(itemVO.getWorkDailyReportItemId(), new ArrayList<>()));
        }
    }
}
