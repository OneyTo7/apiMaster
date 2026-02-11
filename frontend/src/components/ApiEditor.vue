<template>
  <div class="api-editor-container">
    <!-- Tab 栏 -->
    <div class="tabs-header">
      <div class="tabs-list">
        <div
          v-for="tab in tabs"
          :key="tab.id"
          class="tab-item"
          :class="{ active: activeTabId === tab.id, modified: tab.modified }"
          @click="switchTab(tab.id)"
        >
          <el-tag
            :type="getMethodType(tab.method)"
            size="small"
            class="tab-method"
          >
            {{ tab.method }}
          </el-tag>
          <span class="tab-name">{{ tab.name }}</span>
          <el-icon class="tab-close" @click.stop="closeTab(tab.id)">
            <Close />
          </el-icon>
        </div>
        <div v-if="tabs.length === 0" class="empty-tabs">
          暂无打开的接口
        </div>
      </div>
      <div class="tabs-actions">
        <el-button type="primary" size="small" @click="handleSave">
          <el-icon><Check /></el-icon> 保存
        </el-button>
      </div>
    </div>

    <!-- 编辑内容 -->
    <div v-if="currentTab" class="editor-content">
      <!-- 基本信息 -->
      <div class="api-basic-section">
        <div class="api-path-row">
          <el-select
            v-model="currentTab.data.method"
            class="method-select"
            size="default"
          >
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
            <el-option label="PATCH" value="PATCH" />
            <el-option label="HEAD" value="HEAD" />
            <el-option label="OPTIONS" value="OPTIONS" />
          </el-select>
          <el-input
            v-model="currentTab.data.path"
            placeholder="请输入请求路径，如 /api/users"
            class="path-input"
          />
          <el-button type="primary" @click="handleSend">
            <el-icon><Promotion /></el-icon> 发送
          </el-button>
        </div>
        <el-input
          v-model="currentTab.data.name"
          placeholder="接口名称"
          class="name-input"
        />
      </div>

      <!-- 标签页 -->
      <el-tabs v-model="activePanel" class="editor-panels">
        <!-- 文档 -->
        <el-tab-pane label="文档" name="doc">
          <div class="panel-content">
            <el-form label-width="80px">
              <el-form-item label="接口描述">
                <el-input
                  v-model="currentTab.data.description"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入接口描述"
                />
              </el-form-item>
              <el-form-item label="使用场景">
                <el-input
                  v-model="currentTab.data.usage"
                  type="textarea"
                  :rows="2"
                  placeholder="请输入使用场景"
                />
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 运行 -->
        <el-tab-pane label="运行" name="run">
          <div class="panel-content">
            <el-tabs v-model="activeRequestPanel" class="request-tabs">
              <el-tab-pane label="Params" name="params">
                <div class="params-table">
                  <div class="table-header">
                    <span>参数名</span>
                    <span>参数值</span>
                    <span>类型</span>
                    <span>说明</span>
                    <span>操作</span>
                  </div>
                  <div
                    v-for="(param, index) in currentTab.data.params"
                    :key="index"
                    class="table-row"
                  >
                    <el-checkbox v-model="param.enabled" />
                    <el-input v-model="param.name" placeholder="参数名" size="small" />
                    <el-input v-model="param.value" placeholder="参数值" size="small" />
                    <el-select v-model="param.type" size="small" style="width: 80px">
                      <el-option label="string" value="string" />
                      <el-option label="number" value="number" />
                      <el-option label="boolean" value="boolean" />
                    </el-select>
                    <el-input v-model="param.description" placeholder="说明" size="small" />
                    <el-button type="danger" size="small" @click="removeParam(index)">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                  <el-button type="primary" size="small" @click="addParam">
                    <el-icon><Plus /></el-icon> 添加参数
                  </el-button>
                </div>
              </el-tab-pane>

              <el-tab-pane label="Body" name="body">
                <div class="body-section">
                  <el-radio-group v-model="currentTab.data.bodyType" size="small">
                    <el-radio-button label="none">none</el-radio-button>
                    <el-radio-button label="json">json</el-radio-button>
                    <el-radio-button label="form-data">form-data</el-radio-button>
                    <el-radio-button label="x-www-form-urlencoded">x-www-form-urlencoded</el-radio-button>
                    <el-radio-button label="raw">raw</el-radio-button>
                  </el-radio-group>
                  <div v-if="currentTab.data.bodyType !== 'none'" class="body-editor">
                    <el-input
                      v-model="currentTab.data.body"
                      type="textarea"
                      :rows="10"
                      placeholder="请求体内容"
                    />
                  </div>
                </div>
              </el-tab-pane>

              <el-tab-pane label="Headers" name="headers">
                <div class="headers-table">
                  <div class="table-header">
                    <span>Header名</span>
                    <span>Header值</span>
                    <span>说明</span>
                    <span>操作</span>
                  </div>
                  <div
                    v-for="(header, index) in currentTab.data.headers"
                    :key="index"
                    class="table-row"
                  >
                    <el-checkbox v-model="header.enabled" />
                    <el-input v-model="header.name" placeholder="Header名" size="small" />
                    <el-input v-model="header.value" placeholder="Header值" size="small" />
                    <el-input v-model="header.description" placeholder="说明" size="small" />
                    <el-button type="danger" size="small" @click="removeHeader(index)">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                  <el-button type="primary" size="small" @click="addHeader">
                    <el-icon><Plus /></el-icon> 添加Header
                  </el-button>
                </div>
              </el-tab-pane>

              <el-tab-pane label="前置操作" name="pre-processors">
                <div class="pre-processors-section">
                  <el-empty description="暂无前置操作" />
                </div>
              </el-tab-pane>

              <el-tab-pane label="后置操作" name="post-processors">
                <div class="post-processors-section">
                  <el-empty description="暂无后置操作" />
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-tab-pane>

        <!-- 高级 Mock -->
        <el-tab-pane label="高级Mock" name="mock">
          <div class="panel-content">
            <el-form label-width="100px">
              <el-form-item label="Mock开关">
                <el-switch v-model="currentTab.data.mockEnabled" />
              </el-form-item>
              <el-form-item label="Mock响应">
                <el-input
                  v-model="currentTab.data.mockResponse"
                  type="textarea"
                  :rows="8"
                  placeholder="请输入Mock响应内容"
                />
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 调试 -->
        <el-tab-pane label="调试" name="debug">
          <div class="panel-content">
            <div v-if="debugResult" class="debug-result">
              <div class="result-header">
                <el-tag :type="debugResult.success ? 'success' : 'danger'">
                  {{ debugResult.status }} {{ debugResult.statusText }}
                </el-tag>
                <span class="response-time">{{ debugResult.time }}ms</span>
              </div>
              <el-tabs v-model="activeResponsePanel">
                <el-tab-pane label="响应体" name="body">
                  <pre class="code-block">{{ formatJson(debugResult.body) }}</pre>
                </el-tab-pane>
                <el-tab-pane label="响应头" name="headers">
                  <pre class="code-block">{{ formatJson(debugResult.headers) }}</pre>
                </el-tab-pane>
              </el-tabs>
            </div>
            <el-empty v-else description="点击发送按钮开始调试" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <div v-else class="empty-editor">
      <el-empty description="请从左侧选择一个接口，或创建新接口" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Close,
  Check,
  Plus,
  Delete,
  Promotion
} from '@element-plus/icons-vue'
import { useApiStore, type Api, type ApiDetail } from '../stores/api'

