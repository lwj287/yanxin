import request from '@/utils/request'

export const getUserPage = (params: any) => {
  return request({
    url: '/user/page',
    method: 'get',
    params
  })
}

export const getUserById = (id: number) => {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

export const addUser = (data: any) => {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

export const updateUser = (data: any) => {
  return request({
    url: '/user',
    method: 'put',
    data
  })
}

export const deleteUser = (id: number) => {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}