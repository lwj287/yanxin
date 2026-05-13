const TOKEN_KEY = 'token'

export const getToken = () => uni.getStorageSync(TOKEN_KEY) || ''
export const setToken = (token) => uni.setStorageSync(TOKEN_KEY, token || '')
export const clearToken = () => uni.removeStorageSync(TOKEN_KEY)
