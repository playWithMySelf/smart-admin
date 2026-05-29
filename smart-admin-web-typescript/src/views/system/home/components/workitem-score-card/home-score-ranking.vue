<template>
  <default-home-card icon="TrophyOutlined" title="积分排行榜（本部门及以下、当月）">
    <a-spin :spinning="loading">
      <div class="score-ranking-box">
        <a-empty v-if="scoreRankingList.length === 0" description="暂无积分数据" />
        <div v-else class="score-ranking-list">
          <div v-for="(item, index) in scoreRankingList" :key="item.employeeId || index" class="score-ranking-item">
            <span :class="['rank-index', `rank-${index + 1}`]">{{ index + 1 }}</span>
            <div class="rank-user">
              <div class="rank-name">{{ item.employeeName || '-' }}</div>
              <div class="rank-detail">{{ item.reportCount || 0 }} 天日报 · {{ item.itemCount || 0 }} 项明细</div>
            </div>
            <div class="rank-score">
              <span>{{ item.totalScore || 0 }}</span>
              <span class="rank-score-unit">分</span>
            </div>
          </div>
        </div>
      </div>
    </a-spin>
  </default-home-card>
</template>
<script setup lang="ts">
  import { computed, onMounted, ref } from 'vue';
  import dayjs from 'dayjs';
  import DefaultHomeCard from '/@/views/system/home/components/default-home-card.vue';
  import { workitemApi } from '/@/api/business/workitem/workitem-api';
  import { useUserStore } from '/@/store/modules/system/user';
  import { smartSentry } from '/@/lib/smart-sentry';

  type EmployeeScore = {
    employeeId?: number | string;
    employeeName?: string;
    totalScore?: number;
    reportCount?: number;
    itemCount?: number;
  };

  const userStore = useUserStore();
  const loading = ref(false);
  const employeeScoreList = ref<EmployeeScore[]>([]);

  const scoreRankingList = computed(() => {
    return [...employeeScoreList.value].sort((a, b) => (b.totalScore || 0) - (a.totalScore || 0)).slice(0, 10);
  });

  function currentMonthRange() {
    return {
      startDate: dayjs().startOf('month').format('YYYY-MM-DD'),
      endDate: dayjs().format('YYYY-MM-DD'),
      departmentId: userStore.departmentId || undefined,
    };
  }

  async function queryScoreRanking() {
    loading.value = true;
    try {
      const res = await workitemApi.queryEmployeeScore(currentMonthRange());
      employeeScoreList.value = res.data || [];
    } catch (e) {
      smartSentry.captureError(e);
      employeeScoreList.value = [];
    } finally {
      loading.value = false;
    }
  }

  onMounted(queryScoreRanking);
</script>
<style lang="less" scoped>
  .score-ranking-box {
    height: 300px;
    overflow-y: auto;
  }

  .score-ranking-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .score-ranking-item {
    display: flex;
    align-items: center;
    gap: 10px;
    min-height: 46px;
    padding: 7px 4px;
    border-bottom: 1px solid #f0f0f0;
  }

  .rank-index {
    display: inline-flex;
    flex: 0 0 26px;
    align-items: center;
    justify-content: center;
    width: 26px;
    height: 26px;
    border-radius: 50%;
    background: #f5f5f5;
    color: #8c8c8c;
    font-weight: 600;
  }

  .rank-1 {
    background: #fff1b8;
    color: #ad6800;
  }

  .rank-2 {
    background: #e6f4ff;
    color: #0958d9;
  }

  .rank-3 {
    background: #fff2e8;
    color: #d4380d;
  }

  .rank-user {
    flex: 1;
    min-width: 0;
  }

  .rank-name {
    overflow: hidden;
    color: #262626;
    font-weight: 600;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .rank-detail {
    overflow: hidden;
    margin-top: 2px;
    color: #8c8c8c;
    font-size: 12px;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .rank-score {
    flex: 0 0 auto;
    color: #1677ff;
    font-size: 18px;
    font-weight: 700;
  }

  .rank-score-unit {
    margin-left: 2px;
    font-size: 12px;
    font-weight: 400;
  }
</style>
