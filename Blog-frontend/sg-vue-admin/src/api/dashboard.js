import request from '@/utils/request'

// 获取仪表盘统计数据
export function getDashboardStatistics() {
  return request({
    url: '/dashboard/statistics',
    method: 'get'
  })
}

// 获取最新文章列表
export function getRecentArticles(limit = 5) {
  return request({
    url: '/dashboard/recentArticles',
    method: 'get',
    params: { limit }
  })
}

// 获取最新评论列表
export function getRecentComments(limit = 5) {
  return request({
    url: '/dashboard/recentComments',
    method: 'get',
    params: { limit }
  })
}

// 获取文章列表（用于分类分布统计）
export function getArticleList(params = {}) {
  return request({
    url: '/article/articleList',
    method: 'get',
    params: {
      page: 1,
      pageSize: 1000,
      status: 0,
      ...params
    }
  })
}

// 获取分类列表
export function getCategoryList() {
  return request({
    url: '/category/getCategoryList',
    method: 'get'
  })
}

// 获取标签列表
export function getTagList() {
  return request({
    url: '/tag/getAllTag',
    method: 'get'
  })
}
