/**
 * 用户认证相关API
 * 包含登录、注册、登出功能
 */

import { request } from '@/utils/request'
import type { 
  UserLoginDTO, 
  UserRegisterDTO, 
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
 * 用户注册
 * @param registerData 注册信息
 * @returns 注册响应
 */
export const register = (registerData: UserRegisterDTO): Promise<any> => {
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
  logout,
  getCurrentUser
}

export default authApi
