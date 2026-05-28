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
        <a-form-item label="员工" class="smart-query-form-item">
          <a-input v-model:value="queryForm.keywords" style="width: 180px" placeholder="员工姓名" allowClear />
        </a-form-item>
        <a-form-item label="机构" class="smart-query-form-item">
          <DepartmentTreeSelect v-model:value="queryForm.departmentId" :query-api="workitemApi.queryScoreReportDepartmentTree" style="width: 180px" />
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

    <a-modal v-model:open="dateVisible" title="日期明细" :width="1180" :footer="null">
      <div class="score-detail-layout">
        <div class="score-detail-column">
          <div class="detail-column-title">日期+总分</div>
          <a-list :data-source="dateData" :loading="dateLoading" size="small">
            <template #renderItem="{ item }">
              <a-list-item :class="{ active: item.reportDate === currentDate }" @click="selectDateDetail(item)">
                <div class="date-row">
                  <span>{{ item.reportDate }}</span>
                  <span>{{ item.totalScore }}分</span>
                </div>
              </a-list-item>
            </template>
          </a-list>
        </div>
        <div class="score-detail-column item-detail-column">
          <div class="detail-column-title">工作项详情</div>
          <a-table
            size="small"
            bordered
            rowKey="workItemId"
            :loading="itemLoading"
            :dataSource="itemData"
            :columns="itemColumns"
            :pagination="false"
          >
            <template #bodyCell="{ record, column }">
              <template v-if="column.dataIndex === 'workItemTypeName'">
                <a-tag color="blue">{{ record.workItemTypeName || '未分类' }}</a-tag>
              </template>
            </template>
          </a-table>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { onMounted, reactive, ref } from 'vue';
  import { SearchOutlined } from '@ant-design/icons-vue';
  import dayjs from 'dayjs';
  import { workitemApi } from '/@/api/business/workitem/workitem-api';
  import DepartmentTreeSelect from '/@/components/system/department-tree-select/index.vue';
  import { smartSentry } from '/@/lib/smart-sentry';

  type WorkitemRecord = Record<string, any>;

  const queryForm = reactive<any>({
    startDate: dayjs().startOf('month').format('YYYY-MM-DD'),
    endDate: dayjs().format('YYYY-MM-DD'),
    keywords: '',
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
  const dateLoading = ref(false);
  const dateData = ref<any[]>([]);

  async function showDateDetail(record: WorkitemRecord) {
    currentEmployee.value = record;
    currentDate.value = undefined;
    itemData.value = [];
    dateVisible.value = true;
    dateLoading.value = true;
    try {
      const res = await workitemApi.queryDateScore({ ...queryForm, employeeId: record.employeeId });
      dateData.value = res.data || [];
      if (dateData.value.length > 0) {
        await selectDateDetail(dateData.value[0]);
      }
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      dateLoading.value = false;
    }
  }

  async function selectDateDetail(record: WorkitemRecord) {
    currentDate.value = record.reportDate;
    itemData.value = [];
    await queryItemDetail();
  }

  const itemLoading = ref(false);
  const itemData = ref<any[]>([]);
  const itemColumns = [
    { title: '类型', dataIndex: 'workItemTypeName', width: 130 },
    { title: '工作项', dataIndex: 'workItemName', width: 180 },
    { title: '标准分', dataIndex: 'standardScore', width: 100 },
    { title: '最终分', dataIndex: 'finalScore', width: 100 },
    { title: '扣分原因', dataIndex: 'deductReason', width: 180 },
  ];

  async function queryItemDetail() {
    itemLoading.value = true;
    try {
      const res = await workitemApi.queryItemScore({
        ...queryForm,
        employeeId: currentEmployee.value.employeeId,
        reportDate: currentDate.value,
      });
      itemData.value = res.data || [];
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      itemLoading.value = false;
    }
  }

  onMounted(queryEmployeeScore);
</script>

<style scoped lang="less">
  .score-detail-layout {
    display: grid;
    grid-template-columns: 260px minmax(0, 1fr);
    gap: 12px;
    min-height: 520px;
  }

  .score-detail-column {
    min-width: 0;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
    padding: 10px;
    overflow: auto;
  }

  .item-detail-column {
    border-color: #91caff;
  }

  .detail-column-title {
    margin-bottom: 8px;
    font-weight: 600;
  }

  :deep(.ant-list-item) {
    cursor: pointer;
    padding: 8px;
  }

  :deep(.ant-list-item.active) {
    background: #e6f4ff;
    color: #0958d9;
    font-weight: 600;
  }

  .date-row {
    display: flex;
    justify-content: space-between;
    gap: 8px;
    width: 100%;
  }
</style>
