<template>
  <div class="my-daily-report">
    <a-card size="small" :bordered="false">
      <div class="daily-toolbar">
        <a-space>
          <span>日报日期</span>
          <a-date-picker
            valueFormat="YYYY-MM-DD"
            v-model:value="reportDate"
            style="width: 160px"
            :allowClear="false"
            @change="handleReportDateChange"
          />
          <a-tag :color="statusColor">{{ statusText }}</a-tag>
          <span v-if="showLatestFailReason" class="fail-reason">失败原因：{{ detail.latestFailReason }}</span>
          <a-button size="small" @click="showHistoryModal" :disabled="!detail.workDailyReportId">
            <template #icon><HistoryOutlined /></template>
            审批历史
          </a-button>
        </a-space>
        <a-space>
          <a-button @click="loadReportByDate">
            <template #icon><ReloadOutlined /></template>
            刷新
          </a-button>
          <a-button type="primary" @click="saveDraft" :disabled="!editable" v-privilege="'workitem:daily:save'">保存草稿</a-button>
          <a-button type="primary" @click="submitDaily" :disabled="!detail.workDailyReportId || !editable" v-privilege="'workitem:daily:submit'">提交审核</a-button>
        </a-space>
      </div>
      <a-alert
        v-if="reportDateOutOfRange"
        class="date-limit-alert"
        type="warning"
        show-icon
        :message="`只允许填报最近${safeAllowReplenishDays}天内的日报`"
      />

      <div class="daily-layout">
        <div class="type-column">
          <div class="column-title">工作项类型</div>
          <div :class="['type-all-item', { active: !selectedTypeId }]" @click="selectAllType">全部类型</div>
          <a-list :data-source="typeList" size="small">
            <template #renderItem="{ item }">
              <a-list-item :class="{ active: item.workItemTypeId === selectedTypeId }" @click="selectType(item)">
                {{ item.typeName }}
              </a-list-item>
            </template>
          </a-list>
        </div>

        <div class="choice-column">
          <div class="column-title">可选工作项</div>
          <a-input-search v-model:value="itemKeywords" placeholder="搜索工作项" allowClear @search="queryChoiceItems" />
          <a-list :data-source="choiceItemList" :loading="choiceLoading" size="small" class="choice-list">
            <template #renderItem="{ item }">
              <a-list-item>
                <a-list-item-meta>
                  <template #title>
                    <a-tag color="blue">{{ item.standardScore }}分</a-tag>
                    <span>{{ item.workItemName }}</span>
                  </template>
                  <template #description>{{ item.description || item.scoreStandard }}</template>
                </a-list-item-meta>
                <template #actions>
                  <a-button type="link" :disabled="!editable" @click="addReportItem(item)">添加</a-button>
                </template>
              </a-list-item>
            </template>
          </a-list>
        </div>

        <div class="filled-column">
          <div class="column-title">
            <span>已填明细</span>
            <span>共 {{ reportItemList.length }} 项</span>
          </div>
          <a-empty v-if="reportItemList.length === 0" description="暂无已填明细" />
          <div v-for="group in groupedReportItems" :key="group.typeId" class="item-group">
            <div class="group-title">{{ group.typeName }}</div>
            <a-card
              v-for="item in group.itemList"
              :key="getReportItemRowKey(item)"
              :id="`daily-item-${getReportItemRowKey(item)}`"
              size="small"
              class="filled-item"
            >
              <div class="filled-title">
                <span>{{ item.workItemName }}</span>
                <a-space>
                  <a-tag color="blue">{{ item.standardScore }}分</a-tag>
                  <a-button type="link" danger size="small" :disabled="!editable" @click="removeReportItem(item)">删除</a-button>
                </a-space>
              </div>
              <a-textarea v-model:value="item.finishRemark" :disabled="!editable" :rows="3" placeholder="请输入完成说明" />
              <div class="upload-block">
                <Upload
                  accept=".jpg,.jpeg,.png,.gif"
                  :folder="FILE_FOLDER_TYPE_ENUM.WORK_ITEM.value"
                  :multiple="true"
                  :default-file-list="item.fileList"
                  :show-upload-btn="editable"
                  :compress-image="true"
                  buttonText="上传佐证"
                  @change="(fileList) => changeFileList(item, fileList)"
                />
              </div>
            </a-card>
          </div>
        </div>
      </div>
    </a-card>

    <a-modal v-model:open="historyVisible" title="审批历史" :width="640" :footer="null">
      <a-empty v-if="historyList.length === 0" description="暂无审批历史" />
      <a-timeline v-else>
        <a-timeline-item v-for="history in historyList" :key="history.workDailyReportAuditId" :color="historyColor(history.auditResult)">
          <div class="history-title">
            <span>{{ historyActionText(history.auditResult) }}</span>
            <span>{{ history.auditTime }}</span>
          </div>
          <div class="history-desc">
            {{ history.auditEmployeeName || '-' }}
            <span v-if="history.failReason">：{{ history.failReason }}</span>
          </div>
        </a-timeline-item>
      </a-timeline>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { computed, onMounted, reactive, ref } from 'vue';
  import { message, Modal } from 'ant-design-vue';
  import { HistoryOutlined, ReloadOutlined } from '@ant-design/icons-vue';
  import dayjs from 'dayjs';
  import Upload from '/@/components/support/file-upload/index.vue';
  import { workitemApi } from '/@/api/business/workitem/workitem-api';
  import { FILE_FOLDER_TYPE_ENUM } from '/@/constants/support/file-const';
  import { WORK_DAILY_REPORT_STATUS_ENUM, WORK_ITEM_CONFIG_KEY } from '/@/constants/business/workitem/workitem-const';
  import { configApi } from '/@/api/support/config-api';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { smartSentry } from '/@/lib/smart-sentry';

  type WorkitemRecord = Record<string, any>;

  // ---------------------------- 日报状态 ----------------------------
  const reportDate = ref(dayjs().format('YYYY-MM-DD'));
  const detail = reactive<any>({});
  const reportItemList = ref<any[]>([]);
  let tempReportItemIndex = 0;
  const allowReplenishDays = ref(2);
  const safeAllowReplenishDays = computed(() => Math.max(Number(allowReplenishDays.value) || 2, 1));
  const reportDateOutOfRange = computed(() => !isReportDateAllowed(reportDate.value));

  const editable = computed(() => {
    const editableStatus = !detail.status || detail.status === WORK_DAILY_REPORT_STATUS_ENUM.DRAFT.value || detail.status === WORK_DAILY_REPORT_STATUS_ENUM.AUDIT_FAIL.value;
    return editableStatus && !reportDateOutOfRange.value;
  });
  const showLatestFailReason = computed(() => detail.status === WORK_DAILY_REPORT_STATUS_ENUM.AUDIT_FAIL.value && detail.latestFailReason);
  const statusText = computed(() => {
    if (!detail.status) {
      return '未创建';
    }
    return Object.values(WORK_DAILY_REPORT_STATUS_ENUM).find((e: any) => e.value === detail.status)?.desc || '-';
  });
  const statusColor = computed(() => {
    if (detail.status === WORK_DAILY_REPORT_STATUS_ENUM.AUDIT_PASS.value) {
      return 'green';
    }
    if (detail.status === WORK_DAILY_REPORT_STATUS_ENUM.AUDIT_FAIL.value) {
      return 'red';
    }
    if (detail.status === WORK_DAILY_REPORT_STATUS_ENUM.WAIT_AUDIT.value) {
      return 'blue';
    }
    return 'default';
  });

  async function queryAllowReplenishDays() {
    try {
      const res = await configApi.queryByKey(WORK_ITEM_CONFIG_KEY.ALLOW_REPLENISH_DAYS);
      const configValue = Number(res.data?.configValue);
      allowReplenishDays.value = Number.isFinite(configValue) && configValue > 0 ? configValue : 2;
    } catch (e) {
      smartSentry.captureError(e);
    }
  }

  function isReportDateAllowed(dateValue?: string) {
    if (!dateValue) {
      return false;
    }
    const currentDate = dayjs(dateValue).startOf('day');
    const today = dayjs().startOf('day');
    const earliestDate = today.subtract(safeAllowReplenishDays.value - 1, 'day');
    return !currentDate.isBefore(earliestDate) && !currentDate.isAfter(today);
  }

  function remindReportDateLimit() {
    message.warning(`只允许填报最近${safeAllowReplenishDays.value}天内的日报`);
  }

  async function handleReportDateChange() {
    if (reportDateOutOfRange.value) {
      remindReportDateLimit();
    }
    await loadReportByDate();
  }

  async function loadReportByDate() {
    try {
      SmartLoading.show();
      const res = await workitemApi.queryMyDailyPage({
        pageNum: 1,
        pageSize: 1,
        startDate: reportDate.value,
        endDate: reportDate.value,
      });
      const report = res.data.list?.[0];
      if (!report) {
        Object.keys(detail).forEach((key) => delete detail[key]);
        reportItemList.value = [];
        return;
      }
      const detailRes = await workitemApi.getMyDailyDetail(report.workDailyReportId);
      Object.assign(detail, detailRes.data);
      reportItemList.value = normalizeReportItemList(detailRes.data.itemList || []);
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  // ---------------------------- 工作项选择 ----------------------------
  const typeList = ref<any[]>([]);
  const selectedTypeId = ref();
  const itemKeywords = ref('');
  const choiceLoading = ref(false);
  const choiceItemList = ref<any[]>([]);

  async function queryTypeList() {
    const res = await workitemApi.queryTypeList(false);
    typeList.value = res.data || [];
    selectedTypeId.value = undefined;
  }

  function selectType(item: WorkitemRecord) {
    if (selectedTypeId.value === item.workItemTypeId) {
      selectAllType();
      return;
    }
    selectedTypeId.value = item.workItemTypeId;
    queryChoiceItems();
  }

  function selectAllType() {
    selectedTypeId.value = undefined;
    queryChoiceItems();
  }

  async function queryChoiceItems() {
    choiceLoading.value = true;
    try {
      const res = await workitemApi.queryItemList(selectedTypeId.value, itemKeywords.value);
      choiceItemList.value = res.data || [];
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      choiceLoading.value = false;
    }
  }

  function createReportItemRowKey(item: WorkitemRecord) {
    if (item.workDailyReportItemId) {
      return `saved-${item.workDailyReportItemId}`;
    }
    tempReportItemIndex += 1;
    return `temp-${Date.now()}-${tempReportItemIndex}`;
  }

  function normalizeReportItem(item: WorkitemRecord) {
    return {
      ...item,
      rowKey: item.rowKey || createReportItemRowKey(item),
      fileList: item.fileList || [],
    };
  }

  function normalizeReportItemList(itemList: WorkitemRecord[]) {
    return itemList.map((item) => normalizeReportItem(item));
  }

  function getReportItemRowKey(item: WorkitemRecord) {
    if (!item.rowKey) {
      item.rowKey = createReportItemRowKey(item);
    }
    return item.rowKey;
  }

  function addReportItem(item: WorkitemRecord) {
    reportItemList.value.push(
      normalizeReportItem({
        workItemId: item.workItemId,
        workItemTypeId: item.workItemTypeId,
        workItemTypeName: item.workItemTypeName,
        workItemName: item.workItemName,
        description: item.description,
        scoreStandard: item.scoreStandard,
        standardScore: item.standardScore,
        finishRemark: '',
        fileList: [],
      })
    );
  }

  function removeReportItem(item: WorkitemRecord) {
    const rowKey = getReportItemRowKey(item);
    reportItemList.value = reportItemList.value.filter((e) => getReportItemRowKey(e) !== rowKey);
  }

  function changeFileList(item: WorkitemRecord, fileList: WorkitemRecord[]) {
    item.fileList = fileList;
  }

  const groupedReportItems = computed(() => {
    const groupMap = new Map();
    reportItemList.value.forEach((item) => {
      const key = item.workItemTypeId || 0;
      if (!groupMap.has(key)) {
        groupMap.set(key, { typeId: key, typeName: item.workItemTypeName || '未分类', itemList: [] });
      }
      groupMap.get(key).itemList.push(item);
    });
    return Array.from(groupMap.values());
  });

  // ---------------------------- 审批历史 ----------------------------
  const historyVisible = ref(false);
  const historyList = computed(() => detail.auditList || []);

  function showHistoryModal() {
    historyVisible.value = true;
  }

  function historyActionText(auditResult: number) {
    if (auditResult === WORK_DAILY_REPORT_STATUS_ENUM.WAIT_AUDIT.value) {
      return '提交审核';
    }
    return Object.values(WORK_DAILY_REPORT_STATUS_ENUM).find((e: any) => e.value === auditResult)?.desc || '-';
  }

  function historyColor(auditResult: number) {
    if (auditResult === WORK_DAILY_REPORT_STATUS_ENUM.AUDIT_PASS.value) {
      return 'green';
    }
    if (auditResult === WORK_DAILY_REPORT_STATUS_ENUM.AUDIT_FAIL.value) {
      return 'red';
    }
    return 'blue';
  }

  // ---------------------------- 保存和提交 ----------------------------
  async function saveDraft() {
    if (reportDateOutOfRange.value) {
      remindReportDateLimit();
      return;
    }
    if (reportItemList.value.length === 0) {
      message.warning('请至少添加一条工作项');
      return;
    }
    try {
      SmartLoading.show();
      await workitemApi.saveDailyDraft({
        workDailyReportId: detail.workDailyReportId,
        reportDate: reportDate.value,
        itemList: reportItemList.value.map((item: WorkitemRecord) => ({
          workItemId: item.workItemId,
          finishRemark: item.finishRemark,
          fileList: (item.fileList || []).map((file: WorkitemRecord) => ({
            fileId: file.fileId,
            fileKey: file.fileKey,
            fileName: file.fileName || file.name,
          })),
        })),
      });
      message.success('保存成功');
      await loadReportByDate();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  function submitDaily() {
    if (reportDateOutOfRange.value) {
      remindReportDateLimit();
      return;
    }
    Modal.confirm({
      title: '提示',
      content: '确定提交审核吗？提交后将不可编辑，审核失败后可再次修改。',
      onOk: async () => {
        await workitemApi.submitDaily(detail.workDailyReportId);
        message.success('提交成功');
        await loadReportByDate();
      },
    });
  }

  onMounted(async () => {
    await queryAllowReplenishDays();
    await queryTypeList();
    await queryChoiceItems();
    await loadReportByDate();
  });
</script>

<style scoped lang="less">
  .daily-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
  }

  .date-limit-alert {
    margin-bottom: 12px;
  }

  .daily-layout {
    display: grid;
    grid-template-columns: 220px minmax(320px, 1fr) minmax(360px, 1.2fr);
    gap: 12px;
    min-height: calc(100vh - 190px);
  }

  .type-column,
  .choice-column,
  .filled-column {
    min-width: 0;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
    background: #fff;
    padding: 10px;
    overflow: auto;
  }

  .type-column,
  .choice-column {
    background: #fafafa;
  }

  .filled-column {
    border-color: #91caff;
    background: #fafdff;
  }

  .column-title {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;
    font-weight: 600;
  }

  .type-all-item {
    cursor: pointer;
    padding: 8px;
    margin-bottom: 4px;
    border-radius: 4px;
  }

  .type-all-item.active,
  :deep(.ant-list-item.active) {
    background: #e6f4ff;
    color: #0958d9;
    font-weight: 600;
  }

  .choice-list {
    margin-top: 8px;
  }

  :deep(.ant-list-item) {
    cursor: pointer;
    padding: 8px;
  }

  .item-group + .item-group {
    margin-top: 12px;
  }

  .group-title {
    margin-bottom: 8px;
    padding: 4px 8px;
    border-left: 3px solid #1677ff;
    background: #e6f4ff;
    color: #1677ff;
    font-weight: 600;
  }

  .filled-item + .filled-item {
    margin-top: 8px;
  }

  .filled-title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8px;
    font-weight: 600;
  }

  .filled-item {
    border-color: #91caff;
    box-shadow: 0 2px 8px rgba(22, 119, 255, 0.08);
  }

  .upload-block {
    margin-top: 8px;
  }

  .fail-reason {
    color: #cf1322;
  }

  .history-title {
    display: flex;
    justify-content: space-between;
    font-weight: 600;
  }

  .history-desc {
    color: rgba(0, 0, 0, 0.65);
  }
</style>
