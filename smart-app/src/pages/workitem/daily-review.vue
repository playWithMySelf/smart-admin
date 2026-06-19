<template>
  <view class="page">
    <mescroll-body sticky @init="handleMescrollInit" :down="{ auto: false }" :up="{ auto: false }" @down="onDown" @up="onUp">
      <view class="filter-sticky">
        <view class="search-row">
          <view class="input">
            <uni-easyinput
              prefixIcon="search"
              :clearable="true"
              trim="all"
              v-model="queryForm.keywords"
              placeholder="搜索：员工姓名"
              @confirm="search"
              @clear="search"
            />
          </view>
          <view class="search-btn" @click="search">
            <uni-icons type="search" size="22" />
            <view class="search-btn-name">搜索</view>
          </view>
        </view>
        <scroll-view class="status-scroll" scroll-x>
          <view :class="['status-chip', { active: queryForm.status === WORK_DAILY_REPORT_STATUS_ENUM.WAIT_AUDIT.value }]" @click="changeStatus(WORK_DAILY_REPORT_STATUS_ENUM.WAIT_AUDIT.value)">待审核</view>
          <view :class="['status-chip', { active: queryForm.status === WORK_DAILY_REPORT_STATUS_ENUM.AUDIT_PASS.value }]" @click="changeStatus(WORK_DAILY_REPORT_STATUS_ENUM.AUDIT_PASS.value)">审核通过</view>
          <view :class="['status-chip', { active: queryForm.status === WORK_DAILY_REPORT_STATUS_ENUM.AUDIT_FAIL.value }]" @click="changeStatus(WORK_DAILY_REPORT_STATUS_ENUM.AUDIT_FAIL.value)">审核失败</view>
        </scroll-view>
      </view>

      <view class="list-container">
        <view class="review-card" v-for="item in listData" :key="item.workDailyReportId" @click="goDetail(item)">
          <view class="card-head">
            <view>
              <view class="employee-name">{{ item.employeeName }}</view>
              <view class="department-name">{{ item.departmentName || '-' }}</view>
            </view>
            <view :class="['status-tag', statusClass(item.status)]">{{ getWorkDailyReportStatusDesc(item.status) }}</view>
          </view>
          <view class="info-row">
            <view>日期：{{ item.reportDate }}</view>
            <view>总分：{{ item.totalScore || 0 }}</view>
          </view>
          <view class="info-row">
            <view>提交：{{ item.submitTime || '-' }}</view>
          </view>
        </view>
        <view class="empty" v-if="listData.length === 0">暂无日报</view>
      </view>
    </mescroll-body>
  </view>
</template>

