<template>
  <view class="container">
    <uni-card title="待办工作" :isFull="true" padding="0px" spacing="0px">
      <template #extra>
        <view class="add-entry" @click="openAddPopup">
          <uni-icons type="plus" size="18" color="#1a9aff"></uni-icons>
          <text>添加</text>
        </view>
      </template>

      <view class="todo-panel">
        <view class="empty" v-if="toBeDoneList.length === 0">暂无待办工作</view>

        <view class="todo-section" v-if="toDoList.length">
          <view class="section-title">未完成</view>
          <view class="todo-item" v-for="item in toDoList" :key="item.toBeDoneId">
            <view class="check-icon" @click="toggleDone(item)">
              <uni-icons type="circle" size="22" color="#c0c4cc"></uni-icons>
            </view>
            <view class="todo-title">{{ item.title }}</view>
            <view class="icon-btn" @click="toggleStar(item)">
              <uni-icons :type="item.starFlag ? 'star-filled' : 'star'" size="22" :color="item.starFlag ? '#ff8c00' : '#c0c4cc'"></uni-icons>
            </view>
            <view class="icon-btn" @click="confirmDelete(item)">
              <uni-icons type="trash" size="22" color="#d93026"></uni-icons>
            </view>
          </view>
        </view>

        <view class="todo-section" v-if="doneList.length">
          <view class="section-title">已完成</view>
          <view class="todo-item done" v-for="item in doneList" :key="item.toBeDoneId">
            <view class="check-icon" @click="toggleDone(item)">
              <uni-icons type="checkbox-filled" size="22" color="#1a9aff"></uni-icons>
            </view>
            <view class="todo-title">{{ item.title }}</view>
            <view class="icon-btn" @click="confirmDelete(item)">
              <uni-icons type="trash" size="22" color="#d93026"></uni-icons>
            </view>
          </view>
        </view>
      </view>
    </uni-card>

    <uni-popup ref="addPopupRef" type="center">
      <view class="add-popup">
        <view class="popup-title">新建待办</view>
        <uni-easyinput
          class="popup-input"
          :clearable="true"
          trim="all"
          v-model="addTitle"
          placeholder="请输入待办标题"
          @confirm="addToBeDone"
        />
        <view class="popup-actions">
          <view class="popup-btn light" @click="closeAddPopup">取消</view>
          <view class="popup-btn primary" @click="addToBeDone">确定</view>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup>
  import { computed, ref } from 'vue';
  import { onShow } from '@dcloudio/uni-app';
  import { toBeDoneApi } from '@/api/system/to-be-done-api';
  import { SmartLoading, SmartToast } from '@/lib/smart-support';
  import { smartSentry } from '@/lib/smart-sentry';

  const toBeDoneList = ref([]);
  const addPopupRef = ref();
  const addTitle = ref('');

  const toDoList = computed(() => toBeDoneList.value.filter((item) => !item.doneFlag));
  const doneList = computed(() => toBeDoneList.value.filter((item) => item.doneFlag));

  async function queryToBeDoneList() {
    try {
      const res = await toBeDoneApi.queryList();
      toBeDoneList.value = res.data || [];
    } catch (err) {
      smartSentry.captureError(err);
    }
  }

  function openAddPopup() {
    addTitle.value = '';
    addPopupRef.value.open();
  }

  function closeAddPopup() {
    addPopupRef.value.close();
  }

  async function addToBeDone() {
    const title = (addTitle.value || '').trim();
    if (!title) {
      SmartToast.toast('请输入待办标题');
      return;
    }
    try {
      SmartLoading.show();
      await toBeDoneApi.add({ title });
      closeAddPopup();
      SmartToast.success('添加成功');
      await queryToBeDoneList();
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      SmartLoading.hide();
    }
  }

  async function updateToBeDone(item, param) {
    try {
      await toBeDoneApi.update({
        toBeDoneId: item.toBeDoneId,
        ...param,
      });
      await queryToBeDoneList();
    } catch (err) {
      smartSentry.captureError(err);
    }
  }

  function toggleDone(item) {
    updateToBeDone(item, { doneFlag: !item.doneFlag });
  }

  function toggleStar(item) {
    updateToBeDone(item, { starFlag: !item.starFlag });
  }

  function confirmDelete(item) {
    uni.showModal({
      title: '提示',
      content: '确定删除这个待办吗？',
      confirmText: '删除',
      confirmColor: '#d93026',
      success: async (res) => {
        if (!res.confirm) {
          return;
        }
        await deleteToBeDone(item);
      },
    });
  }

  async function deleteToBeDone(item) {
    try {
      await toBeDoneApi.deleteToBeDone(item.toBeDoneId);
      await queryToBeDoneList();
    } catch (err) {
      smartSentry.captureError(err);
    }
  }

  onShow(() => {
    queryToBeDoneList();
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
      background: linear-gradient(180deg, #fff7e8, #fffdf8);
    }
    :deep(.uni-card__header-extra) {
      font-size: 30rpx;
      font-weight: 400;
      color: #1a9aff;
    }
  }

  .add-entry {
    display: flex;
    align-items: center;
    gap: 4rpx;
  }

  .todo-panel {
    padding: 24rpx;
    background: #fff;
  }

  .todo-section + .todo-section {
    margin-top: 20rpx;
  }

  .section-title {
    margin-bottom: 12rpx;
    color: #777;
    font-size: 24rpx;
  }

  .todo-item {
    display: flex;
    align-items: center;
    min-height: 76rpx;
    padding: 0 18rpx;
    border-radius: 10rpx;
    background: #f7f9fc;
    margin-bottom: 12rpx;
  }

  .todo-item.done {
    color: #999;

    .todo-title {
      color: #999;
      text-decoration: line-through;
    }
  }

  .check-icon,
  .icon-btn {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 52rpx;
    height: 52rpx;
  }

  .todo-title {
    flex: 1;
    min-width: 0;
    margin: 0 12rpx;
    color: #323333;
    font-size: 28rpx;
    line-height: 1.4;
    word-break: break-all;
  }

  .empty {
    padding: 54rpx 0;
    color: #999;
    font-size: 28rpx;
    text-align: center;
  }

  .add-popup {
    width: 620rpx;
    padding: 32rpx;
    border-radius: 12rpx;
    background: #fff;
    box-sizing: border-box;
  }

  .popup-title {
    color: #323333;
    font-size: 34rpx;
    font-weight: 700;
  }

  .popup-input {
    margin-top: 24rpx;
  }

  .popup-actions {
    display: flex;
    gap: 16rpx;
    margin-top: 28rpx;
  }

  .popup-btn {
    flex: 1;
    height: 72rpx;
    border-radius: 8rpx;
    font-size: 28rpx;
    line-height: 72rpx;
    text-align: center;
  }

  .popup-btn.light {
    background: #eef7ff;
    color: #1a9aff;
  }

  .popup-btn.primary {
    background: #1a9aff;
    color: #fff;
  }
</style>
