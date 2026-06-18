package net.lab1024.sa.admin.module.business.workitem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.workitem.constant.WorkDailyReportStatusEnum;
import net.lab1024.sa.admin.module.business.workitem.constant.WorkItemConfigConst;
import net.lab1024.sa.admin.module.business.workitem.dao.WorkDailyReportAuditDao;
import net.lab1024.sa.admin.module.business.workitem.dao.WorkDailyReportDao;
import net.lab1024.sa.admin.module.business.workitem.dao.WorkDailyReportFileDao;
import net.lab1024.sa.admin.module.business.workitem.dao.WorkDailyReportItemDao;
import net.lab1024.sa.admin.module.business.workitem.dao.WorkItemDao;
import net.lab1024.sa.admin.module.business.workitem.domain.entity.WorkDailyReportAuditEntity;
import net.lab1024.sa.admin.module.business.workitem.domain.entity.WorkDailyReportEntity;
import net.lab1024.sa.admin.module.business.workitem.domain.entity.WorkDailyReportFileEntity;
import net.lab1024.sa.admin.module.business.workitem.domain.entity.WorkDailyReportItemEntity;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkDailyReportAuditForm;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkDailyReportAuditItemForm;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkDailyReportFileForm;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkDailyReportItemForm;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkDailyReportQueryForm;
import net.lab1024.sa.admin.module.business.workitem.domain.form.WorkDailyReportSaveForm;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkDailyReportAuditVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkDailyReportFileVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkDailyReportItemVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkDailyReportVO;
import net.lab1024.sa.admin.module.business.workitem.domain.vo.WorkItemVO;
import net.lab1024.sa.admin.module.system.datascope.constant.DataScopeTypeEnum;
import net.lab1024.sa.admin.module.system.datascope.constant.DataScopeViewTypeEnum;
import net.lab1024.sa.admin.module.system.datascope.service.DataScopeViewService;
import net.lab1024.sa.admin.module.system.employee.dao.EmployeeDao;
import net.lab1024.sa.admin.module.system.employee.domain.entity.EmployeeEntity;
import net.lab1024.sa.admin.module.system.login.domain.RequestEmployee;
import net.lab1024.sa.admin.module.system.login.manager.LoginManager;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.UserPermission;
import net.lab1024.sa.base.common.enumeration.UserTypeEnum;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.config.AsyncConfig;
import net.lab1024.sa.base.module.support.config.ConfigService;
import net.lab1024.sa.base.module.support.config.domain.ConfigVO;
import net.lab1024.sa.base.module.support.file.service.FileService;
import net.lab1024.sa.base.module.support.message.constant.MessageTypeEnum;
import net.lab1024.sa.base.module.support.message.domain.MessageSendForm;
import net.lab1024.sa.base.module.support.message.service.MessageService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 工作项日报
 *
 * @Author 1024创新实验室: jinwei
 * @Date 2026-05-28
 */
@Slf4j
@Service
public class WorkDailyReportService {

    private static final String WORK_DAILY_REPORT_AUDIT_PERMISSION = "workitem:daily:audit";

    @Resource
    private WorkDailyReportDao workDailyReportDao;

    @Resource
    private WorkDailyReportItemDao workDailyReportItemDao;

    @Resource
    private WorkDailyReportFileDao workDailyReportFileDao;

    @Resource
    private WorkDailyReportAuditDao workDailyReportAuditDao;

    @Resource
    private WorkItemDao workItemDao;

    @Resource
    private EmployeeDao employeeDao;

    @Resource
    private ConfigService configService;

    @Resource
    private FileService fileService;

    @Resource
    private DataScopeViewService dataScopeViewService;

    @Resource
    private LoginManager loginManager;

    @Resource
    private MessageService messageService;

    @Resource(name = AsyncConfig.ASYNC_EXECUTOR_THREAD_NAME)
    private AsyncTaskExecutor asyncTaskExecutor;

