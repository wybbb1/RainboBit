/**
 * 统一API导出文件
 * 集中管理所有API接口，方便统一导入和使用
 */

// 导入各个模块的API
export { articleApi, default as article } from './article'
export { categoryApi, default as category } from './category'
export { tagApi, default as tag } from './tag'
export { commentApi, default as comment } from './comment'
export { linkApi, default as link } from './link'
export { authApi, default as auth } from './auth'
export { uploadApi, default as upload } from './upload'
export { userApi, default as user } from './user'

// 导入工具类型和函数
export type { 
  ApiResponse, 
  PageQuery, 
  PageResult 
} from '@/utils/request'
export { request } from '@/utils/request'

// 统一导出所有API
import articleApi from './article'
import categoryApi from './category'
import tagApi from './tag'
import commentApi from './comment'
import linkApi from './link'
import authApi from './auth'
import uploadApi from './upload'
import userApi from './user'

/**
 * 所有API的集合对象
 * 使用方式：
 * import api from '@/api'
 * api.article.getArticleList(params)
 * api.category.getCategoryList()
 * api.auth.login(loginData)
 * api.user.updateProfile(userData)
 */
export default {
  article: articleApi,
  category: categoryApi,
  tag: tagApi,
  comment: commentApi,
  link: linkApi,
  auth: authApi,
  upload: uploadApi,
  user: userApi
}

/**
 * 使用示例：
 * 
 * 方式1：按需导入
 * import { articleApi, categoryApi } from '@/api'
 * articleApi.getArticleList(params)
 * categoryApi.getCategoryList()
 * 
 * 方式2：统一导入
 * import api from '@/api'
 * api.article.getArticleList(params)
 * api.category.getCategoryList()
 * 
 * 方式3：导入单个模块
 * import { article } from '@/api'
 * article.getArticleList(params)
 */
