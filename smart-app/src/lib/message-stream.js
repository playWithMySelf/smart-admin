import { USER_TOKEN } from '@/constants/local-storage-key-const';

export const MESSAGE_STREAM_EVENT = {
  REFRESH: 'message-refresh',
  AUTH_ERROR: 'message-stream-auth-error',
  ERROR: 'message-stream-error',
  UNSUPPORTED: 'message-stream-unsupported',
};

export const messageStreamEmitter = createEmitter();

const STREAM_RECONNECT_DELAY = 3000;

let streamRunning = false;
let reconnectTimer = null;
let requestTask = null;
let fetchAbortController = null;
let decoder = null;
let streamBuffer = '';
let unsupportedFlag = false;

export function startMessageStream() {
  stopMessageStream();

  const token = getUserToken();
  if (!token || unsupportedFlag) {
    return;
  }

  streamRunning = true;
  streamBuffer = '';
  connectMessageStream(token);
}

export function stopMessageStream() {
  streamRunning = false;
  clearReconnectTimer();
  abortFetchStream();
  abortUniRequest();
  resetDecoder();
  streamBuffer = '';
}

function connectMessageStream(token) {
  if (!streamRunning) {
    return;
  }

  if (canUseFetchStream()) {
    connectByFetch(token);
    return;
  }

  connectByUniRequest(token);
}

async function connectByFetch(token) {
  const controller = new AbortController();
  fetchAbortController = controller;
  streamBuffer = '';
  resetDecoder();

  try {
    const response = await fetch(buildStreamUrl(), {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
        Accept: 'text/event-stream',
      },
      signal: controller.signal,
    });

    const contentType = response.headers.get('content-type') || '';
    if (!response.ok || contentType.indexOf('text/event-stream') === -1) {
      const text = await response.text();
      handleBadResponse(response.status, text);
      return;
    }

    if (!response.body || typeof response.body.getReader !== 'function') {
      connectByUniRequest(token);
      return;
    }

    const reader = response.body.getReader();
    while (streamRunning && !controller.signal.aborted) {
      const result = await reader.read();
      if (result.done) {
        break;
      }
      consumeChunk(result.value, true);
    }

    consumeRemainBuffer();
    if (streamRunning && !controller.signal.aborted) {
      scheduleReconnect(token);
    }
  } catch (e) {
    if (!streamRunning || controller.signal.aborted) {
      return;
    }
    messageStreamEmitter.emit(MESSAGE_STREAM_EVENT.ERROR, e);
    scheduleReconnect(token);
  } finally {
    if (fetchAbortController === controller) {
      fetchAbortController = null;
    }
  }
}

function connectByUniRequest(token) {
  streamBuffer = '';
  resetDecoder();

  requestTask = uni.request({
    url: buildStreamUrl(),
    method: 'GET',
    responseType: 'arraybuffer',
    enableChunked: true,
    header: {
      Authorization: `Bearer ${token}`,
      Accept: 'text/event-stream',
    },
    success: (response) => {
      const statusCode = response.statusCode || 0;
      const contentType = getHeaderValue(response.header, 'content-type');
      if (statusCode < 200 || statusCode >= 300 || contentType.indexOf('text/event-stream') === -1) {
        handleBadResponse(statusCode, decodeChunk(response.data, false));
        return;
      }
      consumeRemainBuffer();
      if (streamRunning) {
        scheduleReconnect(token);
      }
    },
    fail: (e) => {
      if (!streamRunning) {
        return;
      }
      messageStreamEmitter.emit(MESSAGE_STREAM_EVENT.ERROR, e);
      scheduleReconnect(token);
    },
    complete: () => {
      requestTask = null;
    },
  });

  if (!requestTask || typeof requestTask.onChunkReceived !== 'function') {
    markUnsupported();
    abortUniRequest();
    return;
  }

  requestTask.onChunkReceived((result) => {
    consumeChunk(result.data, true);
  });
}

function consumeChunk(chunk, stream) {
  if (!chunk) {
    return;
  }
  streamBuffer += decodeChunk(chunk, stream).replace(/\r\n/g, '\n');
  streamBuffer = consumeSseBuffer(streamBuffer);
}

function consumeSseBuffer(buffer) {
  let frameEndIndex = buffer.indexOf('\n\n');
  while (frameEndIndex !== -1) {
    const frame = buffer.slice(0, frameEndIndex);
    handleSseFrame(frame);
    buffer = buffer.slice(frameEndIndex + 2);
    frameEndIndex = buffer.indexOf('\n\n');
  }
  return buffer;
}

