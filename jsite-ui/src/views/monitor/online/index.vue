<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <a-card :bordered="false" class="search-card">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="登录地址">
          <a-input v-model:value="queryParams.ipaddr" placeholder="请输入登录地址" allow-clear style="width: 200px" />
        </a-form-item>
        <a-form-item label="用户名称">
          <a-input v-model:value="queryParams.userName" placeholder="请输入用户名称" allow-clear style="width: 200px" />
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleQuery">
              <template #icon><SearchOutlined /></template>
              搜索
            </a-button>
            <a-button @click="resetQuery">
              <template #icon><ReloadOutlined /></template>
              重置
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <!-- 数据表格 -->
    <a-card :bordered="false" class="table-card">
      <a-table
        :columns="columns"
        :data-source="onlineList"
        :loading="loading"
        :pagination="pagination"
        row-key="tokenId"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'loginTime'">
            {{ formatLoginTime(record.loginTime) }}
          </template>
          <template v-if="column.dataIndex === 'action'">
            <a-popconfirm
              title="确定要强退该用户吗？"
              @confirm="handleForceLogout(record)"
            >
              <a-button type="link" danger size="small">
                <template #icon><LogoutOutlined /></template>
                强退
              </a-button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import { SearchOutlined, ReloadOutlined, LogoutOutlined } from '@ant-design/icons-vue'
import { listOnline, forceLogout } from '@/api/monitor/online'

const loading = ref(false)
const onlineList = ref<any[]>([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  ipaddr: undefined,
  userName: undefined
})

const pagination = computed(() => ({
  current: queryParams.pageNum,
  pageSize: queryParams.pageSize,
  total: total.value,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条`
}))

const total = ref(0)

const columns = [
  { title: '会话编号', dataIndex: 'tokenId', width: 250, ellipsis: true },
  { title: '用户名称', dataIndex: 'userName', width: 120 },
  { title: '部门名称', dataIndex: 'deptName', width: 150 },
  { title: '登录地址', dataIndex: 'ipaddr', width: 140 },
  { title: '登录地点', dataIndex: 'loginLocation', width: 150 },
  { title: '浏览器', dataIndex: 'browser', width: 120 },
  { title: '操作系统', dataIndex: 'os', width: 120 },
  { title: '登录时间', dataIndex: 'loginTime', width: 180 },
  { title: '操作', dataIndex: 'action', width: 100, fixed: 'right' }
]

function formatLoginTime(timestamp: number) {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

async function getList() {
  loading.value = true
  try {
    const res = await listOnline(queryParams)
    onlineList.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  queryParams.pageNum = 1
  getList()
}

function resetQuery() {
  queryParams.ipaddr = undefined
  queryParams.userName = undefined
  handleQuery()
}

function handleTableChange(pag: any) {
  queryParams.pageNum = pag.current
  queryParams.pageSize = pag.pageSize
  getList()
}

async function handleForceLogout(record: any) {
  await forceLogout(record.tokenId)
  message.success('强退成功')
  getList()
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.search-card {
  margin-bottom: 16px;
}
</style>
