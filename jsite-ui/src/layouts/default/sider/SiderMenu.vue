<template>
  <a-menu
    v-model:selectedKeys="selectedKeys"
    v-model:openKeys="openKeys"
    mode="inline"
    theme="dark"
    :inline-collapsed="collapsed"
    @click="handleMenuClick"
  >
    <template v-for="menu in menuList" :key="menu.path">
      <template v-if="!menu.hidden && !menu.meta?.hidden">
        <!-- 只有一个子菜单：直接显示子菜单 -->
        <template v-if="menu.children && menu.children.length === 1 && !menu.children[0].meta?.hidden">
          <a-menu-item :key="getFullPath(menu.path, menu.children[0].path)">
            <template #icon>
              <component :is="getIcon(menu.children[0].meta?.icon || menu.meta?.icon)" />
            </template>
            {{ menu.children[0].meta?.title || menu.meta?.title }}
          </a-menu-item>
        </template>
        <!-- 有多个子菜单：显示子菜单组 -->
        <a-sub-menu v-else-if="menu.children && menu.children.length > 1" :key="menu.path">
          <template #icon>
            <component :is="getIcon(menu.meta?.icon)" />
          </template>
          <template #title>{{ menu.meta?.title }}</template>
          <template v-for="child in menu.children" :key="getFullPath(menu.path, child.path)">
            <a-menu-item v-if="!child.hidden && !child.meta?.hidden" :key="getFullPath(menu.path, child.path)">
              <template #icon>
                <component :is="getIcon(child.meta?.icon)" />
              </template>
              {{ child.meta?.title }}
            </a-menu-item>
          </template>
        </a-sub-menu>
        <!-- 无子菜单：直接显示 -->
        <a-menu-item v-else :key="menu.path">
          <template #icon>
            <component :is="getIcon(menu.meta?.icon)" />
          </template>
          {{ menu.meta?.title }}
        </a-menu-item>
      </template>
    </template>
  </a-menu>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import * as Icons from '@ant-design/icons-vue'
import { usePermissionStore } from '@/store/modules/permission'

const props = defineProps<{
  collapsed: boolean
}>()

const route = useRoute()
const router = useRouter()
const permissionStore = usePermissionStore()

const selectedKeys = ref<string[]>([])
const openKeys = ref<string[]>([])

// 菜单列表
const menuList = computed(() => {
  return permissionStore.sidebarRouters.filter(
    (item) => item.meta && !item.meta.hidden
  )
})

// 获取图标组件
const getIcon = (icon?: string) => {
  if (!icon) return null
  const iconName = icon.charAt(0).toUpperCase() + icon.slice(1) + 'Outlined'
  return (Icons as any)[iconName] || (Icons as any)['AppstoreOutlined']
}

// 获取完整路径
const getFullPath = (parentPath: string, childPath: string) => {
  if (childPath.startsWith('/')) {
    return childPath
  }
  // 处理父路径是 '/' 的情况，避免双斜杠
  if (parentPath === '/') {
    return '/' + childPath
  }
  return `${parentPath}/${childPath}`
}

// 监听路由变化
watch(
  () => route.path,
  (path) => {
    selectedKeys.value = [path]
    // 展开父菜单
    const matched = route.matched
    if (matched.length > 1) {
      openKeys.value = matched.slice(0, -1).map((item) => item.path)
    }
  },
  { immediate: true }
)

// 监听折叠状态
watch(
  () => props.collapsed,
  (val) => {
    if (val) {
      openKeys.value = []
    }
  }
)

// 菜单点击
const handleMenuClick = ({ key }: { key: string }) => {
  console.log('🔗 Menu clicked, navigating to:', key)
  router.push(key).then(() => {
    console.log('✅ Navigation success, current route:', route.path)
  }).catch(err => {
    console.error('❌ Navigation failed:', err)
  })
}
</script>

<style lang="less" scoped>
:deep(.ant-menu-dark) {
  background: #001529;
}
</style>
