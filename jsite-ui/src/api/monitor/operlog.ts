import request from '@/utils/request'

/**
 * 查询操作日志列表
 */
export function listOperlog(query: any) {
  return request({
    url: '/monitor/operlog/list',
    method: 'get',
    params: query
  })
}

/**
 * 获取操作日志详细
 */
export function getOperlog(operId: number) {
  return request({
    url: `/monitor/operlog/${operId}`,
    method: 'get'
  })
}

/**
 * 删除操作日志
 */
export function delOperlog(operId: string | number) {
  return request({
    url: `/monitor/operlog/${operId}`,
    method: 'delete'
  })
}

/**
 * 清空操作日志
 */
export function cleanOperlog() {
  return request({
    url: '/monitor/operlog/clean',
    method: 'delete'
  })
}
