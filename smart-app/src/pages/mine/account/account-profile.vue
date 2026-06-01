<template>
  <view class="container">
    <view class="smart-form">
      <uni-forms ref="formRef" :label-width="100" :modelValue="form" label-position="left" :rules="rules">
        <view class="smart-form-group">
          <view class="smart-form-group-title"> 基本信息 </view>
          <view class="smart-form-group-content">
            <uni-forms-item class="smart-form-item" label="登录账号：" name="loginName">
              <uni-easyinput trim="all" v-model="form.loginName" disabled />
            </uni-forms-item>
            <uni-forms-item class="smart-form-item" label="部门：" name="departmentName">
              <uni-easyinput trim="all" v-model="form.departmentName" disabled />
            </uni-forms-item>
            <uni-forms-item class="smart-form-item" label="姓名：" name="actualName" required>
              <uni-easyinput trim="all" v-model="form.actualName" placeholder="请输入姓名" />
            </uni-forms-item>
            <uni-forms-item class="smart-form-item" label="性别：" name="gender" required>
              <smart-enum-radio v-model="form.gender" enumName="GENDER_ENUM" @change="onGenderChange" />
            </uni-forms-item>
            <uni-forms-item class="smart-form-item" label="手机号：" name="phone" required>
              <uni-easyinput trim="all" v-model="form.phone" placeholder="请输入手机号" />
            </uni-forms-item>
            <uni-forms-item class="smart-form-item" label="邮箱：" name="email" required>
              <uni-easyinput trim="all" v-model="form.email" placeholder="请输入邮箱" />
            </uni-forms-item>
            <uni-forms-item class="smart-form-item" label="备注：" name="remark">
              <uni-easyinput type="textarea" trim="all" v-model="form.remark" placeholder="请输入备注" />
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
  import { reactive, ref } from 'vue';
  import { onLoad } from '@dcloudio/uni-app';
  import SmartEnumRadio from '@/components/smart-enum-radio/index.vue';
  import { employeeApi } from '@/api/system/employee-api';
  import { loginApi } from '@/api/system/login-api';
  import { regular } from '@/constants/regular-const';
  import { smartSentry } from '@/lib/smart-sentry';
  import { SmartLoading, SmartToast } from '@/lib/smart-support';
  import { useUserStore } from '@/store/modules/system/user';

  const emailReg = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;

  // --------------------- 表单 ---------------------

  const defaultFormData = {
    loginName: '',
    actualName: '',
    gender: 0,
    phone: '',
    departmentName: '',
    email: '',
    positionId: undefined,
    remark: '',
  };
  let form = reactive({ ...defaultFormData });

  const rules = {
    actualName: {
      rules: [
        { required: true, errorMessage: '请输入姓名' },
        { maxLength: 30, errorMessage: '姓名最多30字符' },
      ],
    },
    gender: {
      rules: [{ required: true, errorMessage: '请选择性别' }],
    },
    phone: {
      rules: [
        { required: true, errorMessage: '请输入手机号' },
        { pattern: regular.phone, errorMessage: '手机号格式不正确' },
      ],
    },
    email: {
      rules: [
        { required: true, errorMessage: '请输入邮箱' },
        { pattern: emailReg, errorMessage: '邮箱格式不正确' },
      ],
    },
    remark: {
      rules: [{ maxLength: 200, errorMessage: '备注最多200字符' }],
    },
  };

  const formRef = ref();

  function onGenderChange(value) {
    form.gender = Number(value);
  }

  function setFormData(data) {
    form.loginName = data.loginName || '';
    form.actualName = data.actualName || '';
    form.gender = data.gender ?? 0;
    form.phone = data.phone || '';
    form.departmentName = data.departmentName || '';
    form.email = data.email || '';
    form.positionId = data.positionId;
    form.remark = data.remark || '';
  }

  async function refreshLoginInfo(showLoading = true) {
    try {
      if (showLoading) {
        SmartLoading.show();
      }
      let res = await loginApi.getLoginInfo();
      useUserStore().setUserLoginInfo(res.data);
      setFormData(res.data);
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      if (showLoading) {
        SmartLoading.hide();
      }
    }
  }

  function buildUpdateParam() {
    return {
      actualName: form.actualName,
      gender: Number(form.gender),
      phone: form.phone,
      email: form.email,
      positionId: form.positionId,
      remark: form.remark,
    };
  }

  onLoad(() => {
    refreshLoginInfo();
  });

  // ----------------------- 表单操作 ------------------------

  function cancel() {
    uni.navigateBack();
  }

  function submit() {
    formRef.value
      .validate()
      .then(async () => {
        SmartLoading.show();
        try {
          await employeeApi.updateCenter(buildUpdateParam());
          SmartToast.success('更新成功');
          await refreshLoginInfo(false);
          uni.navigateBack();
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

  .bottom-button {
    position: fixed;
    bottom: 0;
  }
</style>
