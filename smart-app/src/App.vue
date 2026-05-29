<script>
  import { useUserStore } from '@/store/modules/system/user';
  export default {
    async onLaunch() {
      await useUserStore().getLoginInfo();
    },
    onShow: function () {
      const userStore = useUserStore();
      if (userStore.getToken) {
        userStore.queryUnreadMessageCount();
      }
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
