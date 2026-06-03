import imageCompression from 'browser-image-compression';
import { smartSentry } from './smart-sentry';

const DEFAULT_MIN_SIZE_KB = 800;
const DEFAULT_MAX_SIZE_MB = 0.8;
const DEFAULT_MAX_WIDTH_OR_HEIGHT = 1600;
const DEFAULT_INITIAL_QUALITY = 0.82;
const COMPRESSIBLE_IMAGE_TYPES = ['image/jpeg', 'image/png', 'image/webp', 'image/bmp'];
const COMPRESSIBLE_IMAGE_SUFFIXES = ['.jpg', '.jpeg', '.png', '.webp', '.bmp'];

export interface ImageCompressOptions {
  minSizeKb?: number;
  maxSizeMB?: number;
  maxWidthOrHeight?: number;
  initialQuality?: number;
}

export function isCompressibleImageFile(file?: File) {
  if (!file) {
    return false;
  }

  const fileType = file.type?.toLowerCase();
  if (fileType && COMPRESSIBLE_IMAGE_TYPES.includes(fileType)) {
    return true;
  }

  const fileName = file.name?.toLowerCase() || '';
  return COMPRESSIBLE_IMAGE_SUFFIXES.some((suffix) => fileName.endsWith(suffix));
}

export async function compressImageFileBeforeUpload(file: File, options: ImageCompressOptions = {}): Promise<File> {
  if (!isCompressibleImageFile(file)) {
    return file;
  }

  const minSizeKb = options.minSizeKb ?? DEFAULT_MIN_SIZE_KB;
  if (file.size <= minSizeKb * 1024) {
    return file;
  }

  try {
    const compressedFile = await imageCompression(file, {
      maxSizeMB: options.maxSizeMB ?? DEFAULT_MAX_SIZE_MB,
      maxWidthOrHeight: options.maxWidthOrHeight ?? DEFAULT_MAX_WIDTH_OR_HEIGHT,
      initialQuality: options.initialQuality ?? DEFAULT_INITIAL_QUALITY,
      useWebWorker: true,
      preserveExif: false,
    });

    if (compressedFile.size >= file.size) {
      return file;
    }

    return new File([compressedFile], file.name, {
      type: compressedFile.type || file.type,
      lastModified: file.lastModified,
    });
  } catch (e) {
    smartSentry.captureError(e);
    return file;
  }
}
