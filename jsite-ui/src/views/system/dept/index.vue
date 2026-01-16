<template>
  <div class="dept-container">
    <!-- 搜索表单 -->
    <a-card size="small" :bordered="false">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="部门名称">
          <a-input
            v-model:value="queryParams.deptName"
            placeholder="请输入部门名称"
            allow-clear
            @keyup.enter="handleQuery"
          />
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="queryParams.status"
            placeholder="部门状态"
            allow-clear
            style="width: 120px"
          >
            <a-select-option value="0">正常</a-select-option>
            <a-select-option value="1">停用</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleQuery">
              <SearchOutlined />搜索
            </a-button>
            <a-button @click="resetQuery">
              <ReloadOutlined />重置
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <!-- 操作按钮 -->
    <a-card size="small" :bordered="false" style="margin-top: 8px">
      <a-button type="primary" @click="handleAdd()" v-permission="['system:dept:add']">
        <PlusOutlined />新增
      </a-button>
    </a-card>

    <!-- 表格 -->
    <a-card size="small" :bordered="false" style="margin-top: 8px">
      <a-table
        :columns="columns"
        :data-source="deptList"
        :loading="loading"
        :pagination="false"
        row-key="deptId"
        default-expand-all-rows
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'status'">
            <a-tag :color="record.status === '0' ? 'green' : 'red'">
              {{ record.status === '0' ? '正常' : '停用' }}
            </a-tag>
          </template>
          <template v-if="column.dataIndex === 'action'">
            <a-space>
              <a @click="handleUpdate(record)" v-permission="['system:dept:edit']">编辑</a>
              <a @click="handleAdd(record)" v-permission="['system:dept:add']">新增</a>
              <a-popconfirm
                title="确定删除该部门吗？"
                @confirm="handleDelete(record)"
                v-permission="['system:dept:remove']"
              >
                <a class="danger" v-if="record.parentId !== 0">删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 添加或修改部门对话框 -->
    <a-modal
      v-model:open="open"
      :title="title"
      width="500px"
      @ok="submitForm"
      @cancel="cancel"
    >
      <a-form
        ref="formRef"
        :model="form"
        :rules="rules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="上级部门" name="parentId" v-if="form.parentId !== 0">
          <a-tree-select
            v-model:value="form.parentId"
            :tree-data="deptOptions"
            :field-names="{ label: 'deptName', value: 'deptId', children: 'children' }"
            placeholder="选择上级部门"
          />
        </a-form-item>
        <a-form-item label="部门名称" name="deptName">
          <a-input v-model:value="form.deptName" placeholder="请输入部门名称" />
        </a-form-item>
        <a-form-item label="显示排序" name="orderNum">
          <a-input-number v-model:value="form.orderNum" :min="0" style="width: 100%" />
        </a-form-item>
        <a-form-item label="负责人" name="leader">
          <a-input v-model:value="form.leader" placeholder="请输入负责人" />
        </a-form-item>
        <a-form-item label="联系电话" name="phone">
          <a-input v-model:value="form.phone" placeholder="请输入联系电话" />
        </a-form-item>
        <a-form-item label="邮箱" name="email">
          <a-input v-model:value="form.email" placeholder="请输入邮箱" />
        </a-form-item>
        <a-form-item label="部门状态" name="status">
          <a-radio-group v-model:value="form.status">
            <a-radio value="0">正常</a-radio>
            <a-radio value="1">停用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { SearchOutlined, ReloadOutlined, PlusOutlined } from '@ant-design/icons-vue'
import { listDept, getDept, addDept, updateDept, delDept, listDeptExcludeChild } from '@/api/system/dept'

const loading = ref(false)
const open = ref(false)
const title = ref('')
const deptList = ref<any[]>([])
const deptOptions = ref<any[]>([])
const formRef = ref()

const queryParams = reactive({
  deptName: undefined,
  status: undefined,
})

const form = reactive<any>({
  deptId: undefined,
  parentId: 0,
  deptName: undefined,
  orderNum: 0,
  leader: undefined,
  phone: undefined,
  email: undefined,
  status: '0',
})

const rules = {
  deptName: [{ required: true, message: '部门名称不能为空', trigger: 'blur' }],
  orderNum: [{ required: true, message: '显示排序不能为空', trigger: 'blur' }],
}

const columns = [
  { title: '部门名称', dataIndex: 'deptName', width: 200 },
  { title: '排序', dataIndex: 'orderNum', width: 80 },
  { title: '负责人', dataIndex: 'leader', width: 120 },
  { title: '联系电话', dataIndex: 'phone', width: 120 },
  { title: '邮箱', dataIndex: 'email', width: 180 },
  { title: '状态', dataIndex: 'status', width: 80 },
  { title: '创建时间', dataIndex: 'createTime', width: 180 },
  { title: '操作', dataIndex: 'action', width: 180, fixed: 'right' },
]

const handleTree = (data: any[], parentId = 0): any[] => {
  const result: any[] = []
  for (const item of data) {
    if (item.parentId === parentId) {
      const children = handleTree(data, item.deptId)
      if (children.length > 0) {
        item.children = children
      }
      result.push(item)
    }
  }
  return result
}

const getList = async () => {
  loading.value = true
  try {
    const res = await listDept(queryParams)
    deptList.value = handleTree(res.data)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => getList()

const resetQuery = () => {
  queryParams.deptName = undefined
  queryParams.status = undefined
  handleQuery()
}

const cancel = () => {
  open.value = false
  reset()
}

const reset = () => {
  form.deptId = undefined
  form.parentId = 0
  form.deptName = undefined
  form.orderNum = 0
  form.leader = undefined
  form.phone = undefined
  form.email = undefined
  form.status = '0'
}

const handleAdd = async (row?: any) => {
  reset()
  const res = await listDept()
  deptOptions.value = [{ deptId: 0, deptName: '主类目', children: handleTree(res.data) }]
  if (row) {
    form.parentId = row.deptId
  }
  open.value = true
  title.value = '添加部门'
}

const handleUpdate = async (row: any) => {
  reset()
  const [deptRes, listRes] = await Promise.all([
    getDept(row.deptId),
    listDeptExcludeChild(row.deptId),
  ])
  Object.assign(form, deptRes.data)
  deptOptions.value = [{ deptId: 0, deptName: '主类目', children: handleTree(listRes.data) }]
  open.value = true
  title.value = '修改部门'
}

const submitForm = async () => {
  await formRef.value.validate()
  if (form.deptId !== undefined) {
    await updateDept(form)
    message.success('修改成功')
  } else {
    await addDept(form)
    message.success('新增成功')
  }
  open.value = false
  getList()
}

const handleDelete = async (row: any) => {
  await delDept(row.deptId)
  message.success('删除成功')
  getList()
}

onMounted(() => getList())
</script>

<style lang="less" scoped>
.dept-container {
  .danger {
    color: #ff4d4f;
  }
}
</style>
