let appVisible = true;
let lastNotificationMessageId = null;
let notificationClickInited = false;
let plusReadyListenerInited = false;

export function setAppVisible(visible) {
  appVisible = !!visible;
}

export function isAppVisible() {
  return appVisible;
}

export function initMessageLocalNotification() {
  // #ifdef APP-PLUS
  if (notificationClickInited) {
    return;
  }
  if (typeof plus === 'undefined' || !plus.push) {
    bindPlusReady();
    return;
  }
  notificationClickInited = true;
  plus.push.addEventListener('click', (message) => {
    const payload = parsePayload(message && message.payload);
    if (!payload || payload.type !== 'SMART_MESSAGE') {
      return;
    }
    uni.switchTab({ url: '/pages/message/message' });
  });
  // #endif
}

export function showMessageLocalNotification(message) {
  if (appVisible || !message || !message.messageId || message.messageId === lastNotificationMessageId) {
    return;
  }

  // #ifdef APP-PLUS
  if (typeof plus === 'undefined' || !plus.push) {
    bindPlusReady();
    return;
  }

  const title = message.title || '消息通知';
  const content = buildNotificationContent(message);
  const payload = JSON.stringify({
    type: 'SMART_MESSAGE',
    messageId: message.messageId,
  });

  try {
    plus.push.createMessage(content, payload, {
      title,
      cover: false,
    });
    lastNotificationMessageId = message.messageId;
  } catch (e) {
    // 通知权限或运行端不支持时，不影响消息主流程
  }
  // #endif
}

function bindPlusReady() {
  if (plusReadyListenerInited || typeof document === 'undefined') {
    return;
  }
  plusReadyListenerInited = true;
  document.addEventListener('plusready', initMessageLocalNotification, false);
}

function buildNotificationContent(message) {
  const content = message.content ? String(message.content).replace(/<[^>]+>/g, '').trim() : '';
  if (!content) {
    return '你有一条新的未读消息';
  }
  return content.length > 80 ? `${content.slice(0, 80)}...` : content;
}

function parsePayload(payload) {
  if (!payload) {
    return null;
  }
  if (typeof payload === 'object') {
    return payload;
  }
  try {
    return JSON.parse(payload);
  } catch (e) {
    return null;
  }
}
