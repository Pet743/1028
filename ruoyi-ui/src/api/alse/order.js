import request from '@/utils/request'

// 查询商品订单列表
export function listOrder(query) {
  return request({
    url: '/alse/order/list',
    method: 'get',
    params: query
  })
}

// 查询商品订单详细
export function getOrder(orderId) {
  return request({
    url: '/alse/order/' + orderId,
    method: 'get'
  })
}

// 新增商品订单
export function addOrder(data) {
  return request({
    url: '/alse/order',
    method: 'post',
    data: data
  })
}

// 修改商品订单
export function updateOrder(data) {
  return request({
    url: '/alse/order',
    method: 'put',
    data: data
  })
}

// 删除商品订单
export function delOrder(orderId) {
  return request({
    url: '/alse/order/' + orderId,
    method: 'delete'
  })
}
