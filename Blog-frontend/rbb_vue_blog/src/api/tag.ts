import { request } from '@/utils/request'
import type { Tag } from '@/types'

/**
 * 标签API接口
 */
export const tagApi = {
  /**
   * 获取所有标签列表
   */
  getTagList(): Promise<Tag[]> {
    return request.get('/tag/getAllTag')
  },

  /**
   * 根据ID获取标签详情
   * @param id 标签ID
   */
  getTagDetail(id: number): Promise<Tag> {
    return request.get(`/tag/${id}`)
  },

  /**
   * 获取热门标签
   * @param limit 限制数量，默认20
   */
  getHotTags(limit: number = 20): Promise<Tag[]> {
    return request.get('/tag/getHotTag', { limit })
  }
}

export default tagApi
