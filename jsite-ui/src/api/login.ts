import request from '@/utils/http'

/**
 * 登录
 */
export function login(data: {
  username: string
  password: string
  code?: string
  uuid?: string
}) {
  return request({
    url: '/auth/login',
    method: 'post',
    data,
  })
}

/**
 * 获取用户信息
 */
export function getInfo() {
  return request({
    url: '/auth/getInfo',
    method: 'get',
  })
}

/**
 * 退出登录
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post',
  })
}

/**
 * 获取验证码
 */
export function getCaptcha() {
  return request({
    url: '/auth/captcha',
    method: 'get',
  })
}

/**
 * 获取路由
 */
export function getRouters() {
  return request({
    url: '/auth/getRouters',
    method: 'get',
  })
}
