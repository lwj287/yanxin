import request from '@/utils/request'

export const getExamPage = (params: any) => {
  return request({
    url: '/exam/page',
    method: 'get',
    params
  })
}

export const getExamById = (id: number) => {
  return request({
    url: `/exam/${id}`,
    method: 'get'
  })
}

export const addExam = (data: any) => {
  return request({
    url: '/exam',
    method: 'post',
    data
  })
}

export const updateExam = (data: any) => {
  return request({
    url: '/exam',
    method: 'put',
    data
  })
}

export const deleteExam = (id: number) => {
  return request({
    url: `/exam/${id}`,
    method: 'delete'
  })
}

export const getExamRecords = (id: number) => {
  return request({
    url: `/exam/${id}/records`,
    method: 'get'
  })
}