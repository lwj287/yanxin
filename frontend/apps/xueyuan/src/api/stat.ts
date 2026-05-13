import request from '@/utils/request'

export const getDashboardStat = () => {
  return request({
    url: '/stat/dashboard',
    method: 'get'
  })
}