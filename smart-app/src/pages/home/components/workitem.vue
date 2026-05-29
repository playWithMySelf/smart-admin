<template>
  <view class="container">
    <uni-card title="工作项工作台" :isFull="true" padding="0px" spacing="0px">
      <template #extra>
        <view @click="goWorkitemIndex">进入工作项</view>
      </template>
      <view class="workitem-panel">
        <view class="score-card" @click="goScoreReport">
          <view class="score-label">本月积分</view>
          <view class="score-value">{{ overview.totalScore }}</view>
          <view class="score-desc">{{ overview.reportCount }} 天日报 · {{ overview.itemCount }} 项明细</view>
        </view>

        <view class="metric-row">
          <view class="metric-item" @click="goMyDaily">
            <view class="metric-value">{{ todayStatusText }}</view>
            <view class="metric-label">今日日报</view>
          </view>
          <view class="metric-item" @click="goReview">
            <view class="metric-value">{{ reviewCount }}</view>
            <view class="metric-label">待审核</view>
          </view>
          <view class="metric-item" @click="goItemList">
            <view class="metric-value">{{ typeCount }}</view>
            <view class="metric-label">工作项类型</view>
          </view>
        </view>

        <view class="action-row">
          <view class="action-btn primary" @click="goMyDaily">填写日报</view>
          <view class="action-btn" @click="goReview">日报审核</view>
          <view class="action-btn" @click="goItemList">工作项库</view>
        </view>
      </view>
    </uni-card>
  </view>
</template>

<script setup>
  import dayjs from 'dayjs';
  import { computed, ref } from 'vue';
  import { onShow } from '@dcloudio/uni-app';
  import { workitemApi } from '@/api/business/workitem/workitem-api';
  import { useUserStore } from '@/store/modules/system/user';
  import { smartSentry } from '@/lib/smart-sentry';
  import { WORK_DAILY_REPORT_STATUS_ENUM, getWorkDailyReportStatusDesc } from '@/constants/business/workitem/workitem-const';

  const todayReport = ref(null);
  const reviewCount = ref(0);
  const typeCount = ref(0);
  const overview = ref({
    totalScore: 0,
    reportCount: 0,
    itemCount: 0,
  });
  const userStore = useUserStore();

  const todayStatusText = computed(() => getWorkDailyReportStatusDesc(todayReport.value?.status));

  function currentMonthRange() {
    return {
      startDate: dayjs().startOf('month').format('YYYY-MM-DD'),
      endDate: dayjs().format('YYYY-MM-DD'),
    };
  }

  async function queryTodayReport() {
    const today = dayjs().format('YYYY-MM-DD');
    const res = await workitemApi.queryMyDailyPage({
      pageNum: 1,
      pageSize: 1,
      startDate: today,
      endDate: today,
      searchCount: false,
    });
    todayReport.value = res.data?.list?.[0] || null;
  }

  async function queryReviewCount() {
    const res = await workitemApi.queryReviewPage({
      pageNum: 1,
      pageSize: 1,
      status: WORK_DAILY_REPORT_STATUS_ENUM.WAIT_AUDIT.value,
      searchCount: true,
    });
    reviewCount.value = res.data?.total || 0;
  }

  async function queryScoreOverview() {
    const res = await workitemApi.queryEmployeeScore({
      ...currentMonthRange(),
      employeeId: userStore.employeeId || undefined,
    });
    const currentUserScore = (res.data || []).find((item) => item.employeeId === userStore.employeeId) || res.data?.[0];
    overview.value = {
      totalScore: currentUserScore?.totalScore || 0,
      reportCount: currentUserScore?.reportCount || 0,
      itemCount: currentUserScore?.itemCount || 0,
    };
  }

  async function queryTypeCount() {
    const res = await workitemApi.queryTypeList(false);
    typeCount.value = (res.data || []).length;
  }

  async function queryWorkitemOverview() {
    const resultList = await Promise.allSettled([queryTodayReport(), queryReviewCount(), queryScoreOverview(), queryTypeCount()]);
    resultList.forEach((result) => {
      if (result.status === 'rejected') {
        smartSentry.captureError(result.reason);
      }
    });
  }

  function goWorkitemIndex() {
    uni.navigateTo({ url: '/pages/workitem/workitem-index' });
  }

  function goMyDaily() {
    uni.navigateTo({ url: '/pages/workitem/my-daily-report' });
  }

  function goReview() {
    uni.navigateTo({ url: '/pages/workitem/daily-review' });
  }

  function goScoreReport() {
    uni.navigateTo({ url: '/pages/workitem/score-report' });
  }

  function goItemList() {
    uni.navigateTo({ url: '/pages/workitem/workitem-list' });
  }

  onShow(() => {
    queryWorkitemOverview();
  });
</script>

<style lang="scss" scoped>
  .container {
    width: 700rpx;
    margin: 0 auto 20rpx;
    border-radius: 12rpx;
    overflow: hidden;

    :deep(.uni-card__header-box) {
      font-weight: bold;
    }
    :deep(.uni-card__content) {
      padding: 0 !important;
    }
    :deep(.uni-card__header) {
      border: none;
      background: linear-gradient(180deg, #e8f4ff, #f8fcff);
    }
    :deep(.uni-card__header-extra) {
      font-size: 30rpx;
      font-weight: 400;
      text-align: center;
      color: #1a9aff;
    }
  }

  .workitem-panel {
    padding: 24rpx;
    background-color: #fff;
  }

  .score-card {
    padding: 28rpx;
    border-radius: 12rpx;
    background: linear-gradient(133deg, #075fe6 0%, #1d8fff 58%, #31c5ff 100%);
    color: #fff;
  }

  .score-label {
    opacity: 0.75;
    font-size: 26rpx;
  }

  .score-value {
    margin-top: 8rpx;
    font-size: 56rpx;
    font-weight: 700;
  }

  .score-desc {
    margin-top: 4rpx;
    opacity: 0.85;
    font-size: 24rpx;
  }

  .metric-row {
    display: flex;
    gap: 16rpx;
    margin-top: 20rpx;
  }

  .metric-item {
    flex: 1;
    min-width: 0;
    padding: 20rpx 10rpx;
    border-radius: 10rpx;
    background: #f7f9fc;
    text-align: center;
  }

  .metric-value {
    color: #1a9aff;
    font-size: 32rpx;
    font-weight: 700;
  }

  .metric-label {
    margin-top: 6rpx;
    color: #777;
    font-size: 24rpx;
  }

  .action-row {
    display: flex;
    gap: 16rpx;
    margin-top: 22rpx;
  }

  .action-btn {
    flex: 1;
    height: 64rpx;
    border-radius: 8rpx;
    background: #eef7ff;
    color: #1a9aff;
    font-size: 26rpx;
    line-height: 64rpx;
    text-align: center;
  }

  .action-btn.primary {
    background: #1a9aff;
    color: #fff;
  }
</style>
