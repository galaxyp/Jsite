<template>
  <div class="cache-container">
    <a-row :gutter="[16, 16]">
      <!-- 基本信息 -->
      <a-col :xs="24" :sm="24" :md="12" :lg="8" :xl="6">
        <a-card class="info-card">
          <template #title>
            <div class="card-title">
              <DatabaseOutlined class="card-icon" />
              <span>基本信息</span>
            </div>
          </template>
          <a-descriptions :column="1" size="small">
            <a-descriptions-item label="Redis版本">{{ cache.info?.redis_version || '-' }}</a-descriptions-item>
            <a-descriptions-item label="运行模式">{{ cache.info?.redis_mode === 'standalone' ? '单机' : '集群' }}</a-descriptions-item>
            <a-descriptions-item label="端口">{{ cache.info?.tcp_port || '-' }}</a-descriptions-item>
            <a-descriptions-item label="客户端数">{{ cache.info?.connected_clients || '-' }}</a-descriptions-item>
            <a-descriptions-item label="运行时间">{{ formatUptime(cache.info?.uptime_in_seconds) }}</a-descriptions-item>
            <a-descriptions-item label="使用内存">{{ cache.info?.used_memory_human || '-' }}</a-descriptions-item>
            <a-descriptions-item label="CPU占用">{{ cache.info?.used_cpu_user_children || '-' }}</a-descriptions-item>
            <a-descriptions-item label="内存配置">{{ cache.info?.maxmemory_human || '无限制' }}</a-descriptions-item>
            <a-descriptions-item label="AOF状态">{{ cache.info?.aof_enabled === '1' ? '开启' : '关闭' }}</a-descriptions-item>
            <a-descriptions-item label="RDB状态">{{ cache.info?.rdb_last_bgsave_status || '-' }}</a-descriptions-item>
            <a-descriptions-item label="Key数量">{{ cache.dbSize || 0 }}</a-descriptions-item>
            <a-descriptions-item label="网络入口">{{ cache.info?.instantaneous_input_kbps || 0 }} kps</a-descriptions-item>
            <a-descriptions-item label="网络出口">{{ cache.info?.instantaneous_output_kbps || 0 }} kps</a-descriptions-item>
          </a-descriptions>
        </a-card>
      </a-col>

      <!-- 命令统计 -->
      <a-col :xs="24" :sm="24" :md="12" :lg="16" :xl="18">
        <a-card class="info-card chart-card">
          <template #title>
            <div class="card-title">
              <PieChartOutlined class="card-icon" />
              <span>命令统计</span>
            </div>
          </template>
          <div ref="chartRef" class="chart-container"></div>
        </a-card>
      </a-col>

      <!-- 缓存列表 -->
      <a-col :span="24">
        <a-card class="info-card">
          <template #title>
            <div class="card-title">
              <AppstoreOutlined class="card-icon" />
              <span>缓存列表</span>
            </div>
            <a-button type="primary" danger size="small" @click="handleClearAll" style="margin-left: auto;">
              <template #icon><DeleteOutlined /></template>
              清理全部
            </a-button>
          </template>
          <a-row :gutter="16">
            <a-col :span="8">
              <a-card title="缓存名称" size="small" class="cache-list-card">
                <a-list :data-source="cacheNames" size="small" :split="false">
                  <template #renderItem="{ item }">
                    <a-list-item
                      :class="['cache-item', { active: currentCacheName === item.cacheName }]"
                      @click="handleSelectCacheName(item)"
                    >
                      <div class="cache-item-content">
                        <span>{{ item.cacheName }}</span>
                        <span class="cache-remark">{{ item.remark }}</span>
                      </div>
                      <template #actions>
                        <a-button type="link" danger size="small" @click.stop="handleClearCacheName(item)">
                          <DeleteOutlined />
                        </a-button>
                      </template>
                    </a-list-item>
                  </template>
                </a-list>
              </a-card>
            </a-col>
            <a-col :span="8">
              <a-card title="键名列表" size="small" class="cache-list-card">
                <a-list :data-source="cacheKeys" size="small" :split="false">
                  <template #renderItem="{ item }">
                    <a-list-item
                      :class="['cache-item', { active: currentCacheKey === item }]"
                      @click="handleSelectCacheKey(item)"
                    >
                      <span class="cache-key-text">{{ formatCacheKey(item) }}</span>
                      <template #actions>
                        <a-button type="link" danger size="small" @click.stop="handleClearCacheKey(item)">
                          <DeleteOutlined />
                        </a-button>
                      </template>
                    </a-list-item>
                  </template>
                </a-list>
              </a-card>
            </a-col>
            <a-col :span="8">
              <a-card title="缓存内容" size="small" class="cache-list-card">
                <a-descriptions :column="1" size="small" v-if="cacheValue.cacheKey">
                  <a-descriptions-item label="缓存名称">{{ cacheValue.cacheName }}</a-descriptions-item>
                  <a-descriptions-item label="缓存键名">{{ cacheValue.cacheKey }}</a-descriptions-item>
                  <a-descriptions-item label="缓存内容">
                    <pre class="cache-value-content">{{ cacheValue.cacheValue }}</pre>
                  </a-descriptions-item>
                </a-descriptions>
                <a-empty v-else description="请选择缓存键名" />
              </a-card>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { DatabaseOutlined, PieChartOutlined, AppstoreOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import { getCache, getCacheNames, getCacheKeys, getCacheValue, clearCacheName, clearCacheKey, clearCacheAll } from '@/api/monitor/cache'
import * as echarts from 'echarts'

const cache = ref<any>({})
const cacheNames = ref<any[]>([])
const cacheKeys = ref<string[]>([])
const cacheValue = ref<any>({})
const currentCacheName = ref('')
const currentCacheKey = ref('')
const chartRef = ref()
let chartInstance: echarts.ECharts | null = null

function formatUptime(seconds: string) {
  if (!seconds) return '-'
  const s = parseInt(seconds)
  const days = Math.floor(s / 86400)
  const hours = Math.floor((s % 86400) / 3600)
  const minutes = Math.floor((s % 3600) / 60)
  return `${days}天${hours}小时${minutes}分钟`
}

function formatCacheKey(key: string) {
  if (key.length > 30) {
    return key.substring(0, 30) + '...'
  }
  return key
}

async function fetchData() {
  try {
    const [cacheRes, namesRes] = await Promise.all([getCache(), getCacheNames()])
    cache.value = cacheRes.data || {}
    cacheNames.value = namesRes.data || []
    initChart()
  } catch (error) {
    console.error(error)
  }
}

function initChart() {
  nextTick(() => {
    if (!chartRef.value) return
    if (!chartInstance) {
      chartInstance = echarts.init(chartRef.value)
    }
    const commandStats = cache.value.commandStats || []
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
      },
      legend: {
        type: 'scroll',
        orient: 'vertical',
        right: 10,
        top: 20,
        bottom: 20
      },
      series: [
        {
          name: '命令统计',
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['40%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 4,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 14,
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: commandStats.slice(0, 10).map((item: any) => ({
            name: item.name,
            value: parseInt(item.value)
          }))
        }
      ]
    }
    chartInstance.setOption(option)
  })
}

