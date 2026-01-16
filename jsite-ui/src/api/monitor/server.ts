import request from '@/utils/request'

/**
 * 获取服务器信息
 */
export function getServer() {
  return request({
    url: '/monitor/server',
    method: 'get'
  })
}
