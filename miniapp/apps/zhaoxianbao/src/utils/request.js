import { clearToken, getToken } from './auth'

const BASE_URL = 'http://localhost:8080'

const normalizeGetParams = (data) => {
  if (!data || typeof data !== 'object') return {}
  return Object.entries(data).reduce((acc, [key, value]) => {
    if (value === null || value === undefined) return acc
    if (typeof value === 'string') {
      const trimmed = value.trim()
      if (!trimmed || trimmed.toLowerCase() === 'null' || trimmed.toLowerCase() === 'undefined') {
        return acc
      }
      acc[key] = trimmed
      return acc
    }
    acc[key] = value
    return acc
  }, {})
}

export function request({ url, method = 'GET', data = {}, header = {} }) {
  return new Promise((resolve, reject) => {
    const token = getToken()
    const finalHeader = { ...header }
    const normalizedData = method.toUpperCase() === 'GET' ? normalizeGetParams(data) : data
    if (token) {
      finalHeader.Authorization = `Bearer ${token}`
    }
    uni.request({
      url: `${BASE_URL}${url}`,
      method,
      data: normalizedData,
      header: finalHeader,
      success: (res) => {
        const body = res.data || {}
        if (body.code !== 200) {
          if (body.code === 401) {
            clearToken()
          }
          uni.showToast({ title: body.msg || '请求失败', icon: 'none' })
          reject(new Error(body.msg || '请求失败'))
          return
        }
        resolve(body.data)
      },
      fail: (err) => {
        uni.showToast({ title: err.errMsg || '网络错误', icon: 'none' })
        reject(err)
      }
    })
  })
}

export const get = (url, data = {}) => request({ url, method: 'GET', data })
export const post = (url, data = {}) => request({ url, method: 'POST', data })
export const put = (url, data = {}) => request({ url, method: 'PUT', data })
export const del = (url, data = {}) => request({ url, method: 'DELETE', data })
