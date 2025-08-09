<!--
  用户个人资料页面
  用户可以查看和编辑个人信息，包括上传头像
-->
<template>
  <div class="profile-container">
    <div class="profile-content">
      <h2>个人资料</h2>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-message">
        正在加载用户信息...
      </div>
      
      <div v-else class="profile-form">
        <!-- 头像上传区域 -->
        <div class="avatar-section">
          <h3>头像</h3>
          <div class="avatar-upload">
            <div class="current-avatar">
              <img 
                v-if="currentAvatarUrl" 
                :src="currentAvatarUrl" 
                :alt="userInfo?.user.nickName"
                class="avatar-img"
                @load="onAvatarLoad"
                @error="onAvatarError"
              />
              <div v-else class="avatar-placeholder">
                {{ userInfo?.user.nickName?.charAt(0)?.toUpperCase() || 'U' }}
              </div>
            </div>
            
            <div class="upload-controls">
              <input
                ref="fileInput"
                type="file"
                accept="image/*"
                @change="handleFileSelect"
                style="display: none"
              />
              <button 
                @click="triggerFileSelect" 
                :disabled="uploading"
                class="upload-btn"
              >
                {{ uploading ? '上传中...' : '选择头像' }}
              </button>
              <p class="upload-hint">支持 JPG、PNG 格式，文件大小不超过 2MB</p>
            </div>
          </div>
        </div>

        <!-- 用户信息 -->
        <div class="user-info-section">
          <h3>基本信息</h3>
          
          <div class="form-group">
            <label>用户名</label>
            <input 
              v-model="profileForm.userName" 
              type="text" 
              readonly 
              class="readonly-input"
            />
            <small>用户名不可修改</small>
          </div>
          
          <div class="form-group">
            <label>昵称</label>
            <input 
              v-model="profileForm.nickName" 
              type="text" 
              placeholder="请输入昵称"
              :disabled="saving"
            />
          </div>
          
          <div class="form-group">
            <label>邮箱</label>
            <input 
              v-model="profileForm.email" 
              type="email" 
              placeholder="请输入邮箱"
              :disabled="saving"
            />
          </div>
          
          <div class="form-group">
            <label>性别</label>
            <select v-model="profileForm.sex" :disabled="saving">
              <option value="0">男</option>
              <option value="1">女</option>
              <option value="2">保密</option>
            </select>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="form-actions">
          <button 
            @click="saveProfile" 
            :disabled="saving || !hasChanges"
            class="save-btn"
          >
            {{ saving ? '保存中...' : '保存修改' }}
          </button>
          <button 
            @click="resetForm" 
            :disabled="saving"
            class="reset-btn"
          >
            重置
          </button>
        </div>
      </div>

      <!-- 错误和成功消息 -->
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
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { uploadApi, userApi, authApi } from '@/api'
import type { User } from '@/types'

const router = useRouter()
const userStore = useUserStore()
const fileInput = ref<HTMLInputElement>()

// 状态
const uploading = ref(false)
const saving = ref(false)
const loading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

// 表单数据
const profileForm = reactive<Partial<User>>({
  id: 0,
  userName: '',
  nickName: '',
  email: '',
  phonenumber: '',
  sex: '2',
  avatar: ''
})

// 原始数据，用于比较是否有变更
const originalData = ref<Partial<User>>({})

// 计算属性
const userInfo = computed(() => userStore.userInfo)
const isLoggedIn = computed(() => userStore.isLoggedIn)

// 添加一个计算属性来获取当前头像URL
const currentAvatarUrl = computed(() => {
  const url = userInfo.value?.user.avatar || profileForm.avatar
  // 添加时间戳避免缓存问题
  return url ? `${url}?t=${Date.now()}` : ''
})

// 检查是否有变更
const hasChanges = computed(() => {
  return Object.keys(profileForm).some(key => {
    const formKey = key as keyof User
    return profileForm[formKey] !== originalData.value[formKey]
  })
})

/**
 * 初始化表单数据
 */
const initForm = async () => {
  loading.value = true
  errorMessage.value = ''
  
  try {
    // 从API获取最新的用户信息
    const user = await authApi.getCurrentUser()
    
    // 更新表单数据
    Object.assign(profileForm, {
      id: user.id,
      userName: user.userName,
      nickName: user.nickName,
      email: user.email,
      phonenumber: user.phonenumber,
      sex: user.sex,
      avatar: user.avatar
    })
    
    // 保存原始数据
    originalData.value = { ...profileForm }
    
    // 更新store中的用户信息
    if (userInfo.value) {
      userStore.setUserInfo({
        user: user,
        permissions: [...userInfo.value.permissions]
      })
    }
    
  } catch (error: any) {
    console.error('获取用户信息失败:', error)
    errorMessage.value = '获取用户信息失败：' + (error.message || '请重试')
    
    // 如果API获取失败，尝试使用store中的数据
    if (userInfo.value?.user) {
      const user = userInfo.value.user
      Object.assign(profileForm, {
        id: user.id,
        userName: user.userName,
        nickName: user.nickName,
        email: user.email,
        phonenumber: user.phonenumber,
        sex: user.sex,
        avatar: user.avatar
      })
      originalData.value = { ...profileForm }
    }
  } finally {
    loading.value = false
  }
}

