<template>
  <view class="page">
    <view class="filter-card">
      <picker mode="date" :value="queryForm.startDate" @change="changeStartDate">
        <view class="date-box">
          <view class="date-label">开始日期</view>
          <view class="date-value">{{ queryForm.startDate }}</view>
        </view>
      </picker>
      <picker mode="date" :value="queryForm.endDate" @change="changeEndDate">
        <view class="date-box">
          <view class="date-label">结束日期</view>
          <view class="date-value">{{ queryForm.endDate }}</view>
        </view>
      </picker>
      <view class="query-btn" @click="queryEmployeeScore">查询</view>
    </view>

    <view class="summary-card">
      <view>
        <view class="summary-label">{{ currentScore.employeeName || '我的积分' }}</view>
        <view class="summary-score">{{ currentScore.totalScore || 0 }}</view>
        <view class="summary-desc">{{ currentScore.reportCount || 0 }} 天日报 · {{ currentScore.itemCount || 0 }} 项明细</view>
      </view>
      <view class="summary-dept">{{ currentScore.departmentName || '-' }}</view>
    </view>

    <view class="section-title">日期明细</view>
    <view class="date-list">
      <view class="date-item" v-for="item in dateData" :key="item.reportDate" @click="goDetail(item)">
        <view class="date-item-main">
          <view class="date-text">{{ item.reportDate }}</view>
          <view class="date-sub">{{ item.totalScore || 0 }}分 · {{ item.itemCount || 0 }}项</view>
        </view>
        <uni-icons type="right" size="16" color="#c0c4cc"></uni-icons>
      </view>
      <view class="empty" v-if="dateData.length === 0">暂无积分明细</view>
    </view>
  </view>
</template>

<script setup>
  import dayjs from 'dayjs';
  import { reactive, ref } from 'vue';
  import { onShow } from '@dcloudio/uni-app';
  import { workitemApi } from '@/api/business/workitem/workitem-api';
  import { useUserStore } from '@/store/modules/system/user';
  import { SmartLoading } from '@/lib/smart-support';
  import { smartSentry } from '@/lib/smart-sentry';

  const userStore = useUserStore();
  const queryForm = reactive({
    startDate: dayjs().startOf('month').format('YYYY-MM-DD'),
    endDate: dayjs().format('YYYY-MM-DD'),
    employeeId: undefined,
  });
  const currentScore = ref({});
  const dateData = ref([]);

  function buildQueryParam() {
    return {
      ...queryForm,
      employeeId: currentScore.value.employeeId || userStore.employeeId || undefined,
    };
  }

  async function queryEmployeeScore() {
    try {
      SmartLoading.show();
      const res = await workitemApi.queryEmployeeScore({
        ...queryForm,
        employeeId: userStore.employeeId || undefined,
      });
      currentScore.value = (res.data || []).find((item) => item.employeeId === userStore.employeeId) || res.data?.[0] || {};
      await queryDateScore();
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      SmartLoading.hide();
    }
  }

  async function queryDateScore() {
    if (!currentScore.value.employeeId && !userStore.employeeId) {
      dateData.value = [];
      return;
    }
    const res = await workitemApi.queryDateScore(buildQueryParam());
    dateData.value = res.data || [];
  }

  function goDetail(item) {
    uni.navigateTo({
      url: `/pages/workitem/score-report-detail?reportDate=${item.reportDate}&totalScore=${item.totalScore || 0}&itemCount=${item.itemCount || 0}`,
    });
  }

  function changeStartDate(event) {
    queryForm.startDate = event.detail.value;
    queryEmployeeScore();
  }

  function changeEndDate(event) {
    queryForm.endDate = event.detail.value;
    queryEmployeeScore();
  }

  onShow(() => {
    queryEmployeeScore();
  });
</script>

<style lang="scss" scoped>
  .page {
    min-height: 100vh;
    padding: 20rpx;
    background: #f5f5f5;
  }

  .filter-card,
  .summary-card,
  .date-item {
    border-radius: 12rpx;
    background: #fff;
  }

  .filter-card {
    display: grid;
    grid-template-columns: 1fr 1fr 120rpx;
    gap: 14rpx;
    padding: 20rpx;
  }

  .date-box {
    padding: 14rpx;
    border-radius: 8rpx;
    background: #f7f8f9;
  }

  .date-label {
    color: #888;
    font-size: 22rpx;
  }

  .date-value {
    margin-top: 6rpx;
    color: #323333;
    font-size: 26rpx;
  }

  .query-btn {
    height: 92rpx;
    border-radius: 8rpx;
    background: #1a9aff;
    color: #fff;
    font-size: 28rpx;
    line-height: 92rpx;
    text-align: center;
  }

  .summary-card {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 20rpx;
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
    font-size: 64rpx;
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

  .date-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16rpx;
    padding: 24rpx;
  }

  .date-item-main {
    min-width: 0;
  }

  .date-text {
    color: #323333;
    font-size: 28rpx;
    font-weight: 700;
  }

  .date-sub {
    margin-top: 8rpx;
    color: #777;
    font-size: 24rpx;
  }

  .empty {
    padding: 80rpx 0;
    color: #999;
    font-size: 28rpx;
    text-align: center;
  }
</style>
