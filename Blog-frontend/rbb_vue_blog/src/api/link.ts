import { request } from '@/utils/request'
import type { Link } from '@/types'

/**
 * 友链API接口
 */
export const linkApi = {
  /**
   * 获取所有友链列表
   */
  getLinkList(): Promise<Link[]> {
    return request.get('/link/getAllLink')
  }
}

export default linkApi
