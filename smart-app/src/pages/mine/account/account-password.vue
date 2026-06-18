<template>
  <view class="container">
    <view class="smart-form">
      <uni-forms ref="formRef" :label-width="100" :modelValue="form" label-position="left" :rules="rules">
        <view class="smart-form-group">
          <view class="smart-form-group-title"> 修改密码 </view>
          <view class="smart-form-group-content">
            <uni-forms-item class="smart-form-item" label="原密码：" name="oldPassword" required>
              <uni-easyinput type="password" trim="all" v-model="form.oldPassword" placeholder="请输入原密码" />
            </uni-forms-item>
            <uni-forms-item class="smart-form-item" label="新密码：" name="newPassword" required>
              <uni-easyinput type="password" trim="all" v-model="form.newPassword" placeholder="请输入新密码" />
              <view class="password-tips">{{ tips }}</view>
            </uni-forms-item>
            <uni-forms-item class="smart-form-item" label="确认密码：" name="confirmPwd" required>
              <uni-easyinput type="password" trim="all" v-model="form.confirmPwd" placeholder="请输入确认密码" />
            </uni-forms-item>
          </view>
        </view>
      </uni-forms>

      <view class="smart-form-submit smart-margin-top20 bottom-button">
        <button class="smart-form-submit-btn smart-margin-right20" type="default" @click="cancel">取消</button>
        <button class="smart-form-submit-btn" type="primary" @click="submit">保存</button>
      </view>
    </view>
  </view>
</template>

<script setup>
  import { computed, reactive, ref } from 'vue';
  import { onLoad } from '@dcloudio/uni-app';
  import { employeeApi } from './api/employee-api';
  import { smartSentry } from '@/lib/smart-sentry';
  import { SmartLoading, SmartToast } from '@/lib/smart-support';

  // --------------------- 密码规则 ---------------------

  const passwordComplexityEnabledTips = '密码长度8-20位，必须包含字母、数字、特殊符号（如：@#$%^&*()_+-=）等三种字符';
  const passwordTips = '密码长度至少8位';
  const passwordComplexityReg =
    /^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\W_!@#$%^&*`~()-+=]+$)(?![a-z0-9]+$)(?![a-z\W_!@#$%^&*`~()-+=]+$)(?![0-9\W_!@#$%^&*`~()-+=]+$)[a-zA-Z0-9\W_!@#$%^&*`~()-+=]{8,20}$/;

  const passwordComplexityEnabledFlag = ref(false);
  const tips = computed(() => (passwordComplexityEnabledFlag.value ? passwordComplexityEnabledTips : passwordTips));

  async function getPasswordComplexityEnabled() {
    try {
      SmartLoading.show();
      let res = await employeeApi.getPasswordComplexityEnabled();
      passwordComplexityEnabledFlag.value = res.data;
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  onLoad(getPasswordComplexityEnabled);

  // --------------------- 表单 ---------------------

  const defaultFormData = {
    oldPassword: '',
    newPassword: '',
    confirmPwd: '',
  };
  let form = reactive({ ...defaultFormData });
  const formRef = ref();

  const passwordComplexityEnabledRules = {
    oldPassword: {
      rules: [{ required: true, errorMessage: '请输入原密码' }],
    },
    newPassword: {
      rules: [
        { required: true, errorMessage: '请输入新密码' },
        { pattern: passwordComplexityReg, errorMessage: '密码格式错误' },
      ],
    },
    confirmPwd: {
      rules: [
        { required: true, errorMessage: '请输入确认密码' },
        { pattern: passwordComplexityReg, errorMessage: '密码格式错误' },
      ],
    },
  };

  const commonRules = {
    oldPassword: {
      rules: [{ required: true, errorMessage: '请输入原密码' }],
    },
    newPassword: {
      rules: [
        { required: true, errorMessage: '请输入新密码' },
        { minLength: 8, errorMessage: '密码长度至少8位' },
      ],
    },
    confirmPwd: {
      rules: [
        { required: true, errorMessage: '请输入确认密码' },
        { minLength: 8, errorMessage: '密码长度至少8位' },
      ],
    },
  };

  const rules = computed(() => (passwordComplexityEnabledFlag.value ? passwordComplexityEnabledRules : commonRules));

  function resetForm() {
    Object.assign(form, defaultFormData);
    formRef.value.clearValidate();
  }

  // ----------------------- 表单操作 ------------------------

  function cancel() {
    uni.navigateBack();
  }

  function submit() {
    formRef.value
      .validate()
      .then(async () => {
        if (form.newPassword !== form.confirmPwd) {
          SmartToast.toast('新密码与确认密码不一致');
          return;
        }
        SmartLoading.show();
        try {
          await employeeApi.updateEmployeePassword({
            oldPassword: form.oldPassword,
            newPassword: form.newPassword,
          });
          SmartToast.success('修改成功');
          resetForm();
        } catch (error) {
          smartSentry.captureError(error);
        } finally {
          SmartLoading.hide();
        }
      })
      .catch(() => {
        SmartToast.toast('参数验证错误，请仔细填写表单数据!');
      });
  }
</script>

<style lang="scss" scoped>
  .container {
    min-height: 100vh;
    background: #f4f4f4;
  }

  .password-tips {
    margin-top: 8rpx;
    color: #999;
    font-size: 24rpx;
    line-height: 34rpx;
  }

  .bottom-button {
    position: fixed;
    bottom: 0;
  }
</style>
