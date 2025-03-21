import request from '@/utils/request'

// 查询首页内容列表
export function listContent(query) {
  return request({
    url: '/alse/content/list',
    method: 'get',
    params: query
  })
}

// 查询首页内容详细
export function getContent(contentId) {
  return request({
    url: '/alse/content/' + contentId,
    method: 'get'
  })
}

// 新增首页内容
export function addContent(data) {
  return request({
    url: '/alse/content',
    method: 'post',
    data: data
  })
}

// 修改首页内容
export function updateContent(data) {
  return request({
    url: '/alse/content',
    method: 'put',
    data: data
  })
}

// 删除首页内容
export function delContent(contentId) {
  return request({
    url: '/alse/content/' + contentId,
    method: 'delete'
  })
}
