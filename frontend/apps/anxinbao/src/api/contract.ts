import request from '@/utils/request'
import type { Contract, ContractTemplate } from './types'

export const getContractList = () => {
  return request<Contract[]>({
    url: '/contract/list',
    method: 'get'
  })
}

export const getContractDetail = (id: number) => {
  return request<Contract>({
    url: `/contract/${id}`,
    method: 'get'
  })
}

export const voidContract = (id: number) => {
  return request({
    url: `/contract/${id}/void`,
    method: 'put'
  })
}

export const deleteContract = (id: number) => {
  return request({
    url: `/contract/${id}`,
    method: 'delete'
  })
}

export const addContractTemplate = (data: Partial<ContractTemplate>) => {
  return request({
    url: '/contract-template/add',
    method: 'post',
    data
  })
}

export const getContractTemplateList = () => {
  return request<ContractTemplate[]>({
    url: '/contract-template/list',
    method: 'get'
  })
}
