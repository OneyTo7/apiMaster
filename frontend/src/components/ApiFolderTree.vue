<template>
  <div class="folder-tree-container">
    <div class="tree-header">
      <span class="tree-title">接口管理</span>
      <el-dropdown trigger="click">
        <el-button type="primary" size="small" circle>
          <el-icon><Plus /></el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="showCreateFolderDialog()">
              <el-icon><Folder /></el-icon> 新建文件夹
            </el-dropdown-item>
            <el-dropdown-item @click="handleCreateApi()">
              <el-icon><Document /></el-icon> 新建接口
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <div class="tree-search">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索接口"
        size="small"
        clearable
        @input="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <div class="tree-content">
      <el-tree
        ref="treeRef"
        :data="treeData"
        :props="defaultProps"
        node-key="id"
        :expand-on-click-node="false"
        :highlight-current="true"
        @node-click="handleNodeClick"
        @node-contextmenu="handleContextMenu"
      >
        <template #default="{ node, data }">
          <div class="tree-node" :class="{ 'is-api': data.type === 'api' }">
            <el-icon v-if="data.type === 'folder'" class="node-icon">
              <Folder v-if="!node.expanded" />
              <FolderOpened v-else />
            </el-icon>
            <el-icon v-else class="node-icon api-icon">
              <Document />
            </el-icon>
            <span class="node-label">{{ node.label }}</span>
            <span v-if="data.type === 'folder' && data.childrenCount" class="node-count">
              ({{ data.childrenCount }})
            </span>
            <el-tag
              v-if="data.type === 'api'"
              :type="getMethodType(data.method)"
              size="small"
              class="method-tag"
            >
              {{ data.method }}
            </el-tag>
          </div>
        </template>
      </el-tree>
    </div>

    <!-- 创建文件夹对话框 -->
    <el-dialog
      v-model="folderDialogVisible"
      title="新建文件夹"
      width="400px"
    >
      <el-form :model="folderForm" :rules="folderRules" ref="folderFormRef">
        <el-form-item label="文件夹名称" prop="name">
          <el-input v-model="folderForm.name" placeholder="请输入文件夹名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="folderForm.description"
            type="textarea"
            placeholder="请输入描述（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="folderDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreateFolder">确定</el-button>
      </template>
    </el-dialog>

    <!-- 右键菜单 -->
    <el-dropdown
      ref="contextMenuRef"
      trigger="contextmenu"
      @visible-change="handleContextMenuVisibleChange"
    >
      <span></span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="handleRename">
            <el-icon><Edit /></el-icon> 重命名
          </el-dropdown-item>
          <el-dropdown-item @click="handleDelete" divided>
            <el-icon><Delete /></el-icon> 删除
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Folder,
  FolderOpened,
  Document,
  Search,
  Edit,
  Delete
} from '@element-plus/icons-vue'
import { useApiStore, type ApiFolder, type Api } from '../stores/api'

const props = defineProps<{
  projectId: number
}>()

const emit = defineEmits<{
  (e: 'select-api', api: Api): void
  (e: 'create-api', folderId?: number): void
}>()

const route = useRoute()
const router = useRouter()
const apiStore = useApiStore()

const treeRef = ref()
const searchKeyword = ref('')
const folderDialogVisible = ref(false)
const folderFormRef = ref()
const folderForm = ref({
  name: '',
  description: '',
  parentId: undefined as number | undefined
})
const folderRules = {
  name: [{ required: true, message: '请输入文件夹名称', trigger: 'blur' }]
}

const contextMenuRef = ref()
const contextMenuData = ref<any>(null)

