import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res?.code === undefined) {
      return res
    }
    const code = Number(res.code)
    if (code === 200) {
      if (res.data !== undefined) {
        return res.data
      }
      if (res.result !== undefined) {
        return res.result
      }
      return null
    }
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || 'Request Error'))
  },
  (error) => {
    ElMessage.error(error?.message || '网络请求异常，请稍后再试')
    return Promise.reject(error)
  }
)

export default request
