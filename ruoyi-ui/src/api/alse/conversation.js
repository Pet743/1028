import request from '@/utils/request'

// 查询聊天会话列表
export function listConversation(query) {
  return request({
    url: '/alse/conversation/list',
    method: 'get',
    params: query
  })
}

// 查询聊天会话详细
export function getConversation(conversationId) {
  return request({
    url: '/alse/conversation/' + conversationId,
    method: 'get'
  })
}

// 新增聊天会话
export function addConversation(data) {
  return request({
    url: '/alse/conversation',
    method: 'post',
    data: data
  })
}

// 修改聊天会话
export function updateConversation(data) {
  return request({
    url: '/alse/conversation',
    method: 'put',
    data: data
  })
}

// 删除聊天会话
export function delConversation(conversationId) {
  return request({
    url: '/alse/conversation/' + conversationId,
    method: 'delete'
  })
}
