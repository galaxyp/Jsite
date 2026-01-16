import request from '@/utils/request'

/**
 * 获取缓存监控信息
 */
export function getCache() {
  return request({
    url: '/monitor/cache',
    method: 'get'
  })
}

/**
 * 获取缓存名称列表
 */
export function getCacheNames() {
  return request({
    url: '/monitor/cache/getNames',
    method: 'get'
  })
}

/**
 * 获取缓存键名列表
 */
export function getCacheKeys(cacheName: string) {
  return request({
    url: `/monitor/cache/getKeys/${cacheName}`,
    method: 'get'
  })
}

/**
 * 获取缓存内容
 */
export function getCacheValue(cacheName: string, cacheKey: string) {
  return request({
    url: `/monitor/cache/getValue/${cacheName}/${cacheKey}`,
    method: 'get'
  })
}

/**
 * 清理缓存名称
 */
export function clearCacheName(cacheName: string) {
  return request({
    url: `/monitor/cache/clearCacheName/${cacheName}`,
    method: 'delete'
  })
}

/**
 * 清理缓存键名
 */
export function clearCacheKey(cacheKey: string) {
  return request({
    url: `/monitor/cache/clearCacheKey/${cacheKey}`,
    method: 'delete'
  })
}

/**
 * 清理全部缓存
 */
export function clearCacheAll() {
  return request({
    url: '/monitor/cache/clearCacheAll',
    method: 'delete'
  })
}
