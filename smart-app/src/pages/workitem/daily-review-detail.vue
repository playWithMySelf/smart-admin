<template>
  <view class="page">
    <view class="base-card">
      <view class="base-row">
        <view>员工：{{ detail.employeeName || '-' }}</view>
        <view>{{ getWorkDailyReportStatusDesc(detail.status) }}</view>
      </view>
      <view class="base-row">
        <view>部门：{{ detail.departmentName || '-' }}</view>
      </view>
      <view class="base-row">
        <view>日期：{{ detail.reportDate || '-' }}</view>
        <view>总分：{{ detail.totalScore || 0 }}</view>
      </view>
    </view>

    <view class="item-card" v-for="item in detail.itemList || []" :key="item.workDailyReportItemId">
      <view class="item-head">
        <view>
          <view class="item-name">{{ item.workItemName }}</view>
          <view class="item-type">{{ item.workItemTypeName || '未分类' }}</view>
        </view>
        <view class="score-tag">标准 {{ item.standardScore }}</view>
      </view>
      <view class="remark">完成说明：{{ item.finishRemark || '-' }}</view>
      <view class="file-row" v-if="item.fileList && item.fileList.length">
        <view class="file-thumb" v-for="(file, index) in item.fileList" :key="file.fileKey || index" @click="previewFiles(item.fileList, index)">
          <image :src="file.fileUrl" mode="aspectFill"></image>
        </view>
      </view>
      <view class="audit-form" v-if="auditMode">
        <view class="form-label">最终分</view>
        <input class="score-input" type="digit" v-model="item.finalScore" placeholder="请输入最终分" />
        <view class="form-label">扣分原因</view>
        <textarea class="reason-input" v-model="item.deductReason" placeholder="选填" />
      </view>
      <view class="audit-result" v-else>
        <view>最终分：{{ item.finalScore || item.standardScore || 0 }}</view>
        <view v-if="item.deductReason">扣分原因：{{ item.deductReason }}</view>
      </view>
    </view>

    <view class="history-card">
      <view class="history-title">审核历史</view>
      <view class="history-item" v-for="item in detail.auditList || []" :key="item.workDailyReportAuditId">
        <view>{{ item.auditTime || '-' }} {{ item.auditEmployeeName || '-' }}</view>
        <view>{{ getWorkDailyReportStatusDesc(item.auditResult) }}<text v-if="item.failReason">：{{ item.failReason }}</text></view>
      </view>
      <view class="empty-history" v-if="!detail.auditList || detail.auditList.length === 0">暂无审核历史</view>
    </view>

    <view class="bottom-bar" v-if="auditMode">
      <button class="bottom-btn" type="warn" @click="openFailPopup">审核失败</button>
      <button class="bottom-btn" type="primary" @click="confirmAudit(true)">审核通过</button>
    </view>

    <uni-popup ref="failPopupRef" type="bottom">
      <view class="fail-popup">
        <view class="fail-title">审核失败原因</view>
        <textarea class="fail-input" v-model="failReason" placeholder="请输入失败原因" />
        <button type="primary" @click="confirmAudit(false)">确认提交</button>
      </view>
    </uni-popup>
  </view>
</template>

