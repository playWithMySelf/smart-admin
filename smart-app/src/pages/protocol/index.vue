<template>
  <view class="protocol-page">
    <view class="protocol-title">{{ protocol.title }}</view>
    <view class="protocol-updated">更新日期：{{ protocol.updatedAt }}</view>

    <view class="protocol-section" v-for="section in protocol.sections" :key="section.title">
      <view class="section-title">{{ section.title }}</view>
      <text class="section-content">{{ section.content }}</text>
    </view>
  </view>
</template>

<script setup>
  import { computed, ref } from 'vue';
  import { onLoad } from '@dcloudio/uni-app';

  const PROTOCOL_CONTENT = {
    user_agreement: {
      title: '用户协议',
      updatedAt: '2026-06-20',
      sections: [
        {
          title: '一、服务说明',
          content:
            '欢迎使用本应用。您登录、访问或使用本应用，即表示您已阅读并同意遵守本协议。本应用为用户提供移动办公、消息通知、业务处理、日报管理和账号管理等服务。',
        },
        {
          title: '二、账号使用',
          content:
            '您应妥善保管账号、密码及登录凭证，并对账号下的操作行为负责。发现账号异常、密码泄露或未经授权使用时，请及时联系管理员处理。',
        },
        {
          title: '三、使用规则',
          content:
            '您在使用本应用时，应遵守法律法规及组织内部管理要求，不得提交虚假、违法、侵权、干扰系统正常运行或损害他人权益的内容。',
        },
        {
          title: '四、服务变更',
          content:
            '为持续优化体验和保障系统安全，本应用可能对功能、页面或服务规则进行调整。调整后继续使用本应用，视为您接受更新后的服务内容。',
        },
      ],
    },
    privacy_terms: {
      title: '隐私政策',
      updatedAt: '2026-06-20',
      sections: [
        {
          title: '一、信息收集',
          content:
            '为完成登录认证、账号识别、业务办理和安全审计，本应用可能收集您的账号信息、组织信息、设备信息、操作记录以及您主动提交的业务数据。',
        },
        {
          title: '二、信息使用',
          content:
            '收集的信息将用于身份验证、权限控制、消息提醒、业务流转、问题排查、安全防护和服务改进，不会用于与业务无关的用途。',
        },
        {
          title: '三、信息保护',
          content:
            '本应用会采取合理的技术和管理措施保护您的信息安全，防止未经授权的访问、披露、篡改或丢失。请您同时妥善保管个人登录凭证。',
        },
        {
          title: '四、信息管理',
          content:
            '如您需要查询、更正或删除个人信息，或对隐私保护存在疑问，可联系所在组织管理员按内部流程处理。',
        },
      ],
    },
  };

  const protocolKey = ref('user_agreement');

  const protocol = computed(() => {
    return PROTOCOL_CONTENT[protocolKey.value] || PROTOCOL_CONTENT.user_agreement;
  });

  onLoad((option) => {
    protocolKey.value = option.key || 'user_agreement';
    uni.setNavigationBarTitle({
      title: protocol.value.title,
    });
  });
</script>

<style lang="scss" scoped>
  .protocol-page {
    min-height: 100vh;
    box-sizing: border-box;
    padding: 48rpx 36rpx 72rpx;
    background-color: #ffffff;
    color: #333333;
  }

  .protocol-title {
    font-size: 42rpx;
    font-weight: 600;
    line-height: 60rpx;
    text-align: center;
  }

  .protocol-updated {
    margin-top: 16rpx;
    font-size: 26rpx;
    line-height: 40rpx;
    text-align: center;
    color: #999999;
  }

  .protocol-section {
    margin-top: 42rpx;
  }

  .section-title {
    margin-bottom: 16rpx;
    font-size: 32rpx;
    font-weight: 600;
    line-height: 46rpx;
    color: #222222;
  }

  .section-content {
    display: block;
    font-size: 29rpx;
    line-height: 50rpx;
    color: #555555;
  }
</style>
