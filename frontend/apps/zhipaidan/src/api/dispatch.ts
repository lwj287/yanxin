import request from '@/utils/request'

// 获取仪表盘统计数据
export function getDashboardStatistics() {
  return request({
    url: '/zhipaidan/dashboard/statistics',
    method: 'get'
  })
}

// 获取订单列表
export function getOrderList(status?: number) {
  return request({
    url: '/zhipaidan/order/list',
    method: 'get',
    params: { status }
  })
}

// 智能匹配家政员
export function matchStaffForOrder(orderId: number) {
  return request({
    url: `/zhipaidan/order/${orderId}/match-staff`,
    method: 'get'
  })
}

// 人工确认派单
export function dispatchOrder(orderId: number, staffId: number) {
  return request({
    url: `/zhipaidan/order/${orderId}/dispatch`,
    method: 'post',
    params: { staffId }
  })
}

// 获取订单的派单记录
export function getDispatchRecordByOrderId(orderId: number) {
  return request({
    url: `/zhipaidan/dispatch/order/${orderId}`,
    method: 'get'
  })
}
