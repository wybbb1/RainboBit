/**
 * 用户状态管理
 * 管理用户登录状态、用户信息等
 */

import { ref, computed, readonly } from 'vue'
import { defineStore } from 'pinia'
import { authApi } from '@/api'
import type { User, LoginUser, UserLoginDTO, UserRegisterDTO, RegisterUserForm } from '@/types'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<LoginUser | null>(null)
  const isLoggedIn = computed(() => !!token.value && !!userInfo.value)

  /**
   * 设置token
   */
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  /**
   * 设置用户信息
   */
  const setUserInfo = (info: LoginUser) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  /**
   * 清除用户信息
   */
  const clearUserInfo = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  /**
   * 初始化用户信息（从localStorage恢复）
   */
  const initUserInfo = () => {
    const savedUserInfo = localStorage.getItem('userInfo')
    if (savedUserInfo && token.value) {
      try {
        userInfo.value = JSON.parse(savedUserInfo)
      } catch (error) {
        console.error('解析用户信息失败:', error)
        clearUserInfo()
      }
    }
  }

  /**
   * 用户登录
   */
  const login = async (loginData: UserLoginDTO): Promise<void> => {
    try {
      const response = await authApi.login(loginData)
      
      if (!response.token) {
        throw new Error('登录失败：未获取到token')
      }
      
      setToken(response.token)
      
      // 登录成功后获取用户信息
      try {
        const user = await authApi.getCurrentUser()
        const loginUser: LoginUser = {
          user: user,
          permissions: []
        }
        setUserInfo(loginUser)
      } catch (userError) {
        // 如果获取用户信息失败，创建基本用户信息
        const basicUser: User = {
          id: Date.now(),
          userName: loginData.userName,
          nickName: loginData.userName,
          password: '',
          type: '0',
          status: '0',
          email: '',
          phonenumber: '',
          sex: '2',
          avatar: '',
          createBy: 0,
          createTime: new Date().toISOString(),
          updateBy: 0,
          updateTime: new Date().toISOString(),
          delFlag: 0
        }
        
        const loginUser: LoginUser = {
          user: basicUser,
          permissions: []
        }
        setUserInfo(loginUser)
      }
      
    } catch (error) {
      console.error('登录失败详细信息:', error)
      throw error
    }
  }

  /**
   * 用户注册
   */
  const register = async (registerData: UserRegisterDTO | RegisterUserForm): Promise<void> => {
    try {
      // 根据数据类型选择不同的注册接口
      if ('code' in registerData) {
        // 新的带验证码的注册
        await authApi.register(registerData as RegisterUserForm)
      } else {
        // 兼容旧的注册方式
        await authApi.registerLegacy(registerData as UserRegisterDTO)
      }
    } catch (error) {
      console.error('注册失败:', error)
      throw error
    }
  }

  /**
   * 用户登出
   */
  const logout = async (): Promise<void> => {
    try {
      await authApi.logout()
    } catch (error) {
      console.error('登出请求失败:', error)
    } finally {
      // 无论后端请求是否成功，都清除本地状态
      clearUserInfo()
    }
  }

  /**
   * 获取当前用户信息
   */
  const fetchCurrentUser = async (): Promise<User | null> => {
    try {
      if (!token.value) return null
      const user = await authApi.getCurrentUser()
      return user
    } catch (error: any) {
      console.error('获取用户信息失败:', error)
      // 如果是401错误，说明token已过期，清除本地状态
      if (error.message?.includes('登录状态已过期') || error.message?.includes('401')) {
        clearUserInfo()
      }
      return null
    }
  }

  // 初始化
  initUserInfo()

  return {
    // 状态
    token: readonly(token),
    userInfo: readonly(userInfo),
    isLoggedIn,
    
    // 方法
    login,
    register,
    logout,
    setToken,
    setUserInfo,
    clearUserInfo,
    fetchCurrentUser
  }
})
