import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: sessionStorage.getItem('token') || '',
    role: sessionStorage.getItem('role') || '',
    realName: sessionStorage.getItem('realName') || ''
  }),
  actions: {
    setLogin(data) {
      this.token = data.token
      this.role = data.role
      this.realName = data.realName
      sessionStorage.setItem('token', data.token)
      sessionStorage.setItem('role', data.role)
      sessionStorage.setItem('realName', data.realName)
    },
    logout() {
      this.token = ''
      this.role = ''
      this.realName = ''
      sessionStorage.clear()
    }
  }
})