interface Tab {
  id: string
  apiId?: number
  name: string
  method: string
  modified: boolean
  data: ApiDetail
}

const props = defineProps<{
  projectId: number
}>()

const emit = defineEmits<{
  (e: 'save', api: ApiDetail): void
}>()

const apiStore = useApiStore()

const tabs = ref<Tab[]>([])
const activeTabId = ref<string>('')
const activePanel = ref('doc')
const activeRequestPanel = ref('params')
const activeResponsePanel = ref('body')
const debugResult = ref<any>(null)

const currentTab = computed(() => {
  return tabs.value.find(tab => tab.id === activeTabId.value)
})

const getMethodType = (method: string) => {
  switch (method?.toUpperCase()) {
    case 'GET': return 'success'
    case 'POST': return 'primary'
    case 'PUT': return 'warning'
    case 'DELETE': return 'danger'
    default: return 'info'
  }
}

const formatJson = (data: any) => {
  try {
    return JSON.stringify(data, null, 2)
  } catch {
    return String(data)
  }
}

// 打开API
const openApi = (api: Api) => {
  const existingTab = tabs.value.find(tab => tab.apiId === api.id)
  if (existingTab) {
    activeTabId.value = existingTab.id
    return
  }

  const newTab: Tab = {
    id: `tab-${Date.now()}`,
    apiId: api.id,
    name: api.name,
    method: api.method,
    modified: false,
    data: {
      ...api,
      params: [],
      headers: [],
      body: '',
      bodyType: 'json',
      response: '',
      usage: ''
    }
  }

  tabs.value.push(newTab)
  activeTabId.value = newTab.id
}

// 创建新API
const createNewApi = (folderId?: number) => {
  const newTab: Tab = {
    id: `tab-${Date.now()}`,
    name: '新建接口',
    method: 'GET',
    modified: true,
    data: {
      id: 0,
      projectId: props.projectId,
      folderId,
      name: '新建接口',
      path: '',
      method: 'GET',
      description: '',
      status: 1,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      params: [],
      headers: [],
      body: '',
      bodyType: 'json',
      response: '',
      usage: ''
    }
  }

  tabs.value.push(newTab)
  activeTabId.value = newTab.id
}

