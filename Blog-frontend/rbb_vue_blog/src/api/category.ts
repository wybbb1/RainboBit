import { request } from '@/utils/request'
import type { Category } from '@/types'

/**
 * 分类API接口
 */
export const categoryApi = {
  /**
   * 获取所有分类列表
   */
  getCategoryList(): Promise<Category[]> {
    return request.get('/category/getCategoryList')
  },

  /**
   * 根据ID获取分类详情
   * @param id 分类ID
   */
  getCategoryDetail(id: number | string): Promise<Category> {
    return request.get(`/category/${id}`)
  },

}

export default categoryApi
