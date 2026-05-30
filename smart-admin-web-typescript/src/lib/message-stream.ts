import mitt from 'mitt';
import { localRead } from '/@/utils/local-util';
import LocalStorageKeyConst from '/@/constants/local-storage-key-const';

export const MESSAGE_STREAM_EVENT = {
  REFRESH: 'message-refresh',
  AUTH_ERROR: 'message-stream-auth-error',
  ERROR: 'message-stream-error',
} as const;

export const messageStreamEmitter = mitt();

const STREAM_RECONNECT_DELAY = 3000;

let streamAbortController: AbortController | null = null;
let reconnectTimer: ReturnType<typeof setTimeout> | null = null;
let streamRunning = false;

export function startMessageStream() {
  stopMessageStream();
  const token = localRead(LocalStorageKeyConst.USER_TOKEN);
  if (!token) {
    return;
  }

  streamRunning = true;
  void connectMessageStream(token);
}

export function stopMessageStream() {
  streamRunning = false;
  if (reconnectTimer) {
    clearTimeout(reconnectTimer);
    reconnectTimer = null;
  }
  if (streamAbortController) {
    streamAbortController.abort();
    streamAbortController = null;
  }
}

async function connectMessageStream(token: string) {
  if (!streamRunning) {
    return;
  }

  const controller = new AbortController();
  streamAbortController = controller;
  const requestUrl = buildStreamUrl();

  try {
    const response = await fetch(requestUrl, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
        Accept: 'text/event-stream',
      },
      credentials: 'include',
      signal: controller.signal,
    });

    const contentType = response.headers.get('content-type') || '';
    if (!response.ok || !contentType.includes('text/event-stream')) {
      const text = await response.text();
      if (isAuthErrorResponse(response.status, text)) {
        messageStreamEmitter.emit(MESSAGE_STREAM_EVENT.AUTH_ERROR);
        stopMessageStream();
        return;
      }
      throw new Error(text || `消息推送连接失败，HTTP ${response.status}`);
    }

    if (!response.body) {
      throw new Error('当前浏览器不支持流式响应');
    }

    const reader = response.body.getReader();
    const decoder = new TextDecoder('utf-8');
    let buffer = '';

    while (streamRunning && !controller.signal.aborted) {
      const { value, done } = await reader.read();
      if (done) {
        break;
      }
      buffer += decoder.decode(value, { stream: true }).replace(/\r\n/g, '\n');
      buffer = consumeSseBuffer(buffer);
    }

    if (buffer.trim()) {
      handleSseFrame(buffer);
    }

    if (streamRunning && !controller.signal.aborted) {
      scheduleReconnect(token);
    }
  } catch (error) {
    if (!streamRunning || controller.signal.aborted) {
      return;
    }

    messageStreamEmitter.emit(MESSAGE_STREAM_EVENT.ERROR, error);
    scheduleReconnect(token);
  }
}

function scheduleReconnect(token: string) {
  if (!streamRunning) {
    return;
  }
  if (reconnectTimer) {
    clearTimeout(reconnectTimer);
  }
  reconnectTimer = setTimeout(() => {
    reconnectTimer = null;
    void connectMessageStream(token);
  }, STREAM_RECONNECT_DELAY);
}

function consumeSseBuffer(buffer: string) {
  let frameEndIndex = buffer.indexOf('\n\n');
  while (frameEndIndex !== -1) {
    const frame = buffer.slice(0, frameEndIndex);
    handleSseFrame(frame);
    buffer = buffer.slice(frameEndIndex + 2);
    frameEndIndex = buffer.indexOf('\n\n');
  }
  return buffer;
}

function handleSseFrame(frame: string) {
  if (!frame) {
    return;
  }

  let eventName = 'message';
  const dataLines: string[] = [];

  frame.split('\n').forEach((line) => {
    if (!line || line.startsWith(':')) {
      return;
    }
    if (line.startsWith('event:')) {
      eventName = line.slice(6).trim();
      return;
    }
    if (line.startsWith('data:')) {
      dataLines.push(line.slice(5).trimStart());
    }
  });

  if (eventName === 'refresh') {
    messageStreamEmitter.emit(MESSAGE_STREAM_EVENT.REFRESH, dataLines.join('\n'));
  }
}

function isAuthErrorResponse(status: number, responseText: string) {
  if (status === 401 || status === 403) {
    return true;
  }
  if (!responseText) {
    return false;
  }
  try {
    const responseJson = JSON.parse(responseText);
    return [30007, 30008, 30012].includes(responseJson?.code);
  } catch (error) {
    return false;
  }
}

function buildStreamUrl() {
  const baseUrl = (import.meta.env.VITE_APP_API_URL || '').replace(/\/$/, '');
  return `${baseUrl}/support/message/stream`;
}
