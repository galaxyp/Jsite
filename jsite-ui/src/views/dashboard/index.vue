<template>
  <div class="dashboard-container">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <h1 class="welcome-title">欢迎使用 JSite</h1>
        <p class="welcome-desc">快速开发平台 · 让开发更高效</p>
      </div>
      <div class="welcome-decoration">
        <div class="decoration-circle c1"></div>
        <div class="decoration-circle c2"></div>
        <div class="decoration-circle c3"></div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div
        v-for="(item, index) in statItems"
        :key="item.title"
        class="stat-card"
        :class="`stat-card-${index}`"
      >
        <div class="stat-icon" :style="{ background: item.iconBg }">
          <component :is="item.icon" class="stat-icon-svg" />
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-title">{{ item.title }}</div>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <div class="content-left">
        <div class="feature-card">
          <div class="feature-header">
            <span class="feature-badge">功能特点</span>
            <h3>平台能力</h3>
          </div>
          <ul class="feature-list">
            <li>
              <span class="feature-dot"></span>
              <strong>前后端分离</strong>：Spring Boot 3 + Vue 3 + TypeScript
            </li>
            <li>
              <span class="feature-dot"></span>
              <strong>权限管理</strong>：Sa-Token 细粒度控制，菜单/按钮/数据权限
            </li>
            <li>
              <span class="feature-dot"></span>
              <strong>代码生成</strong>：单表、树表、主子表一键生成
            </li>
            <li>
              <span class="feature-dot"></span>
              <strong>系统监控</strong>：操作日志、在线用户、服务监控
            </li>
            <li>
              <span class="feature-dot"></span>
              <strong>易于扩展</strong>：模块化设计，业务热插拔
            </li>
          </ul>
        </div>

        <div class="tech-card">
          <h4>技术栈</h4>
          <div class="tech-tags">
            <span v-for="tag in techTags" :key="tag.name" class="tech-tag" :style="{ borderColor: tag.color }">
              {{ tag.name }}
            </span>
          </div>
        </div>
      </div>

      <div class="content-right">
        <div class="quick-card">
          <h4>快捷入口</h4>
          <div class="quick-links">
            <a
              v-for="link in quickLinks"
              :key="link.path"
              class="quick-link"
              @click="$router.push(link.path)"
            >
              <span class="quick-icon"><component :is="link.icon" /></span>
              <span class="quick-text">{{ link.name }}</span>
              <span class="quick-arrow">→</span>
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  UserOutlined,
  TeamOutlined,
  AppstoreOutlined,
  ApartmentOutlined,
  CodeOutlined,
} from '@ant-design/icons-vue'

const statistics = {
  userCount: 2,
  roleCount: 2,
  menuCount: 60,
  deptCount: 10,
}

const statItems = computed(() => [
  {
    title: '用户总数',
    value: statistics.userCount,
    icon: UserOutlined,
    iconBg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  },
  {
    title: '角色总数',
    value: statistics.roleCount,
    icon: TeamOutlined,
    iconBg: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)',
  },
  {
    title: '菜单总数',
    value: statistics.menuCount,
    icon: AppstoreOutlined,
    iconBg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
  },
  {
    title: '部门总数',
    value: statistics.deptCount,
    icon: ApartmentOutlined,
    iconBg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
  },
])

const techTags = [
  { name: 'Spring Boot 3', color: '#52c41a' },
  { name: 'Vue 3', color: '#42b883' },
  { name: 'TypeScript', color: '#3178c6' },
  { name: 'Ant Design Vue', color: '#1890ff' },
  { name: 'MyBatis-Plus', color: '#e6a23c' },
  { name: 'Sa-Token', color: '#722ed1' },
  { name: 'Redis', color: '#dc382d' },
  { name: 'MySQL', color: '#4479a1' },
]

const quickLinks = [
  { name: '用户管理', path: '/system/user', icon: UserOutlined },
  { name: '角色管理', path: '/system/role', icon: TeamOutlined },
  { name: '菜单管理', path: '/system/menu', icon: AppstoreOutlined },
  { name: '代码生成', path: '/tool/gen', icon: CodeOutlined },
]
</script>

<style lang="less" scoped>
.dashboard-container {
  max-width: 1400px;
  margin: 0 auto;
}

/* 欢迎横幅 */
.welcome-banner {
  position: relative;
  padding: 32px 40px;
  margin-bottom: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.35);

  .welcome-content {
    position: relative;
    z-index: 1;
  }

  .welcome-title {
    margin: 0 0 8px 0;
    font-size: 28px;
    font-weight: 700;
    color: #fff;
    letter-spacing: -0.5px;
  }

  .welcome-desc {
    margin: 0;
    font-size: 15px;
    color: rgba(255, 255, 255, 0.9);
  }

  .welcome-decoration {
    position: absolute;
    right: 40px;
    top: 50%;
    transform: translateY(-50%);
  }

  .decoration-circle {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.15);
  }

  .c1 {
    width: 120px;
    height: 120px;
    right: 0;
    top: -20px;
  }

  .c2 {
    width: 80px;
    height: 80px;
    right: 80px;
    top: 20px;
  }

  .c3 {
    width: 40px;
    height: 40px;
    right: 140px;
    top: -10px;
  }
}

