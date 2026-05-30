<!--
  * 部门 树形选择框

  * @Author: jw
  * @Date:      2022-09-12 23:05:43
  * @Wechat:    zhuda1024
  * @Email:     lab1024@163.com
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
  *
-->
<template>
  <a-tree-select
    :value="props.value"
    :treeData="treeData"
    :fieldNames="{ label: 'departmentName', key: 'departmentId', value: 'departmentId' }"
    show-search
    style="width: 100%"
    :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
    placeholder="请选择部门"
    allow-clear
    tree-default-expand-all
    :multiple="props.multiple"
    @change="onChange"
  />
</template>
<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import type { PropType } from 'vue';
  import { departmentApi } from '/@/api/system/department-api';

  const props = defineProps({
    // 绑定值
    value: Number,
    // 单选多选
    multiple: {
      type: Boolean,
      default: false,
    },
    // 自定义部门树查询接口，用于业务页面按权限范围加载机构
    queryApi: {
      type: Function as PropType<() => Promise<{ data: any[] }>>,
    },
  });

  const emit = defineEmits(['update:value']);

  let treeData = ref([]);
  onMounted(queryDepartmentTree);

  async function queryDepartmentTree() {
    let res = props.queryApi ? await props.queryApi() : await departmentApi.queryDepartmentTree();
    treeData.value = res.data;
  }

  function onChange(e) {
    emit('update:value', e);
  }

  defineExpose({
    queryDepartmentTree,
  });
</script>
