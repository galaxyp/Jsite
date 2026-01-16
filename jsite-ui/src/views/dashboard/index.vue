<template>
  <div class="dashboard-container">
    <a-row :gutter="16">
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="用户总数"
            :value="statistics.userCount"
            :value-style="{ color: '#1890ff' }"
          >
            <template #prefix>
              <UserOutlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="角色总数"
            :value="statistics.roleCount"
            :value-style="{ color: '#52c41a' }"
          >
            <template #prefix>
              <TeamOutlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="菜单总数"
            :value="statistics.menuCount"
            :value-style="{ color: '#faad14' }"
          >
            <template #prefix>
              <AppstoreOutlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="部门总数"
            :value="statistics.deptCount"
            :value-style="{ color: '#722ed1' }"
          >
            <template #prefix>
              <ApartmentOutlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16" class="mt16">
      <a-col :span="16">
        <a-card title="欢迎使用 JSite 快速开发平台">
          <a-typography>
            <a-typography-title :level="4">功能特点</a-typography-title>
            <a-typography-paragraph>
              <ul>
                <li>
                  <strong>前后端分离架构</strong>：基于 Spring Boot 3 + Vue 3 +
                  TypeScript，前后端完全分离
                </li>
                <li>
                  <strong>权限管理</strong>：基于 Sa-Token
                  的细粒度权限控制，支持菜单权限、按钮权限、数据权限
                </li>
                <li>
                  <strong>代码生成</strong>：支持单表、树表、主子表代码生成，一键生成前后端代码
                </li>
                <li>
                  <strong>系统监控</strong>：完善的日志记录、在线用户管理、服务监控
                </li>
                <li>
                  <strong>易于扩展</strong>：模块化设计，支持业务模块热插拔
                </li>
              </ul>
            </a-typography-paragraph>

            <a-typography-title :level="4">技术栈</a-typography-title>
            <a-typography-paragraph>
              <a-space wrap>
                <a-tag color="blue">Spring Boot 3</a-tag>
                <a-tag color="green">Vue 3</a-tag>
                <a-tag color="cyan">TypeScript</a-tag>
                <a-tag color="orange">Ant Design Vue</a-tag>
                <a-tag color="purple">MyBatis-Plus</a-tag>
                <a-tag color="red">Sa-Token</a-tag>
                <a-tag color="magenta">Redis</a-tag>
                <a-tag color="geekblue">MySQL</a-tag>
              </a-space>
            </a-typography-paragraph>
          </a-typography>
        </a-card>
      </a-col>

      <a-col :span="8">
        <a-card title="快捷入口">
          <a-space direction="vertical" style="width: 100%">
            <a-button type="primary" block @click="$router.push('/system/user')">
              <UserOutlined />
              用户管理
            </a-button>
            <a-button block @click="$router.push('/system/role')">
              <TeamOutlined />
              角色管理
            </a-button>
            <a-button block @click="$router.push('/system/menu')">
              <AppstoreOutlined />
              菜单管理
            </a-button>
            <a-button block @click="$router.push('/tool/gen')">
              <CodeOutlined />
              代码生成
            </a-button>
          </a-space>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import {
  UserOutlined,
  TeamOutlined,
  AppstoreOutlined,
  ApartmentOutlined,
  CodeOutlined,
} from '@ant-design/icons-vue'

const statistics = ref({
  userCount: 2,
  roleCount: 2,
  menuCount: 60,
  deptCount: 10,
})
</script>

<style lang="less" scoped>
.dashboard-container {
  .mt16 {
    margin-top: 16px;
  }
}
</style>