    /**
     * 我的日报分页
     */
    public ResponseDTO<PageResult<WorkDailyReportVO>> queryMyPage(RequestEmployee requestEmployee, WorkDailyReportQueryForm queryForm) {
        queryForm.setEmployeeId(requestEmployee.getEmployeeId());
        return this.queryPage(queryForm);
    }

    /**
     * 审核列表分页
     */
    public ResponseDTO<PageResult<WorkDailyReportVO>> queryReviewPage(RequestEmployee requestEmployee, WorkDailyReportQueryForm queryForm) {
        queryForm.setDataScopeEmployeeIdList(this.getDataScopeEmployeeIdList(requestEmployee));
        return this.queryPage(queryForm);
    }

    private ResponseDTO<PageResult<WorkDailyReportVO>> queryPage(WorkDailyReportQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<WorkDailyReportVO> reportList = workDailyReportDao.queryPage(page, queryForm);
        return ResponseDTO.ok(SmartPageUtil.convert2PageResult(page, reportList));
    }

    /**
     * 我的日报详情
     */
    public ResponseDTO<WorkDailyReportVO> getMyDetail(RequestEmployee requestEmployee, Long workDailyReportId) {
        WorkDailyReportEntity reportEntity = workDailyReportDao.selectById(workDailyReportId);
        if (Objects.isNull(reportEntity) || !Objects.equals(reportEntity.getEmployeeId(), requestEmployee.getEmployeeId())) {
            return ResponseDTO.userErrorParam("日报不存在");
        }
        return ResponseDTO.ok(this.buildDetail(reportEntity));
    }

    /**
     * 审核详情
     */
    public ResponseDTO<WorkDailyReportVO> getReviewDetail(RequestEmployee requestEmployee, Long workDailyReportId) {
        WorkDailyReportEntity reportEntity = workDailyReportDao.selectById(workDailyReportId);
        if (Objects.isNull(reportEntity) || !this.canViewReport(requestEmployee, reportEntity)) {
            return ResponseDTO.userErrorParam("日报不存在");
        }
        return ResponseDTO.ok(this.buildDetail(reportEntity));
    }

