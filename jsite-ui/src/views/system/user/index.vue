<template>
  <div class="user-container">
    <a-row :gutter="16">
      <!-- 左侧部门树 -->
      <a-col :span="5">
        <a-card title="部门" size="small">
          <a-input-search
            v-model:value="deptName"
            placeholder="请输入部门名称"
            style="margin-bottom: 8px"
          />
          <a-tree
            :tree-data="deptOptions"
            :field-names="{ title: 'label', key: 'id', children: 'children' }"
            default-expand-all
            @select="handleDeptNodeClick"
          />
        </a-card>
      </a-col>
      <!-- 右侧用户列表 -->
      <a-col :span="19">
        <!-- 搜索表单 -->
        <a-card size="small" :bordered="false">
          <a-form layout="inline" :model="queryParams">
            <a-form-item label="用户名称">
              <a-input
                v-model:value="queryParams.userName"
                placeholder="请输入用户名称"
                allow-clear
                @keyup.enter="handleQuery"
              />
            </a-form-item>
            <a-form-item label="手机号码">
              <a-input
                v-model:value="queryParams.phonenumber"
                placeholder="请输入手机号码"
                allow-clear
                @keyup.enter="handleQuery"
              />
            </a-form-item>
            <a-form-item label="状态">
              <a-select
                v-model:value="queryParams.status"
                placeholder="用户状态"
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
            <a-button type="primary" @click="handleAdd" v-permission="['system:user:add']">
              <PlusOutlined />新增
            </a-button>
            <a-button
              danger
              :disabled="selectedRowKeys.length === 0"
              @click="handleDelete"
              v-permission="['system:user:remove']"
            >
              <DeleteOutlined />删除
            </a-button>
          </a-space>
        </a-card>

        <!-- 表格 -->
        <a-card size="small" :bordered="false" style="margin-top: 8px">
          <a-table
            :columns="columns"
            :data-source="userList"
            :loading="loading"
            :pagination="pagination"
            :row-selection="{ selectedRowKeys, onChange: onSelectChange }"
            row-key="userId"
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
                  <a @click="handleUpdate(record)" v-permission="['system:user:edit']">编辑</a>
                  <a @click="handleResetPwd(record)" v-permission="['system:user:resetPwd']">重置密码</a>
                  <a-popconfirm
                    title="确定删除该用户吗？"
                    @confirm="handleDelete(record)"
                    v-permission="['system:user:remove']"
                  >
                    <a class="danger">删除</a>
                  </a-popconfirm>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
    </a-row>

    <!-- 添加或修改用户对话框 -->
    <a-modal
      v-model:open="open"
      :title="title"
      width="600px"
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
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="用户昵称" name="nickName">
              <a-input v-model:value="form.nickName" placeholder="请输入用户昵称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="归属部门" name="deptId">
              <a-tree-select
                v-model:value="form.deptId"
                :tree-data="deptOptions"
                :field-names="{ label: 'label', value: 'id', children: 'children' }"
                placeholder="请选择归属部门"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="手机号码" name="phonenumber">
              <a-input v-model:value="form.phonenumber" placeholder="请输入手机号码" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="邮箱" name="email">
              <a-input v-model:value="form.email" placeholder="请输入邮箱" />
            </a-form-item>
          </a-col>
          <a-col :span="12" v-if="form.userId === undefined">
            <a-form-item label="用户名称" name="userName">
              <a-input v-model:value="form.userName" placeholder="请输入用户名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12" v-if="form.userId === undefined">
            <a-form-item label="用户密码" name="password">
              <a-input-password v-model:value="form.password" placeholder="请输入用户密码" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="用户性别" name="sex">
              <a-select v-model:value="form.sex" placeholder="请选择">
                <a-select-option value="0">男</a-select-option>
                <a-select-option value="1">女</a-select-option>
                <a-select-option value="2">未知</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="状态" name="status">
              <a-radio-group v-model:value="form.status">
                <a-radio value="0">正常</a-radio>
                <a-radio value="1">停用</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="岗位" name="postIds">
              <a-select
                v-model:value="form.postIds"
                mode="multiple"
                placeholder="请选择"
              >
                <a-select-option
                  v-for="item in postOptions"
                  :key="item.postId"
                  :value="item.postId"
                >
                  {{ item.postName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="角色" name="roleIds">
              <a-select
                v-model:value="form.roleIds"
                mode="multiple"
                placeholder="请选择"
              >
                <a-select-option
                  v-for="item in roleOptions"
                  :key="item.roleId"
                  :value="item.roleId"
                >
                  {{ item.roleName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="备注" name="remark" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
              <a-textarea v-model:value="form.remark" placeholder="请输入内容" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>

    <!-- 重置密码对话框 -->
    <a-modal
      v-model:open="resetPwdOpen"
      title="重置密码"
      @ok="submitResetPwd"
    >
      <a-form :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="用户名称">
          {{ resetPwdForm.userName }}
        </a-form-item>
        <a-form-item label="新密码">
          <a-input-password v-model:value="resetPwdForm.password" placeholder="请输入新密码" />
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
import {
  listUser,
  getUser,
  addUser,
  updateUser,
  delUser,
  resetUserPwd,
  changeUserStatus,
  deptTreeSelect,
} from '@/api/system/user'

const loading = ref(false)
const open = ref(false)
const resetPwdOpen = ref(false)
const title = ref('')
const userList = ref<any[]>([])
const deptOptions = ref<any[]>([])
const postOptions = ref<any[]>([])
const roleOptions = ref<any[]>([])
const selectedRowKeys = ref<number[]>([])
const deptName = ref('')
const formRef = ref()

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: undefined,
  phonenumber: undefined,
  status: undefined,
  deptId: undefined,
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
  userId: undefined,
  deptId: undefined,
  userName: undefined,
  nickName: undefined,
  password: undefined,
  phonenumber: undefined,
  email: undefined,
  sex: undefined,
  status: '0',
  remark: undefined,
  postIds: [],
  roleIds: [],
})

const resetPwdForm = reactive({
  userId: undefined as number | undefined,
  userName: '',
  password: '',
})

const rules = {
  userName: [{ required: true, message: '用户名称不能为空', trigger: 'blur' }],
  nickName: [{ required: true, message: '用户昵称不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '用户密码不能为空', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
  phonenumber: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }],
}

const columns = [
  { title: '用户编号', dataIndex: 'userId', width: 100 },
  { title: '用户名称', dataIndex: 'userName', width: 120 },
  { title: '用户昵称', dataIndex: 'nickName', width: 120 },
  { title: '部门', dataIndex: ['dept', 'deptName'], width: 120 },
  { title: '手机号码', dataIndex: 'phonenumber', width: 120 },
  { title: '状态', dataIndex: 'status', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', width: 180 },
  { title: '操作', dataIndex: 'action', width: 180, fixed: 'right' },
]

// 查询用户列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listUser(queryParams)
    userList.value = res.data.rows
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

// 查询部门下拉树结构
const getDeptTree = async () => {
  const res = await deptTreeSelect()
  deptOptions.value = handleTree(res.data)
}

// 转换部门数据结构
const handleTree = (data: any[], parentId = 0): any[] => {
  const result: any[] = []
  for (const item of data) {
    if (item.parentId === parentId) {
      const children = handleTree(data, item.deptId)
      result.push({
        id: item.deptId,
        label: item.deptName,
        children: children.length > 0 ? children : undefined,
      })
    }
  }
  return result
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const resetQuery = () => {
  queryParams.userName = undefined
  queryParams.phonenumber = undefined
  queryParams.status = undefined
  queryParams.deptId = undefined
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

// 部门树节点点击
const handleDeptNodeClick = (keys: any[]) => {
  queryParams.deptId = keys[0]
  handleQuery()
}

// 取消
const cancel = () => {
  open.value = false
  reset()
}

// 重置表单
const reset = () => {
  form.userId = undefined
  form.deptId = undefined
  form.userName = undefined
  form.nickName = undefined
  form.password = undefined
  form.phonenumber = undefined
  form.email = undefined
  form.sex = undefined
  form.status = '0'
  form.remark = undefined
  form.postIds = []
  form.roleIds = []
}

// 新增
const handleAdd = async () => {
  reset()
  const res = await getUser()
  postOptions.value = res.data.posts
  roleOptions.value = res.data.roles
  open.value = true
  title.value = '添加用户'
}

// 修改
const handleUpdate = async (row: any) => {
  reset()
  const res = await getUser(row.userId)
  Object.assign(form, res.data.user)
  form.postIds = res.data.postIds
  form.roleIds = res.data.roleIds
  postOptions.value = res.data.posts
  roleOptions.value = res.data.roles
  open.value = true
  title.value = '修改用户'
}

// 提交
const submitForm = async () => {
  await formRef.value.validate()
  if (form.userId !== undefined) {
    await updateUser(form)
    message.success('修改成功')
  } else {
    await addUser(form)
    message.success('新增成功')
  }
  open.value = false
  getList()
}

// 删除
const handleDelete = async (row?: any) => {
  const userIds = row?.userId || selectedRowKeys.value.join(',')
  await delUser(userIds)
  message.success('删除成功')
  getList()
}

// 状态修改
const handleStatusChange = async (row: any, checked: boolean) => {
  const status = checked ? '0' : '1'
  await changeUserStatus(row.userId, status)
  message.success('修改成功')
  getList()
}

// 重置密码
const handleResetPwd = (row: any) => {
  resetPwdForm.userId = row.userId
  resetPwdForm.userName = row.userName
  resetPwdForm.password = ''
  resetPwdOpen.value = true
}

// 提交重置密码
const submitResetPwd = async () => {
  if (!resetPwdForm.password) {
    message.warning('请输入新密码')
    return
  }
  await resetUserPwd(resetPwdForm.userId!, resetPwdForm.password)
  message.success('重置成功')
  resetPwdOpen.value = false
}

onMounted(() => {
  getList()
  getDeptTree()
})
</script>

<style lang="less" scoped>
.user-container {
  .danger {
    color: #ff4d4f;
  }
}
</style>
