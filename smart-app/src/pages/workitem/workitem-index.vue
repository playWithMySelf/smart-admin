<template>
  <view class="page">
    <view class="summary-card">
      <view>
        <view class="summary-label">本月工作积分</view>
        <view class="summary-score">{{ overview.totalScore }}</view>
        <view class="summary-desc">{{ overview.reportCount }} 天日报 · {{ overview.itemCount }} 项明细</view>
      </view>
      <view class="summary-action" @click="goScoreReport">积分明细</view>
    </view>

    <view class="menu-panel">
      <view class="menu-item primary" @click="goMyDaily">
        <view class="menu-title">我的日报</view>
        <view class="menu-desc">填写今日工作项，保存草稿或提交审核</view>
      </view>
      <view class="menu-item" @click="goReview">
        <view class="menu-title">日报审核</view>
        <view class="menu-desc">查看待审核日报，移动端快速评分</view>
      </view>
      <view class="menu-item" @click="goScoreReport">
        <view class="menu-title">积分报表</view>
        <view class="menu-desc">查看本月积分和每日明细</view>
      </view>
      <view class="menu-item" @click="goItemList">
        <view class="menu-title">工作项库</view>
        <view class="menu-desc">按类型搜索可用工作项和标准分</view>
      </view>
    </view>
  </view>
</template>

<script setup>
  import dayjs from 'dayjs';
  import { ref } from 'vue';
  import { onShow } from '@dcloudio/uni-app';
  import { workitemApi } from '@/api/business/workitem/workitem-api';
  import { useUserStore } from '@/store/modules/system/user';
  import { smartSentry } from '@/lib/smart-sentry';

  const userStore = useUserStore();
  const overview = ref({
    totalScore: 0,
    reportCount: 0,
    itemCount: 0,
  });

  async function queryScoreOverview() {
    try {
      const res = await workitemApi.queryEmployeeScore({
        startDate: dayjs().startOf('month').format('YYYY-MM-DD'),
        endDate: dayjs().format('YYYY-MM-DD'),
        employeeId: userStore.employeeId || undefined,
      });
      const currentUserScore = (res.data || []).find((item) => item.employeeId === userStore.employeeId) || res.data?.[0];
      overview.value = {
        totalScore: currentUserScore?.totalScore || 0,
        reportCount: currentUserScore?.reportCount || 0,
        itemCount: currentUserScore?.itemCount || 0,
      };
    } catch (err) {
      smartSentry.captureError(err);
    }
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
    queryScoreOverview();
  });
</script>

<style lang="scss" scoped>
  .page {
    min-height: 100vh;
    padding: 20rpx 24rpx;
    background: #f5f5f5;
  }

  .summary-card {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 34rpx 30rpx;
    border-radius: 12rpx;
    background: linear-gradient(133deg, #075fe6 0%, #1d8fff 58%, #31c5ff 100%);
    color: #fff;
  }

  .summary-label {
    opacity: 0.78;
    font-size: 26rpx;
  }

  .summary-score {
    margin-top: 10rpx;
    font-size: 64rpx;
    font-weight: 700;
  }

  .summary-desc {
    margin-top: 6rpx;
    opacity: 0.86;
    font-size: 24rpx;
  }

  .summary-action {
    width: 150rpx;
    height: 56rpx;
    border: 1px solid rgba(255, 255, 255, 0.55);
    border-radius: 28rpx;
    font-size: 24rpx;
    line-height: 56rpx;
    text-align: center;
  }

  .menu-panel {
    margin-top: 24rpx;
  }

  .menu-item {
    margin-bottom: 20rpx;
    padding: 30rpx;
    border-radius: 12rpx;
    background: #fff;
    box-shadow: 0 3rpx 8rpx rgba(24, 144, 255, 0.05);
  }

  .menu-item.primary {
    border-left: 8rpx solid #1a9aff;
  }

  .menu-title {
    color: #323333;
    font-size: 34rpx;
    font-weight: 700;
  }

  .menu-desc {
    margin-top: 12rpx;
    color: #777;
    font-size: 26rpx;
    line-height: 1.5;
  }
</style>
