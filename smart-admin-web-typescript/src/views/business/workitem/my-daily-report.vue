<template>
  <div class="my-daily-report">
    <a-card size="small" :bordered="false">
      <div class="daily-toolbar">
        <a-space>
          <span>日报日期</span>
          <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="reportDate" style="width: 160px" @change="loadReportByDate" />
          <a-tag :color="statusColor">{{ statusText }}</a-tag>
          <span v-if="detail.latestFailReason" class="fail-reason">失败原因：{{ detail.latestFailReason }}</span>
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

      <div class="daily-layout">
        <div class="type-column">
          <div class="column-title">工作项类型</div>
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
                  <a-button v-if="isAdded(item.workItemId)" type="link" @click="scrollToItem(item.workItemId)">已添加</a-button>
                  <a-button v-else type="link" :disabled="!editable" @click="addReportItem(item)">添加</a-button>
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
          <a-empty v-if="reportItemList.length === 0" description="请选择左侧工作项添加" />
          <div v-for="group in groupedReportItems" :key="group.typeId" class="item-group">
            <div class="group-title">{{ group.typeName }}</div>
            <a-card
              v-for="item in group.itemList"
              :key="item.workItemId"
              :id="`daily-item-${item.workItemId}`"
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
                  buttonText="上传佐证"
                  @change="(fileList) => changeFileList(item, fileList)"
                />
              </div>
            </a-card>
          </div>
        </div>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
  import { computed, onMounted, reactive, ref } from 'vue';
  import { message, Modal } from 'ant-design-vue';
  import { ReloadOutlined } from '@ant-design/icons-vue';
  import dayjs from 'dayjs';
  import Upload from '/@/components/support/file-upload/index.vue';
  import { workitemApi } from '/@/api/business/workitem/workitem-api';
  import { FILE_FOLDER_TYPE_ENUM } from '/@/constants/support/file-const';
  import { WORK_DAILY_REPORT_STATUS_ENUM } from '/@/constants/business/workitem/workitem-const';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { smartSentry } from '/@/lib/smart-sentry';

  type WorkitemRecord = Record<string, any>;

  // ---------------------------- 日报状态 ----------------------------
  const reportDate = ref(dayjs().format('YYYY-MM-DD'));
  const detail = reactive<any>({});
  const reportItemList = ref<any[]>([]);

  const editable = computed(() => !detail.status || detail.status === WORK_DAILY_REPORT_STATUS_ENUM.DRAFT.value || detail.status === WORK_DAILY_REPORT_STATUS_ENUM.AUDIT_FAIL.value);
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
      reportItemList.value = detailRes.data.itemList || [];
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
    if (typeList.value.length > 0) {
      selectedTypeId.value = typeList.value[0].workItemTypeId;
    }
  }

  function selectType(item: WorkitemRecord) {
    selectedTypeId.value = item.workItemTypeId;
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

  function isAdded(workItemId: number | string) {
    return reportItemList.value.some((e) => e.workItemId === workItemId);
  }

  function addReportItem(item: WorkitemRecord) {
    reportItemList.value.push({
      workItemId: item.workItemId,
      workItemTypeId: item.workItemTypeId,
      workItemTypeName: item.workItemTypeName,
      workItemName: item.workItemName,
      description: item.description,
      scoreStandard: item.scoreStandard,
      standardScore: item.standardScore,
      finishRemark: '',
      fileList: [],
    });
  }

  function removeReportItem(item: WorkitemRecord) {
    reportItemList.value = reportItemList.value.filter((e) => e.workItemId !== item.workItemId);
  }

  function scrollToItem(workItemId: number | string) {
    document.getElementById(`daily-item-${workItemId}`)?.scrollIntoView({ behavior: 'smooth', block: 'center' });
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

  // ---------------------------- 保存和提交 ----------------------------
  async function saveDraft() {
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
    border: 1px solid #f0f0f0;
    padding: 10px;
    overflow: auto;
  }

  .column-title {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;
    font-weight: 600;
  }

  .choice-list {
    margin-top: 8px;
  }

  :deep(.ant-list-item) {
    cursor: pointer;
    padding: 8px;
  }

  :deep(.ant-list-item.active) {
    background: #e6f4ff;
  }

  .item-group + .item-group {
    margin-top: 12px;
  }

  .group-title {
    margin-bottom: 8px;
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

  .upload-block {
    margin-top: 8px;
  }

  .fail-reason {
    color: #cf1322;
  }
</style>
