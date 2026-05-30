<!--
  * 首页
  *
  * @Author: jw
  * @Date:      2022-09-12 22:34:00
  * @Wechat:    zhuda1024
  * @Email:     lab1024@163.com
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
  *
-->
<template>
  <!--  顶部用户信息-->
  <a-row>
    <HomeHeader />
  </a-row>
  <!--下方左右布局-->
  <a-row :gutter="[10, 10]">
    <a-col :span="16">
      <a-row :gutter="[10, 10]">
        <!--公告信息-->
        <a-col :span="12">
          <HomeNotice title="新闻" :noticeTypeId="1" />
        </a-col>
        <!--企业动态-->
        <a-col :span="12">
          <HomeNotice title="公告" :noticeTypeId="2" />
        </a-col>
      </a-row>
    </a-col>
    <!--待办工作-->
    <a-col :span="8">
      <ToBeDoneCard />
    </a-col>
    <!--积分趋势图-->
    <a-col :span="16">
      <HomeScoreTrend />
    </a-col>
    <!--积分排行榜-->
    <a-col :span="8">
      <HomeScoreRanking />
    </a-col>
<!--    <AdModal/>-->
  </a-row>
</template>
<script setup lang="ts">
  import HomeHeader from './home-header.vue';
  import HomeNotice from './home-notice.vue';
  import ToBeDoneCard from './components/to-be-done-card/home-to-be-done.vue';
  import HomeScoreTrend from './components/workitem-score-card/home-score-trend.vue';
  import HomeScoreRanking from './components/workitem-score-card/home-score-ranking.vue';
  // import AdModal from './ad-modal.vue';
</script>
<style lang="less" scoped>
  @import './index.less';
</style>
