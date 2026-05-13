import request from '@/utils/request'
import type { ClaimRecord } from './types'

export const getClaimList = () => {
  return request<ClaimRecord[]>({
    url: '/claim/list',
    method: 'get'
  })
}

export const getClaimDetail = (id: number) => {
  return request<ClaimRecord>({
    url: `/claim/${id}`,
    method: 'get'
  })
}

export const processClaim = (id: number) => {
  return request({
    url: `/claim/${id}/accept`,
    method: 'put'
  })
}

export const rejectClaim = (id: number) => {
  return request({
    url: `/claim/${id}/reject`,
    method: 'put'
  })
}

export const completeClaim = (id: number) => {
  return request({
    url: `/claim/${id}/complete`,
    method: 'put'
  })
}

export const deleteClaim = (id: number) => {
  return request({
    url: `/claim/${id}`,
    method: 'delete'
  })
}
