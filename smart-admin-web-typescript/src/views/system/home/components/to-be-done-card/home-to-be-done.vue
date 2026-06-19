<!--
  * 待办工作
  * 
  * @Author: jw 
  * @Date:      2022-09-12 22:34:00
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012 
  *
-->
<template>
  <default-home-card extra="添加" icon="StarTwoTone" title="待办工作" @extraClick="showAddToBeDone">
    <div style="height: 280px">
      <div class="center column">
        <a-space direction="vertical" style="width: 100%">
          <a-empty v-if="$lodash.isEmpty(toBeDoneList)" description="暂无待办工作" />
          <div v-for="item in toDoList" :key="item.toBeDoneId" :class="['to-do', { done: item.doneFlag }]">
            <a-checkbox v-model:checked="item.doneFlag" @change="handleCheckbox(item)">
              <span class="task">{{ item.title }}</span>
            </a-checkbox>
            <div v-if="!item.doneFlag" class="star-icon" @click="itemStar(item)">
              <StarFilled v-if="item.starFlag" style="color: #ff8c00" />
              <StarOutlined v-else style="color: #c0c0c0" />
            </div>
            <close-circle-outlined class="delete-icon" @click="toDelete(item)" />
          </div>
          <div v-for="item in doneList" :key="item.toBeDoneId" :class="['to-do', { done: item.doneFlag }]">
            <a-checkbox v-model:checked="item.doneFlag" @change="handleCheckbox(item)">
              <span class="task">{{ item.title }}</span>
            </a-checkbox>
            <div v-if="!item.doneFlag" class="star-icon" @click="itemStar(item)">
              <StarFilled v-if="item.starFlag" style="color: #ff8c00" />
              <StarOutlined v-else style="color: #c0c0c0" />
            </div>
            <close-circle-outlined class="delete-icon" @click="toDelete(item)" />
          </div>
        </a-space>
      </div>
    </div>
  </default-home-card>
  <ToBeDoneModal ref="toBeDoneModalRef" @addToBeDone="addToBeDone" />
</template>
<script setup lang="ts">
  import DefaultHomeCard from '/@/views/system/home/components/default-home-card.vue';
  import ToBeDoneModal from './to-be-done-modal.vue';
  import { useUserStore } from '/@/store/modules/system/user';
  import { computed, ref, onMounted } from 'vue';
  import { Modal } from 'ant-design-vue';
  import { smartSentry } from '/@/lib/smart-sentry';
  import { toBeDoneApi } from '/@/api/system/to-be-done-api';

  let toBeDoneList = ref([]);
  const userStore = useUserStore();

  onMounted(() => {
    queryToBeDoneList();
  });

  async function queryToBeDoneList() {
    try {
      const result = await toBeDoneApi.queryList();
      toBeDoneList.value = result.data || [];
      userStore.toBeDoneCount = toDoList.value.length;
    } catch (err) {
      smartSentry.captureError(err);
    }
  }

  let toDoList = computed(() => {
    return toBeDoneList.value.filter((e) => !e.doneFlag);
  });

  let doneList = computed(() => {
    return toBeDoneList.value.filter((e) => e.doneFlag);
  });

  async function handleCheckbox(item) {
    await updateToBeDone(item, { doneFlag: item.doneFlag });
  }

  async function itemStar(data) {
    await updateToBeDone(data, { starFlag: !data.starFlag });
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

  //-------------------------任务新建-----------------------

  let toBeDoneModalRef = ref();

  function showAddToBeDone() {
    toBeDoneModalRef.value.showModal();
  }

  // 添加待办工作
  async function addToBeDone(data) {
    try {
      await toBeDoneApi.add(data);
      await queryToBeDoneList();
    } catch (err) {
      smartSentry.captureError(err);
    }
  }

  function toDelete(data) {
    if (!data.doneFlag) {
      Modal.confirm({
        title: '提示',
        content: '确定要删除吗?',
        okText: '删除',
        okType: 'danger',
        onOk() {
          deleteToBeDone(data);
        },
        cancelText: '取消',
        onCancel() {},
      });
    } else {
      deleteToBeDone(data);
    }
  }

  // 删除待办工作
  function deleteToBeDone(data) {
    toBeDoneApi
      .deleteToBeDone(data.toBeDoneId)
      .then(queryToBeDoneList)
      .catch((err) => {
        smartSentry.captureError(err);
      });
  }
</script>
<style lang="less" scoped>
  .center {
    display: flex;
    justify-content: center;
    height: 100%;
    overflow-y: auto;

    &.column {
      flex-direction: column;
      width: 100%;
      padding: 0 10px;
      justify-content: flex-start;
    }
  }

  .to-do {
    width: 100%;
    border: 1px solid #d3d3d3;
    border-radius: 4px;
    padding: 4px;
    display: flex;
    align-items: center;
    .star-icon {
      margin-left: auto;
      cursor: pointer;
    }

    &.done {
      text-decoration: line-through;
      color: #8c8c8c;

      .task {
        color: #8c8c8c;
      }
    }
  }

  .delete-icon {
    color: #f08080;
    padding-left: 10px;
    top: -5px;
    right: -5px;
    float: right;
  }
</style>
