import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { useUserStore } from '@/store/modules/user'
import { usePermissionStore } from '@/store/modules/permission'

// 配置 NProgress
NProgress.configure({ showSpinner: false })

// 白名单路由
const whiteList = ['/login', '/register', '/404', '/401']

// 静态路由
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hidden: true },
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404', hidden: true },
  },
  {
    path: '/401',
    name: '401',
    component: () => import('@/views/error/401.vue'),
    meta: { title: '401', hidden: true },
  },
  {
    path: '/',
    name: 'Index',
    component: () => import('@/layouts/default/index.vue'),
    redirect: '/dashboard',
    meta: { title: '首页', icon: 'dashboard' },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'dashboard', affix: true },
      },
    ],
  },
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior: () => ({ left: 0, top: 0 }),
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  NProgress.start()

  const userStore = useUserStore()
  const permissionStore = usePermissionStore()

  // 根路径 / 不渲染布局，直接重定向到登录或首页，避免空白
  if (to.path === '/') {
    if (userStore.token) {
      next('/dashboard')
    } else {
      next('/login?redirect=/')
    }
    NProgress.done()
    return
  }

  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - JSite` : 'JSite'

  // 判断是否已登录
  if (userStore.token) {
    if (to.path === '/login') {
      // 已登录且访问登录页：直接显示登录页，不重定向，避免因 getInfo 卡住而一直加载
      next()
      NProgress.done()
      return
    } else {
      // 判断是否已获取用户信息
      if (userStore.roles.length === 0) {
        try {
          // 获取用户信息（8 秒超时，避免后端无响应时一直白屏）
          const timeout = (ms: number) =>
            new Promise<never>((_, reject) => setTimeout(() => reject(new Error('timeout')), ms))
          await Promise.race([userStore.getInfo(), timeout(8000)])
          // 生成动态路由
          const accessRoutes = await permissionStore.generateRoutes()
          // 添加动态路由
          accessRoutes.forEach((route) => {
            try {
              router.addRoute(route)
            } catch (e) {
              console.error('[路由] 动态路由注册失败:', route.path, e)
            }
          })
          // 注意：不再添加 catch-all 404 路由，避免拦截静态路由
          next({ ...to, replace: true })
        } catch (error) {
          // 超时或失败：清除 Token 并跳转登录页
          await userStore.logout()
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      } else {
        next()
      }
    }
  } else {
    // 未登录
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
