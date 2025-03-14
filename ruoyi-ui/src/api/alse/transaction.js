import request from '@/utils/request'

// 查询钱包交易流水列表
export function listTransaction(query) {
  return request({
    url: '/alse/transaction/list',
    method: 'get',
    params: query
  })
}

// 查询钱包交易流水详细
export function getTransaction(transactionId) {
  return request({
    url: '/alse/transaction/' + transactionId,
    method: 'get'
  })
}

// 新增钱包交易流水
export function addTransaction(data) {
  return request({
    url: '/alse/transaction',
    method: 'post',
    data: data
  })
}

// 修改钱包交易流水
export function updateTransaction(data) {
  return request({
    url: '/alse/transaction',
    method: 'put',
    data: data
  })
}

// 删除钱包交易流水
export function delTransaction(transactionId) {
  return request({
    url: '/alse/transaction/' + transactionId,
    method: 'delete'
  })
}
