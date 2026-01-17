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
        <!-- 有子菜单 -->
        <a-sub-menu v-if="menu.children && menu.children.length > 0" :key="menu.path">
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
        <!-- 无子菜单 -->
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
  router.push(key)
}
</script>

<style lang="less" scoped>
:deep(.ant-menu-dark) {
  background: #001529;
}
</style>
