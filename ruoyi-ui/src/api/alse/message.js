import request from '@/utils/request'

// 查询聊天消息列表
export function listMessage(query) {
  return request({
    url: '/alse/message/list',
    method: 'get',
    params: query
  })
}

// 查询聊天消息详细
export function getMessage(messageId) {
  return request({
    url: '/alse/message/' + messageId,
    method: 'get'
  })
}

// 新增聊天消息
export function addMessage(data) {
  return request({
    url: '/alse/message',
    method: 'post',
    data: data
  })
}

// 修改聊天消息
export function updateMessage(data) {
  return request({
    url: '/alse/message',
    method: 'put',
    data: data
  })
}

// 删除聊天消息
export function delMessage(messageId) {
  return request({
    url: '/alse/message/' + messageId,
    method: 'delete'
  })
}