    /**
     * 保存草稿
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> saveDraft(RequestEmployee requestEmployee, WorkDailyReportSaveForm saveForm) {
        ResponseDTO<String> dateCheck = this.checkReplenishDate(saveForm.getReportDate());
        if (!dateCheck.getOk()) {
            return dateCheck;
        }
        WorkDailyReportEntity reportEntity = this.getOrCreateEditableReport(requestEmployee, saveForm);
        if (Objects.isNull(reportEntity)) {
            return ResponseDTO.userErrorParam("日报不存在或当前状态不可编辑");
        }

        reportEntity.setReportDate(saveForm.getReportDate());
        reportEntity.setStatus(WorkDailyReportStatusEnum.DRAFT.getValue());
        reportEntity.setTotalScore(BigDecimal.ZERO);
        reportEntity.setLatestFailReason(null);
        workDailyReportDao.updateById(reportEntity);
        return this.replaceReportItems(reportEntity.getWorkDailyReportId(), saveForm.getItemList());
    }

    /**
     * 提交审核
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> submit(RequestEmployee requestEmployee, Long workDailyReportId) {
        WorkDailyReportEntity reportEntity = workDailyReportDao.selectById(workDailyReportId);
        if (Objects.isNull(reportEntity) || !Objects.equals(reportEntity.getEmployeeId(), requestEmployee.getEmployeeId())) {
            return ResponseDTO.userErrorParam("日报不存在");
        }
        if (!Objects.equals(reportEntity.getStatus(), WorkDailyReportStatusEnum.DRAFT.getValue())
                && !Objects.equals(reportEntity.getStatus(), WorkDailyReportStatusEnum.AUDIT_FAIL.getValue())) {
            return ResponseDTO.userErrorParam("当前状态不可提交");
        }
        ResponseDTO<String> dateCheck = this.checkReplenishDate(reportEntity.getReportDate());
        if (!dateCheck.getOk()) {
            return dateCheck;
        }
        List<WorkDailyReportItemEntity> itemList = workDailyReportItemDao.selectByReportId(workDailyReportId);
        if (CollectionUtils.isEmpty(itemList)) {
            return ResponseDTO.userErrorParam("请至少添加一条工作项");
        }
        ResponseDTO<String> refreshResult = this.refreshItemSnapshot(itemList);
        if (!refreshResult.getOk()) {
            return refreshResult;
        }
        reportEntity.setStatus(WorkDailyReportStatusEnum.WAIT_AUDIT.getValue());
        reportEntity.setSubmitTime(LocalDateTime.now());
        reportEntity.setLatestFailReason(null);
        workDailyReportDao.updateById(reportEntity);
        this.saveSubmitHistory(requestEmployee, reportEntity);
        this.sendSubmitMessageAfterCommit(reportEntity);
        return ResponseDTO.ok();
    }

    /**
     * 审核
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> audit(RequestEmployee requestEmployee, WorkDailyReportAuditForm auditForm) {
        WorkDailyReportEntity reportEntity = workDailyReportDao.selectById(auditForm.getWorkDailyReportId());
        if (Objects.isNull(reportEntity) || !this.canViewReport(requestEmployee, reportEntity)) {
            return ResponseDTO.userErrorParam("日报不存在");
        }
        if (!Objects.equals(reportEntity.getStatus(), WorkDailyReportStatusEnum.WAIT_AUDIT.getValue())) {
            return ResponseDTO.userErrorParam("只有待审核日报可以审核");
        }
        if (!Boolean.TRUE.equals(auditForm.getPassFlag()) && StringUtils.isBlank(auditForm.getFailReason())) {
            return ResponseDTO.userErrorParam("审核失败原因不能为空");
        }

        List<WorkDailyReportItemEntity> itemList = workDailyReportItemDao.selectByReportId(auditForm.getWorkDailyReportId());
        Map<Long, WorkDailyReportAuditItemForm> auditItemMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(auditForm.getItemList())) {
            auditItemMap = auditForm.getItemList().stream().collect(Collectors.toMap(WorkDailyReportAuditItemForm::getWorkDailyReportItemId, Function.identity(), (a, b) -> a));
        }

        Map<Long, BigDecimal> finalScoreMap = new HashMap<>();
        Map<Long, String> deductReasonMap = new HashMap<>();
        BigDecimal totalScore = BigDecimal.ZERO;
        for (WorkDailyReportItemEntity itemEntity : itemList) {
            WorkDailyReportAuditItemForm auditItem = auditItemMap.get(itemEntity.getWorkDailyReportItemId());
            BigDecimal finalScore = auditItem == null ? itemEntity.getStandardScore() : auditItem.getFinalScore();
            if (finalScore == null) {
                return ResponseDTO.userErrorParam("最终分不能为空");
            }
            if (finalScore.compareTo(BigDecimal.ZERO) < 0) {
                return ResponseDTO.userErrorParam("最终分不能小于0");
            }
            finalScoreMap.put(itemEntity.getWorkDailyReportItemId(), finalScore);
            deductReasonMap.put(itemEntity.getWorkDailyReportItemId(), auditItem == null ? null : auditItem.getDeductReason());
            totalScore = totalScore.add(finalScore);
        }

        for (WorkDailyReportItemEntity itemEntity : itemList) {
            Long itemId = itemEntity.getWorkDailyReportItemId();
            itemEntity.setFinalScore(finalScoreMap.get(itemId));
            itemEntity.setDeductReason(deductReasonMap.get(itemId));
            workDailyReportItemDao.updateById(itemEntity);
        }

        Integer auditResult = Boolean.TRUE.equals(auditForm.getPassFlag()) ? WorkDailyReportStatusEnum.AUDIT_PASS.getValue() : WorkDailyReportStatusEnum.AUDIT_FAIL.getValue();
        LocalDateTime auditTime = LocalDateTime.now();
        reportEntity.setStatus(auditResult);
        reportEntity.setTotalScore(Boolean.TRUE.equals(auditForm.getPassFlag()) ? totalScore : BigDecimal.ZERO);
        reportEntity.setLatestAuditEmployeeId(requestEmployee.getEmployeeId());
        reportEntity.setLatestAuditEmployeeName(requestEmployee.getActualName());
        reportEntity.setLatestAuditTime(auditTime);
        reportEntity.setLatestFailReason(Boolean.TRUE.equals(auditForm.getPassFlag()) ? null : auditForm.getFailReason());
        workDailyReportDao.updateById(reportEntity);

        WorkDailyReportAuditEntity auditEntity = new WorkDailyReportAuditEntity();
        auditEntity.setWorkDailyReportId(reportEntity.getWorkDailyReportId());
        auditEntity.setAuditResult(auditResult);
        auditEntity.setFailReason(auditForm.getFailReason());
        auditEntity.setAuditEmployeeId(requestEmployee.getEmployeeId());
        auditEntity.setAuditEmployeeName(requestEmployee.getActualName());
        auditEntity.setAuditTime(auditTime);
        workDailyReportAuditDao.insert(auditEntity);

        this.sendAuditMessageAfterCommit(reportEntity, Boolean.TRUE.equals(auditForm.getPassFlag()), auditForm.getFailReason());
        return ResponseDTO.ok();
    }

    private WorkDailyReportEntity getOrCreateEditableReport(RequestEmployee requestEmployee, WorkDailyReportSaveForm saveForm) {
        WorkDailyReportEntity reportEntity;
        if (saveForm.getWorkDailyReportId() == null) {
            reportEntity = workDailyReportDao.getByEmployeeAndDate(requestEmployee.getEmployeeId(), saveForm.getReportDate());
            if (reportEntity != null) {
                if (!Objects.equals(reportEntity.getStatus(), WorkDailyReportStatusEnum.DRAFT.getValue())
                        && !Objects.equals(reportEntity.getStatus(), WorkDailyReportStatusEnum.AUDIT_FAIL.getValue())) {
                    return null;
                }
                return reportEntity;
            }
            reportEntity = new WorkDailyReportEntity();
            reportEntity.setEmployeeId(requestEmployee.getEmployeeId());
            reportEntity.setEmployeeName(requestEmployee.getActualName());
            reportEntity.setDepartmentId(requestEmployee.getDepartmentId());
            reportEntity.setDepartmentName(requestEmployee.getDepartmentName());
            reportEntity.setReportDate(saveForm.getReportDate());
            reportEntity.setStatus(WorkDailyReportStatusEnum.DRAFT.getValue());
            reportEntity.setTotalScore(BigDecimal.ZERO);
            workDailyReportDao.insert(reportEntity);
            return reportEntity;
        }

        reportEntity = workDailyReportDao.selectById(saveForm.getWorkDailyReportId());
        if (Objects.isNull(reportEntity) || !Objects.equals(reportEntity.getEmployeeId(), requestEmployee.getEmployeeId())) {
            return null;
        }
        if (!Objects.equals(reportEntity.getStatus(), WorkDailyReportStatusEnum.DRAFT.getValue())
                && !Objects.equals(reportEntity.getStatus(), WorkDailyReportStatusEnum.AUDIT_FAIL.getValue())) {
            return null;
        }
        WorkDailyReportEntity sameDateReport = workDailyReportDao.getByEmployeeAndDate(requestEmployee.getEmployeeId(), saveForm.getReportDate());
        if (sameDateReport != null && !Objects.equals(sameDateReport.getWorkDailyReportId(), reportEntity.getWorkDailyReportId())) {
            return null;
        }
        return reportEntity;
    }

    private List<Long> getDataScopeEmployeeIdList(RequestEmployee requestEmployee) {
        DataScopeViewTypeEnum viewType = dataScopeViewService.getEmployeeDataScopeViewType(DataScopeTypeEnum.WORK_DAILY_REPORT, requestEmployee.getEmployeeId());
        return dataScopeViewService.getCanViewEmployeeId(viewType, requestEmployee.getEmployeeId());
    }

    private boolean canViewReport(RequestEmployee requestEmployee, WorkDailyReportEntity reportEntity) {
        List<Long> employeeIdList = this.getDataScopeEmployeeIdList(requestEmployee);
        return CollectionUtils.isEmpty(employeeIdList) || employeeIdList.contains(reportEntity.getEmployeeId());
    }

    private ResponseDTO<String> checkReplenishDate(LocalDate reportDate) {
        int allowDays = this.getAllowReplenishDays();
        int safeAllowDays = Math.max(allowDays, 1);
        LocalDate earliestDate = LocalDate.now().minusDays(safeAllowDays - 1L);
        if (reportDate.isBefore(earliestDate) || reportDate.isAfter(LocalDate.now())) {
            return ResponseDTO.userErrorParam("只允许填报最近" + safeAllowDays + "天内的日报");
        }
        return ResponseDTO.ok();
    }

    private int getAllowReplenishDays() {
        ConfigVO config = configService.getConfig(WorkItemConfigConst.ALLOW_REPLENISH_DAYS);
        if (config == null || StringUtils.isBlank(config.getConfigValue())) {
            return WorkItemConfigConst.DEFAULT_ALLOW_REPLENISH_DAYS;
        }
        try {
            return Integer.parseInt(config.getConfigValue());
        } catch (NumberFormatException e) {
            return WorkItemConfigConst.DEFAULT_ALLOW_REPLENISH_DAYS;
        }
    }

    private ResponseDTO<String> replaceReportItems(Long workDailyReportId, List<WorkDailyReportItemForm> itemFormList) {
        List<WorkItemVO> workItemList = new ArrayList<>();
        for (WorkDailyReportItemForm itemForm : itemFormList) {
            WorkItemVO workItem = workItemDao.getDetail(itemForm.getWorkItemId(), Boolean.FALSE);
            if (workItem == null || Boolean.TRUE.equals(workItem.getDisabledFlag())) {
                return ResponseDTO.userErrorParam("工作项不存在或已禁用");
            }
            workItemList.add(workItem);
        }

        workDailyReportFileDao.deleteByReportId(workDailyReportId);
        workDailyReportItemDao.deleteByReportId(workDailyReportId);
        int itemSort = 0;
        for (WorkDailyReportItemForm itemForm : itemFormList) {
            WorkDailyReportItemEntity itemEntity = this.buildReportItem(workDailyReportId, workItemList.get(itemSort), itemForm.getFinishRemark(), itemSort);
            itemSort++;
            workDailyReportItemDao.insert(itemEntity);
            this.saveItemFiles(itemEntity.getWorkDailyReportItemId(), itemForm.getFileList());
        }
        return ResponseDTO.ok();
    }

    private ResponseDTO<String> refreshItemSnapshot(List<WorkDailyReportItemEntity> itemList) {
        for (WorkDailyReportItemEntity itemEntity : itemList) {
            WorkItemVO workItem = workItemDao.getDetail(itemEntity.getWorkItemId(), Boolean.FALSE);
            if (workItem == null || Boolean.TRUE.equals(workItem.getDisabledFlag())) {
                return ResponseDTO.userErrorParam("工作项【" + itemEntity.getWorkItemName() + "】不存在或已禁用");
            }
            itemEntity.setWorkItemTypeId(workItem.getWorkItemTypeId());
            itemEntity.setWorkItemTypeName(workItem.getWorkItemTypeName());
            itemEntity.setWorkItemName(workItem.getWorkItemName());
            itemEntity.setDescription(workItem.getDescription());
            itemEntity.setScoreStandard(workItem.getScoreStandard());
            itemEntity.setStandardScore(workItem.getStandardScore());
            itemEntity.setFinalScore(workItem.getStandardScore());
            itemEntity.setDeductReason(null);
            workDailyReportItemDao.updateById(itemEntity);
        }
        return ResponseDTO.ok();
    }

    private WorkDailyReportItemEntity buildReportItem(Long workDailyReportId, WorkItemVO workItem, String finishRemark, Integer sort) {
        WorkDailyReportItemEntity itemEntity = new WorkDailyReportItemEntity();
        itemEntity.setWorkDailyReportId(workDailyReportId);
        itemEntity.setWorkItemId(workItem.getWorkItemId());
        itemEntity.setWorkItemTypeId(workItem.getWorkItemTypeId());
        itemEntity.setWorkItemTypeName(workItem.getWorkItemTypeName());
        itemEntity.setWorkItemName(workItem.getWorkItemName());
        itemEntity.setDescription(workItem.getDescription());
        itemEntity.setScoreStandard(workItem.getScoreStandard());
        itemEntity.setStandardScore(workItem.getStandardScore());
        itemEntity.setFinalScore(workItem.getStandardScore());
        itemEntity.setFinishRemark(finishRemark);
        itemEntity.setSort(sort);
        return itemEntity;
    }

    private void saveItemFiles(Long workDailyReportItemId, List<WorkDailyReportFileForm> fileList) {
        if (CollectionUtils.isEmpty(fileList)) {
            return;
        }
        int sort = 0;
        for (WorkDailyReportFileForm fileForm : fileList) {
            WorkDailyReportFileEntity fileEntity = new WorkDailyReportFileEntity();
            fileEntity.setWorkDailyReportItemId(workDailyReportItemId);
            fileEntity.setFileId(fileForm.getFileId());
            fileEntity.setFileKey(fileForm.getFileKey());
            fileEntity.setFileName(fileForm.getFileName());
            fileEntity.setSort(sort++);
            workDailyReportFileDao.insert(fileEntity);
        }
    }

    private WorkDailyReportVO buildDetail(WorkDailyReportEntity reportEntity) {
        WorkDailyReportVO detail = new WorkDailyReportVO();
        detail.setWorkDailyReportId(reportEntity.getWorkDailyReportId());
        detail.setEmployeeId(reportEntity.getEmployeeId());
        detail.setEmployeeName(reportEntity.getEmployeeName());
        detail.setDepartmentId(reportEntity.getDepartmentId());
        detail.setDepartmentName(reportEntity.getDepartmentName());
        detail.setReportDate(reportEntity.getReportDate());
        detail.setStatus(reportEntity.getStatus());
        detail.setTotalScore(reportEntity.getTotalScore());
        detail.setSubmitTime(reportEntity.getSubmitTime());
        detail.setLatestAuditEmployeeId(reportEntity.getLatestAuditEmployeeId());
        detail.setLatestAuditEmployeeName(reportEntity.getLatestAuditEmployeeName());
        detail.setLatestAuditTime(reportEntity.getLatestAuditTime());
        detail.setLatestFailReason(reportEntity.getLatestFailReason());
        detail.setCreateTime(reportEntity.getCreateTime());
        detail.setUpdateTime(reportEntity.getUpdateTime());

        List<WorkDailyReportItemVO> itemList = workDailyReportItemDao.queryByReportId(reportEntity.getWorkDailyReportId());
        this.fillItemFileList(itemList);
        detail.setItemList(itemList);
        detail.setAuditList(this.buildAuditHistory(reportEntity));
        return detail;
    }

    private List<WorkDailyReportAuditVO> buildAuditHistory(WorkDailyReportEntity reportEntity) {
        List<WorkDailyReportAuditVO> auditList = workDailyReportAuditDao.queryByReportId(reportEntity.getWorkDailyReportId());
        if (reportEntity.getSubmitTime() == null) {
            return auditList;
        }
        boolean hasSubmitHistory = auditList.stream().anyMatch(e -> Objects.equals(e.getAuditResult(), WorkDailyReportStatusEnum.WAIT_AUDIT.getValue()));
        if (!hasSubmitHistory) {
            WorkDailyReportAuditVO submitHistory = new WorkDailyReportAuditVO();
            submitHistory.setWorkDailyReportAuditId(-reportEntity.getWorkDailyReportId());
            submitHistory.setWorkDailyReportId(reportEntity.getWorkDailyReportId());
            submitHistory.setAuditResult(WorkDailyReportStatusEnum.WAIT_AUDIT.getValue());
            submitHistory.setAuditEmployeeId(reportEntity.getEmployeeId());
            submitHistory.setAuditEmployeeName(reportEntity.getEmployeeName());
            submitHistory.setAuditTime(reportEntity.getSubmitTime());
            auditList.add(submitHistory);
            auditList.sort(Comparator.comparing(WorkDailyReportAuditVO::getAuditTime).thenComparing(WorkDailyReportAuditVO::getWorkDailyReportAuditId));
        }
        return auditList;
    }

    private void saveSubmitHistory(RequestEmployee requestEmployee, WorkDailyReportEntity reportEntity) {
        WorkDailyReportAuditEntity submitEntity = new WorkDailyReportAuditEntity();
        submitEntity.setWorkDailyReportId(reportEntity.getWorkDailyReportId());
        submitEntity.setAuditResult(WorkDailyReportStatusEnum.WAIT_AUDIT.getValue());
        submitEntity.setAuditEmployeeId(requestEmployee.getEmployeeId());
        submitEntity.setAuditEmployeeName(requestEmployee.getActualName());
        submitEntity.setAuditTime(reportEntity.getSubmitTime());
        workDailyReportAuditDao.insert(submitEntity);
    }

    private void fillItemFileList(List<WorkDailyReportItemVO> itemList) {
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }
        List<Long> itemIdList = itemList.stream().map(WorkDailyReportItemVO::getWorkDailyReportItemId).collect(Collectors.toList());
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
        for (WorkDailyReportItemVO itemVO : itemList) {
            itemVO.setFileList(fileMap.getOrDefault(itemVO.getWorkDailyReportItemId(), new ArrayList<>()));
        }
    }

    private void sendSubmitMessageAfterCommit(WorkDailyReportEntity reportEntity) {
        Long workDailyReportId = reportEntity.getWorkDailyReportId();
        Long departmentId = reportEntity.getDepartmentId();
        Runnable sendTask = () -> this.sendSubmitMessage(reportEntity);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    executeSubmitMessageTask(sendTask, workDailyReportId, departmentId);
                }
            });
            return;
        }
        this.executeSubmitMessageTask(sendTask, workDailyReportId, departmentId);
    }

    void executeSubmitMessageTask(Runnable sendTask, Long workDailyReportId, Long departmentId) {
        try {
            asyncTaskExecutor.execute(sendTask);
        } catch (Exception e) {
            log.error("提交日报待审核站内信异步任务失败，workDailyReportId:{}, departmentId:{}", workDailyReportId, departmentId, e);
        }
    }

    private void sendSubmitMessage(WorkDailyReportEntity reportEntity) {
        Long workDailyReportId = reportEntity.getWorkDailyReportId();
        Long employeeId = reportEntity.getEmployeeId();
        Long departmentId = reportEntity.getDepartmentId();
        try {
            List<Long> receiverIdList = this.getSubmitMessageReceiverIdList(departmentId);
            if (CollectionUtils.isEmpty(receiverIdList)) {
                log.warn("提交日报待审核站内信未发送，直属部门无审核权限员工，workDailyReportId:{}, departmentId:{}", workDailyReportId, departmentId);
                return;
            }

            List<MessageSendForm> messageSendFormList = receiverIdList.stream().map(receiverId -> {
                MessageSendForm messageSendForm = new MessageSendForm();
                messageSendForm.setMessageType(MessageTypeEnum.MAIL.getValue());
                messageSendForm.setReceiverUserType(UserTypeEnum.ADMIN_EMPLOYEE.getValue());
                messageSendForm.setReceiverUserId(receiverId);
                messageSendForm.setTitle("日报待审核");
                messageSendForm.setContent(this.buildSubmitMessageContent(reportEntity.getEmployeeName(), reportEntity.getDepartmentName(), reportEntity.getReportDate()));
                messageSendForm.setDataId(workDailyReportId);
                return messageSendForm;
            }).collect(Collectors.toList());
            messageService.sendMessage(messageSendFormList);
        } catch (Exception e) {
            log.error("发送提交日报待审核站内信失败，workDailyReportId:{}, employeeId:{}, departmentId:{}", workDailyReportId, employeeId, departmentId, e);
        }
    }

    private List<Long> getSubmitMessageReceiverIdList(Long departmentId) {
        if (departmentId == null) {
            return Collections.emptyList();
        }
        List<EmployeeEntity> employeeList = employeeDao.selectByDepartmentId(departmentId, false);
        if (CollectionUtils.isEmpty(employeeList)) {
            return Collections.emptyList();
        }
        return employeeList.stream()
                .filter(employee -> !Boolean.TRUE.equals(employee.getDisabledFlag()))
                .map(EmployeeEntity::getEmployeeId)
                .filter(this::hasDailyReportAuditPermission)
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean hasDailyReportAuditPermission(Long employeeId) {
        UserPermission userPermission = loginManager.getUserPermission(employeeId);
        if (userPermission == null || CollectionUtils.isEmpty(userPermission.getPermissionList())) {
            return false;
        }
        return userPermission.getPermissionList().contains(WORK_DAILY_REPORT_AUDIT_PERMISSION);
    }

    private String buildSubmitMessageContent(String employeeName, String departmentName, LocalDate reportDate) {
        String departmentNameText = StringUtils.isBlank(departmentName) ? StringUtils.EMPTY : "【" + departmentName + "】";
        String employeeNameText = StringUtils.isBlank(employeeName) ? "员工" : employeeName;
        String reportDateText = reportDate == null ? StringUtils.EMPTY : reportDate.toString();
        return departmentNameText + employeeNameText + "提交了" + reportDateText + "日报，请及时审核。";
    }

    private void sendAuditMessageAfterCommit(WorkDailyReportEntity reportEntity, boolean passFlag, String failReason) {
        Long workDailyReportId = reportEntity.getWorkDailyReportId();
        Long employeeId = reportEntity.getEmployeeId();
        LocalDate reportDate = reportEntity.getReportDate();
        BigDecimal totalScore = reportEntity.getTotalScore();
        Runnable sendTask = () -> this.sendAuditMessage(workDailyReportId, employeeId, reportDate, totalScore, passFlag, failReason);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    executeAuditMessageTask(sendTask, workDailyReportId, employeeId);
                }
            });
            return;
        }
        this.executeAuditMessageTask(sendTask, workDailyReportId, employeeId);
    }

    void executeAuditMessageTask(Runnable sendTask, Long workDailyReportId, Long employeeId) {
        try {
            asyncTaskExecutor.execute(sendTask);
        } catch (Exception e) {
            log.error("提交日报审核站内信异步任务失败，workDailyReportId:{}, employeeId:{}", workDailyReportId, employeeId, e);
        }
    }

    private void sendAuditMessage(Long workDailyReportId, Long employeeId, LocalDate reportDate, BigDecimal totalScore, boolean passFlag, String failReason) {
        try {
            MessageSendForm messageSendForm = new MessageSendForm();
            messageSendForm.setMessageType(MessageTypeEnum.MAIL.getValue());
            messageSendForm.setReceiverUserType(UserTypeEnum.ADMIN_EMPLOYEE.getValue());
            messageSendForm.setReceiverUserId(employeeId);
            messageSendForm.setTitle(passFlag ? "日报审核通过" : "日报审核失败");
            messageSendForm.setContent(this.buildAuditMessageContent(reportDate, totalScore, passFlag, failReason));
            messageSendForm.setDataId(workDailyReportId);
            messageService.sendMessage(messageSendForm);
        } catch (Exception e) {
            log.error("发送日报审核站内信失败，workDailyReportId:{}, employeeId:{}", workDailyReportId, employeeId, e);
        }
    }

    private String buildAuditMessageContent(LocalDate reportDate, BigDecimal totalScore, boolean passFlag, String failReason) {
        String reportDateText = reportDate == null ? StringUtils.EMPTY : reportDate.toString();
        if (passFlag) {
            return "您的" + reportDateText + "日报已审核通过，最终得分：" + totalScore + "。";
        }
        return "您的" + reportDateText + "日报审核失败，失败原因：" + failReason + "。";
    }
}
