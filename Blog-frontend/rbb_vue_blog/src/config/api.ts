/**
 * API配置文件
 * 管理不同环境下的API配置
 */

// API基础路径配置
export const API_CONFIG = {
  // 开发环境
  development: {
    baseURL: 'http://localhost:7777',
    timeout: 10000,
  },
  // 生产环境
  production: {
    baseURL: '/api',
    timeout: 10000,
  },
  // 测试环境
  test: {
    baseURL: 'http://test-api.example.com/api',
    timeout: 15000,
  }
}

// 获取当前环境配置
export const getCurrentConfig = () => {
  const env = import.meta.env.MODE || 'development'
  return API_CONFIG[env as keyof typeof API_CONFIG] || API_CONFIG.development
}

// HTTP状态码
export const HTTP_STATUS = {
  SUCCESS: 200,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  SERVER_ERROR: 500,
  BAD_GATEWAY: 502,
  SERVICE_UNAVAILABLE: 503,
} as const

// 业务状态码（对应后端ResponseResult的code字段）
export const BUSINESS_CODE = {
  SUCCESS: 200,
  SUCCESS_STR: '200', // 兼容字符串类型的成功码
  FAIL: 500,
  LOGIN_ERROR: 501,
  PERMISSION_ERROR: 502,
  NEED_LOGIN: 401,
  SYSTEM_ERROR: 500,
} as const

// 错误消息映射
export const ERROR_MESSAGES = {
  [HTTP_STATUS.UNAUTHORIZED]: '登录状态已过期，请重新登录',
  [HTTP_STATUS.FORBIDDEN]: '没有权限访问该资源',
  [HTTP_STATUS.NOT_FOUND]: '请求的资源不存在',
  [HTTP_STATUS.SERVER_ERROR]: '服务器内部错误',
  [HTTP_STATUS.BAD_GATEWAY]: '网关错误',
  [HTTP_STATUS.SERVICE_UNAVAILABLE]: '服务暂时不可用',
  'NETWORK_ERROR': '网络连接异常',
  'TIMEOUT': '请求超时',
  'DEFAULT': '请求失败，请稍后重试'
} as const
