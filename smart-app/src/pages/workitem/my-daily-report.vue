<template>
  <view class="page">
    <view class="date-card">
      <view>
        <view class="date-label">日报日期</view>
        <picker mode="date" :value="reportDate" @change="handleDateChange">
          <view class="date-value">{{ reportDate }}</view>
        </picker>
      </view>
      <view :class="['status-tag', statusClass]">{{ statusText }}</view>
    </view>

    <view class="fail-reason" v-if="showLatestFailReason">失败原因：{{ detail.latestFailReason }}</view>
    <view class="limit-tip" v-if="reportDateOutOfRange">只允许填报最近{{ safeAllowReplenishDays }}天内的日报</view>

    <view class="toolbar">
      <view class="toolbar-btn" @click="showChoicePopup">添加工作项</view>
      <view class="toolbar-btn light" @click="loadReportByDate">刷新</view>
    </view>

    <view class="item-list">
      <view class="daily-item" v-for="item in reportItemList" :key="item.workItemId">
        <view class="item-head">
          <view>
            <view class="item-name">{{ item.workItemName }}</view>
            <view class="item-type">{{ item.workItemTypeName || '未分类' }}</view>
          </view>
          <view class="score-tag">{{ item.standardScore }}分</view>
        </view>
        <textarea class="remark-input" v-model="item.finishRemark" :disabled="!editable" placeholder="请输入完成说明" />
        <view class="file-row" v-if="item.fileList && item.fileList.length">
          <view class="file-thumb" v-for="(file, index) in item.fileList" :key="file.fileKey || index" @click="previewFiles(item.fileList, index)">
            <image :src="file.fileUrl || file.url" mode="aspectFill"></image>
            <view class="file-remove" v-if="editable" @click.stop="removeFile(item, index)">×</view>
          </view>
        </view>
        <view class="item-actions">
          <view class="small-btn" v-if="editable" @click="chooseImage(item)">上传佐证</view>
          <view class="small-btn danger" v-if="editable" @click="removeReportItem(item)">删除</view>
        </view>
      </view>
      <view class="empty" v-if="reportItemList.length === 0">暂无已填明细，先添加工作项吧</view>
    </view>

    <view class="bottom-bar">
      <button class="bottom-btn" type="default" :disabled="!editable" @click="saveDraft">保存草稿</button>
      <button class="bottom-btn" type="primary" :disabled="!editable" @click="confirmSubmitDaily">提交审核</button>
    </view>

    <uni-popup ref="choicePopupRef" type="bottom">
      <view class="choice-popup">
        <view class="popup-head">
          <view class="popup-title">选择工作项</view>
          <view class="popup-close" @click="closeChoicePopup">关闭</view>
        </view>
        <view class="search-line">
          <uni-easyinput
            prefixIcon="search"
            :clearable="true"
            trim="all"
            v-model="itemKeywords"
            placeholder="搜索工作项"
            @confirm="queryChoiceItems"
            @clear="queryChoiceItems"
          />
        </view>
        <scroll-view class="type-scroll" scroll-x>
          <view :class="['type-chip', { active: !selectedTypeId }]" @click="selectAllType">全部</view>
          <view
            v-for="item in typeList"
            :key="item.workItemTypeId"
            :class="['type-chip', { active: item.workItemTypeId === selectedTypeId }]"
            @click="selectType(item)"
          >
            {{ item.typeName }}
          </view>
        </scroll-view>
        <scroll-view class="choice-list" scroll-y>
          <view class="choice-item" v-for="item in choiceItemList" :key="item.workItemId">
            <view class="choice-main">
              <view class="choice-name">{{ item.workItemName }}</view>
              <view class="choice-desc">{{ item.description || item.scoreStandard || '暂无说明' }}</view>
            </view>
            <view class="choice-action">
              <view class="score-tag">{{ item.standardScore }}分</view>
              <view :class="['add-btn', { disabled: isAdded(item.workItemId) }]" @click="addReportItem(item)">
                {{ isAdded(item.workItemId) ? '已添加' : '添加' }}
              </view>
            </view>
          </view>
        </scroll-view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup>
  import dayjs from 'dayjs';
  import { computed, reactive, ref } from 'vue';
  import { onShow } from '@dcloudio/uni-app';
  import { fileApi } from '@/api/support/file-api';
  import { configApi } from '@/api/support/config-api';
  import { workitemApi } from '@/api/business/workitem/workitem-api';
  import { FILE_FOLDER_TYPE_ENUM } from '@/constants/support/file-const';
  import { WORK_DAILY_REPORT_STATUS_ENUM, WORK_ITEM_CONFIG_KEY, getWorkDailyReportStatusDesc } from '@/constants/business/workitem/workitem-const';
  import { SmartLoading, SmartToast } from '@/lib/smart-support';
  import { smartSentry } from '@/lib/smart-sentry';

  const reportDate = ref(dayjs().format('YYYY-MM-DD'));
  const detail = reactive({});
  const reportItemList = ref([]);
  const allowReplenishDays = ref(2);
  const safeAllowReplenishDays = computed(() => Math.max(Number(allowReplenishDays.value) || 2, 1));
  const reportDateOutOfRange = computed(() => !isReportDateAllowed(reportDate.value));
  const editable = computed(() => {
    const editableStatus = !detail.status || detail.status === WORK_DAILY_REPORT_STATUS_ENUM.DRAFT.value || detail.status === WORK_DAILY_REPORT_STATUS_ENUM.AUDIT_FAIL.value;
    return editableStatus && !reportDateOutOfRange.value;
  });
  const statusText = computed(() => getWorkDailyReportStatusDesc(detail.status));
  const showLatestFailReason = computed(() => detail.status === WORK_DAILY_REPORT_STATUS_ENUM.AUDIT_FAIL.value && detail.latestFailReason);
  const statusClass = computed(() => {
    if (detail.status === WORK_DAILY_REPORT_STATUS_ENUM.AUDIT_PASS.value) {
      return 'success';
    }
    if (detail.status === WORK_DAILY_REPORT_STATUS_ENUM.AUDIT_FAIL.value) {
      return 'danger';
    }
    if (detail.status === WORK_DAILY_REPORT_STATUS_ENUM.WAIT_AUDIT.value) {
      return 'primary';
    }
    return '';
  });

  async function queryAllowReplenishDays() {
    try {
      const res = await configApi.queryByKey(WORK_ITEM_CONFIG_KEY.ALLOW_REPLENISH_DAYS);
      const configValue = Number(res.data?.configValue);
      allowReplenishDays.value = Number.isFinite(configValue) && configValue > 0 ? configValue : 2;
    } catch (err) {
      smartSentry.captureError(err);
    }
  }

  function isReportDateAllowed(dateValue) {
    if (!dateValue) {
      return false;
    }
    const currentDate = dayjs(dateValue).startOf('day');
    const today = dayjs().startOf('day');
    const earliestDate = today.subtract(safeAllowReplenishDays.value - 1, 'day');
    return !currentDate.isBefore(earliestDate) && !currentDate.isAfter(today);
  }

  function clearDetail() {
    Object.keys(detail).forEach((key) => delete detail[key]);
    reportItemList.value = [];
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
      const report = res.data?.list?.[0];
      if (!report) {
        clearDetail();
        return;
      }
      const detailRes = await workitemApi.getMyDailyDetail(report.workDailyReportId);
      clearDetail();
      Object.assign(detail, detailRes.data || {});
      reportItemList.value = detailRes.data?.itemList || [];
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      SmartLoading.hide();
    }
  }

  async function handleDateChange(event) {
    reportDate.value = event.detail.value;
    await loadReportByDate();
  }

  const choicePopupRef = ref();
  const typeList = ref([]);
  const selectedTypeId = ref();
  const itemKeywords = ref('');
  const choiceItemList = ref([]);

  async function queryTypeList() {
    const res = await workitemApi.queryTypeList(false);
    typeList.value = res.data || [];
    selectedTypeId.value = undefined;
  }

  async function queryChoiceItems() {
    try {
      const res = await workitemApi.queryItemList(selectedTypeId.value, itemKeywords.value);
      choiceItemList.value = res.data || [];
    } catch (err) {
      smartSentry.captureError(err);
    }
  }

  async function showChoicePopup() {
    if (!editable.value) {
      SmartToast.toast('当前日报不可编辑');
      return;
    }
    if (typeList.value.length === 0) {
      await queryTypeList();
    }
    await queryChoiceItems();
    choicePopupRef.value.open();
  }

  function closeChoicePopup() {
    choicePopupRef.value.close();
  }

  function selectType(item) {
    selectedTypeId.value = item.workItemTypeId;
    queryChoiceItems();
  }

  function selectAllType() {
    selectedTypeId.value = undefined;
    queryChoiceItems();
  }

  function isAdded(workItemId) {
    return reportItemList.value.some((item) => item.workItemId === workItemId);
  }

  function addReportItem(item) {
    if (isAdded(item.workItemId)) {
      return;
    }
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

  function removeReportItem(item) {
    reportItemList.value = reportItemList.value.filter((reportItem) => reportItem.workItemId !== item.workItemId);
  }

  function chooseImage(item) {
    if (!item.fileList) {
      item.fileList = [];
    }
    uni.chooseImage({
      count: 9,
      success: async (chooseResult) => {
        try {
          SmartLoading.show('上传中');
          for (const filePath of chooseResult.tempFilePaths) {
            const res = await fileApi.upload(filePath, FILE_FOLDER_TYPE_ENUM.WORK_ITEM.value);
            item.fileList.push(res.data);
          }
        } catch (err) {
          smartSentry.captureError(err);
        } finally {
          SmartLoading.hide();
        }
      },
    });
  }

  function previewFiles(fileList, currentIndex) {
    const urls = fileList.map((file) => file.fileUrl || file.url).filter(Boolean);
    if (urls.length === 0) {
      return;
    }
    uni.previewImage({
      urls,
      current: urls[currentIndex],
    });
  }

  function removeFile(item, index) {
    item.fileList.splice(index, 1);
  }

  function buildSaveParam() {
    return {
      workDailyReportId: detail.workDailyReportId,
      reportDate: reportDate.value,
      itemList: reportItemList.value.map((item) => ({
        workItemId: item.workItemId,
        finishRemark: item.finishRemark,
        fileList: (item.fileList || []).map((file) => ({
          fileId: file.fileId,
          fileKey: file.fileKey,
          fileName: file.fileName || file.name,
        })),
      })),
    };
  }

  async function saveDraft(silent) {
    if (reportDateOutOfRange.value) {
      SmartToast.toast(`只允许填报最近${safeAllowReplenishDays.value}天内的日报`);
      return false;
    }
    if (reportItemList.value.length === 0) {
      SmartToast.toast('请至少添加一条工作项');
      return false;
    }
    try {
      SmartLoading.show();
      await workitemApi.saveDailyDraft(buildSaveParam());
      if (!silent) {
        SmartToast.success('保存成功');
      }
      await loadReportByDate();
      return true;
    } catch (err) {
      smartSentry.captureError(err);
      return false;
    } finally {
      SmartLoading.hide();
    }
  }

  function confirmSubmitDaily() {
    uni.showModal({
      title: '提示',
      content: '确定提交审核吗？提交后将不可编辑，审核失败后可再次修改。',
      success: async (res) => {
        if (!res.confirm) {
          return;
        }
        const saved = await saveDraft(true);
        if (!saved || !detail.workDailyReportId) {
          return;
        }
        await submitDaily();
      },
    });
  }

  async function submitDaily() {
    try {
      SmartLoading.show();
      await workitemApi.submitDaily(detail.workDailyReportId);
      SmartToast.success('提交成功');
      await loadReportByDate();
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      SmartLoading.hide();
    }
  }

  onShow(async () => {
    await queryAllowReplenishDays();
    await loadReportByDate();
  });
</script>

<style lang="scss" scoped>
  .page {
    min-height: 100vh;
    padding: 20rpx 20rpx 140rpx;
    background: #f5f5f5;
  }

  .date-card,
  .daily-item,
  .choice-popup {
    border-radius: 12rpx;
    background: #fff;
  }

  .date-card {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 28rpx;
  }

  .date-label {
    color: #777;
    font-size: 24rpx;
  }

  .date-value {
    margin-top: 8rpx;
    color: #323333;
    font-size: 36rpx;
    font-weight: 700;
  }

  .status-tag,
  .score-tag {
    padding: 8rpx 18rpx;
    border-radius: 26rpx;
    background: #f0f3f8;
    color: #64748b;
    font-size: 24rpx;
  }

  .status-tag.primary {
    background: #e8f4ff;
    color: #1a9aff;
  }

  .status-tag.success {
    background: #e8fff2;
    color: #0f9f5f;
  }

  .status-tag.danger {
    background: #fff0f0;
    color: #d93026;
  }

  .fail-reason,
  .limit-tip {
    margin-top: 16rpx;
    padding: 18rpx 24rpx;
    border-radius: 8rpx;
    background: #fff6e6;
    color: #b45309;
    font-size: 26rpx;
    line-height: 1.5;
  }

  .toolbar {
    display: flex;
    gap: 16rpx;
    margin-top: 20rpx;
  }

  .toolbar-btn {
    flex: 1;
    height: 72rpx;
    border-radius: 8rpx;
    background: #1a9aff;
    color: #fff;
    font-size: 28rpx;
    line-height: 72rpx;
    text-align: center;
  }

  .toolbar-btn.light {
    background: #eef7ff;
    color: #1a9aff;
  }

  .item-list {
    margin-top: 20rpx;
  }

  .daily-item {
    margin-bottom: 20rpx;
    padding: 26rpx;
  }

  .item-head {
    display: flex;
    justify-content: space-between;
    gap: 16rpx;
  }

  .item-name {
    color: #323333;
    font-size: 32rpx;
    font-weight: 700;
  }

  .item-type {
    margin-top: 6rpx;
    color: #777;
    font-size: 24rpx;
  }

  .score-tag {
    flex-shrink: 0;
    background: #e8f4ff;
    color: #1a9aff;
  }

  .remark-input {
    width: 100%;
    min-height: 150rpx;
    margin-top: 18rpx;
    padding: 18rpx;
    border-radius: 8rpx;
    background: #f7f8f9;
    color: #323333;
    font-size: 28rpx;
  }

  .file-row {
    display: flex;
    flex-wrap: wrap;
    gap: 14rpx;
    margin-top: 18rpx;
  }

  .file-thumb {
    position: relative;
    width: 132rpx;
    height: 132rpx;
    border-radius: 8rpx;
    overflow: hidden;
    background: #f0f3f8;
  }

  .file-thumb image {
    width: 100%;
    height: 100%;
  }

  .file-remove {
    position: absolute;
    top: 0;
    right: 0;
    width: 36rpx;
    height: 36rpx;
    background: rgba(0, 0, 0, 0.55);
    color: #fff;
    font-size: 28rpx;
    line-height: 34rpx;
    text-align: center;
  }

  .item-actions {
    display: flex;
    gap: 16rpx;
    margin-top: 18rpx;
  }

  .small-btn {
    height: 56rpx;
    padding: 0 22rpx;
    border-radius: 6rpx;
    background: #eef7ff;
    color: #1a9aff;
    font-size: 26rpx;
    line-height: 56rpx;
  }

  .small-btn.danger {
    background: #fff0f0;
    color: #d93026;
  }

  .empty {
    padding: 90rpx 0;
    color: #999;
    font-size: 28rpx;
    text-align: center;
  }

  .bottom-bar {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 0;
    display: flex;
    gap: 16rpx;
    padding: 18rpx 20rpx 28rpx;
    border-top: 1rpx solid #eee;
    background: #fff;
  }

  .bottom-btn {
    flex: 1;
  }

  .choice-popup {
    height: 78vh;
    padding: 24rpx;
  }

  .popup-head {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .popup-title {
    color: #323333;
    font-size: 34rpx;
    font-weight: 700;
  }

  .popup-close {
    color: #1a9aff;
    font-size: 28rpx;
  }

  .search-line {
    margin-top: 20rpx;
  }

  .type-scroll {
    width: 100%;
    margin-top: 18rpx;
    white-space: nowrap;
  }

  .type-chip {
    display: inline-block;
    margin-right: 14rpx;
    padding: 12rpx 24rpx;
    border-radius: 30rpx;
    background: #f0f3f8;
    color: #4b5563;
    font-size: 26rpx;
  }

  .type-chip.active {
    background: #1a9aff;
    color: #fff;
  }

  .choice-list {
    height: calc(78vh - 210rpx);
    margin-top: 20rpx;
  }

  .choice-item {
    display: flex;
    gap: 18rpx;
    padding: 22rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
  }

  .choice-main {
    flex: 1;
    min-width: 0;
  }

  .choice-name {
    color: #323333;
    font-size: 30rpx;
    font-weight: 700;
  }

  .choice-desc {
    margin-top: 8rpx;
    color: #777;
    font-size: 24rpx;
    line-height: 1.4;
  }

  .choice-action {
    width: 130rpx;
    flex-shrink: 0;
    text-align: right;
  }

  .add-btn {
    display: inline-block;
    margin-top: 12rpx;
    padding: 8rpx 18rpx;
    border-radius: 6rpx;
    background: #1a9aff;
    color: #fff;
    font-size: 24rpx;
  }

  .add-btn.disabled {
    background: #d7dce2;
  }
</style>
