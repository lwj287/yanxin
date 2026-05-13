import request from '@/utils/request'
import type { InsuranceProduct } from './types'

export const getInsuranceProductList = () => {
  return request<InsuranceProduct[]>({
    url: '/insurance-product/list',
    method: 'get'
  })
}

export const addInsuranceProduct = (data: Partial<InsuranceProduct>) => {
  return request({
    url: '/insurance-product/add',
    method: 'post',
    data
  })
}

export const updateInsuranceProduct = (data: Partial<InsuranceProduct>) => {
  return request({
    url: '/insurance-product/update',
    method: 'put',
    data
  })
}

export const deleteInsuranceProduct = (id: number) => {
  return request({
    url: `/insurance-product/${id}`,
    method: 'delete'
  })
}

export const updateProductStatus = (id: number, status: number) => {
  return request({
    url: `/insurance-product/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 兼容页面中的旧方法命名
export const getInsuranceList = getInsuranceProductList
export const addInsurance = addInsuranceProduct
export const updateInsurance = updateInsuranceProduct
export const deleteInsurance = deleteInsuranceProduct
export const updateInsuranceStatus = updateProductStatus
