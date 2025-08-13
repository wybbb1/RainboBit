<!--
  用户注册组件
  提供用户注册界面和功能
-->
<template>
  <div class="register-container">
    <div class="register-form">
      <h2>用户注册</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="userName">用户名:</label>
          <input
            id="userName"
            v-model="registerForm.userName"
            type="text"
            required
            placeholder="请输入用户名"
            :disabled="loading"
          />
        </div>
        
        <div class="form-group">
          <label for="nickName">昵称:</label>
          <input
            id="nickName"
            v-model="registerForm.nickName"
            type="text"
            required
            placeholder="请输入昵称"
            :disabled="loading"
          />
        </div>
        
        <div class="form-group">
          <label for="email">邮箱:</label>
          <input
            id="email"
            v-model="registerForm.email"
            type="email"
            required
            placeholder="请输入邮箱"
            :disabled="loading"
          />
        </div>
        
        <div class="form-group">
          <label for="password">密码:</label>
          <input
            id="password"
            v-model="registerForm.password"
            type="password"
            required
            placeholder="请输入密码"
            :disabled="loading"
          />
        </div>
        
        <div class="form-group">
          <label for="confirmPassword">确认密码:</label>
          <input
            id="confirmPassword"
            v-model="confirmPassword"
            type="password"
            required
            placeholder="请再次输入密码"
            :disabled="loading"
          />
        </div>
        
        <div class="form-group">
          <label for="code">验证码:</label>
          <div class="code-input-group">
            <input
              id="code"
              v-model="registerForm.code"
              type="text"
              required
              placeholder="请输入邮箱验证码"
              :disabled="loading"
            />
            <button
              type="button"
              class="send-code-btn"
              :disabled="loading || countdown > 0 || !registerForm.email.trim()"
              @click="sendVerificationCode"
            >
              {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
            </button>
          </div>
        </div>
        
        <div class="form-group">
          <label for="phonenumber">手机号:</label>
          <input
            id="phonenumber"
            v-model="registerForm.phonenumber"
            type="tel"
            placeholder="请输入手机号（可选）"
            :disabled="loading"
          />
        </div>
        
        <div class="form-group">
          <label for="sex">性别:</label>
          <select 
            id="sex" 
            v-model="registerForm.sex" 
            :disabled="loading"
          >
            <option value="2">保密</option>
            <option value="0">男</option>
            <option value="1">女</option>
          </select>
        </div>
        
        <div class="form-actions">
          <button type="submit" :disabled="loading" class="register-btn">
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </div>
        
        <div class="form-links">
          <router-link to="/login">已有账号？立即登录</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api/auth'
import type { RegisterUserForm } from '@/types'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

// 表单数据
const registerForm = reactive<RegisterUserForm>({
  userName: '',
  nickName: '',
  password: '',
  email: '',
  phonenumber: '',
  sex: '2', // 默认保密
  code: ''
})

// 确认密码
const confirmPassword = ref('')

// 状态
const loading = ref(false)

// 验证码倒计时
const countdown = ref(0)
let countdownTimer: number | null = null

/**
 * 发送验证码
 */
const sendVerificationCode = async () => {
  if (!registerForm.email.trim()) {
    ElMessage.error({
      message: '请先输入邮箱地址',
      offset: 80
    })
    return
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(registerForm.email)) {
    ElMessage.error({
      message: '请输入有效的邮箱地址',
      offset: 80
    })
    return
  }

  try {
    loading.value = true
    
    await authApi.sendEmailCode(registerForm.email)
    
    ElMessage.success({
      message: '验证码已发送到您的邮箱',
      offset: 80
    })
    
    // 开始倒计时
    countdown.value = 60
    countdownTimer = window.setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        if (countdownTimer) {
          clearInterval(countdownTimer)
          countdownTimer = null
        }
      }
    }, 1000)
    
  } catch (error: any) {
    ElMessage.error({
      message: error.message || '发送验证码失败，请重试',
      offset: 80
    })
  } finally {
    loading.value = false
  }
}

/**
 * 验证表单数据
 */
const validateForm = (): boolean => {
  if (!registerForm.userName.trim()) {
    ElMessage.error({
      message: '请输入用户名',
      offset: 80
    })
    return false
  }
  
  if (!registerForm.nickName.trim()) {
    ElMessage.error({
      message: '请输入昵称',
      offset: 80
    })
    return false
  }
  
  if (!registerForm.email.trim()) {
    ElMessage.error({
      message: '请输入邮箱',
      offset: 80
    })
    return false
  }
  
  if (!registerForm.password.trim()) {
    ElMessage.error({
      message: '请输入密码',
      offset: 80
    })
    return false
  }
  
  if (registerForm.password.length < 6) {
    ElMessage.error({
      message: '密码长度不能少于6位',
      offset: 80
    })
    return false
  }
  
  if (registerForm.password !== confirmPassword.value) {
    ElMessage.error({
      message: '两次输入的密码不一致',
      offset: 80
    })
    return false
  }
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(registerForm.email)) {
    ElMessage.error({
      message: '请输入有效的邮箱地址',
      offset: 80
    })
    return false
  }
  
  if (!registerForm.code.trim()) {
    ElMessage.error({
      message: '请输入邮箱验证码',
      offset: 80
    })
    return false
  }
  
  return true
}

/**
 * 处理注册
 */
const handleRegister = async () => {
  if (!validateForm()) {
    return
  }

  loading.value = true

  try {
    // 使用新的注册接口
    await authApi.register(registerForm)
    ElMessage.success({
      message: '注册成功！即将跳转到登录页面...',
      offset: 80,
      duration: 3000
    })
    
    // 清理倒计时器
    if (countdownTimer) {
      clearInterval(countdownTimer)
      countdownTimer = null
    }
    
    // 延迟跳转到登录页
    setTimeout(() => {
      router.push('/login')
    }, 2000)
  } catch (error: any) {
    ElMessage.error({
      message: error.message || '注册失败，请重试',
      offset: 80
    })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 20px;
}

.register-form {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 500px;
}

.register-form h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #555;
  font-weight: 500;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #007bff;
}

.form-group input:disabled,
.form-group select:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.code-input-group {
  display: flex;
  gap: 10px;
}

.code-input-group input {
  flex: 1;
}

.send-code-btn {
  padding: 12px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
  white-space: nowrap;
  min-width: 100px;
}

.send-code-btn:hover:not(:disabled) {
  background-color: #0056b3;
}

.send-code-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.form-actions {
  margin: 30px 0 20px;
}

.register-btn {
  width: 100%;
  padding: 12px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.register-btn:hover:not(:disabled) {
  background-color: #218838;
}

.register-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.form-links {
  text-align: center;
}

.form-links a {
  color: #007bff;
  text-decoration: none;
}

.form-links a:hover {
  text-decoration: underline;
}
</style>
