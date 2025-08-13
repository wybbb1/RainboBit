import { request, type PageQuery, type PageResult } from '@/utils/request'
import type { Article } from '@/types'

/**
 * 文章查询参数接口
 */
export interface ArticleQuery extends PageQuery {
  /** 分类ID */
  categoryId?: number | string;
  /** 标签ID */
  tagId?: number | string;
  /** 标签名称 */
  tagName?: string;
  /** 状态（0已发布，1草稿） */
  status?: number | string;
  /** 搜索关键词 */
  keyword?: string;
}

/**
 * 文章API接口
 */
export const articleApi = {
  /**
   * 分页查询文章列表
   * @param params 分页查询参数
   */
  getArticleList(params: ArticleQuery): Promise<PageResult<Article>> {
    return request.get('/article/articleList', params)
  },

  /**
   * 根据ID获取文章详情
   * @param id 文章ID
   */
  getArticleDetail(id: number | string): Promise<Article> {
    return request.get(`/article/${id}`)
  },

  /**
   * 增加文章浏览量
   * @param id 文章ID
   */
  updateViewCount(id: number | string): Promise<void> {
    return request.put(`/article/updateViewCount/${id}`)
  },

  /**
   * 搜索文章
   * @param keyword 搜索关键词
   * @param params 分页参数
   */
  searchArticles(keyword: string, params: PageQuery): Promise<PageResult<Article>> {
    return request.get('/article/search', { keyword, ...params })
  }
}

export default articleApi
