<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <a-card :bordered="false" class="search-card">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="表名称">
          <a-input v-model:value="queryParams.tableName" placeholder="请输入表名称" allow-clear style="width: 200px" />
        </a-form-item>
        <a-form-item label="表描述">
          <a-input v-model:value="queryParams.tableComment" placeholder="请输入表描述" allow-clear style="width: 200px" />
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
          <a-button type="primary" @click="handleImport">
            <template #icon><UploadOutlined /></template>
            导入
          </a-button>
          <a-button type="primary" :disabled="selectedRowKeys.length === 0" @click="handleGenTable">
            <template #icon><CodeOutlined /></template>
            生成
          </a-button>
          <a-button type="primary" danger :disabled="selectedRowKeys.length === 0" @click="handleDelete">
            <template #icon><DeleteOutlined /></template>
            删除
          </a-button>
        </a-space>
      </div>

      <!-- 数据表格 -->
      <a-table
        :columns="columns"
        :data-source="tableList"
        :loading="loading"
        :pagination="pagination"
        :row-selection="{ selectedRowKeys, onChange: onSelectChange }"
        row-key="tableId"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'createTime'">
            {{ formatDateTime(record.createTime) }}
          </template>
          <template v-if="column.dataIndex === 'updateTime'">
            {{ formatDateTime(record.updateTime) }}
          </template>
          <template v-if="column.dataIndex === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handlePreview(record)">
                <template #icon><EyeOutlined /></template>
                预览
              </a-button>
              <a-button type="link" size="small" @click="handleEditTable(record)">
                <template #icon><EditOutlined /></template>
                编辑
              </a-button>
              <a-button type="link" size="small" @click="handleSynchDb(record)">
                <template #icon><SyncOutlined /></template>
                同步
              </a-button>
              <a-button type="link" size="small" @click="handleDownload(record)">
                <template #icon><DownloadOutlined /></template>
                生成
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 导入表对话框 -->
    <a-modal v-model:open="importVisible" title="导入表" width="900px" @ok="handleImportTable" @cancel="importVisible = false">
      <!-- 搜索表单 -->
      <a-form layout="inline" :model="dbQueryParams" style="margin-bottom: 16px;">
        <a-form-item label="表名称">
          <a-input v-model:value="dbQueryParams.tableName" placeholder="请输入表名称" allow-clear style="width: 150px" />
        </a-form-item>
        <a-form-item label="表描述">
          <a-input v-model:value="dbQueryParams.tableComment" placeholder="请输入表描述" allow-clear style="width: 150px" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleDbQuery">搜索</a-button>
        </a-form-item>
      </a-form>
      <a-table
        :columns="dbColumns"
        :data-source="dbTableList"
        :loading="dbLoading"
        :pagination="dbPagination"
        :row-selection="{ selectedRowKeys: dbSelectedRowKeys, onChange: onDbSelectChange }"
        row-key="tableName"
        size="small"
        @change="handleDbTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'createTime'">
            {{ formatDateTime(record.createTime) }}
          </template>
        </template>
      </a-table>
    </a-modal>

    <!-- 预览代码对话框 -->
    <a-modal v-model:open="previewVisible" title="代码预览" width="80%" :footer="null">
      <a-tabs v-model:activeKey="activeTab">
        <a-tab-pane v-for="(code, key) in previewData" :key="key" :tab="getTabName(key)">
          <pre class="code-preview">{{ code }}</pre>
        </a-tab-pane>
      </a-tabs>
    </a-modal>

    <!-- 编辑表配置对话框 -->
    <a-modal v-model:open="editVisible" title="编辑表配置" width="900px" @ok="handleUpdateTable" @cancel="editVisible = false">
      <a-tabs v-model:activeKey="editTab">
        <a-tab-pane key="basic" tab="基本信息">
          <a-form :model="editForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="表名称">
                  <a-input v-model:value="editForm.tableName" disabled />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="表描述">
                  <a-input v-model:value="editForm.tableComment" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="实体类名称">
                  <a-input v-model:value="editForm.className" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="作者">
                  <a-input v-model:value="editForm.functionAuthor" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="生成包路径">
                  <a-input v-model:value="editForm.packageName" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="生成模块名">
                  <a-input v-model:value="editForm.moduleName" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="生成业务名">
                  <a-input v-model:value="editForm.businessName" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="生成功能名">
                  <a-input v-model:value="editForm.functionName" />
                </a-form-item>
              </a-col>
              <a-col :span="24">
                <a-form-item label="备注" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
                  <a-textarea v-model:value="editForm.remark" :rows="2" />
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </a-tab-pane>
        <a-tab-pane key="columns" tab="字段配置">
          <a-table :columns="columnConfigColumns" :data-source="editForm.columns" :pagination="false" size="small" row-key="columnId">
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'columnComment'">
                <a-input v-model:value="record.columnComment" size="small" />
              </template>
              <template v-if="column.dataIndex === 'javaType'">
                <a-select v-model:value="record.javaType" size="small" style="width: 100%">
                  <a-select-option value="Long">Long</a-select-option>
                  <a-select-option value="String">String</a-select-option>
                  <a-select-option value="Integer">Integer</a-select-option>
                  <a-select-option value="Double">Double</a-select-option>
                  <a-select-option value="BigDecimal">BigDecimal</a-select-option>
                  <a-select-option value="LocalDateTime">LocalDateTime</a-select-option>
                </a-select>
              </template>
              <template v-if="column.dataIndex === 'javaField'">
                <a-input v-model:value="record.javaField" size="small" />
              </template>
              <template v-if="column.dataIndex === 'isInsert'">
                <a-checkbox v-model:checked="record.isInsert" :true-value="'1'" :false-value="'0'" />
              </template>
              <template v-if="column.dataIndex === 'isEdit'">
                <a-checkbox v-model:checked="record.isEdit" :true-value="'1'" :false-value="'0'" />
              </template>
              <template v-if="column.dataIndex === 'isList'">
                <a-checkbox v-model:checked="record.isList" :true-value="'1'" :false-value="'0'" />
              </template>
              <template v-if="column.dataIndex === 'isQuery'">
                <a-checkbox v-model:checked="record.isQuery" :true-value="'1'" :false-value="'0'" />
              </template>
              <template v-if="column.dataIndex === 'queryType'">
                <a-select v-model:value="record.queryType" size="small" style="width: 100%">
                  <a-select-option value="EQ">=</a-select-option>
                  <a-select-option value="NE">!=</a-select-option>
                  <a-select-option value="GT">&gt;</a-select-option>
                  <a-select-option value="GTE">&gt;=</a-select-option>
                  <a-select-option value="LT">&lt;</a-select-option>
                  <a-select-option value="LTE">&lt;=</a-select-option>
                  <a-select-option value="LIKE">LIKE</a-select-option>
                </a-select>
              </template>
              <template v-if="column.dataIndex === 'htmlType'">
                <a-select v-model:value="record.htmlType" size="small" style="width: 100%">
                  <a-select-option value="input">文本框</a-select-option>
                  <a-select-option value="textarea">文本域</a-select-option>
                  <a-select-option value="select">下拉框</a-select-option>
                  <a-select-option value="radio">单选框</a-select-option>
                  <a-select-option value="checkbox">复选框</a-select-option>
                  <a-select-option value="datetime">日期控件</a-select-option>
                </a-select>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  SearchOutlined, ReloadOutlined, UploadOutlined, CodeOutlined, DeleteOutlined,
  EyeOutlined, EditOutlined, SyncOutlined, DownloadOutlined
} from '@ant-design/icons-vue'
import { listTable, listDbTable, getGenTable, updateGenTable, importTable, delTable, previewTable, synchDb } from '@/api/tool/gen'

