import request from '@/utils/request'

export const getCoursePage = (params: any) => {
  return request({
    url: '/course/page',
    method: 'get',
    params
  })
}

export const getCourseById = (id: number) => {
  return request({
    url: `/course/${id}`,
    method: 'get'
  })
}

export const addCourse = (data: any) => {
  return request({
    url: '/course',
    method: 'post',
    data
  })
}

export const updateCourse = (data: any) => {
  return request({
    url: '/course',
    method: 'put',
    data
  })
}

export const deleteCourse = (id: number) => {
  return request({
    url: `/course/${id}`,
    method: 'delete'
  })
}

export const getCourseEnrollment = (id: number) => {
  return request({
    url: `/course/${id}/enrollment`,
    method: 'get'
  })
}
