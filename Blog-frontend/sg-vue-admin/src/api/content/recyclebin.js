import request from '@/utils/request'

// 查询回收站文章列表
export function listDeletedArticles(query) {
  return request({
    url: '/content/recyclebin/articles',
    method: 'get',
    params: query
  })
}

// 查询回收站友链列表
export function listDeletedLinks(query) {
  return request({
    url: '/content/recyclebin/links',
    method: 'get',
    params: query
  })
}

// 恢复文章
export function restoreArticle(id) {
  return request({
    url: '/content/recyclebin/articles/' + id + '/restore',
    method: 'put'
  })
}

// 恢复友链
export function restoreLink(id) {
  return request({
    url: '/content/recyclebin/links/' + id + '/restore',
    method: 'put'
  })
}

// 永久删除文章
export function permanentDeleteArticle(id) {
  return request({
    url: '/content/recyclebin/articles/' + id,
    method: 'delete'
  })
}

// 永久删除友链
export function permanentDeleteLink(id) {
  return request({
    url: '/content/recyclebin/links/' + id,
    method: 'delete'
  })
}

// 批量恢复文章
export function batchRestoreArticles(ids) {
  return request({
    url: '/content/recyclebin/articles/batch-restore/' + ids.join(','),
    method: 'put'
  })
}

// 批量恢复友链
export function batchRestoreLinks(ids) {
  return request({
    url: '/content/recyclebin/links/batch-restore/' + ids.join(','),
    method: 'put'
  })
}

// 批量永久删除文章
export function batchPermanentDeleteArticles(ids) {
  return request({
    url: '/content/recyclebin/articles/batch-delete/' + ids.join(','),
    method: 'delete'
  })
}

// 批量永久删除友链
export function batchPermanentDeleteLinks(ids) {
  return request({
    url: '/content/recyclebin/links/batch-delete/' + ids.join(','),
    method: 'delete'
  })
}

