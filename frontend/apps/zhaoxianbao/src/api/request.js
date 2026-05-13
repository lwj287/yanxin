import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 15000
})

request.interceptors.request.use((config) => {
  const token = sessionStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

request.interceptors.response.use((res) => {
  if (res.data.code !== 200) {
    if (res.data.code === 401 || res.data.code === 403) {
      sessionStorage.clear()
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    ElMessage.error(res.data.msg || '请求失败')
    return Promise.reject(new Error(res.data.msg || '请求失败'))
  }
  return res.data
}, (err) => {
  const status = err?.response?.status
  if (status === 401 || status === 403) {
    sessionStorage.clear()
    if (window.location.pathname !== '/login') {
      window.location.href = '/login'
    }
  }
  ElMessage.error(err.message || '网络错误')
  return Promise.reject(err)
})

export default request