function consumeRemainBuffer() {
  if (!streamBuffer.trim()) {
    return;
  }
  handleSseFrame(streamBuffer);
  streamBuffer = '';
}

function handleSseFrame(frame) {
  if (!frame) {
    return;
  }

  let eventName = 'message';
  const dataLines = [];
  frame.split('\n').forEach((line) => {
    if (!line || line.indexOf(':') === 0) {
      return;
    }
    if (line.indexOf('event:') === 0) {
      eventName = line.slice(6).trim();
      return;
    }
    if (line.indexOf('data:') === 0) {
      dataLines.push(line.slice(5).trimStart());
    }
  });

  if (eventName === 'refresh') {
    messageStreamEmitter.emit(MESSAGE_STREAM_EVENT.REFRESH, dataLines.join('\n'));
  }
}

function handleBadResponse(statusCode, responseText) {
  if (isAuthErrorResponse(statusCode, responseText)) {
    messageStreamEmitter.emit(MESSAGE_STREAM_EVENT.AUTH_ERROR);
    stopMessageStream();
    return;
  }

  const error = new Error(responseText || `消息推送连接失败，HTTP ${statusCode}`);
  messageStreamEmitter.emit(MESSAGE_STREAM_EVENT.ERROR, error);
  scheduleReconnect(getUserToken());
}

function scheduleReconnect(token) {
  if (!streamRunning || !token || unsupportedFlag) {
    return;
  }
  clearReconnectTimer();
  reconnectTimer = setTimeout(() => {
    reconnectTimer = null;
    connectMessageStream(token);
  }, STREAM_RECONNECT_DELAY);
}

function markUnsupported() {
  unsupportedFlag = true;
  streamRunning = false;
  clearReconnectTimer();
  messageStreamEmitter.emit(MESSAGE_STREAM_EVENT.UNSUPPORTED);
}

function canUseFetchStream() {
  return typeof fetch === 'function' && typeof ReadableStream !== 'undefined' && typeof AbortController !== 'undefined';
}

function buildStreamUrl() {
  return `${getBaseUrl()}/support/message/stream`;
}

function getBaseUrl() {
  return (import.meta.env.VITE_APP_API_URL || '').replace(/\/$/, '');
}

function getUserToken() {
  return uni.getStorageSync(USER_TOKEN) || '';
}

function decodeChunk(chunk, stream) {
  if (chunk == null) {
    return '';
  }
  if (typeof chunk === 'string') {
    return chunk;
  }
  if (typeof TextDecoder !== 'undefined') {
    if (!decoder) {
      decoder = new TextDecoder('utf-8');
    }
    return decoder.decode(chunk, { stream });
  }

  const bytes = new Uint8Array(chunk);
  let text = '';
  for (let i = 0; i < bytes.length; i++) {
    text += String.fromCharCode(bytes[i]);
  }
  try {
    return decodeURIComponent(escape(text));
  } catch (e) {
    return text;
  }
}

function resetDecoder() {
  decoder = null;
}

function getHeaderValue(header, name) {
  if (!header) {
    return '';
  }
  const targetName = name.toLowerCase();
  const foundKey = Object.keys(header).find((key) => key.toLowerCase() === targetName);
  return foundKey ? String(header[foundKey]).toLowerCase() : '';
}

function isAuthErrorResponse(statusCode, responseText) {
  if (statusCode === 401 || statusCode === 403) {
    return true;
  }
  if (!responseText) {
    return false;
  }
  try {
    const responseJson = JSON.parse(responseText);
    return [30007, 30008, 30012].includes(responseJson && responseJson.code);
  } catch (e) {
    return false;
  }
}

function clearReconnectTimer() {
  if (reconnectTimer) {
    clearTimeout(reconnectTimer);
    reconnectTimer = null;
  }
}

function abortFetchStream() {
  if (fetchAbortController) {
    fetchAbortController.abort();
    fetchAbortController = null;
  }
}

function abortUniRequest() {
  if (requestTask && typeof requestTask.abort === 'function') {
    requestTask.abort();
  }
  requestTask = null;
}

function createEmitter() {
  const eventMap = {};
  return {
    on(eventName, handler) {
      if (!eventMap[eventName]) {
        eventMap[eventName] = [];
      }
      eventMap[eventName].push(handler);
    },
    off(eventName, handler) {
      if (!eventMap[eventName]) {
        return;
      }
      eventMap[eventName] = eventMap[eventName].filter((item) => item !== handler);
    },
    emit(eventName, payload) {
      if (!eventMap[eventName]) {
        return;
      }
      eventMap[eventName].forEach((handler) => handler(payload));
    },
  };
}
