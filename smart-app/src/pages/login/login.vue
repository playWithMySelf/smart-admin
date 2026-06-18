<template>
  <view class="container">
    <view class="top-view">
      <image class="top-bg" src="@/static/images/login/login-top-back.jpg" mode="aspectFill" />
      <view class="login"> 登录 </view>
      <view class="logo">
        <image src="@/static/images/login/login-logo.png" />
      </view>
    </view>
    <view class="bottom-view">
      <view class="input-view smart-margin-top10">
        <image class="input-icon" src="@/static/images/login/login-username.png"></image>
        <uni-easyinput
          class="input"
          placeholder="请输入用户名"
          :clearable="true"
          placeholderStyle="color:#CCCCCC"
          border="none"
          v-model="loginForm.loginName"
        />
      </view>

      <view class="input-view smart-margin-top10" v-if="emailCodeShowFlag">
        <image class="input-icon" src="@/static/images/login/login-password.png"></image>
        <uni-easyinput
          class="input"
          placeholder="请输入邮箱验证码"
          :clearable="true"
          placeholderStyle="color:#CCCCCC"
          border="none"
          v-model="loginForm.emailCode"
        />
        <button @click="sendSmsCode" class="code-btn" :disabled="emailCodeButtonDisabled">
              {{ emailCodeTips }}
        </button>
      </view>

      <view class="input-view smart-margin-top10">
        <image class="input-icon" src="@/static/images/login/login-password.png"></image>
        <uni-easyinput
          class="input"
          placeholder="请输入密码"
          :clearable="true"
          :password="true"
          placeholderStyle="color:#CCCCCC"
          border="none"
          v-model="loginForm.password"
        />
      </view>

      <view class="input-view smart-margin-top10">
        <image class="input-icon" src="@/static/images/login/login-password.png"></image>
        <uni-easyinput
          class="input captcha-input"
          placeholder="请输入验证码"
          :clearable="true"
          :password="false"
          placeholderStyle="color:#CCCCCC"
          border="none"
          v-model="loginForm.captchaCode"
        />
        <image class="captcha-img" :src="captchaImageSrc" mode="aspectFit" @click="getCaptcha" />
      </view>

      <view class="code-login-view smart-margin-top10">
        <!-- <text class="code-text">验证码登录</text> -->
        <text class="forget-text">忘记密码？</text>
      </view>

      <view @click="login" class="button login-btn smart-margin-top20"> 登录 </view>
      <!-- <view @click="login" class="button register-btn smart-margin-top20"> 创建账号 </view> -->
      <!-- <OtherWayBox /> -->
      <LoginCheckBox class="login-check-box" ref="loginCheckBoxRef" />
    </view>
  </view>
