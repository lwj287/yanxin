import request from '../utils/request'

export const getTemplateList = (params: any) => {
  return request({
    url: '/zhijian/template/list',
    method: 'get',
    params
  })
}

export const addTemplate = (data: any) => {
  return request({
    url: '/zhijian/template/save',
    method: 'post',
    data
  })
}

export const updateTemplate = (data: any) => {
  return request({
    url: '/zhijian/template/update',
    method: 'post',
    data
  })
}

export const deleteTemplate = (id: number) => {
  return request({
    url: `/zhijian/template/delete/${id}`,
    method: 'post'
  })
}

export const updateTemplateStatus = (id: number, status: number) => {
  return request({
    url: `/zhijian/template/status/${id}`,
    method: 'post',
    params: { status }
  })
}

export const getTaskList = (params: any) => {
  return request({
    url: '/zhijian/task/list',
    method: 'get',
    params
  })
}

export const getTaskDetail = (id: number) => {
  return request({
    url: `/zhijian/task/detail/${id}`,
    method: 'get'
  })
}

export const updateTask = (data: any) => {
  return request({
    url: '/zhijian/task/update',
    method: 'post',
    data
  })
}

export const uploadTaskImage = (data: FormData) => {
  return request({
    url: '/zhijian/task/upload-image',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export const deleteTaskImage = (id: number) => {
  return request({
    url: `/zhijian/task/delete-image/${id}`,
    method: 'post'
  })
}

export const deleteTask = (id: number) => {
  return request({
    url: `/zhijian/task/delete/${id}`,
    method: 'post'
  })
}

export const getAppealList = (params: any) => {
  return request({
    url: '/zhijian/appeal/list',
    method: 'get',
    params
  })
}

export const handleAppeal = (data: any) => {
  return request({
    url: '/zhijian/appeal/handle',
    method: 'post',
    data
  })
}

export const updateAppeal = (data: any) => {
  return request({
    url: '/zhijian/appeal/update',
    method: 'post',
    data
  })
}

export const deleteAppeal = (id: number) => {
  return request({
    url: `/zhijian/appeal/delete/${id}`,
    method: 'post'
  })
}

// 服务类型管理
export const getServiceTypeList = (params?: any) => {
  return request({
    url: '/zhijian/service-type/list',
    method: 'get',
    params
  })
}

export const addServiceType = (data: any) => {
  return request({
    url: '/zhijian/service-type/save',
    method: 'post',
    data
  })
}

export const updateServiceType = (data: any) => {
  return request({
    url: '/zhijian/service-type/update',
    method: 'post',
    data
  })
}

export const updateServiceTypeStatus = (id: number, status: number) => {
  return request({
    url: `/zhijian/service-type/status/${id}`,
    method: 'post',
    params: { status }
  })
}

export const deleteServiceType = (id: number) => {
  return request({
    url: `/zhijian/service-type/delete/${id}`,
    method: 'post'
  })
}

// 首页概览数据统计
export const getDashboardStat = () => {
  return request({
    url: '/zhijian/dashboard/stat',
    method: 'get'
  })
}

// 首页图表数据
export const getDashboardChart = () => {
  return request({
    url: '/zhijian/dashboard/chart',
    method: 'get'
  })
}