async function handleSelectCacheName(item: any) {
  currentCacheName.value = item.cacheName
  currentCacheKey.value = ''
  cacheValue.value = {}
  const res = await getCacheKeys(item.cacheName)
  cacheKeys.value = res.data || []
}

async function handleSelectCacheKey(key: string) {
  currentCacheKey.value = key
  const keyPart = key.replace(currentCacheName.value, '')
  const res = await getCacheValue(currentCacheName.value, keyPart)
  cacheValue.value = res.data || {}
}

async function handleClearCacheName(item: any) {
  Modal.confirm({
    title: '确认清理',
    content: `确定要清理缓存名称 "${item.cacheName}" 下的所有键吗？`,
    async onOk() {
      await clearCacheName(item.cacheName)
      message.success('清理成功')
      if (currentCacheName.value === item.cacheName) {
        cacheKeys.value = []
        cacheValue.value = {}
      }
    }
  })
}

async function handleClearCacheKey(key: string) {
  Modal.confirm({
    title: '确认清理',
    content: `确定要清理缓存键 "${formatCacheKey(key)}" 吗？`,
    async onOk() {
      await clearCacheKey(key)
      message.success('清理成功')
      cacheKeys.value = cacheKeys.value.filter(k => k !== key)
      if (currentCacheKey.value === key) {
        cacheValue.value = {}
      }
    }
  })
}

async function handleClearAll() {
  Modal.confirm({
    title: '确认清理',
    content: '确定要清理全部缓存吗？此操作不可恢复！',
    async onOk() {
      await clearCacheAll()
      message.success('清理成功')
      cacheKeys.value = []
      cacheValue.value = {}
      fetchData()
    }
  })
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', () => chartInstance?.resize())
})

onUnmounted(() => {
  chartInstance?.dispose()
  window.removeEventListener('resize', () => chartInstance?.resize())
})
</script>

<style scoped lang="less">
.cache-container {
  padding: 16px;
  background: #f0f2f5;
  min-height: calc(100vh - 120px);
}

.info-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

  .card-title {
    display: flex;
    align-items: center;
    font-weight: 600;

    .card-icon {
      font-size: 18px;
      margin-right: 8px;
      color: #1890ff;
    }
  }

  :deep(.ant-card-head) {
    display: flex;
    align-items: center;
  }

  :deep(.ant-card-head-title) {
    display: flex;
    align-items: center;
    width: 100%;
  }
}

.chart-card {
  .chart-container {
    height: 300px;
  }
}

.cache-list-card {
  height: 350px;
  overflow: auto;

  .cache-item {
    cursor: pointer;
    padding: 8px 12px;
    border-radius: 4px;
    transition: all 0.3s;

    &:hover {
      background: #f5f5f5;
    }

    &.active {
      background: #e6f7ff;
      border-left: 3px solid #1890ff;
    }

    .cache-item-content {
      display: flex;
      flex-direction: column;

      .cache-remark {
        font-size: 12px;
        color: #8c8c8c;
      }
    }

    .cache-key-text {
      word-break: break-all;
      font-size: 13px;
    }
  }
}

.cache-value-content {
  max-height: 200px;
  overflow: auto;
  background: #f5f5f5;
  padding: 8px;
  border-radius: 4px;
  font-size: 12px;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
