<template>
  <view class="page">
    <uni-nav-bar title="首页" :border="false" fixed>
      <!-- <template #right>
        <view class="right">
          <view class="">
            <image src="@/static/images/index/ic_scan.png" mode=""></image>
          </view>
          <view class="">
            <image src="@/static/images/index/ic_search.png" mode=""></image>
          </view>
        </view>
      </template> -->
    </uni-nav-bar>

    <!-- 数据表 -->
    <Statistics v-if="!showBannerFlag" />

    <!-- Banner -->
    <Banner v-if="showBannerFlag" />

    <!-- 功能菜单 -->
    <!-- <Menu @changeHome="changeHome" /> -->

    <!-- 工作台 -->
    <Workitem />

    <!-- 通知公告 -->
    <Notice />

    <!-- 商品 -->
    <!-- <Goods /> -->
  </view>
</template>

<script setup>
  import Banner from './components/banner.vue';
  import Workitem from './components/workitem.vue';
  import Statistics from './components/statistics.vue';
  import Notice from './components/notice.vue';
  import { ref } from 'vue';
  import { onShow } from '@dcloudio/uni-app';
  import { useUserStore } from '@/store/modules/system/user';

  const showBannerFlag = ref(true);

  onShow(() => {
    if (!useUserStore().getToken) {
      uni.reLaunch({ url: '/pages/login/login' });
      return;
    }

    uni.pageScrollTo({
      scrollTop: 0,
      duration: 300,
    });
  });
</script>

<style lang="scss" scoped>
  .page {
    background-color: #f5f5f5;
  }

  :deep(.uni-navbar__header-btns) {
    width: 260rpx !important;
  }

  .right {
    position: relative;
    display: flex;
    z-index: 999;

    view {
      width: 56rpx;
      height: 56rpx;
      margin-left: 36rpx;

      image {
        width: 100%;
        height: 100%;
      }
    }

    .right-menu {
      width: 108rpx;
      height: 56rpx;
      margin-left: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 4rpx;
      border-radius: 28rpx;
      background: #eef7ff;

      text {
        color: #1a9aff;
        font-size: 22rpx;
        line-height: 56rpx;
      }
    }
  }
</style>
