<!--
  登录状态测试页面
  用于调试和验证登录功能
-->
<template>
  <div class="debug-container">
    <h2>登录状态调试</h2>
    
    <div class="debug-info">
      <h3>当前状态:</h3>
      <ul>
        <li>是否已登录: {{ userStore.isLoggedIn }}</li>
        <li>Token: {{ userStore.token || '无' }}</li>
        <li>用户信息: {{ userStore.userInfo ? JSON.stringify(userStore.userInfo, null, 2) : '无' }}</li>
      </ul>
    </div>
    
    <div class="debug-actions">
      <h3>操作:</h3>
      <button @click="testLogin" :disabled="userStore.isLoggedIn">测试登录</button>
      <button @click="testLogout" :disabled="!userStore.isLoggedIn">测试登出</button>
      <button @click="clearStorage">清除本地存储</button>
    </div>
    
    <div class="debug-storage">
      <h3>本地存储:</h3>
      <ul>
        <li>localStorage.token: {{ localToken || '无' }}</li>
        <li>localStorage.userInfo: {{ localUserInfo || '无' }}</li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const localToken = ref('')
const localUserInfo = ref('')

const updateLocalStorage = () => {
  localToken.value = localStorage.getItem('token') || ''
  localUserInfo.value = localStorage.getItem('userInfo') || ''
}

const testLogin = async () => {
  try {
    await userStore.login({
      userName: 'test',
      password: 'test123'
    })
    console.log('测试登录成功')
    updateLocalStorage()
  } catch (error) {
    console.error('测试登录失败:', error)
  }
}

const testLogout = async () => {
  try {
    await userStore.logout()
    console.log('测试登出成功')
    updateLocalStorage()
  } catch (error) {
    console.error('测试登出失败:', error)
  }
}

const clearStorage = () => {
  localStorage.clear()
  location.reload()
}

onMounted(() => {
  updateLocalStorage()
})
</script>

<style scoped>
.debug-container {
  max-width: 800px;
  margin: 100px auto;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.debug-info,
.debug-actions,
.debug-storage {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.debug-info ul,
.debug-storage ul {
  list-style: none;
  padding: 0;
}

.debug-info li,
.debug-storage li {
  padding: 5px 0;
  border-bottom: 1px solid #eee;
  word-break: break-all;
}

.debug-actions button {
  margin-right: 10px;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  background: #007bff;
  color: white;
}

.debug-actions button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

pre {
  background: #f8f9fa;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
}
</style>
