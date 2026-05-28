<template>
  <div>
    <a-form class="smart-query-form">
      <a-row class="smart-query-form-row">
        <a-form-item label="员工" class="smart-query-form-item">
          <a-input style="width: 160px" v-model:value="queryForm.keywords" placeholder="员工姓名" allowClear />
        </a-form-item>
        <a-form-item label="状态" class="smart-query-form-item">
          <SmartEnumSelect enum-name="WORK_DAILY_REPORT_STATUS_ENUM" v-model:value="queryForm.status" width="140px" />
        </a-form-item>
        <a-form-item label="开始日期" class="smart-query-form-item">
          <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="queryForm.startDate" style="width: 150px" />
        </a-form-item>
        <a-form-item label="结束日期" class="smart-query-form-item">
          <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="queryForm.endDate" style="width: 150px" />
        </a-form-item>
        <a-form-item class="smart-query-form-item">
          <a-button-group>
            <a-button type="primary" @click="onSearch" v-privilege="'workitem:daily:review'">
              <template #icon><SearchOutlined /></template>
              查询
            </a-button>
            <a-button @click="resetQuery" v-privilege="'workitem:daily:review'">
              <template #icon><ReloadOutlined /></template>
              重置
            </a-button>
          </a-button-group>
        </a-form-item>
      </a-row>
    </a-form>

    <a-card size="small" :bordered="false">
      <a-table size="small" bordered rowKey="workDailyReportId" :loading="tableLoading" :dataSource="tableData" :columns="columns" :pagination="false">
        <template #bodyCell="{ text, record, column }">
          <template v-if="column.dataIndex === 'status'">
            <span>{{ $smartEnumPlugin.getDescByValue('WORK_DAILY_REPORT_STATUS_ENUM', text) }}</span>
          </template>
          <template v-if="column.dataIndex === 'action'">
            <div class="smart-table-operate">
              <a-button type="link" @click="showDetail(record)">详情</a-button>
              <a-button v-if="record.status === WORK_DAILY_REPORT_STATUS_ENUM.WAIT_AUDIT.value" type="link" @click="showAudit(record)" v-privilege="'workitem:daily:audit'">审核</a-button>
            </div>
          </template>
        </template>
      </a-table>
      <div class="smart-query-table-page">
        <a-pagination
          showSizeChanger
          showQuickJumper
          :pageSizeOptions="PAGE_SIZE_OPTIONS"
          v-model:current="queryForm.pageNum"
          v-model:pageSize="queryForm.pageSize"
          :total="total"
          @change="queryData"
          :show-total="showTotal"
        />
      </div>
    </a-card>

    <a-modal v-model:open="detailVisible" :title="auditMode ? '审核日报' : '日报详情'" :width="1000" @ok="submitAudit" @cancel="closeDetail">
      <template #footer>
        <a-space v-if="auditMode">
          <a-button @click="closeDetail">取消</a-button>
          <a-button danger @click="submitAudit(false)">审核失败</a-button>
          <a-button type="primary" @click="submitAudit(true)">审核通过</a-button>
        </a-space>
        <a-button v-else @click="closeDetail">关闭</a-button>
      </template>

      <a-descriptions size="small" bordered :column="4">
        <a-descriptions-item label="员工">{{ detail.employeeName }}</a-descriptions-item>
        <a-descriptions-item label="部门">{{ detail.departmentName }}</a-descriptions-item>
        <a-descriptions-item label="日期">{{ detail.reportDate }}</a-descriptions-item>
        <a-descriptions-item label="状态">{{ $smartEnumPlugin.getDescByValue('WORK_DAILY_REPORT_STATUS_ENUM', detail.status) }}</a-descriptions-item>
      </a-descriptions>

      <a-table
        class="detail-table"
        size="small"
        bordered
        rowKey="workDailyReportItemId"
        :dataSource="detail.itemList || []"
        :columns="detailColumns"
        :pagination="false"
      >
        <template #bodyCell="{ record, column }">
          <template v-if="column.dataIndex === 'score'">
            <a-space>
              <span>标准 {{ record.standardScore }}</span>
              <a-input-number v-if="auditMode" v-model:value="record.finalScore" :min="0" :precision="2" style="width: 100px" />
              <span v-else>最终 {{ record.finalScore }}</span>
            </a-space>
          </template>
          <template v-if="column.dataIndex === 'deductReason'">
            <a-input v-if="auditMode" v-model:value="record.deductReason" placeholder="扣分原因（选填）" />
            <span v-else>{{ record.deductReason }}</span>
          </template>
          <template v-if="column.dataIndex === 'fileList'">
            <a-image-preview-group>
              <a-space wrap>
                <a-image v-for="file in record.fileList" :key="file.fileKey" :width="48" :height="48" :src="file.fileUrl" />
              </a-space>
            </a-image-preview-group>
          </template>
        </template>
      </a-table>

      <a-divider>审核历史</a-divider>
      <a-timeline>
        <a-timeline-item v-for="audit in detail.auditList || []" :key="audit.workDailyReportAuditId">
          {{ audit.auditTime }} {{ audit.auditEmployeeName }}
          {{ historyActionText(audit.auditResult) }}
          <span v-if="audit.failReason">：{{ audit.failReason }}</span>
        </a-timeline-item>
      </a-timeline>
    </a-modal>

    <a-modal v-model:open="failModalVisible" title="审核失败" @ok="confirmFailAudit">
      <a-textarea v-model:value="failReason" :rows="4" placeholder="请输入失败原因" />
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { onMounted, reactive, ref } from 'vue';
  import { message } from 'ant-design-vue';
  import { ReloadOutlined, SearchOutlined } from '@ant-design/icons-vue';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';
  import { workitemApi } from '/@/api/business/workitem/workitem-api';
  import { WORK_DAILY_REPORT_STATUS_ENUM } from '/@/constants/business/workitem/workitem-const';
  import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { smartSentry } from '/@/lib/smart-sentry';

  type WorkitemRecord = Record<string, any>;

  function cloneForm<T>(form: T): T {
    return { ...form };
  }

  // ---------------------------- 查询 ----------------------------
  const queryFormState = { keywords: '', status: WORK_DAILY_REPORT_STATUS_ENUM.WAIT_AUDIT.value, startDate: undefined, endDate: undefined, pageNum: 1, pageSize: 10, sortItemList: [] };
  const queryForm = reactive(cloneForm(queryFormState));
  const tableLoading = ref(false);
  const tableData = ref<any[]>([]);
  const total = ref(0);
  const columns = [
    { title: '员工', dataIndex: 'employeeName', width: 120 },
    { title: '部门', dataIndex: 'departmentName', width: 160 },
    { title: '日期', dataIndex: 'reportDate', width: 120 },
    { title: '状态', dataIndex: 'status', width: 100 },
    { title: '总分', dataIndex: 'totalScore', width: 90 },
    { title: '提交时间', dataIndex: 'submitTime', width: 170 },
    { title: '操作', dataIndex: 'action', fixed: 'right', width: 120 },
  ];

  async function queryData() {
    tableLoading.value = true;
    try {
      const res = await workitemApi.queryReviewPage(queryForm);
      tableData.value = res.data.list;
      total.value = res.data.total;
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  function onSearch() {
    queryForm.pageNum = 1;
    queryData();
  }

  function resetQuery() {
    const pageSize = queryForm.pageSize;
    Object.assign(queryForm, cloneForm(queryFormState));
    queryForm.pageSize = pageSize;
    queryData();
  }

  function showTotal(total: number) {
    return `共${total}条`;
  }

  // ---------------------------- 详情和审核 ----------------------------
  const detailVisible = ref(false);
  const auditMode = ref(false);
  const detail = reactive<any>({});
  const failModalVisible = ref(false);
  const failReason = ref('');
  const detailColumns = [
    { title: '类型', dataIndex: 'workItemTypeName', width: 120 },
    { title: '工作项', dataIndex: 'workItemName', width: 160 },
    { title: '完成说明', dataIndex: 'finishRemark' },
    { title: '分数', dataIndex: 'score', width: 210 },
    { title: '扣分原因', dataIndex: 'deductReason', width: 180 },
    { title: '图片', dataIndex: 'fileList', width: 180 },
  ];

  async function loadDetail(record: WorkitemRecord) {
    const res = await workitemApi.getReviewDetail(record.workDailyReportId);
    Object.assign(detail, res.data);
  }

  async function showDetail(record: WorkitemRecord) {
    auditMode.value = false;
    await loadDetail(record);
    detailVisible.value = true;
  }

  async function showAudit(record: WorkitemRecord) {
    auditMode.value = true;
    await loadDetail(record);
    detailVisible.value = true;
  }

  function closeDetail() {
    detailVisible.value = false;
  }

  function historyActionText(auditResult: number) {
    if (auditResult === WORK_DAILY_REPORT_STATUS_ENUM.WAIT_AUDIT.value) {
      return '提交审核';
    }
    return Object.values(WORK_DAILY_REPORT_STATUS_ENUM).find((e: any) => e.value === auditResult)?.desc || '-';
  }

  function submitAudit(passFlag: boolean) {
    if (!auditMode.value) {
      closeDetail();
      return;
    }
    const doSubmit = async (failReason = '') => {
      try {
        SmartLoading.show();
        await workitemApi.auditDaily({
          workDailyReportId: detail.workDailyReportId,
          passFlag,
          failReason,
          itemList: (detail.itemList || []).map((item: WorkitemRecord) => ({
            workDailyReportItemId: item.workDailyReportItemId,
            finalScore: item.finalScore,
            deductReason: item.deductReason,
          })),
        });
        message.success('审核成功');
        closeDetail();
        queryData();
      } catch (e) {
        smartSentry.captureError(e);
      } finally {
        SmartLoading.hide();
      }
    };

    if (passFlag) {
      doSubmit();
      return;
    }
    failReason.value = '';
    failModalVisible.value = true;
  }

  async function confirmFailAudit() {
    if (!failReason.value) {
      message.error('请输入失败原因');
      return;
    }
    try {
      SmartLoading.show();
      await workitemApi.auditDaily({
        workDailyReportId: detail.workDailyReportId,
        passFlag: false,
        failReason: failReason.value,
        itemList: (detail.itemList || []).map((item: WorkitemRecord) => ({
          workDailyReportItemId: item.workDailyReportItemId,
          finalScore: item.finalScore,
          deductReason: item.deductReason,
        })),
      });
      message.success('审核成功');
      failModalVisible.value = false;
      closeDetail();
      queryData();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  onMounted(queryData);
</script>

<style scoped lang="less">
  .detail-table {
    margin-top: 12px;
  }
</style>
