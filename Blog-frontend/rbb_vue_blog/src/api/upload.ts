/**
 * 文件上传相关API
 */

import service from '@/utils/request'

/**
 * 上传图片文件
 * @param file 图片文件
 * @returns 上传响应数据 - 直接返回图片URL字符串
 */
export const uploadImage = (file: File): Promise<string> => {
  const formData = new FormData()
  formData.append('img', file)
  
  return service.post('/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }).then(response => {
    return response.data || response
  })
}

/**
 * 文件上传API对象
 */
export const uploadApi = {
  uploadImage
}

export default uploadApi
