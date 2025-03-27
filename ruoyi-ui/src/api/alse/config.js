import request from '@/utils/request'

// 查询支付通道配置列表
export function listConfig(query) {
  return request({
    url: '/alse/config/list',
    method: 'get',
    params: query
  })
}

// 查询支付通道配置详细
export function getConfig(id) {
  return request({
    url: '/alse/config/' + id,
    method: 'get'
  })
}

// 新增支付通道配置
export function addConfig(data) {
  return request({
    url: '/alse/config',
    method: 'post',
    data: data
  })
}

// 修改支付通道配置
export function updateConfig(data) {
  return request({
    url: '/alse/config',
    method: 'put',
    data: data
  })
}

// 删除支付通道配置
export function delConfig(id) {
  return request({
    url: '/alse/config/' + id,
    method: 'delete'
  })
}
