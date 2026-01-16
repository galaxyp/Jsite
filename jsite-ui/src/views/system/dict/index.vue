<template>
  <div class="dict-container">
    <!-- 搜索表单 -->
    <a-card size="small" :bordered="false">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="字典名称">
          <a-input v-model:value="queryParams.dictName" placeholder="请输入字典名称" allow-clear />
        </a-form-item>
        <a-form-item label="字典类型">
          <a-input v-model:value="queryParams.dictType" placeholder="请输入字典类型" allow-clear />
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="queryParams.status" placeholder="字典状态" allow-clear style="width: 120px">
            <a-select-option value="0">正常</a-select-option>
            <a-select-option value="1">停用</a-select-option>
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
        <a-button type="primary" @click="handleAdd" v-permission="['system:dict:add']"><PlusOutlined />新增</a-button>
        <a-button danger :disabled="selectedRowKeys.length === 0" @click="handleDelete" v-permission="['system:dict:remove']"><DeleteOutlined />删除</a-button>
        <a-button type="default" @click="handleRefreshCache" v-permission="['system:dict:remove']"><SyncOutlined />刷新缓存</a-button>
      </a-space>
    </a-card>

    <a-card size="small" :bordered="false" style="margin-top: 8px">
      <a-table :columns="columns" :data-source="dictList" :loading="loading" :pagination="pagination" :row-selection="{ selectedRowKeys, onChange: onSelectChange }" row-key="dictId" @change="handleTableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'dictType'">
            <a @click="openDictData(record)">{{ record.dictType }}</a>
          </template>
          <template v-if="column.dataIndex === 'status'">
            <a-tag :color="record.status === '0' ? 'green' : 'red'">{{ record.status === '0' ? '正常' : '停用' }}</a-tag>
          </template>
          <template v-if="column.dataIndex === 'action'">
            <a-space>
              <a @click="handleUpdate(record)" v-permission="['system:dict:edit']">编辑</a>
              <a-popconfirm title="确定删除该字典类型吗？" @confirm="handleDelete(record)" v-permission="['system:dict:remove']">
                <a class="danger">删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal v-model:open="open" :title="title" width="500px" @ok="submitForm" @cancel="cancel">
      <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="字典名称" name="dictName">
          <a-input v-model:value="form.dictName" placeholder="请输入字典名称" />
        </a-form-item>
        <a-form-item label="字典类型" name="dictType">
          <a-input v-model:value="form.dictType" placeholder="请输入字典类型" />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="form.status">
            <a-radio value="0">正常</a-radio>
            <a-radio value="1">停用</a-radio>
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
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { SearchOutlined, ReloadOutlined, PlusOutlined, DeleteOutlined, SyncOutlined } from '@ant-design/icons-vue'
import { listType, getType, addType, updateType, delType, refreshCache } from '@/api/system/dict'

const router = useRouter()
const loading = ref(false)
const open = ref(false)
const title = ref('')
const dictList = ref<any[]>([])
const selectedRowKeys = ref<number[]>([])
const formRef = ref()

const queryParams = reactive({ pageNum: 1, pageSize: 10, dictName: undefined, dictType: undefined, status: undefined })
const pagination = reactive({ current: 1, pageSize: 10, total: 0, showSizeChanger: true, showQuickJumper: true, showTotal: (total: number) => `共 ${total} 条` })
const form = reactive<any>({ dictId: undefined, dictName: undefined, dictType: undefined, status: '0', remark: undefined })
const rules = { dictName: [{ required: true, message: '字典名称不能为空', trigger: 'blur' }], dictType: [{ required: true, message: '字典类型不能为空', trigger: 'blur' }] }
const columns = [
  { title: '字典编号', dataIndex: 'dictId', width: 100 },
  { title: '字典名称', dataIndex: 'dictName', width: 150 },
  { title: '字典类型', dataIndex: 'dictType', width: 200 },
  { title: '状态', dataIndex: 'status', width: 100 },
  { title: '备注', dataIndex: 'remark', width: 200 },
  { title: '创建时间', dataIndex: 'createTime', width: 180 },
  { title: '操作', dataIndex: 'action', width: 150, fixed: 'right' },
]

const getList = async () => {
  loading.value = true
  try {
    const res = await listType(queryParams)
    dictList.value = res.data.rows
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryParams.dictName = undefined; queryParams.dictType = undefined; queryParams.status = undefined; handleQuery() }
const handleTableChange = (pag: any) => { queryParams.pageNum = pag.current; queryParams.pageSize = pag.pageSize; pagination.current = pag.current; pagination.pageSize = pag.pageSize; getList() }
const onSelectChange = (keys: number[]) => { selectedRowKeys.value = keys }
const cancel = () => { open.value = false; reset() }
const reset = () => { form.dictId = undefined; form.dictName = undefined; form.dictType = undefined; form.status = '0'; form.remark = undefined }

const handleAdd = () => { reset(); open.value = true; title.value = '添加字典类型' }
const handleUpdate = async (row: any) => { reset(); const res = await getType(row.dictId); Object.assign(form, res.data); open.value = true; title.value = '修改字典类型' }
const submitForm = async () => { await formRef.value.validate(); if (form.dictId !== undefined) { await updateType(form); message.success('修改成功') } else { await addType(form); message.success('新增成功') }; open.value = false; getList() }
const handleDelete = async (row?: any) => { const dictIds = row?.dictId || selectedRowKeys.value.join(','); await delType(dictIds); message.success('删除成功'); getList() }
const handleRefreshCache = async () => { await refreshCache(); message.success('刷新成功') }
const openDictData = (row: any) => { router.push('/system/dict-data/' + row.dictType) }

onMounted(() => getList())
</script>

<style lang="less" scoped>
.dict-container { .danger { color: #ff4d4f; } }
</style>
