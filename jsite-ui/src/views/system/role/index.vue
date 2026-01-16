<template>
  <div class="role-container">
    <!-- 搜索表单 -->
    <a-card size="small" :bordered="false">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="角色名称">
          <a-input
            v-model:value="queryParams.roleName"
            placeholder="请输入角色名称"
            allow-clear
            @keyup.enter="handleQuery"
          />
        </a-form-item>
        <a-form-item label="权限字符">
          <a-input
            v-model:value="queryParams.roleKey"
            placeholder="请输入权限字符"
            allow-clear
            @keyup.enter="handleQuery"
          />
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="queryParams.status"
            placeholder="角色状态"
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
      <a-space>
        <a-button type="primary" @click="handleAdd" v-permission="['system:role:add']">
          <PlusOutlined />新增
        </a-button>
        <a-button
          danger
          :disabled="selectedRowKeys.length === 0"
          @click="handleDelete"
          v-permission="['system:role:remove']"
        >
          <DeleteOutlined />删除
        </a-button>
      </a-space>
    </a-card>

    <!-- 表格 -->
    <a-card size="small" :bordered="false" style="margin-top: 8px">
      <a-table
        :columns="columns"
        :data-source="roleList"
        :loading="loading"
        :pagination="pagination"
        :row-selection="{ selectedRowKeys, onChange: onSelectChange }"
        row-key="roleId"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'status'">
            <a-switch
              :checked="record.status === '0'"
              checked-children="正常"
              un-checked-children="停用"
              @change="(checked: boolean) => handleStatusChange(record, checked)"
            />
          </template>
          <template v-if="column.dataIndex === 'action'">
            <a-space>
              <a @click="handleUpdate(record)" v-permission="['system:role:edit']">编辑</a>
              <a @click="handleDataScope(record)" v-permission="['system:role:edit']">数据权限</a>
              <a-popconfirm
                title="确定删除该角色吗？"
                @confirm="handleDelete(record)"
                v-permission="['system:role:remove']"
              >
                <a class="danger">删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 添加或修改角色对话框 -->
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
        <a-form-item label="角色名称" name="roleName">
          <a-input v-model:value="form.roleName" placeholder="请输入角色名称" />
        </a-form-item>
        <a-form-item label="权限字符" name="roleKey">
          <a-input v-model:value="form.roleKey" placeholder="请输入权限字符" />
        </a-form-item>
        <a-form-item label="角色顺序" name="roleSort">
          <a-input-number v-model:value="form.roleSort" :min="0" style="width: 100%" />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="form.status">
            <a-radio value="0">正常</a-radio>
            <a-radio value="1">停用</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="菜单权限">
          <a-tree
            v-model:checkedKeys="menuCheckedKeys"
            :tree-data="menuOptions"
            checkable
            :field-names="{ title: 'label', key: 'id', children: 'children' }"
            style="max-height: 300px; overflow: auto"
          />
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
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  DeleteOutlined,
} from '@ant-design/icons-vue'
import { listRole, getRole, addRole, updateRole, delRole, changeRoleStatus } from '@/api/system/role'
import { roleMenuTreeselect } from '@/api/system/menu'

const loading = ref(false)
const open = ref(false)
const title = ref('')
const roleList = ref<any[]>([])
const menuOptions = ref<any[]>([])
const menuCheckedKeys = ref<number[]>([])
const selectedRowKeys = ref<number[]>([])
const formRef = ref()

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roleName: undefined,
  roleKey: undefined,
  status: undefined,
})

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条`,
})

const form = reactive<any>({
  roleId: undefined,
  roleName: undefined,
  roleKey: undefined,
  roleSort: 0,
  status: '0',
  menuIds: [],
  remark: undefined,
})

const rules = {
  roleName: [{ required: true, message: '角色名称不能为空', trigger: 'blur' }],
  roleKey: [{ required: true, message: '权限字符不能为空', trigger: 'blur' }],
}

const columns = [
  { title: '角色编号', dataIndex: 'roleId', width: 100 },
  { title: '角色名称', dataIndex: 'roleName', width: 150 },
  { title: '权限字符', dataIndex: 'roleKey', width: 150 },
  { title: '显示顺序', dataIndex: 'roleSort', width: 100 },
  { title: '状态', dataIndex: 'status', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', width: 180 },
  { title: '操作', dataIndex: 'action', width: 200, fixed: 'right' },
]

// 查询角色列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listRole(queryParams)
    roleList.value = res.data.rows
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const resetQuery = () => {
  queryParams.roleName = undefined
  queryParams.roleKey = undefined
  queryParams.status = undefined
  handleQuery()
}

// 表格变化
const handleTableChange = (pag: any) => {
  queryParams.pageNum = pag.current
  queryParams.pageSize = pag.pageSize
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  getList()
}

// 多选
const onSelectChange = (keys: number[]) => {
  selectedRowKeys.value = keys
}

// 取消
const cancel = () => {
  open.value = false
  reset()
}

// 重置表单
const reset = () => {
  form.roleId = undefined
  form.roleName = undefined
  form.roleKey = undefined
  form.roleSort = 0
  form.status = '0'
  form.menuIds = []
  form.remark = undefined
  menuCheckedKeys.value = []
}

// 转换菜单数据结构
const handleTree = (data: any[]): any[] => {
  return data.map((item) => ({
    id: item.menuId,
    label: item.menuName,
    children: item.children ? handleTree(item.children) : undefined,
  }))
}

// 新增
const handleAdd = async () => {
  reset()
  const res = await roleMenuTreeselect(0)
  menuOptions.value = handleTree(res.data.menus)
  open.value = true
  title.value = '添加角色'
}

// 修改
const handleUpdate = async (row: any) => {
  reset()
  const roleId = row.roleId
  const [roleRes, menuRes] = await Promise.all([
    getRole(roleId),
    roleMenuTreeselect(roleId),
  ])
  Object.assign(form, roleRes.data)
  menuOptions.value = handleTree(menuRes.data.menus)
  menuCheckedKeys.value = menuRes.data.checkedKeys
  open.value = true
  title.value = '修改角色'
}

// 数据权限
const handleDataScope = (row: any) => {
  message.info('数据权限功能开发中...')
}

// 提交
const submitForm = async () => {
  await formRef.value.validate()
  form.menuIds = menuCheckedKeys.value
  if (form.roleId !== undefined) {
    await updateRole(form)
    message.success('修改成功')
  } else {
    await addRole(form)
    message.success('新增成功')
  }
  open.value = false
  getList()
}

// 删除
const handleDelete = async (row?: any) => {
  const roleIds = row?.roleId || selectedRowKeys.value.join(',')
  await delRole(roleIds)
  message.success('删除成功')
  getList()
}

// 状态修改
const handleStatusChange = async (row: any, checked: boolean) => {
  const status = checked ? '0' : '1'
  await changeRoleStatus(row.roleId, status)
  message.success('修改成功')
  getList()
}

onMounted(() => {
  getList()
})
</script>

<style lang="less" scoped>
.role-container {
  .danger {
    color: #ff4d4f;
  }
}
</style>
