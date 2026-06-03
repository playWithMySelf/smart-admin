<!--
  * 文件上传
  *
  * @Author: 善逸
  * @Date:      2022-08-12 20:19:39
  * @Wechat:    zhuda1024
  * @Email:     lab1024@163.com
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
  *
-->
<template>
  <div class="clearfix">
    <a-upload
      :multiple="props.multiple"
      :accept="props.accept"
      :before-upload="beforeUpload"
      :customRequest="customRequest"
      :file-list="fileList"
      :headers="{ Authorization: 'Bearer ' + useUserStore().getToken }"
      :list-type="listType"
      @change="handleChange"
      @preview="handlePreview"
      @remove="handleRemove"
    >
      <div v-if="props.showUploadBtn && fileList.length < props.maxUploadSize">
        <template v-if="listType === 'picture-card'">
          <PlusOutlined />
          <div class="ant-upload-text">
            {{ buttonText }}
          </div>
        </template>
        <template v-if="listType === 'text'">
          <a-button>
            <upload-outlined />
            {{ buttonText }}
          </a-button>
        </template>
      </div>
    </a-upload>
    <a-modal :footer="null" :open="previewVisible" @cancel="handleCancel">
      <img :src="previewUrl" alt="example" style="width: 100%" />
    </a-modal>
  </div>
