import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { message, Modal } from 'ant-design-vue'
import { useUserStore } from '@/store/modules/user'
import { getToken } from '@/utils/auth'

// 是否正在刷新Token
let isRefreshing = false
// 重试队列
let requests: ((token: string) => void)[] = []

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8',
  },
})

// 请求拦截器
service.interceptors.request.use(
  (config: AxiosRequestConfig): any => {
    const token = getToken()
    if (token) {
      config.headers = config.headers || {}
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data

    // 文件下载
    if (response.config.responseType === 'blob') {
      return response
    }

    // 业务错误
    if (res.code !== 200) {
      message.error(res.msg || '请求失败')

      // Token过期
      if (res.code === 401) {
        if (!isRefreshing) {
          isRefreshing = true
          Modal.confirm({
            title: '系统提示',
            content: '登录状态已过期，您可以继续留在该页面，或者重新登录',
            okText: '重新登录',
            cancelText: '取消',
            onOk: () => {
              isRefreshing = false
              const userStore = useUserStore()
              userStore.logout().then(() => {
                location.href = '/login'
              })
            },
            onCancel: () => {
              isRefreshing = false
            },
          })
        }
      }

      return Promise.reject(new Error(res.msg || 'Error'))
    }

    return res
  },
  (error) => {
    console.error('响应错误:', error)
    let errorMessage = error.message

    if (error.response) {
      switch (error.response.status) {
        case 400:
          errorMessage = '请求参数错误'
          break
        case 401:
          errorMessage = '未授权，请重新登录'
          break
        case 403:
          errorMessage = '拒绝访问'
          break
        case 404:
          errorMessage = '请求地址不存在'
          break
        case 500:
          errorMessage = '服务器内部错误'
          break
        default:
          errorMessage = `连接错误${error.response.status}`
      }
    } else if (error.message.includes('timeout')) {
      errorMessage = '请求超时'
    } else if (error.message.includes('Network')) {
      errorMessage = '网络异常'
    }

    message.error(errorMessage)
    return Promise.reject(error)
  }
)

export default service
