<template>
  <default-home-card icon="LineChartOutlined" title="积分趋势图（本部门及以下、当月）">
    <a-spin :spinning="loading">
      <div class="score-trend-box">
        <div ref="chartRef" class="score-trend-main"></div>
      </div>
    </a-spin>
  </default-home-card>
</template>
<script setup lang="ts">
  import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue';
  import dayjs from 'dayjs';
  import * as echarts from 'echarts';
  import DefaultHomeCard from '/@/views/system/home/components/default-home-card.vue';
  import { workitemApi } from '/@/api/business/workitem/workitem-api';
  import { useUserStore } from '/@/store/modules/system/user';
  import { smartSentry } from '/@/lib/smart-sentry';

  type DateScore = {
    reportDate?: string;
    totalScore?: number;
  };

  const userStore = useUserStore();
  const loading = ref(false);
  const chartRef = ref<HTMLElement>();
  let chartInstance: echarts.ECharts | undefined;
  let dateScoreList: DateScore[] = [];

  function currentMonthRange() {
    return {
      startDate: dayjs().startOf('month').format('YYYY-MM-DD'),
      endDate: dayjs().format('YYYY-MM-DD'),
      departmentId: userStore.departmentId || undefined,
    };
  }

  function buildMonthAxis() {
    const startDate = dayjs().startOf('month');
    const dayCount = dayjs().diff(startDate, 'day') + 1;
    const scoreMap = new Map(dateScoreList.map((item) => [item.reportDate, item.totalScore || 0]));
    const dateList: string[] = [];
    const scoreList: number[] = [];

    for (let index = 0; index < dayCount; index++) {
      const currentDate = startDate.add(index, 'day').format('YYYY-MM-DD');
      dateList.push(currentDate);
      scoreList.push(scoreMap.get(currentDate) || 0);
    }

    return { dateList, scoreList };
  }

  function renderChart() {
    if (!chartRef.value) {
      return;
    }
    if (!chartInstance) {
      chartInstance = echarts.init(chartRef.value);
    }

    const { dateList, scoreList } = buildMonthAxis();
    const option = {
      color: ['#37A2FF'],
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross',
          label: {
            backgroundColor: '#6a7985',
          },
        },
        valueFormatter: (value: number | string) => `${value || 0} 分`,
      },
      legend: {
        data: ['积分'],
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
      },
      xAxis: [
        {
          type: 'category',
          boundaryGap: false,
          data: dateList,
          axisLabel: {
            formatter: (value: string) => dayjs(value).format('MM-DD'),
          },
        },
      ],
      yAxis: [
        {
          type: 'value',
          minInterval: 1,
        },
      ],
      series: [
        {
          name: '积分',
          type: 'line',
          smooth: true,
          lineStyle: {
            width: 0,
          },
          showSymbol: false,
          areaStyle: {
            opacity: 0.8,
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: 'rgb(55, 162, 255)',
              },
              {
                offset: 1,
                color: 'rgb(116, 21, 219)',
              },
            ]),
          },
          emphasis: {
            focus: 'series',
          },
          data: scoreList,
        },
      ],
    };
    chartInstance.setOption(option);
  }

  async function queryScoreTrend() {
    loading.value = true;
    try {
      const res = await workitemApi.queryDateScore(currentMonthRange());
      dateScoreList = res.data || [];
      await nextTick();
      renderChart();
    } catch (e) {
      smartSentry.captureError(e);
      dateScoreList = [];
      renderChart();
    } finally {
      loading.value = false;
    }
  }

  function resizeChart() {
    chartInstance?.resize();
  }

  onMounted(() => {
    queryScoreTrend();
    window.addEventListener('resize', resizeChart);
  });

  onBeforeUnmount(() => {
    window.removeEventListener('resize', resizeChart);
    chartInstance?.dispose();
    chartInstance = undefined;
  });
</script>
<style lang="less" scoped>
  .score-trend-box {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .score-trend-main {
    width: 100%;
    height: 300px;
  }
</style>