/* 统计卡片 */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03), 0 1px 6px -1px rgba(0, 0, 0, 0.02);
  border: 1px solid rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
  }

  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    .stat-icon-svg {
      font-size: 28px;
      color: #fff;
    }
  }

  .stat-info {
    flex: 1;
    min-width: 0;
  }

  .stat-value {
    font-size: 28px;
    font-weight: 700;
    color: #1f2937;
    line-height: 1.2;
  }

  .stat-title {
    font-size: 14px;
    color: #6b7280;
    margin-top: 4px;
  }
}

/* 主内容区 */
.main-content {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 24px;
}

.content-left {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-card {
  padding: 28px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03), 0 1px 6px -1px rgba(0, 0, 0, 0.02);
  border: 1px solid rgba(0, 0, 0, 0.04);

  .feature-header {
    margin-bottom: 20px;

    .feature-badge {
      display: inline-block;
      padding: 4px 8px;
      font-size: 12px;
      color: #667eea;
      background: rgba(102, 126, 234, 0.1);
      border-radius: 6px;
      margin-bottom: 8px;
    }

    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      color: #1f2937;
    }
  }

  .feature-list {
    margin: 0;
    padding: 0;
    list-style: none;

    li {
      display: flex;
      align-items: flex-start;
      gap: 12px;
      padding: 10px 0;
      font-size: 14px;
      color: #4b5563;
      line-height: 1.6;
      border-bottom: 1px solid #f3f4f6;

      &:last-child {
        border-bottom: none;
      }

      strong {
        color: #1f2937;
        margin-right: 4px;
      }
    }
  }

  .feature-dot {
    width: 6px;
    height: 6px;
    margin-top: 8px;
    border-radius: 50%;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    flex-shrink: 0;
  }
}

.tech-card {
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03), 0 1px 6px -1px rgba(0, 0, 0, 0.02);
  border: 1px solid rgba(0, 0, 0, 0.04);

  h4 {
    margin: 0 0 16px 0;
    font-size: 16px;
    font-weight: 600;
    color: #1f2937;
  }

  .tech-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }

  .tech-tag {
    padding: 6px 12px;
    font-size: 13px;
    color: #4b5563;
    background: #fff;
    border: 1px solid;
    border-radius: 8px;
    transition: all 0.2s;

    &:hover {
      background: rgba(0, 0, 0, 0.02);
    }
  }
}

.quick-card {
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03), 0 1px 6px -1px rgba(0, 0, 0, 0.02);
  border: 1px solid rgba(0, 0, 0, 0.04);
  height: fit-content;
  position: sticky;
  top: 20px;

  h4 {
    margin: 0 0 20px 0;
    font-size: 16px;
    font-weight: 600;
    color: #1f2937;
  }

  .quick-links {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .quick-link {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 14px 16px;
    background: #f9fafb;
    border-radius: 10px;
    cursor: pointer;
    transition: all 0.2s;
    text-decoration: none;
    color: inherit;
    border: 1px solid transparent;

    &:hover {
      background: #f3f4f6;
      border-color: rgba(102, 126, 234, 0.3);
      transform: translateX(4px);

      .quick-arrow {
        opacity: 1;
        color: #667eea;
      }
    }

    .quick-icon {
      width: 36px;
      height: 36px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 8px;
      color: #fff;
      font-size: 16px;
    }

    .quick-text {
      flex: 1;
      font-size: 14px;
      font-weight: 500;
      color: #1f2937;
    }

    .quick-arrow {
      font-size: 16px;
      opacity: 0.5;
      transition: all 0.2s;
    }
  }
}

/* 暗色主题适配 */
body[data-theme='dark'] {
  .welcome-banner {
    background: linear-gradient(135deg, #4c1d95 0%, #2d1b69 100%);
    box-shadow: 0 8px 32px rgba(76, 29, 149, 0.4);
  }

  .stat-card,
  .feature-card,
  .tech-card,
  .quick-card {
    background: var(--card-bg);
    border-color: var(--border-color);
  }

  .stat-value {
    color: var(--text-color);
  }

  .stat-title {
    color: rgba(255, 255, 255, 0.65);
  }

  .feature-header h3,
  .tech-card h4,
  .quick-card h4 {
    color: var(--text-color);
  }

  .feature-list li strong,
  .quick-link .quick-text {
    color: var(--text-color);
  }

  .feature-list li {
    color: rgba(255, 255, 255, 0.75);
    border-bottom-color: rgba(255, 255, 255, 0.08);
  }

  .tech-tag {
    color: rgba(255, 255, 255, 0.9);
    background: rgba(255, 255, 255, 0.08) !important;
    border-color: rgba(255, 255, 255, 0.2) !important;
  }

  .quick-link {
    background: rgba(255, 255, 255, 0.08);

    &:hover {
      background: rgba(255, 255, 255, 0.12);
    }
  }
}

/* 响应式 */
@media (max-width: 1200px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .main-content {
    grid-template-columns: 1fr;
  }

  .quick-card {
    position: static;
  }
}

@media (max-width: 768px) {
  .stat-cards {
    grid-template-columns: 1fr;
  }

  .welcome-banner {
    padding: 24px;
  }

  .welcome-decoration {
    display: none;
  }
}
</style>
