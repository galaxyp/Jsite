<template>
  <div class="menu-container">
    <!-- 搜索表单 -->
    <a-card size="small" :bordered="false">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="菜单名称">
          <a-input
            v-model:value="queryParams.menuName"
            placeholder="请输入菜单名称"
            allow-clear
            @keyup.enter="handleQuery"
          />
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="queryParams.status"
            placeholder="菜单状态"
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
        <a-button type="primary" @click="handleAdd()" v-permission="['system:menu:add']">
          <PlusOutlined />新增
        </a-button>
        <a-button @click="toggleExpandAll">
          {{ isExpandAll ? '折叠' : '展开' }}
        </a-button>
      </a-space>
    </a-card>

    <!-- 表格 -->
    <a-card size="small" :bordered="false" style="margin-top: 8px">
      <a-table
        :loading="loading"
        :columns="columns"
        :data-source="menuList"
        :pagination="false"
        :expanded-row-keys="expandedRowKeys"
        :row-key="(record) => String(record.menuId)"
        @expand="onExpand"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'icon'">
            <component :is="resolveIcon(record.icon)" />
          </template>
          <template v-if="column.dataIndex === 'status'">
            <a-tag :color="record.status === '0' ? 'green' : 'red'">
              {{ record.status === '0' ? '正常' : '停用' }}
            </a-tag>
          </template>
          <template v-if="column.dataIndex === 'action'">
            <a-space>
              <a @click="handleUpdate(record)" v-permission="['system:menu:edit']">编辑</a>
              <a @click="handleAdd(record)" v-permission="['system:menu:add']">新增</a>
              <a-popconfirm
                title="确定删除该菜单吗？"
                @confirm="handleDelete(record)"
              >
                <a class="danger" v-permission="['system:menu:remove']">删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 添加或修改菜单对话框 -->
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
          <a-col :span="24">
            <a-form-item label="上级菜单" name="parentId">
              <a-tree-select
                v-model:value="form.parentId"
                :tree-data="menuOptions"
                :field-names="{ label: 'menuName', value: 'menuId', children: 'children' }"
                placeholder="选择上级菜单"
              />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="菜单类型" name="menuType">
              <a-radio-group v-model:value="form.menuType">
                <a-radio value="M">目录</a-radio>
                <a-radio value="C">菜单</a-radio>
                <a-radio value="F">按钮</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="菜单名称" name="menuName">
              <a-input v-model:value="form.menuName" placeholder="请输入菜单名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="显示排序" name="orderNum">
              <a-input-number v-model:value="form.orderNum" :min="0" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12" v-if="form.menuType !== 'F'">
            <a-form-item label="路由地址" name="path">
              <a-input v-model:value="form.path" placeholder="请输入路由地址" />
            </a-form-item>
          </a-col>
          <a-col :span="12" v-if="form.menuType === 'C'">
            <a-form-item label="组件路径" name="component">
              <a-input v-model:value="form.component" placeholder="请输入组件路径" />
            </a-form-item>
          </a-col>
          <a-col :span="12" v-if="form.menuType !== 'M'">
            <a-form-item label="权限标识" name="perms">
              <a-input v-model:value="form.perms" placeholder="请输入权限标识" />
            </a-form-item>
          </a-col>
          <a-col :span="12" v-if="form.menuType !== 'F'">
            <a-form-item label="菜单图标" name="icon">
              <a-input v-model:value="form.icon" placeholder="请输入图标名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="菜单状态" name="status">
              <a-radio-group v-model:value="form.status">
                <a-radio value="0">正常</a-radio>
                <a-radio value="1">停用</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col :span="12" v-if="form.menuType !== 'F'">
            <a-form-item label="显示状态" name="visible">
              <a-radio-group v-model:value="form.visible">
                <a-radio value="0">显示</a-radio>
                <a-radio value="1">隐藏</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
} from '@ant-design/icons-vue'
import { listMenu, getMenu, addMenu, updateMenu, delMenu } from '@/api/system/menu'

const loading = ref(false)
const open = ref(false)
const title = ref('')
const menuList = ref<any[]>([])
const menuOptions = ref<any[]>([])
const expandedRowKeys = ref<string[]>([])
const isExpandAll = ref(false)
const formRef = ref()

const queryParams = reactive({
  menuName: undefined,
  status: undefined,
})

const form = reactive<any>({
  menuId: undefined,
  parentId: 0,
  menuType: 'M',
  menuName: undefined,
  icon: undefined,
  orderNum: 0,
  isFrame: '1',
  isCache: '0',
  visible: '0',
  status: '0',
  path: undefined,
  component: undefined,
  perms: undefined,
})

const rules = computed(() => ({
  menuName: [{ required: true, message: '菜单名称不能为空', trigger: 'blur' }],
  orderNum: [{ required: true, message: '显示排序不能为空', trigger: 'blur' }],
  path: form.menuType !== 'F'
    ? [{ required: true, message: '路由地址不能为空', trigger: 'blur' }]
    : [],
}))

// 解析图标名称：排除空值和 '#' 占位符，防止生成非法标签名
const resolveIcon = (icon: string): string => {
  if (!icon || icon === '#' || icon.trim() === '') return 'AppstoreOutlined'
  // 已经是完整组件名（以 Outlined/Filled/TwoTone 结尾），直接用
  if (icon.endsWith('Outlined') || icon.endsWith('Filled') || icon.endsWith('TwoTone')) {
    return icon
  }
  return icon + 'Outlined'
}