</template>
<script setup>
  import { reactive, ref } from 'vue';
  import { onShow } from '@dcloudio/uni-app';
  // import OtherWayBox from './components/other-way-box.vue';
  import LoginCheckBox from './components/login-check-box.vue';
  import { loginApi } from '@/api/system/login-api';
  import { LOGIN_DEVICE_ENUM } from '@/constants/system/login-device-const';
  import { LAST_LOGIN_FORM } from '@/constants/local-storage-key-const';
  import { encryptData } from '@/lib/encrypt';
  import { useUserStore } from '@/store/modules/system/user';
  import { smartSentry } from '@/lib/smart-sentry';

  function getLoginDevice() {
    let loginDevice = LOGIN_DEVICE_ENUM.H5.value;

    // #ifdef APP-PLUS
    const systemInfo = uni.getSystemInfoSync();
    loginDevice = systemInfo.platform === 'ios' ? LOGIN_DEVICE_ENUM.APPLE.value : LOGIN_DEVICE_ENUM.ANDROID.value;
    // #endif

    // #ifdef MP
    loginDevice = LOGIN_DEVICE_ENUM.WEIXIN_MP.value;
    // #endif

    return loginDevice;
  }

  const loginForm = reactive({
    loginName: '',
    password: '',
    captchaCode: '',
    captchaUuid: '',
    loginDevice: getLoginDevice(),
  });

  const loginCheckBoxRef = ref();
  const loginInfoChecking = ref(false);

  async function redirectToHomeIfLoggedIn() {
    if (loginInfoChecking.value) {
      return;
    }

    const userStore = useUserStore();
    if (!userStore.getToken) {
      await initLoginPage();
      return;
    }

    try {
      loginInfoChecking.value = true;
      const loginInfoReady = await userStore.getLoginInfo();
      if (loginInfoReady) {
        stopRefreshCaptchaInterval();
        uni.switchTab({ url: '/pages/home/index' });
        return;
      }
      await initLoginPage();
    } finally {
      loginInfoChecking.value = false;
    }
  }

  async function initLoginPage() {
    loadLastLoginForm();
    await Promise.all([getCaptcha(), getTwoFactorLoginFlag()]);
  }

  function loadLastLoginForm() {
    try {
      const lastLoginForm = uni.getStorageSync(LAST_LOGIN_FORM);
      if (!lastLoginForm) {
        return;
      }
      loginForm.loginName = lastLoginForm.loginName || '';
      loginForm.password = lastLoginForm.password || '';
    } catch (e) {
      smartSentry.captureError(e);
    }
  }

  function saveLastLoginForm() {
    uni.setStorageSync(LAST_LOGIN_FORM, {
      loginName: loginForm.loginName,
      password: loginForm.password,
    });
  }

  async function login() {
    if (!loginCheckBoxRef.value.agreeFlag) {
      uni.showToast({
        icon: 'none',
        title: '请阅读并同意《用户协议》、《隐私政策》',
      });
      return;
    }
    if (!loginForm.loginName) {
      uni.showToast({
        icon: 'none',
        title: '请输入用户名',
      });
      return;
    }
    if (!loginForm.password) {
      uni.showToast({
        icon: 'none',
        title: '请输入密码',
      });
      return;
    }

    try {
      uni.showLoading({ title: '登录中' });
      // 密码加密
      loginForm.loginDevice = getLoginDevice();
      let encryptPasswordForm = Object.assign({}, loginForm, {
        password: encryptData(loginForm.password),
      });
      const res = await loginApi.login(encryptPasswordForm);
      stopRefreshCaptchaInterval();
      uni.showToast({ title: '登录成功' });
      saveLastLoginForm();
      //更新用户信息到 pinia
      const userStore = useUserStore();
      userStore.setUserLoginInfo(res.data);

      const loginInfoReady = await userStore.getLoginInfo();
      if (!loginInfoReady) {
        uni.showToast({
          icon: 'none',
          title: '登录态校验失败，请检查服务器域名和请求头',
        });
        await getCaptcha();
        return;
      }

      uni.switchTab({ url: '/pages/home/index' });
    } catch (e) {
      if (e.data && e.data.code !== 0) {
        loginForm.captchaCode = '';
        getCaptcha();
      }
      smartSentry.captureError(e);
    } finally {
      uni.hideLoading();
    }
  }

  //--------------------- 验证码 ---------------------------------

  const captchaImageSrc = ref('');

  async function getCaptcha() {
    try {
      let captchaResult = await loginApi.getCaptcha();
      loginForm.captchaUuid = captchaResult.data.captchaUuid;
      captchaImageSrc.value = await resolveCaptchaImageSrc(captchaResult.data.captchaBase64Image, captchaResult.data.captchaUuid);
      beginRefreshCaptchaInterval(captchaResult.data.expireSeconds);
    } catch (e) {
      smartSentry.captureError(e);
    }
  }

  async function resolveCaptchaImageSrc(captchaBase64Image, captchaUuid) {
    // #ifdef MP-WEIXIN
    try {
      return await writeCaptchaImageToLocalFile(captchaBase64Image, captchaUuid);
    } catch (e) {
      smartSentry.captureError(e);
      return captchaBase64Image;
    }
    // #endif

    // #ifndef MP-WEIXIN
    return captchaBase64Image;
    // #endif
  }

  // #ifdef MP-WEIXIN
  // 微信体验版对 data:image base64 的展示更严格，写成本地文件后再交给原生 image 渲染。
  function writeCaptchaImageToLocalFile(captchaBase64Image, captchaUuid) {
    const imageSource = (captchaBase64Image || '').trim();
    if (!imageSource || /^(https?:|wxfile:|\/)/.test(imageSource)) {
      return imageSource;
    }

    const pureBase64 = getPureImageBase64(imageSource);
    const arrayBuffer = wx.base64ToArrayBuffer(pureBase64);
    const extension = getImageExtension(arrayBuffer);
    const filePath = `${wx.env.USER_DATA_PATH}/captcha-${captchaUuid || Date.now()}.${extension}`;

    return new Promise((resolve, reject) => {
      wx.getFileSystemManager().writeFile({
        filePath,
        data: pureBase64,
        encoding: 'base64',
        success: () => resolve(filePath),
        fail: reject,
      });
    });
  }

  function getPureImageBase64(imageSource) {
    const match = /^data:image\/[a-zA-Z0-9.+-]+;base64,(.+)$/i.exec(imageSource);
    return (match ? match[1] : imageSource).replace(/[\r\n\s]/g, '');
  }

  function getImageExtension(arrayBuffer) {
    const bytes = new Uint8Array(arrayBuffer);
    if (bytes[0] === 0xff && bytes[1] === 0xd8 && bytes[2] === 0xff) {
      return 'jpg';
    }
    if (bytes[0] === 0x89 && bytes[1] === 0x50 && bytes[2] === 0x4e && bytes[3] === 0x47) {
      return 'png';
    }
    return 'jpg';
  }
  // #endif

  let refreshCaptchaInterval = null;

  function beginRefreshCaptchaInterval(expireSeconds) {
    if (refreshCaptchaInterval === null) {
      refreshCaptchaInterval = setInterval(getCaptcha, (expireSeconds - 5) * 1000);
    }
  }

  function stopRefreshCaptchaInterval() {
    if (refreshCaptchaInterval != null) {
      clearInterval(refreshCaptchaInterval);
      refreshCaptchaInterval = null;
    }
  }

  const emailCodeShowFlag = ref(false);
  let emailCodeTips = ref('获取邮箱验证码');
  let emailCodeButtonDisabled = ref(false);
  // 定时器
  let countDownTimer = null;
  // 开始倒计时
  function runCountDown() {
    emailCodeButtonDisabled.value = true;
    let countDown = 60;
    emailCodeTips.value = `${countDown}秒后重新获取`;
    countDownTimer = setInterval(() => {
      if (countDown > 1) {
        countDown--;
        emailCodeTips.value = `${countDown}秒后重新获取`;
      } else {
        clearInterval(countDownTimer);
        emailCodeButtonDisabled.value = false;
        emailCodeTips.value = '获取验证码';
      }
    }, 1000);
  }

  // 获取双因子登录标识
  async function getTwoFactorLoginFlag() {
    try {
      let result = await loginApi.getTwoFactorLoginFlag();
      emailCodeShowFlag.value = result.data;
    } catch (e) {
      smartSentry.captureError(e);
    }
  }
  // 发送邮箱验证码
  async function sendSmsCode() {
    try {
      uni.showLoading();
      await loginApi.sendLoginEmailCode(loginForm.loginName);
      uni.showToast({ title: '验证码发送成功!请登录邮箱查看验证码~', icon: 'none' });
      runCountDown();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      uni.hideLoading();
    }
  }
  onShow(() => {
    redirectToHomeIfLoggedIn();
  });
