import request from '@/utils/http'

/**
 * 查询生成表数�?
 */
export function listTable(query: any) {
  return request({
    url: '/tool/gen/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询数据库列�?
 */
export function listDbTable(query: any) {
  return request({
    url: '/tool/gen/db/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询表详细信�?
 */
export function getGenTable(tableId: number) {
  return request({
    url: `/tool/gen/${tableId}`,
    method: 'get'
  })
}

/**
 * 修改代码生成信息
 */
export function updateGenTable(data: any) {
  return request({
    url: '/tool/gen',
    method: 'put',
    data: data
  })
}

/**
 * 导入�?
 */
export function importTable(tables: string) {
  return request({
    url: '/tool/gen/importTable',
    method: 'post',
    params: { tables }
  })
}

/**
 * 删除表数�?
 */
export function delTable(tableId: string | number) {
  return request({
    url: `/tool/gen/${tableId}`,
    method: 'delete'
  })
}

/**
 * 预览生成代码
 */
export function previewTable(tableId: number) {
  return request({
    url: `/tool/gen/preview/${tableId}`,
    method: 'get'
  })
}

/**
 * 生成代码（自定义路径�?
 */
export function genCode(tableName: string) {
  return request({
    url: `/tool/gen/genCode/${tableName}`,
    method: 'get'
  })
}

/**
 * 同步数据�?
 */
export function synchDb(tableName: string) {
  return request({
    url: `/tool/gen/synchDb/${tableName}`,
    method: 'get'
  })
}