const loading = ref(false)
const tableList = ref<any[]>([])
const selectedRowKeys = ref<number[]>([])
const selectedRows = ref<any[]>([])

// 导入表相关
const importVisible = ref(false)
const dbLoading = ref(false)
const dbTableList = ref<any[]>([])
const dbSelectedRowKeys = ref<string[]>([])
const dbTotal = ref(0)

// 预览相关
const previewVisible = ref(false)
const previewData = ref<Record<string, string>>({})
const activeTab = ref('')

// 编辑相关
const editVisible = ref(false)
const editTab = ref('basic')
const editForm = ref<any>({})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  tableName: undefined,
  tableComment: undefined
})

const dbQueryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  tableName: undefined,
  tableComment: undefined
})

const pagination = computed(() => ({
  current: queryParams.pageNum,
  pageSize: queryParams.pageSize,
  total: total.value,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条`
}))

const dbPagination = computed(() => ({
  current: dbQueryParams.pageNum,
  pageSize: dbQueryParams.pageSize,
  total: dbTotal.value,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`
}))

const total = ref(0)

const columns = [
  { title: '表名称', dataIndex: 'tableName', width: 200 },
  { title: '表描述', dataIndex: 'tableComment', width: 200 },
  { title: '实体类', dataIndex: 'className', width: 150 },
  { title: '创建时间', dataIndex: 'createTime', width: 180 },
  { title: '更新时间', dataIndex: 'updateTime', width: 180 },
  { title: '操作', dataIndex: 'action', width: 280, fixed: 'right' }
]

const dbColumns = [
  { title: '表名称', dataIndex: 'tableName', width: 200 },
  { title: '表描述', dataIndex: 'tableComment', width: 200 },
  { title: '创建时间', dataIndex: 'createTime', width: 180 }
]

