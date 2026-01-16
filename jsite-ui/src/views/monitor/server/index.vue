<template>
  <div class="server-container">
    <a-spin :spinning="loading">
      <a-row :gutter="[16, 16]">
        <!-- CPU信息 -->
        <a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="6">
          <a-card class="info-card cpu-card">
            <template #title>
              <div class="card-title">
                <DesktopOutlined class="card-icon" />
                <span>CPU</span>
              </div>
            </template>
            <div class="progress-wrapper">
              <a-progress
                type="dashboard"
                :percent="server.cpu?.used || 0"
                :stroke-color="getProgressColor(server.cpu?.used)"
                :width="120"
              />
            </div>
            <a-descriptions :column="1" size="small" class="info-desc">
              <a-descriptions-item label="核心数">{{ server.cpu?.cpuNum }} 核</a-descriptions-item>
              <a-descriptions-item label="用户使用率">{{ server.cpu?.used }}%</a-descriptions-item>
              <a-descriptions-item label="系统使用率">{{ server.cpu?.sys }}%</a-descriptions-item>
              <a-descriptions-item label="空闲率">{{ server.cpu?.free }}%</a-descriptions-item>
            </a-descriptions>
          </a-card>
        </a-col>

        <!-- 内存信息 -->
        <a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="6">
          <a-card class="info-card mem-card">
            <template #title>
              <div class="card-title">
                <DatabaseOutlined class="card-icon" />
                <span>内存</span>
              </div>
            </template>
            <div class="progress-wrapper">
              <a-progress
                type="dashboard"
                :percent="server.mem?.usage || 0"
                :stroke-color="getProgressColor(server.mem?.usage)"
                :width="120"
              />
            </div>
            <a-descriptions :column="1" size="small" class="info-desc">
              <a-descriptions-item label="总内存">{{ server.mem?.total }} GB</a-descriptions-item>
              <a-descriptions-item label="已用内存">{{ server.mem?.used }} GB</a-descriptions-item>
              <a-descriptions-item label="剩余内存">{{ server.mem?.free }} GB</a-descriptions-item>
              <a-descriptions-item label="使用率">{{ server.mem?.usage }}%</a-descriptions-item>
            </a-descriptions>
          </a-card>
        </a-col>

        <!-- JVM信息 -->
        <a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="6">
          <a-card class="info-card jvm-card">
            <template #title>
              <div class="card-title">
                <CodeOutlined class="card-icon" />
                <span>JVM</span>
              </div>
            </template>
            <div class="progress-wrapper">
              <a-progress
                type="dashboard"
                :percent="server.jvm?.usage || 0"
                :stroke-color="getProgressColor(server.jvm?.usage)"
                :width="120"
              />
            </div>
            <a-descriptions :column="1" size="small" class="info-desc">
              <a-descriptions-item label="最大内存">{{ server.jvm?.max }} MB</a-descriptions-item>
              <a-descriptions-item label="已用内存">{{ server.jvm?.used }} MB</a-descriptions-item>
              <a-descriptions-item label="空闲内存">{{ server.jvm?.free }} MB</a-descriptions-item>
              <a-descriptions-item label="使用率">{{ server.jvm?.usage }}%</a-descriptions-item>
            </a-descriptions>
          </a-card>
        </a-col>

        <!-- 系统信息 -->
        <a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="6">
          <a-card class="info-card sys-card">
            <template #title>
              <div class="card-title">
                <CloudServerOutlined class="card-icon" />
                <span>系统信息</span>
              </div>
            </template>
            <a-descriptions :column="1" size="small" class="info-desc sys-desc">
              <a-descriptions-item label="服务器名">{{ server.sys?.computerName }}</a-descriptions-item>
              <a-descriptions-item label="服务器IP">{{ server.sys?.computerIp }}</a-descriptions-item>
              <a-descriptions-item label="操作系统">{{ server.sys?.osName }}</a-descriptions-item>
              <a-descriptions-item label="系统架构">{{ server.sys?.osArch }}</a-descriptions-item>
            </a-descriptions>
          </a-card>
        </a-col>

        <!-- JVM详细信息 -->
        <a-col :span="24">
          <a-card class="info-card detail-card">
            <template #title>
              <div class="card-title">
                <SettingOutlined class="card-icon" />
                <span>Java虚拟机信息</span>
              </div>
            </template>
            <a-descriptions :column="{ xxl: 4, xl: 3, lg: 3, md: 2, sm: 1, xs: 1 }" bordered>
              <a-descriptions-item label="Java名称">{{ server.jvm?.name }}</a-descriptions-item>
              <a-descriptions-item label="Java版本">{{ server.jvm?.version }}</a-descriptions-item>
              <a-descriptions-item label="启动时间">{{ server.jvm?.startTime }}</a-descriptions-item>
              <a-descriptions-item label="运行时长">{{ server.jvm?.runTime }}</a-descriptions-item>
              <a-descriptions-item label="安装路径" :span="2">{{ server.jvm?.home }}</a-descriptions-item>
              <a-descriptions-item label="项目路径" :span="2">{{ server.sys?.userDir }}</a-descriptions-item>
            </a-descriptions>
          </a-card>
        </a-col>

        <!-- 磁盘信息 -->
        <a-col :span="24">
          <a-card class="info-card detail-card">
            <template #title>
              <div class="card-title">
                <HddOutlined class="card-icon" />
                <span>磁盘状态</span>
              </div>
            </template>
            <a-table :columns="diskColumns" :data-source="server.sysFiles" :pagination="false" row-key="dirName">
              <template #bodyCell="{ column, record }">
                <template v-if="column.dataIndex === 'usage'">
                  <a-progress
                    :percent="record.usage"
                    :stroke-color="getProgressColor(record.usage)"
                    size="small"
                    :show-info="true"
                  />
                </template>
              </template>
            </a-table>
          </a-card>
        </a-col>
      </a-row>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import {
  DesktopOutlined, DatabaseOutlined, CodeOutlined, CloudServerOutlined,
  SettingOutlined, HddOutlined
} from '@ant-design/icons-vue'
import { getServer } from '@/api/monitor/server'

