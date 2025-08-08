import axios, { type AxiosResponse, type InternalAxiosRequestConfig, type CreateAxiosDefaults } from 'axios'
import { getCurrentConfig, HTTP_STATUS, BUSINESS_CODE, ERROR_MESSAGES } from '@/config/api'

// 定义响应数据结构，对应后端 ResponseResult
export interface ApiResponse<T = any> {
  code: number | string
  msg: string
  data: T
}

// 定义分页查询参数，对应后端 PageQuery
export interface PageQuery {
  page?: number
  pageSize?: number
}

// 定义分页结果，对应后端 PageResult
export interface PageResult<T> {
  total: number | string
  rows: T[]
}

// 默认配置
const apiConfig = getCurrentConfig()
const defaultConfig: CreateAxiosDefaults = {
  baseURL: apiConfig.baseURL,
  timeout: apiConfig.timeout,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
}

// 创建axios实例
const service = axios.create(defaultConfig)

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 可以在这里添加token等认证信息
    const token = localStorage.getItem('token')
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    return config
  },
  (error: any) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { code, msg, data } = response.data
    
    // 成功响应 (支持数字和字符串类型的状态码)
    if (code === BUSINESS_CODE.SUCCESS || code === BUSINESS_CODE.SUCCESS_STR) {
      return data
    }
    
    // 业务错误处理
    let errorMessage = msg || ERROR_MESSAGES.DEFAULT
    if (code === BUSINESS_CODE.NEED_LOGIN) {
      errorMessage = ERROR_MESSAGES[HTTP_STATUS.UNAUTHORIZED]
      // 可以在这里处理登录跳转
      // router.push('/login')
    }
    
    console.error('业务错误:', errorMessage)
    return Promise.reject(new Error(errorMessage))
  },
  (error: any) => {
    let message: string = ERROR_MESSAGES.DEFAULT
    
    if (error.response) {
      // 服务器响应错误
      const { status } = error.response
      const errorMsg = ERROR_MESSAGES[status as keyof typeof ERROR_MESSAGES]
      message = errorMsg || `连接错误${status}`
    } else if (error.request) {
      // 请求超时或网络错误
      if (error.code === 'ECONNABORTED') {
        message = ERROR_MESSAGES.TIMEOUT
      } else {
        message = ERROR_MESSAGES.NETWORK_ERROR
      }
    } else {
      // 其他错误
      message = error.message || ERROR_MESSAGES.DEFAULT
    }
    
    console.error('请求失败:', message)
    return Promise.reject(new Error(message))
  }
)

// 封装请求方法
export const request = {
  get<T = any>(url: string, params?: any): Promise<T> {
    return service.get(url, { params })
  },
  
  post<T = any>(url: string, data?: any): Promise<T> {
    return service.post(url, data)
  },
  
  put<T = any>(url: string, data?: any): Promise<T> {
    return service.put(url, data)
  },
  
  delete<T = any>(url: string): Promise<T> {
    return service.delete(url)
  }
}

export default service
