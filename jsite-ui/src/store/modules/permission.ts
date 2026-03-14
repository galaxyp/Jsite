import { defineStore } from 'pinia'
import { RouteRecordRaw } from 'vue-router'
import { constantRoutes } from '@/router'
import { getRouters } from '@/api/login'

// 动态导入视图组件
const modules = import.meta.glob('../../views/**/*.vue')

// 特殊布局组件
const Layout = () => import('@/layouts/default/index.vue')
const ParentView = () => import('@/layouts/ParentView.vue')
const InnerLink = () => import('@/layouts/InnerLink.vue')

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

    // 跳过 path 无效的路由（防止 router.addRoute 异常导致整棵路由树注册失败）
    if (!tmp.path) {
      console.warn('[路由] 跳过无效路由（path 为空）:', tmp.name || tmp.meta?.title)
      return
    }

    // 处理组件
    if (tmp.component) {
      const componentStr = tmp.component as string
      if (componentStr === 'Layout') {
        tmp.component = Layout
      } else if (componentStr === 'ParentView') {
        tmp.component = ParentView
      } else if (componentStr === 'InnerLink') {
        tmp.component = InnerLink
      } else {
        const loaded = loadComponent(componentStr)
        if (loaded) {
          tmp.component = loaded
        } else {
          console.warn(`[路由] 组件文件不存在: views/${componentStr}.vue，已跳过该路由`)
          return
        }
      }
    }

    // 处理子路由（去重，防止同 path 的子路由覆盖已有路由）
    if (tmp.children && tmp.children.length > 0) {
      const filtered = filterAsyncRoutes(tmp.children)
      const seen = new Set<string>()
      tmp.children = filtered.filter((child) => {
        if (seen.has(child.path as string)) {
          console.warn('[路由] 跳过重复子路由:', child.path)
          return false
        }
        seen.add(child.path as string)
        return true
      })
    }

    res.push(tmp)
  })

  return res
}

/**
 * 加载视图组件
 */
function loadComponent(component: string) {
  const path = `../../views/${component}.vue`
  return modules[path] || null
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
