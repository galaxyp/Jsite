import { defineStore } from 'pinia'
import { RouteRecordRaw } from 'vue-router'
import { constantRoutes } from '@/router'
import { getRouters } from '@/api/login'

// 动态导入视图组件
const modules = import.meta.glob('../../views/**/*.vue')

// Layout组件
const Layout = () => import('@/layouts/default/index.vue')

interface PermissionState {
  routes: RouteRecordRaw[]
  addRoutes: RouteRecordRaw[]
  sidebarRouters: RouteRecordRaw[]
}

/**
 * 过滤动态路由
 */
function filterAsyncRoutes(routes: any[]): RouteRecordRaw[] {
  const res: RouteRecordRaw[] = []

  routes.forEach((route) => {
    const tmp = { ...route }

    // 处理组件
    if (tmp.component) {
      if (tmp.component === 'Layout') {
        tmp.component = Layout
      } else {
        const component = tmp.component as string
        tmp.component = loadComponent(component)
      }
    }

    // 处理子路由
    if (tmp.children && tmp.children.length > 0) {
      tmp.children = filterAsyncRoutes(tmp.children)
    }

    res.push(tmp)
  })

  return res
}

/**
 * 加载组件
 */
function loadComponent(component: string) {
  const path = `../../views/${component}.vue`
  return modules[path]
}

export const usePermissionStore = defineStore('permission', {
  state: (): PermissionState => ({
    routes: [],
    addRoutes: [],
    sidebarRouters: [],
  }),

  actions: {
    /**
     * 生成路由
     */
    async generateRoutes(): Promise<RouteRecordRaw[]> {
      const res = await getRouters()
      const asyncRoutes = res.data

      // 过滤动态路由
      const accessedRoutes = filterAsyncRoutes(asyncRoutes)

      this.addRoutes = accessedRoutes
      this.routes = constantRoutes.concat(accessedRoutes)
      this.sidebarRouters = constantRoutes.concat(accessedRoutes)

      return accessedRoutes
    },
  },
})
