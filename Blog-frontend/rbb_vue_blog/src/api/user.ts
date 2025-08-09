/**
 * 用户信息管理相关API
 * 包含用户信息查询、更新等功能
 */

import { request } from '@/utils/request'
import type { User } from '@/types'

/**
 * 更新用户信息
 * @param userData 用户信息
 * @returns 更新响应
 */
export const updateProfile = (userData: Partial<User>): Promise<any> => {
  return request.put('/user/userInfo', userData)
}

/**
 * 更新用户头像
 * @param avatarUrl 头像URL
 * @returns 更新响应
 */
export const updateAvatar = (avatarUrl: string): Promise<any> => {
  return request.put('/user/avatar', { avatar: avatarUrl })
}

/**
 * 修改密码
 * @param passwords 密码信息
 * @returns 修改响应
 */
export const changePassword = (passwords: {
  oldPassword: string
  newPassword: string
}): Promise<any> => {
  return request.put('/user/password', passwords)
}

/**
 * 用户管理API对象
 */
export const userApi = {
  updateProfile,
  updateAvatar,
  changePassword
}

export default userApi
