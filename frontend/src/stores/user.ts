import { defineStore } from 'pinia'

interface User {
  id: number
  username: string
  nickname: string
  email: string
  role: string
}

export const useUserStore = defineStore('user', {
  state: () => ({
    isLoggedIn: false,
    user: null as User | null,
    token: ''
  }),
  getters: {
    getUser: (state) => state.user,
    getToken: (state) => state.token,
    getIsLoggedIn: (state) => state.isLoggedIn
  },
  actions: {
    login(user: User, token: string) {
      this.isLoggedIn = true
      this.user = user
      this.token = token
      localStorage.setItem('user', JSON.stringify(user))
      localStorage.setItem('token', token)
    },
    logout() {
      this.isLoggedIn = false
      this.user = null
      this.token = ''
      localStorage.removeItem('user')
      localStorage.removeItem('token')
    },
    checkLogin() {
      const userStr = localStorage.getItem('user')
      const token = localStorage.getItem('token')
      if (userStr && token) {
        try {
          const user = JSON.parse(userStr)
          this.isLoggedIn = true
          this.user = user
          this.token = token
        } catch (error) {
          this.logout()
        }
      }
    }
  }
})
