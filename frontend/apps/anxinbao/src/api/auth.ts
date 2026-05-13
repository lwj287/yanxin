import request from '@/utils/request'
import type { RealnameAuth } from './types'

export const getAuthList = () => {
  return request<RealnameAuth[]>({
    url: '/realname-auth/list',
    method: 'get'
  })
}

export const approveAuth = (id: number) => {
  return request({
    url: `/realname-auth/${id}/approve`,
    method: 'put'
  })
}

export const rejectAuth = (id: number, reason?: string) => {
  return request({
    url: `/realname-auth/${id}/reject`,
    method: 'put',
    data: { reason }
  })
}

export const deleteAuth = (id: number) => {
  return request({
    url: `/realname-auth/${id}`,
    method: 'delete'
  })
}
