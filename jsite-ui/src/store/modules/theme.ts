import { defineStore } from 'pinia'
import { ref, watch } from 'vue'
import type { ThemeMode, ThemeConfig } from '@/types/theme'
import { THEME_CONFIGS } from '@/types/theme'
import { theme } from 'ant-design-vue'

const THEME_STORAGE_KEY = 'jsite-theme'

const VALID_MODES: ThemeMode[] = ['light', 'dark', 'blue', 'gray']

function getInitialMode(): ThemeMode {
  const saved = localStorage.getItem(THEME_STORAGE_KEY) as ThemeMode | null
  return saved && VALID_MODES.includes(saved) ? saved : 'light'
}

export const useThemeStore = defineStore('theme', () => {
  // 从 localStorage 读取保存的主题，仅接受合法值，否则用 light
  const currentMode = ref<ThemeMode>(getInitialMode())
  const currentTheme = ref<ThemeConfig>(THEME_CONFIGS[currentMode.value])

  // 切换主题
  const setTheme = (mode: ThemeMode) => {
    const safeMode = VALID_MODES.includes(mode) ? mode : 'light'
    currentMode.value = safeMode
    currentTheme.value = THEME_CONFIGS[safeMode]
    applyTheme()
    localStorage.setItem(THEME_STORAGE_KEY, safeMode)
  }

  // 应用主题到页面（确保 config 存在，避免 localStorage 脏数据导致报错）
  const applyTheme = () => {
    const config = currentTheme.value ?? THEME_CONFIGS.light
    const root = document.documentElement

    // 设置 CSS 变量
    root.style.setProperty('--primary-color', config.primary)
    root.style.setProperty('--success-color', config.success)
    root.style.setProperty('--warning-color', config.warning)
    root.style.setProperty('--error-color', config.error)
    root.style.setProperty('--info-color', config.info)
    root.style.setProperty('--bg-color', config.bgColor)
    root.style.setProperty('--text-color', config.textColor)
    root.style.setProperty('--border-color', config.borderColor)
    root.style.setProperty('--header-bg', config.headerBg)
    root.style.setProperty('--sider-bg', config.siderBg)
    root.style.setProperty('--menu-active-bg', config.menuActiveBg)
    root.style.setProperty('--card-bg', config.cardBg)

    // 设置 body 的 data-theme 属性（用于 CSS 选择器）
    document.body.setAttribute('data-theme', config.mode)
  }

  // 获取 Ant Design Vue 主题配置
  const getAntdTheme = () => {
    const config = currentTheme.value ?? THEME_CONFIGS.light
    const isDark = config.mode === 'dark'

    return {
      algorithm: isDark ? theme.darkAlgorithm : theme.defaultAlgorithm,
      token: {
        colorPrimary: config.primary,
        colorSuccess: config.success,
        colorWarning: config.warning,
        colorError: config.error,
        colorInfo: config.info,
        colorBgContainer: config.cardBg,
        colorBgLayout: config.bgColor,
        colorText: config.textColor,
        colorBorder: config.borderColor,
      },
    }
  }

  // 初始化时应用主题
  applyTheme()

  // 监听主题变化
  watch(currentMode, () => {
    applyTheme()
  })

  return {
    currentMode,
    currentTheme,
    setTheme,
    applyTheme,
    getAntdTheme,
  }
})
