import request from '@/utils/request'
import type { Staff } from './types'

export const getStaffList = () => {
  return request<Staff[]>({
    url: '/staff/list',
    method: 'get'
  })
}

export const addStaff = (data: Partial<Staff>) => {
  return request({
    url: '/staff/add',
    method: 'post',
    data
  })
}

export const updateStaff = (data: Partial<Staff>) => {
  return request({
    url: '/staff/update',
    method: 'put',
    data
  })
}

export const deleteStaff = (id: number) => {
  return request({
    url: `/staff/${id}`,
    method: 'delete'
  })
}

export const updateStaffStatus = (id: number, status: number) => {
  return request({
    url: `/staff/${id}/status`,
    method: 'put',
    params: { status }
  })
}
