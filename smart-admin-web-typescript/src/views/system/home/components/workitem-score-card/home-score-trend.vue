<template>
  <default-home-card icon="LineChartOutlined" title="部门个人积分趋势图">
    <template #extra>
      <a-date-picker
        v-model:value="selectedMonth"
        picker="month"
        valueFormat="YYYY-MM"
        :allowClear="false"
        class="score-month-picker"
        @change="queryScoreTrend"
      />
    </template>
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

  type EmployeeDateScore = {
    employeeId?: number | string;
    employeeName?: string;
    reportDate?: string;
    totalScore?: number;
  };

  const userStore = useUserStore();
  const loading = ref(false);
  const selectedMonth = ref(dayjs().format('YYYY-MM'));
  const chartRef = ref<HTMLElement>();
  let chartInstance: echarts.ECharts | undefined;
  let employeeDateScoreList: EmployeeDateScore[] = [];

  function selectedMonthRange() {
    const monthStartDate = dayjs(`${selectedMonth.value}-01`);
    const currentMonth = dayjs().format('YYYY-MM');
    const monthEndDate = selectedMonth.value === currentMonth ? dayjs() : monthStartDate.endOf('month');

    return {
      startDate: monthStartDate.format('YYYY-MM-DD'),
      endDate: monthEndDate.format('YYYY-MM-DD'),
      departmentId: userStore.departmentId || undefined,
    };
  }

  function buildMonthAxis() {
    const startDate = dayjs(`${selectedMonth.value}-01`);
    const currentMonth = dayjs().format('YYYY-MM');
    const dayCount = selectedMonth.value === currentMonth ? dayjs().diff(startDate, 'day') + 1 : startDate.daysInMonth();
    const dateList: string[] = [];

    for (let index = 0; index < dayCount; index++) {
      const currentDate = startDate.add(index, 'day').format('YYYY-MM-DD');
      dateList.push(currentDate);
    }

    return dateList;
  }

  function buildEmployeeSeries(dateList: string[]) {
    const employeeMap = new Map<string | number, string>();
    const scoreMap = new Map<string, number>();

    employeeDateScoreList.forEach((item) => {
      if (!item.employeeId || !item.reportDate) {
        return;
      }
      employeeMap.set(item.employeeId, item.employeeName || '-');
      scoreMap.set(`${item.employeeId}_${item.reportDate}`, item.totalScore || 0);
    });

    const employeeNameCountMap = Array.from(employeeMap.values()).reduce((countMap, employeeName) => {
      countMap.set(employeeName, (countMap.get(employeeName) || 0) + 1);
      return countMap;
    }, new Map<string, number>());
    const employeeLegendNameMap = new Map<string | number, string>();
    Array.from(employeeMap.entries()).forEach(([employeeId, employeeName]) => {
      employeeLegendNameMap.set(employeeId, (employeeNameCountMap.get(employeeName) || 0) > 1 ? `${employeeName}(${employeeId})` : employeeName);
    });
    const legendData = Array.from(employeeLegendNameMap.values());
    const series = Array.from(employeeMap.entries()).map(([employeeId, employeeName]) => {
      return {
        name: employeeLegendNameMap.get(employeeId) || employeeName,
        type: 'line',
        smooth: true,
        showSymbol: false,
        lineStyle: {
          width: 2,
        },
        emphasis: {
          focus: 'series',
        },
        data: dateList.map((date) => scoreMap.get(`${employeeId}_${date}`) || 0),
      };
    });

    return { legendData, series };
  }

  function renderChart() {
    if (!chartRef.value) {
      return;
    }
    if (!chartInstance) {
      chartInstance = echarts.init(chartRef.value);
    }

    const dateList = buildMonthAxis();
    const { legendData, series } = buildEmployeeSeries(dateList);
    const option = {
      color: ['#37A2FF', '#80FFA5', '#FFBF00', '#FF0087', '#00DDFF', '#9A60B4', '#EA7CCC', '#91CC75', '#FAC858', '#EE6666'],
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
        type: 'scroll',
        data: legendData,
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
      series,
    };
    chartInstance.setOption(option, true);
  }

  async function queryScoreTrend() {
    loading.value = true;
    try {
      const res = await workitemApi.queryEmployeeDateScore(selectedMonthRange());
      employeeDateScoreList = res.data || [];
      await nextTick();
      renderChart();
    } catch (e) {
      smartSentry.captureError(e);
      employeeDateScoreList = [];
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
    height: 420px;
  }

  .score-month-picker {
    width: 118px;
  }
</style>
