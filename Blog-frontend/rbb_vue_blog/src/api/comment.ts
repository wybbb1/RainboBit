import { request, type PageQuery, type PageResult } from '@/utils/request'
import type { Comment } from '@/types'

/**
 * 评论API接口
 */
export const commentApi = {
  /**
   * 分页查询评论列表
   * @param articleId 文章ID
   * @param params 分页参数
   */
  getCommentList(articleId: number | string, params: PageQuery): Promise<PageResult<Comment>> {
    return request.get('/comment/commentList', { articleId, ...params })
  },

  /**
   * 添加评论
   * @param comment 评论数据
   */
  addComment(comment: {
    articleId: number | string
    content: string
    rootId?: number | string
    toCommentUserId?: number | string
    toCommentId?: number | string
  }): Promise<void> {
    return request.post('/comment', comment)
  }
}

export default commentApi
