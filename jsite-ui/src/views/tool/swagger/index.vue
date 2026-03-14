<template>
  <div class="swagger-container">
    <iframe :src="swaggerUrl" class="swagger-iframe" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useThemeStore } from '@/store/modules/theme'

const themeStore = useThemeStore()

const swaggerUrl = computed(() => {
  const backendUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'
  const base = `${backendUrl}/doc.html`
  // 暗色主题时尝试传递 theme 参数（部分版本 Knife4j 支持）
  if (themeStore.currentMode === 'dark') {
    return `${base}?theme=dark`
  }
  return base
})
</script>

<style scoped>
.swagger-container {
  width: 100%;
  height: 100%;
  min-height: calc(100vh - 120px);
  background: #fff;
}

.swagger-iframe {
  width: 100%;
  height: calc(100vh - 84px);
  border: none;
  display: block;
}
</style>