const switchTab = (tabId: string) => {
  activeTabId.value = tabId
}

const closeTab = (tabId: string) => {
  const index = tabs.value.findIndex(tab => tab.id === tabId)
  if (index === -1) return

  const tab = tabs.value[index]
  if (tab.modified) {
    // 提示保存
  }

  tabs.value.splice(index, 1)

  if (activeTabId.value === tabId) {
    if (tabs.value.length > 0) {
      activeTabId.value = tabs.value[Math.min(index, tabs.value.length - 1)].id
    } else {
      activeTabId.value = ''
    }
  }
}

const addParam = () => {
  if (!currentTab.value) return
  currentTab.value.data.params?.push({
    name: '',
    value: '',
    type: 'string',
    description: '',
    enabled: true
  })
  currentTab.value.modified = true
}

const removeParam = (index: number) => {
  if (!currentTab.value) return
  currentTab.value.data.params?.splice(index, 1)
  currentTab.value.modified = true
}

const addHeader = () => {
  if (!currentTab.value) return
  currentTab.value.data.headers?.push({
    name: '',
    value: '',
    description: '',
    enabled: true
  })
  currentTab.value.modified = true
}

const removeHeader = (index: number) => {
  if (!currentTab.value) return
  currentTab.value.data.headers?.splice(index, 1)
  currentTab.value.modified = true
}

const handleSave = async () => {
  if (!currentTab.value) return

  try {
    const apiData = currentTab.value.data
    if (apiData.id) {
      await apiStore.updateApi(apiData.id, apiData)
    } else {
      const newApi = await apiStore.createApi(apiData)
      currentTab.value.apiId = newApi.id
      currentTab.value.data.id = newApi.id
    }
    currentTab.value.modified = false
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const handleSend = async () => {
  if (!currentTab.value) return

  // 模拟发送请求
  debugResult.value = {
    success: true,
    status: 200,
    statusText: 'OK',
    time: 156,
    body: {
      code: 200,
      message: 'success',
      data: {}
    },
    headers: {
      'Content-Type': 'application/json'
    }
  }
  activePanel.value = 'debug'
}

// 监听数据变化
watch(() => currentTab.value?.data, () => {
  if (currentTab.value) {
    currentTab.value.modified = true
  }
}, { deep: true })

// 暴露方法
defineExpose({
  openApi,
  createNewApi
})
</script>

<style scoped>
.api-editor-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
}

.tabs-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #e0e0e0;
}

.tabs-list {
  display: flex;
  gap: 4px;
  overflow-x: auto;
  flex: 1;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background-color: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
  font-size: 13px;
}

.tab-item:hover {
  background-color: #f5f7fa;
}

.tab-item.active {
  background-color: #ecf5ff;
  border-color: #409eff;
}

.tab-item.modified::after {
  content: '●';
  color: #f56c6c;
  font-size: 8px;
}

.tab-method {
  font-size: 10px;
  height: 18px;
  line-height: 16px;
}

.tab-name {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.tab-close {
  font-size: 12px;
  color: #909399;
  cursor: pointer;
}

.tab-close:hover {
  color: #f56c6c;
}

.empty-tabs {
  color: #909399;
  font-size: 13px;
  padding: 6px 12px;
}

.editor-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.api-basic-section {
  padding: 16px;
  border-bottom: 1px solid #e0e0e0;
}

.api-path-row {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.method-select {
  width: 120px;
}

.path-input {
  flex: 1;
}

.name-input {
  width: 100%;
}

.editor-panels {
  flex: 1;
  overflow: hidden;
}

.editor-panels :deep(.el-tabs__header) {
  margin: 0;
  padding: 0 16px;
  background-color: #f5f7fa;
}

.editor-panels :deep(.el-tabs__content) {
  flex: 1;
  overflow: auto;
  padding: 16px;
}

.panel-content {
  height: 100%;
}

.params-table,
.headers-table {
  margin-bottom: 16px;
}

.table-header {
  display: grid;
  grid-template-columns: 40px 1fr 1fr 80px 1fr 50px;
  gap: 8px;
  padding: 8px;
  background-color: #f5f7fa;
  font-weight: 600;
  font-size: 13px;
}

.table-row {
  display: grid;
  grid-template-columns: 40px 1fr 1fr 80px 1fr 50px;
  gap: 8px;
  padding: 8px;
  align-items: center;
  border-bottom: 1px solid #e0e0e0;
}

.body-section {
  padding: 16px;
}

.body-editor {
  margin-top: 16px;
}

.debug-result {
  height: 100%;
}

.result-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.response-time {
  color: #909399;
  font-size: 13px;
}

.code-block {
  background-color: #f5f7fa;
  padding: 16px;
  border-radius: 4px;
  overflow: auto;
  font-family: monospace;
  font-size: 13px;
  line-height: 1.6;
}

.empty-editor {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
