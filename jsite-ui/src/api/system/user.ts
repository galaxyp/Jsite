import request from '@/utils/http'

/**
 * 查询用户列表
 */
export function listUser(query: any) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params: query,
  })
}

/**
 * 查询用户详细
 */
export function getUser(userId?: number) {
  return request({
    url: '/system/user/' + (userId || ''),
    method: 'get',
  })
}

/**
 * 新增用户
 */
export function addUser(data: any) {
  return request({
    url: '/system/user',
    method: 'post',
    data,
  })
}

/**
 * 修改用户
 */
export function updateUser(data: any) {
  return request({
    url: '/system/user',
    method: 'put',
    data,
  })
}

/**
 * 删除用户
 */
export function delUser(userId: number | number[]) {
  return request({
    url: '/system/user/' + userId,
    method: 'delete',
  })
}

/**
 * 用户密码重置
 */
export function resetUserPwd(userId: number, password: string) {
  return request({
    url: '/system/user/resetPwd',
    method: 'put',
    data: { userId, password },
  })
}

/**
 * 用户状态修改
 */
export function changeUserStatus(userId: number, status: string) {
  return request({
    url: '/system/user/changeStatus',
    method: 'put',
    data: { userId, status },
  })
}

/**
 * 查询部门下拉树结构
 */
export function deptTreeSelect() {
  return request({
    url: '/system/user/deptTree',
    method: 'get',
  })
}
