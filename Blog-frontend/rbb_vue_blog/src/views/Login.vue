<!--
  用户登录组件
  提供用户登录界面和功能
-->
<template>
  <div class="login-container">
    <div class="login-form">
      <h2>用户登录</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="userName">用户名:</label>
          <input
            id="userName"
            v-model="loginForm.userName"
            type="text"
            required
            placeholder="请输入用户名"
            :disabled="loading"
          />
        </div>
        
        <div class="form-group">
          <label for="password">密码:</label>
          <input
            id="password"
            v-model="loginForm.password"
            type="password"
            required
            placeholder="请输入密码"
            :disabled="loading"
          />
        </div>
        
        <div class="form-actions">
          <button type="submit" :disabled="loading" class="login-btn">
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </div>
        
        <div class="form-links">
          <router-link to="/register">还没有账号？立即注册</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import type { UserLoginDTO } from '@/types'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

// 表单数据
const loginForm = reactive<UserLoginDTO>({
  userName: '',
  password: ''
})

// 状态
const loading = ref(false)

/**
 * 处理登录
 */
const handleLogin = async () => {
  if (!loginForm.userName.trim() || !loginForm.password.trim()) {
    ElMessage.error({
      message: '请填写完整的登录信息',
      offset: 80
    })
    return
  }

  loading.value = true

  try {
    await userStore.login(loginForm)
    
    ElMessage.success({
      message: '登录成功！',
      offset: 80
    })
    
    // 登录成功，跳转到首页或之前访问的页面
    const redirect = router.currentRoute.value.query.redirect as string
    router.push(redirect || '/')
    
  } catch (error: any) {
    let message = '登录失败，请检查用户名和密码'
    
    // 处理特定的错误消息
    if (error.message) {
      if (error.message.includes('用户名') || error.message.includes('密码')) {
        message = '用户名或密码错误'
      } else if (error.message.includes('账号被禁用')) {
        message = '账号已被禁用，请联系管理员'
      } else if (error.message.includes('网络')) {
        message = '网络连接失败，请检查网络后重试'
      } else {
        message = error.message
      }
    }
    
    ElMessage.error({
      message: message,
      offset: 80
    })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 20px;
}

.login-form {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.login-form h2 {
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

.form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #007bff;
}

.form-group input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.form-actions {
  margin: 30px 0 20px;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-btn:hover:not(:disabled) {
  background-color: #0056b3;
}

.login-btn:disabled {
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
