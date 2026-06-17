<script>
  import { useUserStore } from '@/store/modules/system/user';
  import { initMessageLocalNotification, setAppVisible } from '@/lib/message-local-notification';
  export default {
    async onLaunch() {
      // 小程序版本更新检测（仅小程序环境生效）
      // #ifdef MP-WEIXIN || MP-ALIPAY || MP-BAIDU || MP-TOUTIAO
      this.checkUpdate();
      // #endif
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
    methods: {
      /**
       * 检测小程序版本更新，有新版自动下载并引导用户重启
       */
      checkUpdate() {
        // #ifdef MP-WEIXIN
        if (wx.getUpdateManager) {
          const updateManager = wx.getUpdateManager();
          updateManager.onCheckForUpdate((res) => {
            if (res.hasUpdate) {
              console.log('[更新] 检测到新版本');
            }
          });
          updateManager.onUpdateReady(() => {
            wx.showModal({
              title: '更新提示',
              content: '新版本已准备好，是否立即重启小程序？',
              showCancel: false,
              confirmText: '重启',
              success: (modalRes) => {
                if (modalRes.confirm) {
                  updateManager.applyUpdate();
                }
              },
            });
          });
          updateManager.onUpdateFailed(() => {
            console.log('[更新] 新版本下载失败');
          });
        }
        // #endif

        // #ifdef MP-ALIPAY
        if (my.getUpdateManager) {
          const updateManager = my.getUpdateManager();
          updateManager.onCheckForUpdate((res) => {
            if (res.hasUpdate) {
              console.log('[更新] 检测到新版本');
            }
          });
          updateManager.onUpdateReady(() => {
            my.confirm({
              title: '更新提示',
              content: '新版本已准备好，是否立即重启？',
              confirmButtonText: '重启',
              cancelButtonText: '稍后',
              success: (modalRes) => {
                if (modalRes.confirm) {
                  updateManager.applyUpdate();
                }
              },
            });
          });
          updateManager.onUpdateFailed(() => {
            console.log('[更新] 新版本下载失败');
          });
        }
        // #endif
      },
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