const columnConfigColumns = [
  { title: '列名', dataIndex: 'columnName', width: 120 },
  { title: '字段描述', dataIndex: 'columnComment', width: 120 },
  { title: '物理类型', dataIndex: 'columnType', width: 100 },
  { title: 'Java类型', dataIndex: 'javaType', width: 110 },
  { title: 'Java属性', dataIndex: 'javaField', width: 110 },
  { title: '插入', dataIndex: 'isInsert', width: 60 },
  { title: '编辑', dataIndex: 'isEdit', width: 60 },
  { title: '列表', dataIndex: 'isList', width: 60 },
  { title: '查询', dataIndex: 'isQuery', width: 60 },
  { title: '查询方式', dataIndex: 'queryType', width: 100 },
  { title: '显示类型', dataIndex: 'htmlType', width: 100 }
]

function formatDateTime(dateTime: string) {
  if (!dateTime) return ''
  return dateTime.replace('T', ' ')
}

function getTabName(key: string) {
  const names: Record<string, string> = {
    'vm/java/domain.java.vm': 'domain.java',
    'vm/java/mapper.java.vm': 'mapper.java',
    'vm/java/service.java.vm': 'service.java',
    'vm/java/serviceImpl.java.vm': 'serviceImpl.java',
    'vm/java/controller.java.vm': 'controller.java',
    'vm/xml/mapper.xml.vm': 'mapper.xml',
    'vm/ts/api.ts.vm': 'api.ts',
    'vm/vue/index.vue.vm': 'index.vue'
  }
  return names[key] || key
}

async function getList() {
  loading.value = true
  try {
    const res = await listTable(queryParams)
    tableList.value = res.rows || []
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
  queryParams.tableName = undefined
  queryParams.tableComment = undefined
  handleQuery()
}

function handleTableChange(pag: any) {
  queryParams.pageNum = pag.current
  queryParams.pageSize = pag.pageSize
  getList()
}

function onSelectChange(keys: number[], rows: any[]) {
  selectedRowKeys.value = keys
  selectedRows.value = rows
}

// 导入表
async function handleImport() {
  importVisible.value = true
  dbSelectedRowKeys.value = []
  await getDbList()
}

async function getDbList() {
  dbLoading.value = true
  try {
    const res = await listDbTable(dbQueryParams)
    dbTableList.value = res.rows || []
    dbTotal.value = res.total || 0
  } finally {
    dbLoading.value = false
  }
}

function handleDbQuery() {
  dbQueryParams.pageNum = 1
  getDbList()
}

function handleDbTableChange(pag: any) {
  dbQueryParams.pageNum = pag.current
  dbQueryParams.pageSize = pag.pageSize
  getDbList()
}

function onDbSelectChange(keys: string[]) {
  dbSelectedRowKeys.value = keys
}

async function handleImportTable() {
  if (dbSelectedRowKeys.value.length === 0) {
    message.warning('请选择要导入的表')
    return
  }
  await importTable(dbSelectedRowKeys.value.join(','))
  message.success('导入成功')
  importVisible.value = false
  getList()
}

// 预览代码
async function handlePreview(row: any) {
  const res = await previewTable(row.tableId)
  previewData.value = res.data || {}
  activeTab.value = Object.keys(previewData.value)[0] || ''
  previewVisible.value = true
}

// 编辑表配置
async function handleEditTable(row: any) {
  const res = await getGenTable(row.tableId)
  editForm.value = res.data?.info || {}
  editForm.value.columns = res.data?.rows || []
  editTab.value = 'basic'
  editVisible.value = true
}

async function handleUpdateTable() {
  await updateGenTable(editForm.value)
  message.success('修改成功')
  editVisible.value = false
  getList()
}

// 同步数据库
async function handleSynchDb(row: any) {
  Modal.confirm({
    title: '确认同步',
    content: `确定要同步表 "${row.tableName}" 的结构吗？`,
    async onOk() {
      await synchDb(row.tableName)
      message.success('同步成功')
      getList()
    }
  })
}

// 下载代码
function handleDownload(row: any) {
  window.open(`/dev-api/tool/gen/download/${row.tableName}`)
}

// 批量生成
function handleGenTable() {
  if (selectedRows.value.length === 0) {
    message.warning('请选择要生成的表')
    return
  }
  const tableNames = selectedRows.value.map(item => item.tableName).join(',')
  window.open(`/dev-api/tool/gen/batchGenCode?tables=${tableNames}`)
}

// 删除
function handleDelete() {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请选择要删除的记录')
    return
  }
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 条记录吗？`,
    async onOk() {
      await delTable(selectedRowKeys.value.join(','))
      message.success('删除成功')
      selectedRowKeys.value = []
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
.code-preview {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 4px;
  max-height: 500px;
  overflow: auto;
  font-size: 12px;
  line-height: 1.5;
}
</style>