// 构建树形数据
const treeData = computed(() => {
  const folders = apiStore.getFolders
  const apis = apiStore.getApis

  // 构建文件夹树
  const buildFolderTree = (parentId?: number): any[] => {
    const result: any[] = []

    // 添加当前层级的文件夹
    const currentFolders = folders.filter(f => f.parentId === parentId)
    currentFolders.forEach(folder => {
      const children = buildFolderTree(folder.id)
      const folderApis = apis.filter(a => a.folderId === folder.id)

      const folderNode: any = {
        id: `folder-${folder.id}`,
        label: folder.name,
        type: 'folder',
        folderId: folder.id,
        childrenCount: children.length + folderApis.length,
        children: [
          ...children,
          ...folderApis.map(api => ({
            id: `api-${api.id}`,
            label: api.name,
            type: 'api',
            apiId: api.id,
            method: api.method,
            data: api
          }))
        ]
      }
      result.push(folderNode)
    })

    return result
  }

  // 获取根级文件夹和未分类的API
  const rootFolders = buildFolderTree(undefined)
  const uncategorizedApis = apis.filter(a => !a.folderId)

  return [
    {
      id: 'root',
      label: '全部接口',
      type: 'root',
      children: [
        ...rootFolders,
        ...uncategorizedApis.map(api => ({
          id: `api-${api.id}`,
          label: api.name,
          type: 'api',
          apiId: api.id,
          method: api.method,
          data: api
        }))
      ]
    }
  ]
})

const defaultProps = {
  children: 'children',
  label: 'label'
}

const getMethodType = (method: string) => {
  switch (method?.toUpperCase()) {
    case 'GET':
      return 'success'
    case 'POST':
      return 'primary'
    case 'PUT':
      return 'warning'
    case 'DELETE':
      return 'danger'
    default:
      return 'info'
  }
}

const handleNodeClick = (data: any) => {
  if (data.type === 'api') {
    emit('select-api', data.data)
  }
}

const handleContextMenu = (event: Event, data: any) => {
  event.preventDefault()
  contextMenuData.value = data
  // 显示右键菜单
}

const handleContextMenuVisibleChange = (visible: boolean) => {
  if (!visible) {
    contextMenuData.value = null
  }
}

const handleRename = () => {
  if (!contextMenuData.value) return
  // 实现重命名逻辑
}

const handleDelete = async () => {
  if (!contextMenuData.value) return

  try {
    await ElMessageBox.confirm('确定要删除吗？', '提示', {
      type: 'warning'
    })

    if (contextMenuData.value.type === 'folder') {
      await apiStore.deleteFolder(contextMenuData.value.folderId)
    } else if (contextMenuData.value.type === 'api') {
      await apiStore.deleteApi(contextMenuData.value.apiId)
    }

    ElMessage.success('删除成功')
  } catch (error) {
    // 取消删除
  }
}

const showCreateFolderDialog = (parentId?: number) => {
  folderForm.value = {
    name: '',
    description: '',
    parentId
  }
  folderDialogVisible.value = true
}

const submitCreateFolder = async () => {
  if (!folderFormRef.value) return

  try {
    await folderFormRef.value.validate()
    await apiStore.createFolder({
      ...folderForm.value,
      projectId: props.projectId,
      sort: 0
    })
    ElMessage.success('创建成功')
    folderDialogVisible.value = false
  } catch (error) {
    console.error('创建文件夹失败:', error)
  }
}

const handleCreateApi = (folderId?: number) => {
  emit('create-api', folderId)
}

const handleSearch = () => {
  if (searchKeyword.value) {
    apiStore.searchApis(props.projectId, searchKeyword.value)
  } else {
    apiStore.fetchApisByProject(props.projectId)
  }
}

// 加载数据
const loadData = async () => {
  await Promise.all([
    apiStore.fetchFoldersByProject(props.projectId),
    apiStore.fetchApisByProject(props.projectId)
  ])
}

watch(() => props.projectId, loadData, { immediate: true })
</script>

<style scoped>
.folder-tree-container {
  width: 280px;
  height: 100%;
  background-color: #fff;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
}

.tree-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #e0e0e0;
}

.tree-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.tree-search {
  padding: 12px 16px;
  border-bottom: 1px solid #e0e0e0;
}

.tree-content {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.tree-node {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 4px 0;
}

.node-icon {
  margin-right: 8px;
  font-size: 16px;
  color: #909399;
}

.node-icon.api-icon {
  color: #409eff;
}

.node-label {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
}

.node-count {
  margin-left: 4px;
  font-size: 12px;
  color: #909399;
}

.method-tag {
  margin-left: 8px;
  font-size: 10px;
  height: 20px;
  line-height: 18px;
}

:deep(.el-tree-node__content) {
  height: 36px;
  padding-right: 16px;
}

:deep(.el-tree-node__content:hover) {
  background-color: #f5f7fa;
}

:deep(.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content) {
  background-color: #ecf5ff;
}
</style>