</script>
<style lang="scss" scoped>
  .bottom-view {
    position: relative;
    z-index: 2;
    flex-shrink: 0;
    box-sizing: border-box;
    margin-top: -280rpx;
    border-radius: 20rpx 20rpx 0 0;
    width: 100%;
    background-color: white;
    padding: 0 60rpx;
    .input-view {
      display: flex;
      flex-direction: row;
      align-items: center;
      background-color: $page-bg-color;
      border-radius: 4px;
      height: 100rpx;
      .input-icon {
        flex-shrink: 0;
        margin-left: 30rpx;
        width: 44rpx;
        height: 44rpx;
      }
      .input {
        flex: 1;
        min-width: 0;
        margin: 0 16rpx;
        background-color: $page-bg-color;
      }
      .captcha-img {
        flex-shrink: 0;
        margin-left: 5px;
        height: 100rpx;
        width: 40%;
      }
      .captcha-input {
        flex: 1;
        width: auto;
      }
    }
    .code-login-view {
      margin: 50rpx 0 0;
      height: 40rpx;
      display: flex;
      flex-direction: row;
      align-items: center;
      justify-content: space-between;
      .code-text {
        height: 40rpx;
        font-size: $main-size;
        font-weight: 400;
        text-align: left;
        color: $main-font-color;
      }
      .forget-text {
        height: 40rpx;
        font-size: $main-size;
        font-weight: 400;
        text-align: right;
        color: $second-font-color;
      }
    }
  }
  .button {
    flex-shrink: 0;
    width: 100%;
    height: 90rpx;
    border-radius: 4px;
    box-shadow: 0px 5px 8px 0px rgba(58, 121, 255, 0.2);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: $main-size;

    &.disabled {
      opacity: 0.4;
    }
    &.login-btn {
      background: $main-color;
      color: #ffffff;
    }

    &.register-btn {
      background: white;
      color: $main-color;
      border: 0.5px solid $main-color;
      border-color: rgba(26, 154, 255, 0.3);
    }
  }

  .logo {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: row;
    height: 220rpx;

    image {
      width: 208rpx;
      height: 220rpx;
    }
  }

  ::v-deep .uni-easyinput__content {
    background-color: transparent !important;
  }
  ::v-deep .is-input-border {
    border: none;
  }
  .container {
    display: flex;
    align-items: center;
    flex-direction: column;
    min-height: 100vh;
    width: 100vw;
    .back-icon {
      width: 18px;
      height: 18px;
    }
    .top-view {
      position: relative;
      z-index: 1;
      flex-shrink: 0;
      display: flex;
      flex-direction: column;
      align-items: center;
      width: 100%;
      height: 720rpx;
      overflow: hidden;
      .top-bg {
        position: absolute;
        z-index: 0;
        display: block;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        margin: 0;
        pointer-events: none;
      }
      .login {
        position: relative;
        z-index: 1;
        font-weight: bold;
        margin-top: 70rpx;
      }
      .logo {
        position: relative;
        z-index: 1;
        width: 260rpx;
        height: 260rpx;
      }
    }
  }

  .login-check-box {
    flex-shrink: 0;
    margin-top: 150rpx;
    margin-bottom: 120rpx;
    align-self: flex-start;
  }
  .code-btn{
    width: 240rpx;
    font-size: 24rpx;
    margin-right: 20rpx;
    background-color: $main-color;
    color: #fff;
  }
</style>
