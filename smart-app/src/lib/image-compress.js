import { smartSentry } from '@/lib/smart-sentry';

const DEFAULT_MIN_SIZE_KB = 800;
const DEFAULT_QUALITY = 80;
const DEFAULT_MAX_WIDTH_OR_HEIGHT = 1600;
const IMAGE_SUFFIX_REGEXP = /\.(jpe?g|png|webp|bmp)$/i;

function isImagePath(filePath) {
  return typeof filePath === 'string' && IMAGE_SUFFIX_REGEXP.test(filePath.split('?')[0]);
}

function getFileInfo(filePath) {
  return new Promise((resolve) => {
    if (typeof uni.getFileInfo !== 'function') {
      resolve({});
      return;
    }
    uni.getFileInfo({
      filePath,
      success: resolve,
      fail: () => resolve({}),
    });
  });
}

function getImageInfo(filePath) {
  return new Promise((resolve) => {
    if (typeof uni.getImageInfo !== 'function') {
      resolve({});
      return;
    }
    uni.getImageInfo({
      src: filePath,
      success: resolve,
      fail: () => resolve({}),
    });
  });
}

function compressImage(filePath, options) {
  return new Promise((resolve, reject) => {
    uni.compressImage({
      src: filePath,
      quality: options.quality,
      compressedWidth: options.compressedWidth,
      compressedHeight: options.compressedHeight,
      success: resolve,
      fail: reject,
    });
  });
}

function buildResizeOptions(imageInfo, maxWidthOrHeight) {
  const width = Number(imageInfo.width);
  const height = Number(imageInfo.height);
  if (!width || !height || Math.max(width, height) <= maxWidthOrHeight) {
    return {};
  }

  const ratio = maxWidthOrHeight / Math.max(width, height);
  return {
    compressedWidth: Math.round(width * ratio),
    compressedHeight: Math.round(height * ratio),
  };
}

export async function compressImagePathBeforeUpload(filePath, options = {}) {
  if (!isImagePath(filePath) || typeof uni.compressImage !== 'function') {
    return filePath;
  }

  try {
    const minSizeKb = options.minSizeKb ?? DEFAULT_MIN_SIZE_KB;
    const fileInfo = await getFileInfo(filePath);
    if (fileInfo.size && fileInfo.size <= minSizeKb * 1024) {
      return filePath;
    }

    const maxWidthOrHeight = options.maxWidthOrHeight ?? DEFAULT_MAX_WIDTH_OR_HEIGHT;
    const imageInfo = await getImageInfo(filePath);
    const compressResult = await compressImage(filePath, {
      quality: options.quality ?? DEFAULT_QUALITY,
      ...buildResizeOptions(imageInfo, maxWidthOrHeight),
    });

    return compressResult.tempFilePath || filePath;
  } catch (err) {
    smartSentry.captureError(err);
    return filePath;
  }
}
