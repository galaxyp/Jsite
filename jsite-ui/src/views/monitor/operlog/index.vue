<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <a-card :bordered="false" class="search-card">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="系统模块">
          <a-input v-model:value="queryParams.title" placeholder="请输入系统模块" allow-clear style="width: 200px" />
        </a-form-item>
        <a-form-item label="操作人员">
          <a-input v-model:value="queryParams.operName" placeholder="请输入操作人员" allow-clear style="width: 200px" />
        </a-form-item>
        <a-form-item label="操作类型">
          <a-select v-model:value="queryParams.businessType" placeholder="请选择操作类型" allow-clear style="width: 200px">
            <a-select-option :value="0">其他</a-select-option>
            <a-select-option :value="1">新增</a-select-option>
            <a-select-option :value="2">修改</a-select-option>
            <a-select-option :value="3">删除</a-select-option>
            <a-select-option :value="4">授权</a-select-option>
            <a-select-option :value="5">导出</a-select-option>
            <a-select-option :value="6">导入</a-select-option>
            <a-select-option :value="7">强退</a-select-option>
            <a-select-option :value="8">清空</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="操作状态">
          <a-select v-model:value="queryParams.status" placeholder="请选择操作状态" allow-clear style="width: 200px">
            <a-select-option :value="0">成功</a-select-option>
            <a-select-option :value="1">失败</a-select-option>
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
        :data-source="operlogList"
        :loading="loading"
        :pagination="pagination"
        :row-selection="{ selectedRowKeys, onChange: onSelectChange }"
        row-key="operId"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'businessType'">
            <a-tag :color="getBusinessTypeColor(record.businessType)">
              {{ getBusinessTypeName(record.businessType) }}
            </a-tag>
          </template>
          <template v-if="column.dataIndex === 'status'">
            <a-tag :color="record.status === 0 ? 'success' : 'error'">
              {{ record.status === 0 ? '成功' : '失败' }}
            </a-tag>
          </template>
          <template v-if="column.dataIndex === 'operTime'">
            {{ formatDateTime(record.operTime) }}
          </template>
          <template v-if="column.dataIndex === 'action'">
            <a-button type="link" size="small" @click="handleView(record)">
              <template #icon><EyeOutlined /></template>
              详情
            </a-button>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 详情对话框 -->
    <a-modal v-model:open="detailVisible" title="操作日志详情" :footer="null" width="800px">
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="日志编号">{{ currentRow?.operId }}</a-descriptions-item>
        <a-descriptions-item label="操作模块">{{ currentRow?.title }}</a-descriptions-item>
        <a-descriptions-item label="操作类型">{{ getBusinessTypeName(currentRow?.businessType) }}</a-descriptions-item>
        <a-descriptions-item label="请求方式">{{ currentRow?.requestMethod }}</a-descriptions-item>
        <a-descriptions-item label="操作人员">{{ currentRow?.operName }}</a-descriptions-item>
        <a-descriptions-item label="操作地址">{{ currentRow?.operIp }}</a-descriptions-item>
        <a-descriptions-item label="操作状态">
          <a-tag :color="currentRow?.status === 0 ? 'success' : 'error'">
            {{ currentRow?.status === 0 ? '成功' : '失败' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="操作时间">{{ formatDateTime(currentRow?.operTime) }}</a-descriptions-item>
        <a-descriptions-item label="操作方法" :span="2">{{ currentRow?.method }}</a-descriptions-item>
        <a-descriptions-item label="请求URL" :span="2">{{ currentRow?.operUrl }}</a-descriptions-item>
        <a-descriptions-item label="请求参数" :span="2">
          <div style="max-height: 200px; overflow: auto;">{{ currentRow?.operParam }}</div>
        </a-descriptions-item>
        <a-descriptions-item label="返回参数" :span="2">
          <div style="max-height: 200px; overflow: auto;">{{ currentRow?.jsonResult }}</div>
        </a-descriptions-item>
        <a-descriptions-item v-if="currentRow?.errorMsg" label="错误消息" :span="2">
          <span style="color: #ff4d4f;">{{ currentRow?.errorMsg }}</span>
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { SearchOutlined, ReloadOutlined, DeleteOutlined, ClearOutlined, EyeOutlined } from '@ant-design/icons-vue'
import { listOperlog, delOperlog, cleanOperlog } from '@/api/monitor/operlog'

const loading = ref(false)
const operlogList = ref<any[]>([])
const selectedRowKeys = ref<number[]>([])
const detailVisible = ref(false)
const currentRow = ref<any>(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: undefined,
  operName: undefined,
  businessType: undefined,
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
  { title: '日志编号', dataIndex: 'operId', width: 100 },
  { title: '系统模块', dataIndex: 'title', width: 150 },
  { title: '操作类型', dataIndex: 'businessType', width: 100 },
  { title: '请求方式', dataIndex: 'requestMethod', width: 100 },
  { title: '操作人员', dataIndex: 'operName', width: 120 },
  { title: '操作地址', dataIndex: 'operIp', width: 140 },
  { title: '操作状态', dataIndex: 'status', width: 100 },
  { title: '操作时间', dataIndex: 'operTime', width: 180 },
  { title: '操作', dataIndex: 'action', width: 100, fixed: 'right' }
]

const businessTypeMap: Record<number, { name: string; color: string }> = {
  0: { name: '其他', color: 'default' },
  1: { name: '新增', color: 'success' },
  2: { name: '修改', color: 'processing' },
  3: { name: '删除', color: 'error' },
  4: { name: '授权', color: 'warning' },
  5: { name: '导出', color: 'purple' },
  6: { name: '导入', color: 'cyan' },
  7: { name: '强退', color: 'magenta' },
  8: { name: '清空', color: 'red' }
}

function getBusinessTypeName(type: number) {
  return businessTypeMap[type]?.name || '未知'
}

function getBusinessTypeColor(type: number) {
  return businessTypeMap[type]?.color || 'default'
}

function formatDateTime(dateTime: string) {
  if (!dateTime) return ''
  return dateTime.replace('T', ' ')
}

async function getList() {
  loading.value = true
  try {
    const res = await listOperlog(queryParams)
    operlogList.value = res.rows || []
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
  queryParams.title = undefined
  queryParams.operName = undefined
  queryParams.businessType = undefined
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

function handleView(row: any) {
  currentRow.value = row
  detailVisible.value = true
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
      await delOperlog(selectedRowKeys.value.join(','))
      message.success('删除成功')
      selectedRowKeys.value = []
      getList()
    }
  })
}

function handleClean() {
  Modal.confirm({
    title: '确认清空',
    content: '确定要清空所有操作日志吗？此操作不可恢复！',
    async onOk() {
      await cleanOperlog()
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
