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
        <a-form-item label="登录状态">
          <a-select v-model:value="queryParams.status" placeholder="请选择登录状态" allow-clear style="width: 200px">
            <a-select-option value="0">成功</a-select-option>
            <a-select-option value="1">失败</a-select-option>
          </a-select>
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

    <!-- 操作按钮 -->
    <a-card :bordered="false" class="table-card">
      <div class="table-operations">
        <a-space>
          <a-button type="primary" danger :disabled="selectedRowKeys.length === 0" @click="handleDelete">
            <template #icon><DeleteOutlined /></template>
            删除
          </a-button>
          <a-button type="primary" danger @click="handleClean">
            <template #icon><ClearOutlined /></template>
            清空
          </a-button>
        </a-space>
      </div>

      <!-- 数据表格 -->
      <a-table
        :columns="columns"
        :data-source="logininforList"
        :loading="loading"
        :pagination="pagination"
        :row-selection="{ selectedRowKeys, onChange: onSelectChange }"
        row-key="infoId"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'status'">
            <a-tag :color="record.status === '0' ? 'success' : 'error'">
              {{ record.status === '0' ? '成功' : '失败' }}
            </a-tag>
          </template>
          <template v-if="column.dataIndex === 'loginTime'">
            {{ formatDateTime(record.loginTime) }}
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { SearchOutlined, ReloadOutlined, DeleteOutlined, ClearOutlined } from '@ant-design/icons-vue'
import { listLogininfor, delLogininfor, cleanLogininfor } from '@/api/monitor/logininfor'

const loading = ref(false)
const logininforList = ref<any[]>([])
const selectedRowKeys = ref<number[]>([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  ipaddr: undefined,
  userName: undefined,
  status: undefined
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
  { title: '访问编号', dataIndex: 'infoId', width: 100 },
  { title: '用户名称', dataIndex: 'userName', width: 120 },
  { title: '登录地址', dataIndex: 'ipaddr', width: 140 },
  { title: '登录地点', dataIndex: 'loginLocation', width: 150 },
  { title: '浏览器', dataIndex: 'browser', width: 120 },
  { title: '操作系统', dataIndex: 'os', width: 120 },
  { title: '登录状态', dataIndex: 'status', width: 100 },
  { title: '提示消息', dataIndex: 'msg', width: 150 },
  { title: '登录时间', dataIndex: 'loginTime', width: 180 }
]

function formatDateTime(dateTime: string) {
  if (!dateTime) return ''
  return dateTime.replace('T', ' ')
}

async function getList() {
  loading.value = true
  try {
    const res = await listLogininfor(queryParams)
    logininforList.value = res.rows || []
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
  queryParams.status = undefined
  handleQuery()
}

function handleTableChange(pag: any) {
  queryParams.pageNum = pag.current
  queryParams.pageSize = pag.pageSize
  getList()
}

function onSelectChange(keys: number[]) {
  selectedRowKeys.value = keys
}

function handleDelete() {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请选择要删除的记录')
    return
  }
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 条记录吗？`,
    async onOk() {
      await delLogininfor(selectedRowKeys.value.join(','))
      message.success('删除成功')
      selectedRowKeys.value = []
      getList()
    }
  })
}

function handleClean() {
  Modal.confirm({
    title: '确认清空',
    content: '确定要清空所有登录日志吗？此操作不可恢复！',
    async onOk() {
      await cleanLogininfor()
      message.success('清空成功')
      getList()
    }
  })
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.search-card {
  margin-bottom: 16px;
}
.table-operations {
  margin-bottom: 16px;
}
</style>
