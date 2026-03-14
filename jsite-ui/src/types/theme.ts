/**
 * 主题类型定义
 */

export type ThemeMode = 'light' | 'dark' | 'blue' | 'gray'

export interface ThemeConfig {
  mode: ThemeMode
  name: string
  primary: string
  success: string
  warning: string
  error: string
  info: string
  bgColor: string
  textColor: string
  borderColor: string
  headerBg: string
  siderBg: string
  menuActiveBg: string
  cardBg: string
}

export const THEME_CONFIGS: Record<ThemeMode, ThemeConfig> = {
  light: {
    mode: 'light',
    name: '经典白',
    primary: '#1677ff',
    success: '#52c41a',
    warning: '#faad14',
    error: '#ff4d4f',
    info: '#1677ff',
    bgColor: '#f5f5f5',
    textColor: 'rgba(0, 0, 0, 0.88)',
    borderColor: '#e8e8e8',
    headerBg: '#ffffff',
    siderBg: '#001529',
    menuActiveBg: '#1677ff',
    cardBg: '#ffffff',
  },
  dark: {
    mode: 'dark',
    name: '深邃黑',
    primary: '#4096ff',
    success: '#49aa19',
    warning: '#d89614',
    error: '#d32029',
    info: '#4096ff',
    bgColor: '#141414',
    textColor: 'rgba(255, 255, 255, 0.88)',
    borderColor: '#424242',
    headerBg: '#1f1f1f',
    siderBg: '#0d1117',
    menuActiveBg: '#4096ff',
    cardBg: '#1f1f1f',
  },
  blue: {
    mode: 'blue',
    name: '海洋蓝',
    primary: '#0960bd',
    success: '#00b96b',
    warning: '#ed7b2f',
    error: '#d32029',
    info: '#0960bd',
    bgColor: '#e6f4ff',
    textColor: '#1d2129',
    borderColor: '#bae0ff',
    headerBg: '#ffffff',
    siderBg: '#0c1e3c',
    menuActiveBg: '#0960bd',
    cardBg: '#ffffff',
  },
  gray: {
    mode: 'gray',
    name: '雾灰',
    primary: '#475569',
    success: '#059669',
    warning: '#d97706',
    error: '#dc2626',
    info: '#475569',
    bgColor: '#f8fafc',
    textColor: '#334155',
    borderColor: '#e2e8f0',
    headerBg: '#ffffff',
    siderBg: '#1e293b',
    menuActiveBg: '#475569',
    cardBg: '#ffffff',
  },
}