const loading = ref(false)
const server = ref<any>({})
let timer: any = null

const diskColumns = [
  { title: '盘符路径', dataIndex: 'dirName', width: 120 },
  { title: '文件系统', dataIndex: 'sysTypeName', width: 120 },
  { title: '盘符类型', dataIndex: 'typeName', width: 150 },
  { title: '总大小', dataIndex: 'total', width: 100 },
  { title: '可用大小', dataIndex: 'free', width: 100 },
  { title: '已用大小', dataIndex: 'used', width: 100 },
  { title: '已用百分比', dataIndex: 'usage', width: 200 }
]

function getProgressColor(percent: number) {
  if (percent < 50) return { '0%': '#87d068', '100%': '#87d068' }
  if (percent < 80) return { '0%': '#faad14', '100%': '#faad14' }
  return { '0%': '#ff4d4f', '100%': '#ff4d4f' }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getServer()
    server.value = res.data || {}
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
  // 每30秒刷新一次
  timer = setInterval(fetchData, 30000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped lang="less">
.server-container {
  padding: 16px;
  background: #f0f2f5;
  min-height: calc(100vh - 120px);
}

.info-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    transform: translateY(-2px);
  }

  .card-title {
    display: flex;
    align-items: center;
    font-weight: 600;

    .card-icon {
      font-size: 18px;
      margin-right: 8px;
    }
  }

  .progress-wrapper {
    display: flex;
    justify-content: center;
    padding: 16px 0;
  }

  .info-desc {
    :deep(.ant-descriptions-item-label) {
      color: #8c8c8c;
    }
    :deep(.ant-descriptions-item-content) {
      color: #262626;
      font-weight: 500;
    }
  }

  .sys-desc {
    padding-top: 24px;
  }
}

.cpu-card .card-icon { color: #1890ff; }
.mem-card .card-icon { color: #52c41a; }
.jvm-card .card-icon { color: #722ed1; }
.sys-card .card-icon { color: #fa8c16; }

.detail-card {
  .card-icon {
    color: #1890ff;
  }
}
</style>