/**
 * 头像加载成功回调
 */
const onAvatarLoad = () => {
  // 头像加载成功
}

/**
 * 头像加载失败回调
 */
const onAvatarError = () => {
  // 头像加载失败
}

/**
 * 重置表单
 */
const resetForm = () => {
  Object.assign(profileForm, originalData.value)
  errorMessage.value = ''
  successMessage.value = ''
}

/**
 * 触发文件选择
 */
const triggerFileSelect = () => {
  fileInput.value?.click()
}

/**
 * 处理文件选择
 */
const handleFileSelect = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  
  if (!file) return
  
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    errorMessage.value = '请选择图片文件'
    return
  }
  
  // 验证文件大小（2MB）
  if (file.size > 2 * 1024 * 1024) {
    errorMessage.value = '图片大小不能超过 2MB'
    return
  }
  
  uploading.value = true
  errorMessage.value = ''
  
  try {
    const response = await uploadApi.uploadImage(file)
    
    // 响应拦截器已经提取了data字段，response就是URL字符串
    const newAvatarUrl = response as string
    
    // 同时更新表单数据和store
    profileForm.avatar = newAvatarUrl
    
    // 立即更新用户信息显示
    if (userInfo.value) {
      const updatedUser = { ...userInfo.value.user, avatar: newAvatarUrl }
      userStore.setUserInfo({
        user: updatedUser,
        permissions: [...userInfo.value.permissions]
      })
    }
    
    // 清除错误信息并显示成功消息
    errorMessage.value = ''
    successMessage.value = '头像上传成功！'
  } catch (error: any) {
    console.error('头像上传失败:', error)
    errorMessage.value = error.message || '头像上传失败'
  } finally {
    uploading.value = false
    // 清空 input 的值，以便可以重新选择同一个文件
    if (target) target.value = ''
  }
}

/**
 * 保存个人资料
 */
const saveProfile = async () => {
  if (!hasChanges.value) return
  
  saving.value = true
  errorMessage.value = ''
  successMessage.value = ''
  
  try {
    // 调用更新用户信息的API
    await userApi.updateProfile(profileForm)
    
    successMessage.value = '个人资料保存成功！'
    originalData.value = { ...profileForm }
    
    // 更新store中的用户信息
    if (userInfo.value) {
      const updatedUser = { ...userInfo.value.user, ...profileForm }
      userStore.setUserInfo({
        user: updatedUser,
        permissions: [...userInfo.value.permissions]
      })
    }
  } catch (error: any) {
    console.error('保存用户资料失败:', error)
    errorMessage.value = error.message || '保存失败，请重试'
  } finally {
    saving.value = false
  }
}

// 检查登录状态
onMounted(async () => {
  if (!isLoggedIn.value) {
    router.push('/login?redirect=/profile')
    return
  }
  
  await initForm()
})
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 80px auto 40px;
  padding: 0 20px;
}

.profile-content {
  background: white;
  border-radius: 8px;
  padding: 40px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.profile-content h2 {
  margin-bottom: 30px;
  color: #333;
  text-align: center;
}

.profile-form h3 {
  margin-bottom: 20px;
  color: #555;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 10px;
}

.avatar-section {
  margin-bottom: 40px;
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 30px;
}

.current-avatar {
  flex-shrink: 0;
}

.avatar-img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #f0f0f0;
}

.avatar-placeholder {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
}

.upload-controls {
  flex: 1;
}

.upload-btn {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-bottom: 10px;
}

.upload-btn:hover:not(:disabled) {
  background-color: #0056b3;
}

.upload-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.upload-hint {
  color: #666;
  font-size: 0.9rem;
  margin: 0;
}

.user-info-section {
  margin-bottom: 30px;
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

.readonly-input {
  background-color: #f8f9fa;
  cursor: not-allowed;
}

.form-group small {
  color: #666;
  font-size: 0.85rem;
}

.form-actions {
  display: flex;
  gap: 15px;
  margin-top: 30px;
}

.save-btn,
.reset-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s;
}

.save-btn {
  background-color: #28a745;
  color: white;
}

.save-btn:hover:not(:disabled) {
  background-color: #218838;
}

.save-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.reset-btn {
  background-color: #6c757d;
  color: white;
}

.reset-btn:hover:not(:disabled) {
  background-color: #545b62;
}

.error-message {
  margin-top: 20px;
  padding: 12px;
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
}

.success-message {
  margin-top: 20px;
  padding: 12px;
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
  border-radius: 4px;
}

.loading-message {
  text-align: center;
  padding: 40px;
  color: #666;
  font-size: 1.1rem;
}

@media (max-width: 768px) {
  .profile-content {
    padding: 20px;
  }
  
  .avatar-upload {
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: 20px;
  }
  
  .form-actions {
    flex-direction: column;
  }
}
</style>
