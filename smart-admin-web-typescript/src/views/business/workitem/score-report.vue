<template>
  <div>
    <a-form class="smart-query-form">
      <a-row class="smart-query-form-row">
        <a-form-item label="开始日期" class="smart-query-form-item">
          <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="queryForm.startDate" style="width: 150px" />
        </a-form-item>
        <a-form-item label="结束日期" class="smart-query-form-item">
          <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="queryForm.endDate" style="width: 150px" />
        </a-form-item>
        <a-form-item label="员工ID" class="smart-query-form-item">
          <a-input-number v-model:value="queryForm.employeeId" style="width: 120px" placeholder="员工ID" />
        </a-form-item>
        <a-form-item label="部门ID" class="smart-query-form-item">
          <a-input-number v-model:value="queryForm.departmentId" style="width: 120px" placeholder="部门ID" />
        </a-form-item>
        <a-form-item class="smart-query-form-item">
          <a-button type="primary" @click="queryEmployeeScore" v-privilege="'workitem:score:report'">
            <template #icon><SearchOutlined /></template>
            查询
          </a-button>
        </a-form-item>
      </a-row>
    </a-form>

    <a-card size="small" :bordered="false">
      <a-table size="small" bordered rowKey="employeeId" :loading="employeeLoading" :dataSource="employeeData" :columns="employeeColumns" :pagination="false">
        <template #bodyCell="{ record, column }">
          <template v-if="column.dataIndex === 'action'">
            <a-button type="link" @click="showDateDetail(record)">日期明细</a-button>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal v-model:open="dateVisible" title="日期明细" :width="760" :footer="null">
      <a-table size="small" bordered rowKey="reportDate" :dataSource="dateData" :columns="dateColumns" :pagination="false">
        <template #bodyCell="{ record, column }">
          <template v-if="column.dataIndex === 'action'">
            <a-button type="link" @click="showTypeDetail(record)">类型明细</a-button>
          </template>
        </template>
      </a-table>
    </a-modal>

    <a-modal v-model:open="typeVisible" title="类型明细" :width="760" :footer="null">
      <a-table size="small" bordered rowKey="workItemTypeId" :dataSource="typeData" :columns="typeColumns" :pagination="false">
        <template #bodyCell="{ record, column }">
          <template v-if="column.dataIndex === 'action'">
            <a-button type="link" @click="showItemDetail(record)">工作项明细</a-button>
          </template>
        </template>
      </a-table>
    </a-modal>

    <a-modal v-model:open="itemVisible" title="工作项明细" :width="900" :footer="null">
      <a-table size="small" bordered rowKey="workItemName" :dataSource="itemData" :columns="itemColumns" :pagination="false" />
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { onMounted, reactive, ref } from 'vue';
  import { SearchOutlined } from '@ant-design/icons-vue';
  import dayjs from 'dayjs';
  import { workitemApi } from '/@/api/business/workitem/workitem-api';
  import { smartSentry } from '/@/lib/smart-sentry';

  type WorkitemRecord = Record<string, any>;

  const queryForm = reactive<any>({
    startDate: dayjs().startOf('month').format('YYYY-MM-DD'),
    endDate: dayjs().format('YYYY-MM-DD'),
    employeeId: undefined,
    departmentId: undefined,
  });

  const employeeLoading = ref(false);
  const employeeData = ref<any[]>([]);
  const currentEmployee = ref<any>();
  const currentDate = ref<any>();
  const employeeColumns = [
    { title: '员工', dataIndex: 'employeeName', width: 120 },
    { title: '部门', dataIndex: 'departmentName', width: 160 },
    { title: '通过日报天数', dataIndex: 'reportCount', width: 120 },
    { title: '明细数量', dataIndex: 'itemCount', width: 100 },
    { title: '总分', dataIndex: 'totalScore', width: 100 },
    { title: '操作', dataIndex: 'action', width: 120 },
  ];

  async function queryEmployeeScore() {
    employeeLoading.value = true;
    try {
      const res = await workitemApi.queryEmployeeScore(queryForm);
      employeeData.value = res.data || [];
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      employeeLoading.value = false;
    }
  }

  const dateVisible = ref(false);
  const dateData = ref<any[]>([]);
  const dateColumns = [
    { title: '日期', dataIndex: 'reportDate' },
    { title: '明细数量', dataIndex: 'itemCount' },
    { title: '总分', dataIndex: 'totalScore' },
    { title: '操作', dataIndex: 'action' },
  ];

  async function showDateDetail(record: WorkitemRecord) {
    currentEmployee.value = record;
    const res = await workitemApi.queryDateScore({ ...queryForm, employeeId: record.employeeId });
    dateData.value = res.data || [];
    dateVisible.value = true;
  }

  const typeVisible = ref(false);
  const typeData = ref<any[]>([]);
  const typeColumns = [
    { title: '类型', dataIndex: 'workItemTypeName' },
    { title: '明细数量', dataIndex: 'itemCount' },
    { title: '总分', dataIndex: 'totalScore' },
    { title: '操作', dataIndex: 'action' },
  ];

  async function showTypeDetail(record: WorkitemRecord) {
    currentDate.value = record.reportDate;
    const res = await workitemApi.queryTypeScore({ ...queryForm, employeeId: currentEmployee.value.employeeId, reportDate: record.reportDate });
    typeData.value = res.data || [];
    typeVisible.value = true;
  }

  const itemVisible = ref(false);
  const itemData = ref<any[]>([]);
  const itemColumns = [
    { title: '工作项', dataIndex: 'workItemName', width: 160 },
    { title: '标准分', dataIndex: 'standardScore', width: 100 },
    { title: '最终分', dataIndex: 'finalScore', width: 100 },
    { title: '扣分原因', dataIndex: 'deductReason', width: 180 },
    { title: '完成说明', dataIndex: 'finishRemark' },
  ];

  async function showItemDetail(record: WorkitemRecord) {
    const res = await workitemApi.queryItemScore({
      ...queryForm,
      employeeId: currentEmployee.value.employeeId,
      reportDate: currentDate.value,
      workItemTypeId: record.workItemTypeId,
    });
    itemData.value = res.data || [];
    itemVisible.value = true;
  }

  onMounted(queryEmployeeScore);
</script>
