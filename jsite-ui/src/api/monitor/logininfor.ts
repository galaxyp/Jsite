import request from '@/utils/http'

/**
 * 查询登录日志列表
 */
export function listLogininfor(query: any) {
  return request({
    url: '/monitor/logininfor/list',
    method: 'get',
    params: query
  })
}

/**
 * 删除登录日志
 */
export function delLogininfor(infoId: string | number) {
  return request({
    url: `/monitor/logininfor/${infoId}`,
    method: 'delete'
  })
}

/**
 * 清空登录日志
 */
export function cleanLogininfor() {
  return request({
    url: '/monitor/logininfor/clean',
    method: 'delete'
  })
}

/**
 * 解锁用户登录状�?
 */
export function unlockLogininfor(userName: string) {
  return request({
    url: `/monitor/logininfor/unlock/${userName}`,
    method: 'get'
  })
}