const columns = [
  { title: '菜单名称', dataIndex: 'menuName', width: 200 },
  { title: '图标', dataIndex: 'icon', width: 60 },
  { title: '排序', dataIndex: 'orderNum', width: 80 },
  { title: '权限标识', dataIndex: 'perms', width: 150 },
  { title: '组件路径', dataIndex: 'component', width: 200 },
  { title: '状态', dataIndex: 'status', width: 80 },
  { title: '创建时间', dataIndex: 'createTime', width: 180 },
  { title: '操作', dataIndex: 'action', width: 180, fixed: 'right' },
]

// 将后端平铺列表转换为树形结构
const buildTree = (data: any[]): any[] => {
  if (!data || data.length === 0) return []
  
  const map = new Map<number, any>()
  const roots: any[] = []
  
  // 第一遍：为每个节点创建副本并建立索引
  data.forEach(item => {
    map.set(item.menuId, { ...item, children: [] })
  })
  
  // 第二遍：建立父子关系
  data.forEach(item => {
    const node = map.get(item.menuId)!
    if (item.parentId === 0) {
      // 根节点
      roots.push(node)
    } else {
      // 子节点：挂到父节点下
      const parent = map.get(item.parentId)
      if (parent) {
        parent.children.push(node)
      } else {
        // 找不到父节点，当作根节点
        roots.push(node)
      }
    }
  })
  
  // 第三遍：清理空 children（让 Ant Design Vue 不显示展开图标）
  const removeEmptyChildren = (nodes: any[]) => {
    nodes.forEach(node => {
      if (node.children && node.children.length === 0) {
        delete node.children
      } else if (node.children && node.children.length > 0) {
        removeEmptyChildren(node.children)
      }
    })
  }
  removeEmptyChildren(roots)
  
  return roots
}

// 递归收集树中所有有子节点的节点 ID（转为字符串，与 row-key 保持一致）
const collectExpandedKeys = (items: any[]): string[] => {
  const keys: string[] = []
  for (const item of items) {
    if (item.children && item.children.length > 0) {
      keys.push(String(item.menuId))
      keys.push(...collectExpandedKeys(item.children))
    }
  }
  return keys
}

// 查询菜单列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listMenu(queryParams)
    if (!res || !res.data) {
      message.warning('未获取到菜单数据')
    menuList.value = []
    expandedRowKeys.value = [] as string[]
    return
  }
  const flatList: any[] = res.data
  const tree = buildTree(flatList)
  menuList.value = tree
  menuOptions.value = [{ menuId: 0, menuName: '主类目', children: tree }]
  // 默认折叠，不设置 expandedRowKeys
  expandedRowKeys.value = []
  } catch (e) {
    console.error('菜单列表加载失败', e)
    message.error('加载菜单列表失败，请刷新重试')
    menuList.value = []
    expandedRowKeys.value = [] as string[]
  } finally {
    loading.value = false
  }
}

// 展开/折叠行（用户点击触发）
const onExpand = (expanded: boolean, record: any) => {
  const id = String(record.menuId)
  const keySet = new Set(expandedRowKeys.value)
  if (expanded && !keySet.has(id)) {
    expandedRowKeys.value = [...expandedRowKeys.value, id]
  } else if (!expanded && keySet.has(id)) {
    expandedRowKeys.value = expandedRowKeys.value.filter((k) => k !== id)
  }
}

// 搜索
const handleQuery = () => {
  getList()
}

// 重置
const resetQuery = () => {
  queryParams.menuName = undefined
  queryParams.status = undefined
  handleQuery()
}

// 展开/折叠全部
const toggleExpandAll = () => {
  isExpandAll.value = !isExpandAll.value
  if (isExpandAll.value) {
    expandedRowKeys.value = collectExpandedKeys(menuList.value)
  } else {
    expandedRowKeys.value = []
  }
}

// 取消
const cancel = () => {
  open.value = false
  reset()
}

// 重置表单
const reset = () => {
  form.menuId = undefined
  form.parentId = 0
  form.menuType = 'M'
  form.menuName = undefined
  form.icon = undefined
  form.orderNum = 0
  form.isFrame = '1'
  form.isCache = '0'
  form.visible = '0'
  form.status = '0'
  form.path = undefined
  form.component = undefined
  form.perms = undefined
}

// 新增
const handleAdd = (row?: any) => {
  reset()
  if (row) {
    form.parentId = row.menuId
  }
  open.value = true
  title.value = '添加菜单'
}

// 修改
const handleUpdate = async (row: any) => {
  reset()
  const res = await getMenu(row.menuId)
  Object.assign(form, res.data)
  open.value = true
  title.value = '修改菜单'
}

// 提交
const submitForm = async () => {
  await formRef.value.validate()
  if (form.menuId !== undefined) {
    await updateMenu(form)
    message.success('修改成功')
  } else {
    await addMenu(form)
    message.success('新增成功')
  }
  open.value = false
  await getList()
}

// 删除
const handleDelete = async (row: any) => {
  await delMenu(row.menuId)
  message.success('删除成功')
  await getList()
}

onMounted(() => {
  getList()
})
</script>

<style lang="less" scoped>
.menu-container {
  .danger {
    color: #ff4d4f;
  }
}
</style>
