import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api', // Vite 中配置了 proxy
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data
    
    // 如果返回的不是统一封装的Result结构（如下载文件等），直接返回数据
    if (res.code === undefined) {
      return res
    }

    // 200 表示成功
    if (res.code === 200) {
      return res.data
    } 
    // 401 表示未登录或 token 过期
    else if (res.code === 401) {
      ElMessage.error('登录状态已过期，请重新登录')
      localStorage.removeItem('token')
      // router.push('/login')
      return Promise.reject(new Error(res.message))
    } 
    // 其他错误
    else {
      ElMessage.error(res.message || '系统异常')
      return Promise.reject(new Error(res.message || 'Error'))
    }
  },
  (error) => {
    console.error('响应拦截异常:', error)
    const status = error.response?.status
    if (status === 401) {
      ElMessage.error('登录状态已过期，请重新登录')
      localStorage.removeItem('token')
      // router.push('/login')
    } else {
      ElMessage.error(error.message || '网络请求异常，请稍后再试')
    }
    return Promise.reject(error)
  }
)

export default request
