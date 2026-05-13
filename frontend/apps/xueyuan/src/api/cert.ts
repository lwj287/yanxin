import request from '@/utils/request'

export const getCertPage = (params: any) => {
  return request({
    url: '/cert/page',
    method: 'get',
    params
  })
}

export const getCertById = (id: number) => {
  return request({
    url: `/cert/${id}`,
    method: 'get'
  })
}

export const addCert = (data: any) => {
  return request({
    url: '/cert',
    method: 'post',
    data
  })
}

export const updateCert = (data: any) => {
  return request({
    url: '/cert',
    method: 'put',
    data
  })
}

export const deleteCert = (id: number) => {
  return request({
    url: `/cert/${id}`,
    method: 'delete'
  })
}