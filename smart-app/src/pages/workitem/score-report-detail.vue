<template>
  <view class="page">
    <view class="summary-card">
      <view>
        <view class="summary-label">积分日期</view>
        <view class="summary-score">{{ reportDate || '-' }}</view>
        <view class="summary-desc">{{ totalScore }}分 · {{ itemCount }}项明细</view>
      </view>
      <view class="summary-dept">工作项积分</view>
    </view>

    <view class="section-title">工作项明细</view>
    <view class="score-item" v-for="(item, index) in itemData" :key="getScoreItemRowKey(item, index)">
      <view class="item-head">
        <view class="item-name">{{ item.workItemName }}</view>
        <view class="score-tag">{{ item.finalScore || 0 }}分</view>
      </view>
      <view class="item-meta">{{ item.workItemTypeName || '未分类' }} · 标准 {{ item.standardScore || 0 }}分</view>
      <view class="item-meta" v-if="item.deductReason">扣分原因：{{ item.deductReason }}</view>
    </view>
    <view class="empty" v-if="itemData.length === 0">暂无工作项明细</view>
  </view>
</template>

<script setup>
  import { ref } from 'vue';
  import { onLoad } from '@dcloudio/uni-app';
  import dayjs from 'dayjs';
  import { workitemApi } from '@/api/business/workitem/workitem-api';
  import { useUserStore } from '@/store/modules/system/user';
  import { SmartLoading } from '@/lib/smart-support';
  import { smartSentry } from '@/lib/smart-sentry';

  const userStore = useUserStore();
  const reportDate = ref('');
  const totalScore = ref(0);
  const itemCount = ref(0);
  const itemData = ref([]);

  function getScoreItemRowKey(item, index) {
    return item.workDailyReportItemId || `${item.workItemId}-${index}`;
  }

  async function queryItemScore() {
    try {
      SmartLoading.show();
      const res = await workitemApi.queryItemScore({
        startDate: reportDate.value,
        endDate: reportDate.value,
        reportDate: reportDate.value,
        employeeId: userStore.employeeId || undefined,
      });
      itemData.value = res.data || [];
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      SmartLoading.hide();
    }
  }

  onLoad((options) => {
    reportDate.value = options.reportDate || dayjs().format('YYYY-MM-DD');
    totalScore.value = Number(options.totalScore) || 0;
    itemCount.value = Number(options.itemCount) || 0;
    queryItemScore();
  });
</script>

<style lang="scss" scoped>
  .page {
    min-height: 100vh;
    padding: 20rpx;
    background: #f5f5f5;
  }

  .summary-card,
  .score-item {
    border-radius: 12rpx;
    background: #fff;
  }

  .summary-card {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 34rpx 30rpx;
    background: linear-gradient(133deg, #075fe6 0%, #1d8fff 58%, #31c5ff 100%);
    color: #fff;
  }

  .summary-label {
    opacity: 0.78;
    font-size: 26rpx;
  }

  .summary-score {
    margin-top: 8rpx;
    font-size: 52rpx;
    font-weight: 700;
  }

  .summary-desc,
  .summary-dept {
    margin-top: 6rpx;
    opacity: 0.86;
    font-size: 24rpx;
  }

  .section-title {
    margin: 28rpx 4rpx 16rpx;
    color: #323333;
    font-size: 32rpx;
    font-weight: 700;
  }

  .score-item {
    margin-bottom: 16rpx;
    padding: 24rpx;
  }

  .item-head {
    display: flex;
    justify-content: space-between;
    gap: 16rpx;
  }

  .item-name {
    color: #323333;
    font-size: 30rpx;
    font-weight: 700;
  }

  .score-tag {
    flex-shrink: 0;
    padding: 8rpx 18rpx;
    border-radius: 24rpx;
    background: #e8f4ff;
    color: #1a9aff;
    font-size: 24rpx;
  }

  .item-meta {
    margin-top: 12rpx;
    color: #777;
    font-size: 26rpx;
    line-height: 1.5;
  }

  .empty {
    padding: 80rpx 0;
    color: #999;
    font-size: 28rpx;
    text-align: center;
  }
</style>
