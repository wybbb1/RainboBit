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
      
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>
      
      <div v-if="successMessage" class="success-message">
        {{ successMessage }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import type { UserRegisterDTO } from '@/types'

const router = useRouter()
const userStore = useUserStore()

// 表单数据
const registerForm = reactive<UserRegisterDTO>({
  userName: '',
  nickName: '',
  password: '',
  email: '',
  phonenumber: '',
  sex: '2' // 默认保密
})

// 确认密码
const confirmPassword = ref('')

// 状态
const loading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

/**
 * 验证表单数据
 */
const validateForm = (): boolean => {
  if (!registerForm.userName.trim()) {
    errorMessage.value = '请输入用户名'
    return false
  }
  
  if (!registerForm.nickName.trim()) {
    errorMessage.value = '请输入昵称'
    return false
  }
  
  if (!registerForm.email.trim()) {
    errorMessage.value = '请输入邮箱'
    return false
  }
  
  if (!registerForm.password.trim()) {
    errorMessage.value = '请输入密码'
    return false
  }
  
  if (registerForm.password.length < 6) {
    errorMessage.value = '密码长度不能少于6位'
    return false
  }
  
  if (registerForm.password !== confirmPassword.value) {
    errorMessage.value = '两次输入的密码不一致'
    return false
  }
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(registerForm.email)) {
    errorMessage.value = '请输入有效的邮箱地址'
    return false
  }
  
  return true
}

/**
 * 处理注册
 */
const handleRegister = async () => {
  errorMessage.value = ''
  successMessage.value = ''
  
  if (!validateForm()) {
    return
  }

  loading.value = true

  try {
    await userStore.register(registerForm)
    successMessage.value = '注册成功！即将跳转到登录页面...'
    
    // 延迟跳转到登录页
    setTimeout(() => {
      router.push('/login')
    }, 2000)
  } catch (error: any) {
    errorMessage.value = error.message || '注册失败，请重试'
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

.error-message {
  margin-top: 15px;
  padding: 10px;
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
  text-align: center;
}

.success-message {
  margin-top: 15px;
  padding: 10px;
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
  border-radius: 4px;
  text-align: center;
}
</style>
