import request from '@/utils/http'

/**
 * 获取服务器信�?
 */
export function getServer() {
  return request({
    url: '/monitor/server',
    method: 'get'
  })
}
