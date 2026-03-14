<template>
  <a-dropdown :trigger="['click']">
    <div class="theme-switcher">
      <BgColorsOutlined class="theme-icon" />
      <span class="theme-text">{{ currentTheme.name }}</span>
    </div>
    <template #overlay>
      <a-menu @click="handleThemeChange">
        <a-menu-item
          v-for="(config, mode) in THEME_CONFIGS"
          :key="mode"
          :class="{ 'active-theme': currentMode === mode }"
        >
          <div class="theme-option">
            <div class="theme-color-preview" :style="{ backgroundColor: config.primary }"></div>
            <span>{{ config.name }}</span>
            <CheckOutlined v-if="currentMode === mode" class="check-icon" />
          </div>
        </a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { BgColorsOutlined, CheckOutlined } from '@ant-design/icons-vue'
import { useThemeStore } from '@/store/modules/theme'
import { THEME_CONFIGS } from '@/types/theme'
import type { ThemeMode } from '@/types/theme'


const themeStore = useThemeStore()
const currentMode = computed(() => themeStore.currentMode)
const currentTheme = computed(() => themeStore.currentTheme)

const handleThemeChange = ({ key }: { key: string }) => {
  themeStore.setTheme(key as ThemeMode)
}
</script>

<style lang="less" scoped>
.theme-switcher {
  display: flex;
  align-items: center;
  padding: 0 12px;
  height: 48px;
  margin-right: 8px;
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 6px;

  &:hover {
    background: rgba(0, 0, 0, 0.04);
  }

  .theme-icon {
    font-size: 18px;
    color: rgba(0, 0, 0, 0.65);
  }

  .theme-text {
    margin-left: 8px;
    color: rgba(0, 0, 0, 0.85);
    font-size: 14px;
  }
}

/* 暗色主题下头部主题切换器样式 */
body[data-theme='dark'] .theme-switcher {
  .theme-icon,
  .theme-text {
    color: rgba(255, 255, 255, 0.85);
  }

  &:hover {
    background: rgba(255, 255, 255, 0.08);
  }
}

.theme-option {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 140px;

  .theme-color-preview {
    width: 16px;
    height: 16px;
    border-radius: 4px;
    border: 1px solid rgba(0, 0, 0, 0.08);
    flex-shrink: 0;
  }

  .check-icon {
    margin-left: auto;
    color: #52c41a;
    font-size: 12px;
  }
}

.active-theme {
  background: #e6f4ff;
}
</style>
