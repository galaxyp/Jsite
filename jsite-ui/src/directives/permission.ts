import { App, Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '@/store/modules/user'

/**
 * 权限指令
 * 使用方式：v-permission="['system:user:add']"
 */
const permissionDirective: Directive = {
  mounted(el: Element, binding: DirectiveBinding) {
    const { value } = binding
    const userStore = useUserStore()
    const permissions = userStore.permissions
    const all_permission = '*:*:*'

    if (value && value instanceof Array && value.length > 0) {
      const hasPermission = permissions.some((permission: string) => {
        return all_permission === permission || value.includes(permission)
      })

      if (!hasPermission) {
        el.parentNode?.removeChild(el)
      }
    } else {
      throw new Error('需要指定权限标识，如 v-permission="[\'system:user:add\']"')
    }
  },
}

/**
 * 角色指令
 * 使用方式：v-role="['admin']"
 */
const roleDirective: Directive = {
  mounted(el: Element, binding: DirectiveBinding) {
    const { value } = binding
    const userStore = useUserStore()
    const roles = userStore.roles
    const super_admin = 'admin'

    if (value && value instanceof Array && value.length > 0) {
      const hasRole = roles.some((role: string) => {
        return super_admin === role || value.includes(role)
      })

      if (!hasRole) {
        el.parentNode?.removeChild(el)
      }
    } else {
      throw new Error('需要指定角色标识，如 v-role="[\'admin\']"')
    }
  },
}

/**
 * 注册权限指令
 */
export function setupPermissionDirective(app: App) {
  app.directive('permission', permissionDirective)
  app.directive('role', roleDirective)
}

export { permissionDirective, roleDirective }
