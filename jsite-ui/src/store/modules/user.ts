import { defineStore } from 'pinia'
import { login, logout, getInfo } from '@/api/login'
import { getToken, setToken, removeToken } from '@/utils/auth'

interface UserState {
  token: string
  id: number | null
  name: string
  avatar: string
  roles: string[]
  permissions: string[]
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: getToken() || '',
    id: null,
    name: '',
    avatar: '',
    roles: [],
    permissions: [],
  }),

  actions: {
    /**
     * 登录
     */
    async login(loginForm: { username: string; password: string; code?: string; uuid?: string }) {
      const { username, password, code, uuid } = loginForm
      const res = await login({ username: username.trim(), password, code, uuid })
      const token = res.data.token
      this.token = token
      setToken(token)
      return res
    },

    /**
     * 获取用户信息
     */
    async getInfo() {
      const res = await getInfo()
      const user = res.data.user
      const roles = res.data.roles
      const permissions = res.data.permissions

      if (roles && roles.length > 0) {
        this.roles = roles
        this.permissions = permissions
      } else {
        this.roles = ['ROLE_DEFAULT']
      }

      this.id = user.userId
      this.name = user.userName
      this.avatar = user.avatar || ''

      return res
    },

    /**
     * 退出登录
     */
    async logout() {
      try {
        await logout()
      } catch (error) {
        console.error('退出登录失败', error)
      } finally {
        this.token = ''
        this.roles = []
        this.permissions = []
        removeToken()
      }
    },

    /**
     * 重置Token
     */
    resetToken() {
      this.token = ''
      this.roles = []
      this.permissions = []
      removeToken()
    },
  },

  persist: {
    key: 'jsite-user',
    storage: localStorage,
    paths: ['token'],
  },
})
