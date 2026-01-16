<template>
  <div class="post-container">
    <!-- 搜索表单 -->
    <a-card size="small" :bordered="false">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="岗位编码">
          <a-input v-model:value="queryParams.postCode" placeholder="请输入岗位编码" allow-clear />
        </a-form-item>
        <a-form-item label="岗位名称">
          <a-input v-model:value="queryParams.postName" placeholder="请输入岗位名称" allow-clear />
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="queryParams.status" placeholder="岗位状态" allow-clear style="width: 120px">
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
        <a-button type="primary" @click="handleAdd" v-permission="['system:post:add']"><PlusOutlined />新增</a-button>
        <a-button danger :disabled="selectedRowKeys.length === 0" @click="handleDelete" v-permission="['system:post:remove']"><DeleteOutlined />删除</a-button>
      </a-space>
    </a-card>

    <a-card size="small" :bordered="false" style="margin-top: 8px">
      <a-table :columns="columns" :data-source="postList" :loading="loading" :pagination="pagination" :row-selection="{ selectedRowKeys, onChange: onSelectChange }" row-key="postId" @change="handleTableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'status'">
            <a-tag :color="record.status === '0' ? 'green' : 'red'">{{ record.status === '0' ? '正常' : '停用' }}</a-tag>
          </template>
          <template v-if="column.dataIndex === 'action'">
            <a-space>
              <a @click="handleUpdate(record)" v-permission="['system:post:edit']">编辑</a>
              <a-popconfirm title="确定删除该岗位吗？" @confirm="handleDelete(record)" v-permission="['system:post:remove']">
                <a class="danger">删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal v-model:open="open" :title="title" width="500px" @ok="submitForm" @cancel="cancel">
      <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="岗位名称" name="postName">
          <a-input v-model:value="form.postName" placeholder="请输入岗位名称" />
        </a-form-item>
        <a-form-item label="岗位编码" name="postCode">
          <a-input v-model:value="form.postCode" placeholder="请输入岗位编码" />
        </a-form-item>
        <a-form-item label="岗位顺序" name="postSort">
          <a-input-number v-model:value="form.postSort" :min="0" style="width: 100%" />
        </a-form-item>
        <a-form-item label="岗位状态" name="status">
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
import { message } from 'ant-design-vue'
import { SearchOutlined, ReloadOutlined, PlusOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import { listPost, getPost, addPost, updatePost, delPost } from '@/api/system/post'

const loading = ref(false)
const open = ref(false)
const title = ref('')
const postList = ref<any[]>([])
const selectedRowKeys = ref<number[]>([])
const formRef = ref()

const queryParams = reactive({ pageNum: 1, pageSize: 10, postCode: undefined, postName: undefined, status: undefined })
const pagination = reactive({ current: 1, pageSize: 10, total: 0, showSizeChanger: true, showQuickJumper: true, showTotal: (total: number) => `共 ${total} 条` })
const form = reactive<any>({ postId: undefined, postName: undefined, postCode: undefined, postSort: 0, status: '0', remark: undefined })
const rules = { postName: [{ required: true, message: '岗位名称不能为空', trigger: 'blur' }], postCode: [{ required: true, message: '岗位编码不能为空', trigger: 'blur' }] }
const columns = [
  { title: '岗位编号', dataIndex: 'postId', width: 100 },
  { title: '岗位编码', dataIndex: 'postCode', width: 150 },
  { title: '岗位名称', dataIndex: 'postName', width: 150 },
  { title: '岗位排序', dataIndex: 'postSort', width: 100 },
  { title: '状态', dataIndex: 'status', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', width: 180 },
  { title: '操作', dataIndex: 'action', width: 150, fixed: 'right' },
]

const getList = async () => {
  loading.value = true
  try {
    const res = await listPost(queryParams)
    postList.value = res.data.rows
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryParams.postCode = undefined; queryParams.postName = undefined; queryParams.status = undefined; handleQuery() }
const handleTableChange = (pag: any) => { queryParams.pageNum = pag.current; queryParams.pageSize = pag.pageSize; pagination.current = pag.current; pagination.pageSize = pag.pageSize; getList() }
const onSelectChange = (keys: number[]) => { selectedRowKeys.value = keys }
const cancel = () => { open.value = false; reset() }
const reset = () => { form.postId = undefined; form.postName = undefined; form.postCode = undefined; form.postSort = 0; form.status = '0'; form.remark = undefined }

const handleAdd = () => { reset(); open.value = true; title.value = '添加岗位' }
const handleUpdate = async (row: any) => { reset(); const res = await getPost(row.postId); Object.assign(form, res.data); open.value = true; title.value = '修改岗位' }
const submitForm = async () => { await formRef.value.validate(); if (form.postId !== undefined) { await updatePost(form); message.success('修改成功') } else { await addPost(form); message.success('新增成功') }; open.value = false; getList() }
const handleDelete = async (row?: any) => { const postIds = row?.postId || selectedRowKeys.value.join(','); await delPost(postIds); message.success('删除成功'); getList() }

onMounted(() => getList())
</script>

<style lang="less" scoped>
.post-container { .danger { color: #ff4d4f; } }
</style>
