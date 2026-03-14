<template>
  <a-config-provider :locale="zhCN" :theme="antdTheme">
    <router-view />
  </a-config-provider>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import zhCN from 'ant-design-vue/es/locale/zh_CN'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import { useThemeStore } from '@/store/modules/theme'

dayjs.locale('zh-cn')

const themeStore = useThemeStore()
const antdTheme = computed(() => themeStore.getAntdTheme())
</script>

<style lang="less">
@import '@/assets/styles/theme.css';

#app {
  width: 100%;
  height: 100%;
}
</style>
