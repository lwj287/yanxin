import request from '@/utils/request'
import type { InsuranceOrder } from './types'

export const getOrderList = () => {
  return request<InsuranceOrder[]>({
    url: '/insurance-order/list',
    method: 'get'
  })
}

export const getOrderDetail = (id: number) => {
  return request<InsuranceOrder>({
    url: `/insurance-order/${id}`,
    method: 'get'
  })
}

export const updateOrder = (data: Partial<InsuranceOrder>) => {
  return request({
    url: '/insurance-order/update',
    method: 'put',
    data
  })
}

export const deleteOrder = (id: number) => {
  return request({
    url: `/insurance-order/${id}`,
    method: 'delete'
  })
}
