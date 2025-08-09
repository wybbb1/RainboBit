<!--
  文件上传组件
  支持图片上传功能
-->
<template>
  <div class="upload-container">
    <div class="upload-area" @click="triggerFileInput" @drop="handleDrop" @dragover.prevent @dragleave.prevent>
      <input
        ref="fileInput"
        type="file"
        accept="image/*"
        style="display: none"
        @change="handleFileSelect"
      />
      
      <div v-if="!uploading && !imageUrl" class="upload-placeholder">
        <div class="upload-icon">📷</div>
        <p>点击或拖拽上传图片</p>
        <p class="upload-hint">支持 jpg、png、gif 格式</p>
      </div>
      
      <div v-if="uploading" class="uploading">
        <div class="loading-spinner"></div>
        <p>上传中...</p>
      </div>
      
      <div v-if="imageUrl && !uploading" class="upload-success">
        <img :src="imageUrl" alt="上传的图片" class="uploaded-image" />
        <div class="image-actions">
          <button @click.stop="copyImageUrl" class="action-btn">复制链接</button>
          <button @click.stop="clearImage" class="action-btn danger">删除</button>
        </div>
      </div>
    </div>
    
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
    
    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { uploadApi } from '@/api'

// Props
interface Props {
  modelValue?: string
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: ''
})

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: string]
  'upload-success': [url: string]
  'upload-error': [error: string]
}>()

// 状态
const fileInput = ref<HTMLInputElement>()
const uploading = ref(false)
const imageUrl = ref(props.modelValue)
const errorMessage = ref('')
const successMessage = ref('')

/**
 * 触发文件选择
 */
const triggerFileInput = () => {
  if (uploading.value) return
  fileInput.value?.click()
}

/**
 * 处理文件选择
 */
const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    uploadFile(file)
  }
}

/**
 * 处理拖拽上传
 */
const handleDrop = (event: DragEvent) => {
  event.preventDefault()
  const file = event.dataTransfer?.files[0]
  if (file && file.type.startsWith('image/')) {
    uploadFile(file)
  } else {
    showError('请上传图片文件')
  }
}

/**
 * 验证文件
 */
const validateFile = (file: File): boolean => {
  // 检查文件类型
  if (!file.type.startsWith('image/')) {
    showError('请选择图片文件')
    return false
  }
  
  // 检查文件大小（限制5MB）
  const maxSize = 5 * 1024 * 1024
  if (file.size > maxSize) {
    showError('图片大小不能超过5MB')
    return false
  }
  
  return true
}

/**
 * 上传文件
 */
const uploadFile = async (file: File) => {
  if (!validateFile(file)) return
  
  uploading.value = true
  errorMessage.value = ''
  successMessage.value = ''
  
  try {
    const response = await uploadApi.uploadImage(file)
    imageUrl.value = response.url
    emit('update:modelValue', response.url)
    emit('upload-success', response.url)
    showSuccess('图片上传成功')
  } catch (error: any) {
    const message = error.message || '上传失败，请重试'
    showError(message)
    emit('upload-error', message)
  } finally {
    uploading.value = false
  }
}

/**
 * 复制图片链接
 */
const copyImageUrl = async () => {
  if (!imageUrl.value) return
  
  try {
    await navigator.clipboard.writeText(imageUrl.value)
    showSuccess('图片链接已复制到剪贴板')
  } catch (error) {
    // 兼容处理
    const textArea = document.createElement('textarea')
    textArea.value = imageUrl.value
    document.body.appendChild(textArea)
    textArea.select()
    document.execCommand('copy')
    document.body.removeChild(textArea)
    showSuccess('图片链接已复制到剪贴板')
  }
}

/**
 * 清除图片
 */
const clearImage = () => {
  imageUrl.value = ''
  emit('update:modelValue', '')
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

/**
 * 显示错误信息
 */
const showError = (message: string) => {
  errorMessage.value = message
  setTimeout(() => {
    errorMessage.value = ''
  }, 3000)
}

/**
 * 显示成功信息
 */
const showSuccess = (message: string) => {
  successMessage.value = message
  setTimeout(() => {
    successMessage.value = ''
  }, 3000)
}
</script>

<style scoped>
.upload-container {
  width: 100%;
  max-width: 400px;
}

.upload-area {
  border: 2px dashed #ddd;
  border-radius: 8px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  min-height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.upload-area:hover {
  border-color: #007bff;
  background-color: #f8f9fa;
}

.upload-placeholder {
  color: #666;
}

.upload-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.upload-hint {
  font-size: 12px;
  color: #999;
  margin: 8px 0 0;
}

.uploading {
  color: #007bff;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.upload-success {
  width: 100%;
}

.uploaded-image {
  max-width: 100%;
  max-height: 300px;
  border-radius: 4px;
  object-fit: contain;
}

.image-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s;
}

.action-btn:not(.danger) {
  background-color: #007bff;
  color: white;
}

.action-btn:not(.danger):hover {
  background-color: #0056b3;
}

.action-btn.danger {
  background-color: #dc3545;
  color: white;
}

.action-btn.danger:hover {
  background-color: #c82333;
}

.error-message {
  margin-top: 12px;
  padding: 8px 12px;
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
  font-size: 14px;
}

.success-message {
  margin-top: 12px;
  padding: 8px 12px;
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
  border-radius: 4px;
  font-size: 14px;
}
</style>
