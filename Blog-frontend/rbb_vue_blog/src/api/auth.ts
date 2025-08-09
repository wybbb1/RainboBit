/**
 * 用户认证相关API
 * 包含登录、注册、登出功能
 */

import { request } from '@/utils/request'
import type { 
  UserLoginDTO, 
  UserRegisterDTO,
  RegisterUserForm,
  LoginResponse, 
  User
} from '@/types'

/**
 * 用户登录
 * @param loginData 登录信息
 * @returns 登录响应数据
 */
export const login = (loginData: UserLoginDTO): Promise<LoginResponse> => {
  return request.post('/login', loginData)
}

/**
 * 发送邮箱验证码
 * @param email 邮箱地址
 * @returns 发送结果
 */
export const sendEmailCode = (email: string): Promise<any> => {
  return request.post(`/user/code?email=${encodeURIComponent(email)}`)
}

/**
 * 用户注册 - 新版本
 * @param registerData 注册表单数据
 * @returns 注册响应
 */
export const register = (registerData: RegisterUserForm): Promise<any> => {
  return request.post('/user/register', registerData)
}

/**
 * 用户注册 - 兼容旧版本
 * @param registerData 注册信息
 * @returns 注册响应
 */
export const registerLegacy = (registerData: UserRegisterDTO): Promise<any> => {
  return request.post('/user/register', registerData)
}

/**
 * 用户登出
 * @returns 登出响应
 */
export const logout = (): Promise<any> => {
  return request.post('/logout')
}

/**
 * 获取当前用户信息
 * @returns 用户信息
 */
export const getCurrentUser = (): Promise<User> => {
  return request.get('/user/userInfo')
}

/**
 * 认证API对象
 * 统一导出所有认证相关接口
 */
export const authApi = {
  login,
  register,
  registerLegacy,
  sendEmailCode,
  logout,
  getCurrentUser
}

export default authApi
