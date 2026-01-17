<template>
  <a-layout class="layout-container">
    <!-- 侧边栏 -->
    <a-layout-sider
      v-model:collapsed="collapsed"
      :trigger="null"
      collapsible
      :width="220"
      :collapsed-width="48"
      class="layout-sider"
    >
      <div class="logo">
        <img src="@/assets/images/logo.png" alt="logo" v-if="!collapsed" />
        <h1 v-if="!collapsed">JSite</h1>
      </div>
      <SiderMenu :collapsed="collapsed" />
    </a-layout-sider>

    <a-layout>
      <!-- 头部 -->
      <a-layout-header class="layout-header">
        <div class="header-left">
          <MenuFoldOutlined
            v-if="!collapsed"
            class="trigger"
            @click="collapsed = true"
          />
          <MenuUnfoldOutlined
            v-else
            class="trigger"
            @click="collapsed = false"
          />
          <a-breadcrumb class="breadcrumb">
            <a-breadcrumb-item v-for="item in breadcrumb" :key="item.path">
              {{ item.meta?.title }}
            </a-breadcrumb-item>
          </a-breadcrumb>
        </div>
        <div class="header-right">
          <a-dropdown>
            <div class="user-info">
              <a-avatar :size="28">
                <template #icon><UserOutlined /></template>
              </a-avatar>
              <span class="username">{{ userStore.name }}</span>
            </div>
            <template #overlay>
              <a-menu>
                <a-menu-item key="profile">
                  <UserOutlined />
                  <span>个人中心</span>
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout" @click="handleLogout">
                  <LogoutOutlined />
                  <span>退出登录</span>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>

      <!-- 标签页 -->
      <div class="layout-tabs">
        <a-tabs
          v-model:activeKey="activeTab"
          type="editable-card"
          hide-add
          @edit="handleTabEdit"
          @change="handleTabChange"
        >
          <a-tab-pane
            v-for="tab in tabList"
            :key="tab.path"
            :tab="tab.meta?.title"
            :closable="tab.meta?.affix !== true"
          />
        </a-tabs>
      </div>

      <!-- 内容区 -->
      <a-layout-content class="layout-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <keep-alive :include="cachedViews">
              <component :is="Component" />
            </keep-alive>
          </transition>
        </router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter, RouteLocationMatched } from 'vue-router'
import { Modal } from 'ant-design-vue'
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  UserOutlined,
  LogoutOutlined,
} from '@ant-design/icons-vue'
import { useUserStore } from '@/store/modules/user'
import SiderMenu from './sider/SiderMenu.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const collapsed = ref(false)
const activeTab = ref('')
const tabList = ref<RouteLocationMatched[]>([])
const cachedViews = ref<string[]>([])

// 面包屑
const breadcrumb = computed(() => {
  return route.matched.filter((item) => item.meta && item.meta.title)
})

/**
 * 添加标签页
 */
const addTab = () => {
  const { path, meta, name } = route
  if (meta?.hidden) return

  const exist = tabList.value.find((item) => item.path === path)
  if (!exist) {
    tabList.value.push({ path, meta, name } as RouteLocationMatched)
  }

  // 缓存组件
  if (name && meta?.cache !== false) {
    if (!cachedViews.value.includes(name as string)) {
      cachedViews.value.push(name as string)
    }
  }
}

// 监听路由变化，更新标签页
watch(
  () => route.path,
  () => {
    activeTab.value = route.path
    addTab()
  },
  { immediate: true }
)

/**
 * 处理标签页编辑（关闭）
 */
const handleTabEdit = (targetKey: string) => {
  const index = tabList.value.findIndex((item) => item.path === targetKey)
  if (index !== -1) {
    const tab = tabList.value[index]
    tabList.value.splice(index, 1)

    // 移除缓存
    const nameIndex = cachedViews.value.indexOf(tab.name as string)
    if (nameIndex !== -1) {
      cachedViews.value.splice(nameIndex, 1)
    }

    // 如果关闭的是当前标签，跳转到最后一个标签
    if (targetKey === activeTab.value && tabList.value.length > 0) {
      const lastTab = tabList.value[tabList.value.length - 1]
      router.push(lastTab.path)
    }
  }
}

/**
 * 处理标签页切换
 */
const handleTabChange = (key: string) => {
  router.push(key)
}

/**
 * 退出登录
 */
const handleLogout = () => {
  Modal.confirm({
    title: '提示',
    content: '确定要退出登录吗？',
    okText: '确定',
    cancelText: '取消',
    onOk: async () => {
      await userStore.logout()
      router.push('/login')
    },
  })
}
</script>

<style lang="less" scoped>
.layout-container {
  height: 100vh;
}

.layout-sider {
  background: #001529;
  overflow: auto;

  .logo {
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 16px;
    background: rgba(255, 255, 255, 0.1);

    img {
      width: 28px;
      height: 28px;
      margin-right: 8px;
    }

    h1 {
      color: #fff;
      font-size: 18px;
      font-weight: 600;
      margin: 0;
    }
  }
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  height: 48px;
  line-height: 48px;

  .header-left {
    display: flex;
    align-items: center;

    .trigger {
      font-size: 18px;
      cursor: pointer;
      padding: 0 12px;

      &:hover {
        color: #1890ff;
      }
    }

    .breadcrumb {
      margin-left: 16px;
    }
  }

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      padding: 0 12px;

      &:hover {
        background: rgba(0, 0, 0, 0.025);
      }

      .username {
        margin-left: 8px;
      }
    }
  }
}

.layout-tabs {
  background: #fff;
  padding: 0 16px;
  border-bottom: 1px solid #e8e8e8;

  :deep(.ant-tabs) {
    .ant-tabs-nav {
      margin: 0;
    }
  }
}

.layout-content {
  margin: 16px;
  padding: 16px;
  background: #fff;
  border-radius: 4px;
  overflow: auto;
}

// 页面过渡动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
