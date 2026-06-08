export function getFileDisplayUrl(file) {
  if (!file) {
    return '';
  }
  return file.fileUrl || file.url || file.tempFilePath || '';
}

export function getFileDisplayUrls(fileList) {
  return (fileList || []).map((file) => getFileDisplayUrl(file)).filter(Boolean);
}

export function normalizeFileForDisplay(file, tempFilePath) {
  const normalizedFile = { ...(file || {}) };
  const displayUrl = getFileDisplayUrl(normalizedFile) || tempFilePath || '';

  if (displayUrl) {
    normalizedFile.fileUrl = normalizedFile.fileUrl || displayUrl;
    normalizedFile.url = normalizedFile.url || displayUrl;
  }
  if (normalizedFile.fileName && !normalizedFile.name) {
    normalizedFile.name = normalizedFile.fileName;
  }
  if (tempFilePath) {
    normalizedFile.tempFilePath = tempFilePath;
  }

  return normalizedFile;
}