</template>
<script setup lang="ts">
  import { computed, ref, watch } from 'vue';
  import type { PropType } from 'vue';
  import { Modal } from 'ant-design-vue';
  import { fileApi } from '/@/api/support/file-api';
  import { useUserStore } from '/@/store/modules/system/user';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { FILE_FOLDER_TYPE_ENUM } from '/@/constants/support/file-const';
  import { smartSentry } from '/@/lib/smart-sentry';
  import { compressImageFileBeforeUpload, isCompressibleImageFile } from '/@/lib/image-compress';

  type UploadFileRecord = Record<string, any>;

  const props = defineProps({
    value: String,
    buttonText: {
      type: String,
      default: '点击上传附件',
    },
    showUploadBtn: {
      type: Boolean,
      default: true,
    },
    defaultFileList: {
      type: Array as PropType<UploadFileRecord[]>,
      default: () => [],
    },
    multiple: {
      type: Boolean,
      default: false,
    },
    // 最多上传文件数量
    maxUploadSize: {
      type: Number,
      default: 10,
    },
    maxSize: {
      type: Number,
      default: 10,
    },
    // 是否上传前压缩图片
    compressImage: {
      type: Boolean,
      default: false,
    },
    // 开启压缩后允许选择的原图大小
    maxOriginalImageSize: {
      type: Number,
      default: 30,
    },
    compressMinSizeKb: {
      type: Number,
      default: 800,
    },
    compressMaxSizeMb: {
      type: Number,
      default: 0.8,
    },
    compressMaxWidthOrHeight: {
      type: Number,
      default: 1600,
    },
    // 上传的文件类型
    accept: {
      type: String,
      default: '',
    },
    // 文件上传类型
    folder: {
      type: Number,
      default: FILE_FOLDER_TYPE_ENUM.COMMON.value,
    },
    // 上传列表的内建样式，支持三种基本样式 text, picture 和 picture-card
    listType: {
      type: String,
      default: 'picture-card',
    },
  });

  // 图片类型的后缀名
  const imgFileType = ['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp'];

  // 重新修改图片展示字段
  const files = computed(() => {
    let res: UploadFileRecord[] = [];
    if (props.defaultFileList && props.defaultFileList.length > 0) {
      props.defaultFileList.forEach((element) => {
        element.url = element.fileUrl;
        element.name = element.fileName;
        res.push(element);
      });
      return res;
    }
    return res;
  });
  // -------------------- 逻辑 --------------------

  const previewVisible = ref(false);
  const fileList = ref<UploadFileRecord[]>([]);
  const previewUrl = ref('');

  watch(
    files,
    (value) => {
      fileList.value = value;
    },
    {
      immediate: true,
    }
  );

  const emit = defineEmits(['update:value', 'change']);
  const customRequest = async (options: UploadFileRecord) => {
    SmartLoading.show();
    try {
      const uploadFile = await buildUploadFile(options.file);
      if (!checkUploadFileSize(uploadFile)) {
        return;
      }
      const formData = new FormData();
      formData.append('file', uploadFile);
      let res = await fileApi.uploadFile(formData, props.folder);
      let file = res.data;
      file.url = file.fileUrl;
      file.name = file.fileName;
      fileList.value.push(file);
      emit('change', fileList.value);
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  };

  async function buildUploadFile(file: File) {
    if (!props.compressImage) {
      return file;
    }

    return compressImageFileBeforeUpload(file, {
      minSizeKb: props.compressMinSizeKb,
      maxSizeMB: props.compressMaxSizeMb,
      maxWidthOrHeight: props.compressMaxWidthOrHeight,
    });
  }

  function checkUploadFileSize(file: File) {
    const isLimitSize = file.size / 1024 / 1024 < props.maxSize;
    if (!isLimitSize) {
      showErrorMsgOnce(`单个文件大小必须小于 ${props.maxSize} Mb`);
    }
    return isLimitSize;
  }

  function handleChange(info: UploadFileRecord) {
    let fileStatus = info.file.status;
    let file = info.file;
    if (fileStatus === 'removed') {
      let index = fileList.value.findIndex((e) => e.fileId === file.fileId);
      if (index !== -1) {
        fileList.value.splice(index, 1);
        emit('change', fileList.value);
      }
    }
  }

  function handleRemove(_file: UploadFileRecord) {
    return true;
  }

  function beforeUpload(file: UploadFileRecord, files: UploadFileRecord[]) {
    if (fileList.value.length + files.length > props.maxUploadSize) {
      showErrorMsgOnce(`最多支持上传 ${props.maxUploadSize} 个文件哦！`);
      return false;
    }

    if (props.accept) {
      const suffixIndex = file.name.lastIndexOf('.');
      const fileSuffix = file.name.substring(suffixIndex <= -1 ? 0 : suffixIndex);
      if (props.accept.indexOf(fileSuffix) === -1) {
        showErrorMsgOnce(`只支持上传 ${props.accept.replaceAll(',', ' ')} 格式的文件`);
        return false;
      }
    }

    if (props.compressImage && isCompressibleImageFile(file)) {
      const isOriginalImageLimitSize = file.size / 1024 / 1024 < props.maxOriginalImageSize;
      if (!isOriginalImageLimitSize) {
        showErrorMsgOnce(`单张原图大小必须小于 ${props.maxOriginalImageSize} Mb`);
      }
      return isOriginalImageLimitSize;
    }

    return checkUploadFileSize(file);
  }

  const showErrorModalFlag = ref(true);
  const showErrorMsgOnce = (content: string) => {
    if (showErrorModalFlag.value) {
      Modal.error({
        title: '提示',
        content: content,
        okType: 'danger',
        centered: true,
        onOk() {
          showErrorModalFlag.value = true;
        },
      });
      showErrorModalFlag.value = false;
    }
  };

  function handleCancel() {
    previewVisible.value = false;
  }

  const handlePreview = async (file: UploadFileRecord) => {
    if (isImageFile(file)) {
      previewUrl.value = file.url || file.fileUrl || file.preview;
      previewVisible.value = true;
    } else {
      fileApi.downLoadFile(file.fileKey);
    }
  };

  function isImageFile(file: UploadFileRecord) {
    const fileType = normalizeImageType(file.fileType);
    if (fileType && imgFileType.includes(fileType)) {
      return true;
    }

    const fileNameType = getFileSuffix(file.fileName || file.name);
    if (fileNameType && imgFileType.includes(fileNameType)) {
      return true;
    }

    const fileUrlType = getFileSuffix(file.fileUrl || file.url);
    return !!fileUrlType && imgFileType.includes(fileUrlType);
  }

  function normalizeImageType(fileType?: string) {
    return (fileType || '').toLowerCase().replace(/^image\//, '').replace(/^\./, '');
  }

  function getFileSuffix(fileName?: string) {
    const cleanFileName = (fileName || '').split('?')[0].toLowerCase();
    const suffixIndex = cleanFileName.lastIndexOf('.');
    return suffixIndex <= -1 ? '' : cleanFileName.substring(suffixIndex + 1);
  }

  // ------------------------ 清空 上传 ------------------------
  function clear() {
    fileList.value = [];
  }

  defineExpose({
    clear,
  });
</script>
<style lang="less" scoped>
  :deep(.ant-upload-picture-card-wrapper) {
    display: flex;
  }
</style>
