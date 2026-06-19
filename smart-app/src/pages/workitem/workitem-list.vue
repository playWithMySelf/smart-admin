<template>
  <view class="page">
    <view class="filter-sticky">
      <view class="search-row">
        <view class="input">
          <uni-easyinput
            prefixIcon="search"
            :clearable="true"
            trim="all"
            v-model="keywords"
            placeholder="搜索：工作项名称、说明"
            @confirm="search"
            @clear="search"
          />
        </view>
        <view class="search-btn" @click="search">
          <uni-icons type="search" size="22"></uni-icons>
          <view class="search-btn-name">搜索</view>
        </view>
      </view>
      <scroll-view class="type-scroll" scroll-x>
        <view :class="['type-chip', { active: !selectedTypeId }]" @click="selectType()">全部</view>
        <view
          v-for="item in typeList"
          :key="item.workItemTypeId"
          :class="['type-chip', { active: selectedTypeId === item.workItemTypeId }]"
          @click="selectType(item)"
        >
          {{ item.typeName }}
        </view>
      </scroll-view>
    </view>

    <view class="list-container">
      <view class="workitem-card" v-for="item in itemList" :key="item.workItemId">
        <view class="card-head">
          <view class="item-name">{{ item.workItemName }}</view>
          <view class="score-tag">{{ item.standardScore }}分</view>
        </view>
        <view class="type-name">{{ item.workItemTypeName || '未分类' }}</view>
        <view class="desc" v-if="item.description">{{ item.description }}</view>
        <view class="standard" v-if="item.scoreStandard">评分标准：{{ item.scoreStandard }}</view>
      </view>
      <view class="empty" v-if="itemList.length === 0">暂无工作项</view>
    </view>
  </view>
</template>

<script setup>
  import { ref } from 'vue';
  import { onShow } from '@dcloudio/uni-app';
  import { workitemApi } from '@/api/business/workitem/workitem-api';
  import { smartSentry } from '@/lib/smart-sentry';
  import { SmartLoading } from '@/lib/smart-support';

  const keywords = ref('');
  const selectedTypeId = ref();
  const typeList = ref([]);
  const itemList = ref([]);

  async function queryTypeList() {
    const res = await workitemApi.queryTypeList(false);
    typeList.value = res.data || [];
  }

  async function queryItemList() {
    try {
      SmartLoading.show();
      const res = await workitemApi.queryItemList(selectedTypeId.value, keywords.value);
      itemList.value = res.data || [];
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      SmartLoading.hide();
    }
  }

  function selectType(item) {
    selectedTypeId.value = item?.workItemTypeId;
    queryItemList();
  }

  function search() {
    queryItemList();
    uni.pageScrollTo({ scrollTop: 0 });
  }

  onShow(async () => {
    if (typeList.value.length === 0) {
      await queryTypeList();
    }
    await queryItemList();
  });
</script>

<style lang="scss" scoped>
  .page {
    min-height: 100vh;
    background-color: #f5f5f5;
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

  .type-scroll {
    width: 100%;
    white-space: nowrap;
    padding: 18rpx 20rpx;
    box-sizing: border-box;
  }

  .type-chip {
    display: inline-block;
    margin-right: 16rpx;
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

  .list-container {
    padding: 20rpx;
  }

  .workitem-card {
    margin-bottom: 20rpx;
    padding: 28rpx;
    border-radius: 12rpx;
    background: #fff;
  }

  .card-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .item-name {
    flex: 1;
    min-width: 0;
    color: #323333;
    font-size: 32rpx;
    font-weight: 700;
  }

  .score-tag {
    margin-left: 16rpx;
    padding: 6rpx 18rpx;
    border-radius: 24rpx;
    background: #e8f4ff;
    color: #1a9aff;
    font-size: 24rpx;
  }

  .type-name,
  .desc,
  .standard,
  .empty {
    margin-top: 14rpx;
    color: #777;
    font-size: 26rpx;
    line-height: 1.55;
  }

  .empty {
    padding: 80rpx 0;
    text-align: center;
  }
</style>
