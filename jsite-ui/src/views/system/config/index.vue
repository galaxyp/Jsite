<template>
  <div class="config-container">
    <!-- 搜索表单 -->
    <a-card size="small" :bordered="false">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="参数名称">
          <a-input v-model:value="queryParams.configName" placeholder="请输入参数名称" allow-clear />
        </a-form-item>
        <a-form-item label="参数键名">
          <a-input v-model:value="queryParams.configKey" placeholder="请输入参数键名" allow-clear />
        </a-form-item>
        <a-form-item label="系统内置">
          <a-select v-model:value="queryParams.configType" placeholder="系统内置" allow-clear style="width: 120px">
            <a-select-option value="Y">是</a-select-option>
            <a-select-option value="N">否</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleQuery"><SearchOutlined />搜索</a-button>
            <a-button @click="resetQuery"><ReloadOutlined />重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card size="small" :bordered="false" style="margin-top: 8px">
      <a-space>
        <a-button type="primary" @click="handleAdd" v-permission="['system:config:add']"><PlusOutlined />新增</a-button>
        <a-button danger :disabled="selectedRowKeys.length === 0" @click="handleDelete" v-permission="['system:config:remove']"><DeleteOutlined />删除</a-button>
        <a-button type="default" @click="handleRefreshCache" v-permission="['system:config:remove']"><SyncOutlined />刷新缓存</a-button>
      </a-space>
    </a-card>

    <a-card size="small" :bordered="false" style="margin-top: 8px">
      <a-table :columns="columns" :data-source="configList" :loading="loading" :pagination="pagination" :row-selection="{ selectedRowKeys, onChange: onSelectChange }" row-key="configId" @change="handleTableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'configType'">
            <a-tag :color="record.configType === 'Y' ? 'blue' : 'default'">{{ record.configType === 'Y' ? '是' : '否' }}</a-tag>
          </template>
          <template v-if="column.dataIndex === 'action'">
            <a-space>
              <a @click="handleUpdate(record)" v-permission="['system:config:edit']">编辑</a>
              <a-popconfirm title="确定删除该参数吗？" @confirm="handleDelete(record)" v-permission="['system:config:remove']">
                <a class="danger">删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal v-model:open="open" :title="title" width="500px" @ok="submitForm" @cancel="cancel">
      <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="参数名称" name="configName">
          <a-input v-model:value="form.configName" placeholder="请输入参数名称" />
        </a-form-item>
        <a-form-item label="参数键名" name="configKey">
          <a-input v-model:value="form.configKey" placeholder="请输入参数键名" />
        </a-form-item>
        <a-form-item label="参数键值" name="configValue">
          <a-input v-model:value="form.configValue" placeholder="请输入参数键值" />
        </a-form-item>
        <a-form-item label="系统内置" name="configType">
          <a-radio-group v-model:value="form.configType">
            <a-radio value="Y">是</a-radio>
            <a-radio value="N">否</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="备注" name="remark">
          <a-textarea v-model:value="form.remark" placeholder="请输入内容" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { SearchOutlined, ReloadOutlined, PlusOutlined, DeleteOutlined, SyncOutlined } from '@ant-design/icons-vue'
import { listConfig, getConfig, addConfig, updateConfig, delConfig, refreshCache } from '@/api/system/config'

const loading = ref(false)
const open = ref(false)
const title = ref('')
const configList = ref<any[]>([])
const selectedRowKeys = ref<number[]>([])
const formRef = ref()

const queryParams = reactive({ pageNum: 1, pageSize: 10, configName: undefined, configKey: undefined, configType: undefined })
const pagination = reactive({ current: 1, pageSize: 10, total: 0, showSizeChanger: true, showQuickJumper: true, showTotal: (total: number) => `共 ${total} 条` })
const form = reactive<any>({ configId: undefined, configName: undefined, configKey: undefined, configValue: undefined, configType: 'N', remark: undefined })
const rules = { configName: [{ required: true, message: '参数名称不能为空', trigger: 'blur' }], configKey: [{ required: true, message: '参数键名不能为空', trigger: 'blur' }], configValue: [{ required: true, message: '参数键值不能为空', trigger: 'blur' }] }
const columns = [
  { title: '参数编号', dataIndex: 'configId', width: 100 },
  { title: '参数名称', dataIndex: 'configName', width: 200 },
  { title: '参数键名', dataIndex: 'configKey', width: 200 },
  { title: '参数键值', dataIndex: 'configValue', width: 150 },
  { title: '系统内置', dataIndex: 'configType', width: 100 },
  { title: '备注', dataIndex: 'remark', width: 200 },
  { title: '创建时间', dataIndex: 'createTime', width: 180 },
  { title: '操作', dataIndex: 'action', width: 150, fixed: 'right' },
]

const getList = async () => {
  loading.value = true
  try {
    const res = await listConfig(queryParams)
    configList.value = res.data.rows
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryParams.configName = undefined; queryParams.configKey = undefined; queryParams.configType = undefined; handleQuery() }
const handleTableChange = (pag: any) => { queryParams.pageNum = pag.current; queryParams.pageSize = pag.pageSize; pagination.current = pag.current; pagination.pageSize = pag.pageSize; getList() }
const onSelectChange = (keys: number[]) => { selectedRowKeys.value = keys }
const cancel = () => { open.value = false; reset() }
const reset = () => { form.configId = undefined; form.configName = undefined; form.configKey = undefined; form.configValue = undefined; form.configType = 'N'; form.remark = undefined }

const handleAdd = () => { reset(); open.value = true; title.value = '添加参数' }
const handleUpdate = async (row: any) => { reset(); const res = await getConfig(row.configId); Object.assign(form, res.data); open.value = true; title.value = '修改参数' }
const submitForm = async () => { await formRef.value.validate(); if (form.configId !== undefined) { await updateConfig(form); message.success('修改成功') } else { await addConfig(form); message.success('新增成功') }; open.value = false; getList() }
const handleDelete = async (row?: any) => { const configIds = row?.configId || selectedRowKeys.value.join(','); await delConfig(configIds); message.success('删除成功'); getList() }
const handleRefreshCache = async () => { await refreshCache(); message.success('刷新成功') }

onMounted(() => getList())
</script>

<style lang="less" scoped>
.config-container { .danger { color: #ff4d4f; } }
</style>