<script setup>
  import { computed, reactive, ref } from 'vue';
  import { onPageScroll, onReachBottom, onShow } from '@dcloudio/uni-app';
  import useMescroll from '@/uni_modules/uni-mescroll/hooks/useMescroll';
  import { workitemApi } from '@/api/business/workitem/workitem-api';
  import { WORK_DAILY_REPORT_STATUS_ENUM, getWorkDailyReportStatusDesc } from '@/constants/business/workitem/workitem-const';
  import { smartSentry } from '@/lib/smart-sentry';
  import { useUserStore } from '@/store/modules/system/user';
  import { SmartToast } from '@/lib/smart-support';

  const defaultForm = {
    keywords: '',
    status: WORK_DAILY_REPORT_STATUS_ENUM.WAIT_AUDIT.value,
    pageNum: 1,
    pageSize: 10,
    sortItemList: [],
  };

  const queryForm = reactive({ ...defaultForm });
  const listData = ref([]);
  const userStore = useUserStore();
  const canReview = computed(() => userStore.hasPermission('workitem:daily:review'));
  let isRefreshPending = false;

  function buildQueryParam(pageNum) {
    queryForm.pageNum = pageNum;
    return Object.assign({}, queryForm, { pageNum });
  }

  async function query(mescroll, isDownFlag, param) {
    if (!canReview.value) {
      listData.value = [];
      mescroll.endSuccess(0, false);
      return;
    }
    try {
      const res = await workitemApi.queryReviewPage(param);
      if (!isDownFlag) {
        listData.value = listData.value.concat(res.data.list);
      } else {
        listData.value = res.data.list;
      }
      mescroll.endSuccess(res.data.list.length, res.data.pages > res.data.pageNum);
    } catch (err) {
      smartSentry.captureError(err);
      mescroll.endErr();
    }
  }

  const { mescrollInit, getMescroll } = useMescroll(onPageScroll, onReachBottom);

  function handleMescrollInit(mescroll) {
    mescrollInit(mescroll);
    if (isRefreshPending) {
      refreshReviewList();
    }
  }

  function search() {
    refreshReviewList();
    uni.pageScrollTo({ scrollTop: 0 });
  }

  function onDown(mescroll) {
    queryForm.pageNum = 1;
    mescroll.resetUpScroll(true);
  }

  function onUp(mescroll) {
    query(mescroll, mescroll.num === 1, buildQueryParam(mescroll.num));
  }

  function changeStatus(status) {
    queryForm.status = status;
    search();
  }

  function refreshReviewList() {
    const mescroll = getMescroll();
    if (!mescroll) {
      isRefreshPending = true;
      return;
    }
    isRefreshPending = false;
    mescroll.resetUpScroll(false);
    uni.pageScrollTo({ scrollTop: 0 });
  }

  function statusClass(status) {
    if (status === WORK_DAILY_REPORT_STATUS_ENUM.AUDIT_PASS.value) {
      return 'success';
    }
    if (status === WORK_DAILY_REPORT_STATUS_ENUM.AUDIT_FAIL.value) {
      return 'danger';
    }
    return 'primary';
  }

  function goDetail(item) {
    uni.navigateTo({ url: `/pages/workitem/daily-review-detail?workDailyReportId=${item.workDailyReportId}` });
  }

  onShow(() => {
    if (!canReview.value) {
      SmartToast.toast('暂无日报审核权限');
      return;
    }
    refreshReviewList();
  });
</script>

<style lang="scss" scoped>
  .page {
    min-height: 100vh;
    background: #f5f5f5;
  }

  .filter-sticky {
    position: sticky;
    top: 0;
    z-index: 8;
    background: #fff;
    box-shadow: 0 4rpx 12rpx rgba(15, 23, 42, 0.04);
  }

  .search-row {
    display: flex;
    align-items: center;
    gap: 16rpx;
    padding: 16rpx 20rpx 0;
  }

  .input {
    flex: 1;
    min-width: 0;
    height: 60rpx;
    border-radius: 4px;
    background: #f7f8f9;
    display: flex;
    align-items: center;
  }

  .search-btn {
    flex-shrink: 0;
    width: 112rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    height: 60rpx;
    border-radius: 30rpx;
    background: #eef7ff;
    color: #1a9aff;
    .search-btn-name {
      margin-left: 6rpx;
      font-size: 28rpx;
    }
  }

  .status-scroll {
    width: 100%;
    padding: 18rpx 20rpx;
    white-space: nowrap;
    box-sizing: border-box;
  }

  .status-chip {
    display: inline-block;
    margin-right: 16rpx;
    padding: 12rpx 26rpx;
    border-radius: 30rpx;
    background: #f0f3f8;
    color: #4b5563;
    font-size: 26rpx;
  }

  .status-chip.active {
    background: #1a9aff;
    color: #fff;
  }

  .list-container {
    padding: 20rpx;
  }

  .review-card {
    margin-bottom: 20rpx;
    padding: 28rpx;
    border-radius: 12rpx;
    background: #fff;
  }

  .card-head,
  .info-row {
    display: flex;
    justify-content: space-between;
    gap: 16rpx;
  }

  .employee-name {
    color: #323333;
    font-size: 34rpx;
    font-weight: 700;
  }

  .department-name,
  .info-row {
    color: #777;
    font-size: 26rpx;
  }

  .department-name {
    margin-top: 8rpx;
  }

  .info-row {
    margin-top: 18rpx;
  }

  .status-tag {
    height: 48rpx;
    padding: 0 18rpx;
    border-radius: 24rpx;
    background: #e8f4ff;
    color: #1a9aff;
    font-size: 24rpx;
    line-height: 48rpx;
  }

  .status-tag.success {
    background: #e8fff2;
    color: #0f9f5f;
  }

  .status-tag.danger {
    background: #fff0f0;
    color: #d93026;
  }

  .empty {
    padding: 90rpx 0;
    color: #999;
    font-size: 28rpx;
    text-align: center;
  }
</style>
