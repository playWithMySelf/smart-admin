<template>
  <div class="work-item-maintain">
    <a-card size="small" :bordered="false">
      <div class="maintain-layout">
        <div class="type-panel">
          <div class="panel-header">
            <span>工作项类型</span>
            <a-button type="primary" size="small" @click="showTypeModal()" v-privilege="'workitem:type:add'">
              <template #icon><PlusOutlined /></template>
            </a-button>
          </div>
          <a-list :data-source="typeList" :loading="typeLoading" size="small">
            <template #renderItem="{ item }">
              <a-list-item
                :class="{ active: item.workItemTypeId === selectedTypeId }"
                @click="selectType(item)"
              >
                <a-list-item-meta>
                  <template #title>
                    <span>{{ item.typeName }}</span>
                    <a-tag v-if="item.disabledFlag" color="red">停用</a-tag>
                  </template>
                </a-list-item-meta>
                <template #actions>
                  <a-button type="link" size="small" @click.stop="showTypeModal(item)" v-privilege="'workitem:type:update'">编辑</a-button>
                  <a-button type="link" danger size="small" @click.stop="deleteType(item)" v-privilege="'workitem:type:delete'">删除</a-button>
                </template>
              </a-list-item>
            </template>
          </a-list>
        </div>

        <div class="item-panel">
          <a-form class="smart-query-form">
            <a-row class="smart-query-form-row">
              <a-form-item label="关键字" class="smart-query-form-item">
                <a-input style="width: 220px" v-model:value="queryForm.keywords" placeholder="名称/描述/评分标准" allowClear />
              </a-form-item>
              <a-form-item class="smart-query-form-item">
                <a-button-group>
                  <a-button type="primary" @click="onSearch" v-privilege="'workitem:item:query'">
                    <template #icon><SearchOutlined /></template>
                    查询
                  </a-button>
                  <a-button @click="resetQuery" v-privilege="'workitem:item:query'">
                    <template #icon><ReloadOutlined /></template>
                    重置
                  </a-button>
                </a-button-group>
              </a-form-item>
            </a-row>
          </a-form>

          <a-row class="smart-table-btn-block">
            <div class="smart-table-operate-block">
              <a-button type="primary" @click="showItemModal()" :disabled="!selectedTypeId" v-privilege="'workitem:item:add'">
                <template #icon><PlusOutlined /></template>
                新建工作项
              </a-button>
              <a-button danger @click="batchDeleteItem" :disabled="!hasSelectedItem" v-privilege="'workitem:item:delete'">
                <template #icon><DeleteOutlined /></template>
                批量删除
              </a-button>
            </div>
          </a-row>

          <a-table
            size="small"
            bordered
            rowKey="workItemId"
            :loading="tableLoading"
            :dataSource="tableData"
            :columns="columns"
            :pagination="false"
            :row-selection="{ selectedRowKeys: selectedRowKeyList, onChange: onSelectChange }"
          >
            <template #bodyCell="{ text, record, column }">
              <template v-if="column.dataIndex === 'disabledFlag'">
                <a-tag :color="text ? 'red' : 'green'">{{ text ? '停用' : '启用' }}</a-tag>
              </template>
              <template v-if="column.dataIndex === 'action'">
                <div class="smart-table-operate">
                  <a-button type="link" @click="showItemModal(record)" v-privilege="'workitem:item:update'">编辑</a-button>
                  <a-button type="link" danger @click="deleteItem(record)" v-privilege="'workitem:item:delete'">删除</a-button>
                </div>
              </template>
            </template>
          </a-table>
          <div class="smart-query-table-page">
            <a-pagination
              showSizeChanger
              showQuickJumper
              :pageSizeOptions="PAGE_SIZE_OPTIONS"
              v-model:current="queryForm.pageNum"
              v-model:pageSize="queryForm.pageSize"
              :total="total"
              @change="queryItemPage"
              :show-total="showTotal"
            />
          </div>
        </div>
      </div>
    </a-card>

    <a-modal v-model:open="typeModalVisible" :title="typeForm.workItemTypeId ? '编辑类型' : '新增类型'" @ok="submitType">
      <a-form ref="typeFormRef" :model="typeForm" :rules="typeRules" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
        <a-form-item label="类型名称" name="typeName">
          <a-input v-model:value="typeForm.typeName" placeholder="请输入类型名称" />
        </a-form-item>
        <a-form-item label="排序" name="sort">
          <a-input-number v-model:value="typeForm.sort" style="width: 100%" :min="0" />
        </a-form-item>
        <a-form-item label="启用状态" name="disabledFlag">
          <a-switch v-model:checked="typeEnabledChecked" @change="typeForm.disabledFlag = !typeEnabledChecked" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal v-model:open="itemModalVisible" :title="itemForm.workItemId ? '编辑工作项' : '新增工作项'" :width="760" @ok="submitItem">
      <a-form ref="itemFormRef" :model="itemForm" :rules="itemRules" :label-col="{ span: 4 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="工作项名称" name="workItemName">
          <a-input v-model:value="itemForm.workItemName" placeholder="请输入工作项名称" />
        </a-form-item>
        <a-form-item label="描述" name="description">
          <a-textarea v-model:value="itemForm.description" :rows="3" placeholder="请输入描述" />
        </a-form-item>
        <a-form-item label="评分标准" name="scoreStandard">
          <a-textarea v-model:value="itemForm.scoreStandard" :rows="4" placeholder="请输入评分标准" />
        </a-form-item>
        <a-form-item label="标准分" name="standardScore">
          <a-input-number v-model:value="itemForm.standardScore" style="width: 100%" :min="0" :precision="2" />
        </a-form-item>
        <a-form-item label="排序" name="sort">
          <a-input-number v-model:value="itemForm.sort" style="width: 100%" :min="0" />
        </a-form-item>
        <a-form-item label="启用状态" name="disabledFlag">
          <a-switch v-model:checked="itemEnabledChecked" @change="itemForm.disabledFlag = !itemEnabledChecked" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
  import { computed, onMounted, reactive, ref } from 'vue';
  import { message, Modal } from 'ant-design-vue';
  import { DeleteOutlined, PlusOutlined, ReloadOutlined, SearchOutlined } from '@ant-design/icons-vue';
  import { workitemApi } from '/@/api/business/workitem/workitem-api';
  import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { smartSentry } from '/@/lib/smart-sentry';

  type WorkitemRecord = Record<string, any>;

  function cloneForm<T>(form: T): T {
    return { ...form };
  }

  // ---------------------------- 类型 ----------------------------
  const typeLoading = ref(false);
  const typeList = ref<any[]>([]);
  const selectedTypeId = ref();

  async function queryTypeList() {
    typeLoading.value = true;
    try {
      const res = await workitemApi.queryTypeList();
      typeList.value = res.data || [];
      if (!selectedTypeId.value && typeList.value.length > 0) {
        selectedTypeId.value = typeList.value[0].workItemTypeId;
        queryForm.workItemTypeId = selectedTypeId.value;
      }
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      typeLoading.value = false;
    }
  }

  function selectType(item: WorkitemRecord) {
    selectedTypeId.value = item.workItemTypeId;
    queryForm.workItemTypeId = item.workItemTypeId;
    onSearch();
  }

  const typeFormRef = ref();
  const typeModalVisible = ref(false);
  const typeEnabledChecked = ref(true);
  const typeFormDefault = { workItemTypeId: undefined, typeName: '', sort: 0, disabledFlag: false };
  const typeForm = reactive({ ...typeFormDefault });
  const typeRules = { typeName: [{ required: true, message: '请输入类型名称' }] };

  function showTypeModal(record?: WorkitemRecord) {
    Object.assign(typeForm, record || typeFormDefault);
    typeEnabledChecked.value = !typeForm.disabledFlag;
    typeModalVisible.value = true;
  }

  async function submitType() {
    try {
      await typeFormRef.value.validate();
      SmartLoading.show();
      if (typeForm.workItemTypeId) {
        await workitemApi.updateType(typeForm);
      } else {
        await workitemApi.addType(typeForm);
      }
      message.success('保存成功');
      typeModalVisible.value = false;
      await queryTypeList();
      queryItemPage();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  function deleteType(record: WorkitemRecord) {
    Modal.confirm({
      title: '提示',
      content: `确定删除【${record.typeName}】吗？`,
      okType: 'danger',
      onOk: async () => {
        await workitemApi.deleteType(record.workItemTypeId);
        message.success('删除成功');
        selectedTypeId.value = undefined;
        await queryTypeList();
        queryItemPage();
      },
    });
  }

  // ---------------------------- 工作项 ----------------------------
  const columns = [
    { title: '工作项名称', dataIndex: 'workItemName', width: 160 },
    { title: '标准分', dataIndex: 'standardScore', width: 90 },
    { title: '评分标准', dataIndex: 'scoreStandard', width: 160, ellipsis: true },
    { title: '排序', dataIndex: 'sort', width: 80 },
    { title: '状态', dataIndex: 'disabledFlag', width: 90 },
    { title: '操作', dataIndex: 'action', fixed: 'right', width: 120 },
  ];
  const queryFormState = { workItemTypeId: undefined, keywords: '', disabledFlag: undefined, pageNum: 1, pageSize: 10, sortItemList: [] };
  const queryForm = reactive(cloneForm(queryFormState));
  const tableLoading = ref(false);
  const tableData = ref<any[]>([]);
  const total = ref(0);
  const selectedRowKeyList = ref<(string | number)[]>([]);
  const hasSelectedItem = computed(() => selectedRowKeyList.value.length > 0);

  async function queryItemPage() {
    tableLoading.value = true;
    try {
      const res = await workitemApi.queryItemPage(queryForm);
      tableData.value = res.data.list;
      total.value = res.data.total;
      selectedRowKeyList.value = [];
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  function onSearch() {
    queryForm.pageNum = 1;
    queryItemPage();
  }

  function resetQuery() {
    const pageSize = queryForm.pageSize;
    Object.assign(queryForm, cloneForm(queryFormState));
    queryForm.pageSize = pageSize;
    queryForm.workItemTypeId = selectedTypeId.value;
    queryItemPage();
  }

  function onSelectChange(selectedRowKeys: (string | number)[]) {
    selectedRowKeyList.value = selectedRowKeys;
  }

  const itemFormRef = ref();
  const itemModalVisible = ref(false);
  const itemEnabledChecked = ref(true);
  const itemFormDefault = {
    workItemId: undefined,
    workItemTypeId: undefined,
    workItemName: '',
    description: '',
    scoreStandard: '',
    standardScore: 0,
    sort: 0,
    disabledFlag: false,
  };
  const itemForm = reactive({ ...itemFormDefault });
  const itemRules = {
    workItemName: [{ required: true, message: '请输入工作项名称' }],
    standardScore: [{ required: true, message: '请输入标准分' }],
  };

  function showItemModal(record?: WorkitemRecord) {
    Object.assign(itemForm, record || itemFormDefault);
    itemForm.workItemTypeId = record?.workItemTypeId || selectedTypeId.value;
    itemEnabledChecked.value = !itemForm.disabledFlag;
    itemModalVisible.value = true;
  }

  async function submitItem() {
    try {
      await itemFormRef.value.validate();
      SmartLoading.show();
      if (itemForm.workItemId) {
        await workitemApi.updateItem(itemForm);
      } else {
        await workitemApi.addItem(itemForm);
      }
      message.success('保存成功');
      itemModalVisible.value = false;
      queryItemPage();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  function deleteItem(record: WorkitemRecord) {
    Modal.confirm({
      title: '提示',
      content: `确定删除【${record.workItemName}】吗？`,
      okType: 'danger',
      onOk: async () => {
        await workitemApi.deleteItem(record.workItemId);
        message.success('删除成功');
        queryItemPage();
      },
    });
  }

  function batchDeleteItem() {
    if (!hasSelectedItem.value) {
      message.warning('请选择要删除的工作项');
      return;
    }
    Modal.confirm({
      title: '提示',
      content: `确定删除选中的 ${selectedRowKeyList.value.length} 个工作项吗？`,
      okType: 'danger',
      onOk: async () => {
        await workitemApi.batchDeleteItem(selectedRowKeyList.value);
        message.success('删除成功');
        selectedRowKeyList.value = [];
        queryItemPage();
      },
    });
  }

  function showTotal(total: number) {
    return `共${total}条`;
  }

  onMounted(async () => {
    await queryTypeList();
    queryItemPage();
  });
</script>

<style scoped lang="less">
  .maintain-layout {
    display: flex;
    gap: 12px;
    min-height: calc(100vh - 160px);
  }

  .type-panel {
    width: 300px;
    border-right: 1px solid #f0f0f0;
    padding-right: 12px;
  }

  .item-panel {
    flex: 1;
    min-width: 0;
  }

  .panel-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8px;
    font-weight: 600;
  }

  :deep(.ant-list-item) {
    cursor: pointer;
    padding: 8px;
  }

  :deep(.ant-list-item.active) {
    background: #e6f4ff;
  }

</style>
<style lang="less">
  .work-item-maintain {
    :where(.css-dev-only-do-not-override-vgohfh).ant-list .ant-list-item .ant-list-item-action>li {
      padding: 0 !important;
    }
    .ant-list-items {
      .ant-list-item-action {
        margin-left: 10px;
        margin-inline-start: 10px;
      }
    }
  }
</style>