<script setup>
  import { computed, reactive, ref } from 'vue';
  import { onLoad } from '@dcloudio/uni-app';
  import { workitemApi } from '@/api/business/workitem/workitem-api';
  import { WORK_DAILY_REPORT_STATUS_ENUM, getWorkDailyReportStatusDesc } from '@/constants/business/workitem/workitem-const';
  import { SmartLoading, SmartToast } from '@/lib/smart-support';
  import { smartSentry } from '@/lib/smart-sentry';

  const detail = reactive({});
  const failPopupRef = ref();
  const failReason = ref('');
  const auditMode = computed(() => detail.status === WORK_DAILY_REPORT_STATUS_ENUM.WAIT_AUDIT.value);

  function clearDetail() {
    Object.keys(detail).forEach((key) => delete detail[key]);
  }

  async function loadDetail(workDailyReportId) {
    try {
      SmartLoading.show();
      const res = await workitemApi.getReviewDetail(workDailyReportId);
      clearDetail();
      Object.assign(detail, res.data || {});
      (detail.itemList || []).forEach((item) => {
        item.finalScore = item.finalScore || item.standardScore || 0;
      });
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      SmartLoading.hide();
    }
  }

  function previewFiles(fileList, currentIndex) {
    const urls = fileList.map((file) => file.fileUrl).filter(Boolean);
    if (urls.length === 0) {
      return;
    }
    uni.previewImage({
      urls,
      current: urls[currentIndex],
    });
  }

  function buildAuditParam(passFlag) {
    return {
      workDailyReportId: detail.workDailyReportId,
      passFlag,
      failReason: passFlag ? '' : failReason.value,
      itemList: (detail.itemList || []).map((item) => ({
        workDailyReportItemId: item.workDailyReportItemId,
        finalScore: item.finalScore,
        deductReason: item.deductReason,
      })),
    };
  }

  function openFailPopup() {
    failReason.value = '';
    failPopupRef.value.open();
  }

  function confirmAudit(passFlag) {
    if (!passFlag && !failReason.value) {
      SmartToast.toast('请输入失败原因');
      return;
    }
    uni.showModal({
      title: '提示',
      content: passFlag ? '确定审核通过吗？' : '确定审核失败吗？',
      success: async (res) => {
        if (res.confirm) {
          await doAudit(passFlag);
        }
      },
    });
  }

  async function doAudit(passFlag) {
    try {
      SmartLoading.show();
      await workitemApi.auditDaily(buildAuditParam(passFlag));
      SmartToast.success('审核成功');
      setTimeout(() => {
        uni.navigateBack();
      }, 500);
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      SmartLoading.hide();
    }
  }

  onLoad((options) => {
    if (options.workDailyReportId) {
      loadDetail(options.workDailyReportId);
    }
  });
</script>

<style lang="scss" scoped>
  .page {
    min-height: 100vh;
    padding: 20rpx 20rpx 140rpx;
    background: #f5f5f5;
  }

  .base-card,
  .item-card,
  .history-card,
  .fail-popup {
    border-radius: 12rpx;
    background: #fff;
  }

  .base-card {
    padding: 26rpx;
  }

  .base-row {
    display: flex;
    justify-content: space-between;
    gap: 18rpx;
    margin-bottom: 12rpx;
    color: #323333;
    font-size: 28rpx;
  }

  .item-card,
  .history-card {
    margin-top: 20rpx;
    padding: 26rpx;
  }

  .item-head {
    display: flex;
    justify-content: space-between;
    gap: 18rpx;
  }

  .item-name {
    color: #323333;
    font-size: 32rpx;
    font-weight: 700;
  }

  .item-type,
  .remark,
  .audit-result,
  .history-item,
  .empty-history {
    margin-top: 10rpx;
    color: #777;
    font-size: 26rpx;
    line-height: 1.55;
  }

  .score-tag {
    flex-shrink: 0;
    height: 48rpx;
    padding: 0 18rpx;
    border-radius: 24rpx;
    background: #e8f4ff;
    color: #1a9aff;
    font-size: 24rpx;
    line-height: 48rpx;
  }

  .file-row {
    display: flex;
    flex-wrap: wrap;
    gap: 14rpx;
    margin-top: 18rpx;
  }

  .file-thumb {
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

  .audit-form {
    margin-top: 18rpx;
  }

  .form-label {
    margin: 18rpx 0 8rpx;
    color: #555;
    font-size: 26rpx;
  }

  .score-input,
  .reason-input,
  .fail-input {
    width: 100%;
    padding: 18rpx;
    border-radius: 8rpx;
    background: #f7f8f9;
    color: #323333;
    font-size: 28rpx;
  }

  .reason-input {
    min-height: 120rpx;
  }

  .history-title,
  .fail-title {
    color: #323333;
    font-size: 32rpx;
    font-weight: 700;
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

  .fail-popup {
    padding: 28rpx;
  }

  .fail-input {
    min-height: 180rpx;
    margin: 20rpx 0;
  }
</style>
