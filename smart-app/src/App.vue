<script>
  import { useUserStore } from '@/store/modules/system/user';
  import { initMessageLocalNotification, setAppVisible } from '@/lib/message-local-notification';
  export default {
    async onLaunch() {
      initMessageLocalNotification();
      await useUserStore().getLoginInfo();
    },
    onShow: function () {
      setAppVisible(true);
      const userStore = useUserStore();
      if (userStore.getToken) {
        userStore.startUserMessageStream();
        userStore.queryUnreadMessageCount();
      }
    },
    onHide: function () {
      setAppVisible(false);
    },
  };
</script>

<style lang="scss">
  @import '@/uni_modules/uni-scss/index.scss';
  /* 设置基准字体大小为16px */
  body {
    font-size: 16px;
  }
</style>
