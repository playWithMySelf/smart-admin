const DEV_API_PROXY_PREFIX = '/api';

export function getApiBaseUrl() {
  return import.meta.env.DEV ? DEV_API_PROXY_PREFIX : import.meta.env.VITE_APP_API_URL;
}
