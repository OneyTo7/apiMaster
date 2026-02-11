<template>
  <div class="api-design-container">
    <el-container class="full-height">
      <el-header height="60px" class="header">
        <div class="logo">API Master</div>
        <el-menu :default-active="activeIndex" class="menu" mode="horizontal">
          <el-menu-item index="1">
            <router-link to="/">首页</router-link>
          </el-menu-item>
          <el-menu-item index="2">
            <router-link to="/teams">团队管理</router-link>
          </el-menu-item>
          <el-sub-menu index="3" :class="{ active: true }">
            <template #title>API管理</template>
            <el-menu-item index="3-1" :class="{ active: true }">
              <router-link to="/api-design">API设计</router-link>
            </el-menu-item>
            <el-menu-item index="3-2">
              <router-link to="/api-test">API测试</router-link>
            </el-menu-item>
            <el-menu-item index="3-3">
              <router-link to="/mock">Mock服务</router-link>
            </el-menu-item>
            <el-menu-item index="3-4">
              <router-link to="/docs">文档生成</router-link>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
        <div class="user">
          <template v-if="userStore.getIsLoggedIn">
            <el-dropdown>
              <span class="user-info">
                {{ userStore.getUser?.nickname }} <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>个人中心</el-dropdown-item>
                  <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary">
              <router-link to="/login">登录</router-link>
            </el-button>
          </template>
        </div>
      </el-header>
      <el-main class="main">
        <!-- 面包屑 -->
        <div class="breadcrumb">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>
              <router-link to="/teams">团队管理</router-link>
            </el-breadcrumb-item>
            <el-breadcrumb-item>
              <router-link :to="`/teams/${teamId}/projects`">{{ teamName }}</router-link>
            </el-breadcrumb-item>
            <el-breadcrumb-item>
              <router-link :to="`/teams/${teamId}/projects`">{{ projectName }}</router-link>
            </el-breadcrumb-item>
            <el-breadcrumb-item>API设计</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!-- 主要内容 -->
      <el-card shadow="hover" class="api-design-card">
        <template #header>
          <div class="card-header">
            <span>API接口设计</span>
            <el-button type="primary" size="small" class="add-api-btn">
              <el-icon><Plus /></el-icon> 新建API
            </el-button>
          </div>
        </template>
        
        <!-- API基本信息 -->
        <div class="api-basic-info">
          <el-form :model="apiForm" label-width="80px" class="api-basic-form">
            <el-form-item label="API名称">
              <el-input v-model="apiForm.name" placeholder="请输入API名称" />
            </el-form-item>
            <el-form-item label="接口描述">
              <el-input type="textarea" v-model="apiForm.description" placeholder="请输入接口描述" />
            </el-form-item>
          </el-form>
        </div>
        
        <!-- 请求配置 -->
        <div class="request-config">
          <div class="request-header">
            <el-select v-model="apiForm.method" placeholder="请选择请求方法" class="method-select">
              <el-option label="GET" value="GET" />
              <el-option label="POST" value="POST" />
              <el-option label="PUT" value="PUT" />
              <el-option label="DELETE" value="DELETE" />
            </el-select>
            <el-input v-model="apiForm.path" placeholder="请输入请求路径，如 /api/users" class="path-input" />
            <div class="request-actions">
              <el-button type="primary" @click="saveApi">
                <el-icon><Download /></el-icon> 保存
              </el-button>
              <el-button @click="previewApi">
                <el-icon><Document /></el-icon> 预览
              </el-button>
              <el-button @click="testApi">
                <el-icon><Search /></el-icon> 测试
              </el-button>
            </div>
          </div>
          
          <!-- 标签页 -->
          <el-tabs v-model="activeTab" class="api-tabs">
            <!-- 请求标签 -->
            <el-tab-pane label="请求" name="request">
              <!-- 请求子标签 -->
              <el-tabs v-model="activeRequestTab" class="request-sub-tabs">
                <el-tab-pane label="Params" name="params">
                  <div class="params-section">
                    <div class="params-header">
                      <span>Query参数</span>
                      <el-button type="primary" size="small" @click="addParam">
                        <el-icon><Plus /></el-icon> 添加参数
                      </el-button>
                    </div>
                    
                    <div class="params-list">
                      <div v-for="(param, index) in apiForm.params" :key="index" class="param-item">
                        <div class="param-row">
                          <el-checkbox v-model="param.enabled" class="param-enabled" />
                          <el-input v-model="param.name" placeholder="参数名" class="param-name" />
                          <el-input v-model="param.value" placeholder="参数值" class="param-value" />
                          <el-select v-model="param.type" placeholder="类型" class="param-type">
                            <el-option label="string" value="string" />
                            <el-option label="number" value="number" />
                            <el-option label="boolean" value="boolean" />
                          </el-select>
                          <el-input v-model="param.description" placeholder="说明" class="param-description" />
                          <div class="param-actions">
                            <el-button type="danger" size="small" @click="deleteParam(index)" icon="Delete" />
                          </div>
                        </div>
                      </div>
                      
                      <div v-if="apiForm.params.length === 0" class="empty-params">
                        <p>暂无参数，请点击上方按钮添加</p>
                      </div>
                    </div>
                  </div>
                </el-tab-pane>
                
                <el-tab-pane label="Body" name="body">
                  <div class="body-section">
                    <el-select v-model="apiForm.bodyType" placeholder="请选择请求体类型" class="body-type-select">
                      <el-option label="JSON" value="json" />
                      <el-option label="Form Data" value="form-data" />
                      <el-option label="Text" value="text" />
                    </el-select>
                    <el-input type="textarea" v-model="apiForm.body" placeholder="请输入请求体内容" :rows="8" class="body-input" />
                  </div>
                </el-tab-pane>
                
                <el-tab-pane label="Headers" name="headers">
                  <div class="headers-section">
                    <div class="headers-list">
                      <div v-for="(header, index) in apiForm.headers" :key="index" class="header-item">
                        <el-input v-model="header.name" placeholder="Header名称" class="header-name" />
                        <el-input v-model="header.value" placeholder="Header值" class="header-value" />
                        <el-button type="danger" size="small" @click="deleteHeader(index)" icon="Delete" />
                      </div>
                      <div v-if="apiForm.headers.length === 0" class="empty-headers">
                        <p>暂无Headers，请点击下方按钮添加</p>
                      </div>
                    </div>
                    <el-button type="primary" size="small" @click="addHeader">
                      <el-icon><Plus /></el-icon> 添加Header
                    </el-button>
                  </div>
                </el-tab-pane>
              </el-tabs>
            </el-tab-pane>
            
            <!-- 响应定义标签 -->
            <el-tab-pane label="响应定义" name="response">
              <div class="response-section">
                <el-form :model="apiForm" label-width="120px">
                  <el-form-item label="响应示例">
                    <el-input type="textarea" v-model="apiForm.response" placeholder="请输入响应示例" :rows="8" />
                  </el-form-item>
                </el-form>
              </div>
            </el-tab-pane>
            
            <!-- 接口说明标签 -->
            <el-tab-pane label="接口说明" name="description">
              <div class="description-section">
                <el-form :model="apiForm" label-width="120px">
                  <el-form-item label="接口描述">
                    <el-input type="textarea" v-model="apiForm.description" placeholder="请输入接口详细说明" :rows="6" />
                  </el-form-item>
                  <el-form-item label="使用场景">
                    <el-input type="textarea" v-model="apiForm.usage" placeholder="请输入接口使用场景" :rows="4" />
                  </el-form-item>
                </el-form>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-card>
      </el-main>
    </el-container>
  </div>
  
  <!-- 预览对话框 -->
  <el-dialog
    v-model="showPreview"
    title="API预览"
    width="800px"
  >
    <el-scrollbar>
      <pre class="preview-content">{{ previewContent }}</pre>
    </el-scrollbar>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showPreview = false">关闭</el-button>
      </span>
    </template>
  </el-dialog>
  
  <!-- 测试对话框 -->
  <el-dialog
    v-model="showTest"
    title="API测试"
    width="800px"
  >
    <el-scrollbar v-if="testResult">
      <div v-if="testResult.success" class="test-result success">
        <div class="test-status">
          <span class="status-code">{{ testResult.status }}</span>
          <span class="status-text">{{ testResult.statusText }}</span>
        </div>
        <div class="test-section">
          <h4>响应头</h4>
          <pre>{{ JSON.stringify(testResult.headers, null, 2) }}</pre>
        </div>
        <div class="test-section">
          <h4>响应体</h4>
          <pre>{{ JSON.stringify(testResult.body, null, 2) }}</pre>
        </div>
      </div>
      <div v-else class="test-result error">
        <h4>测试失败</h4>
        <p>{{ testResult.error }}</p>
      </div>
    </el-scrollbar>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showTest = false">关闭</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Plus, Edit, Delete, Check, Close, Document, Search, Download, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 导航栏激活项
const activeIndex = ref('3-1')

// 从 URL 参数中获取 teamId 和 projectId
const teamId = computed(() => route.query.teamId || '')
const projectId = computed(() => route.query.projectId || '')

// 团队名称和项目名称，实际应用中应该从 store 中获取
const teamName = computed(() => '团队名称')
const projectName = computed(() => '项目名称')

// 标签页状态
const activeTab = ref('request')
const activeRequestTab = ref('params')

// 预览和测试状态
const showPreview = ref(false)
const showTest = ref(false)
const testResult = ref(null)
const previewContent = ref('')

// 检查登录状态
onMounted(() => {
  userStore.checkLogin()
})

// 处理退出登录
const handleLogout = () => {
  userStore.logout()
  ElMessage.success('退出登录成功')
  router.push('/login')
}

const apiForm = reactive({
  name: '',
  method: 'GET',
  path: '',
  description: '',
  usage: '',
  params: [], // 移除默认的 page 和 size 参数
  headers: [],
  bodyType: 'json',
  body: `{
  "name": "",
  "description": ""
}`,
  response: `{
  "code": 200,
  "message": "success",
  "data": {}
}`
})

// 添加参数
const addParam = () => {
  apiForm.params.push({
    enabled: true,
    name: '',
    value: '',
    type: 'string',
    description: ''
  })
}

// 删除参数
const deleteParam = (index) => {
  apiForm.params.splice(index, 1)
}

// 添加Header
const addHeader = () => {
  apiForm.headers.push({
    name: '',
    value: ''
  })
}

// 删除Header
const deleteHeader = (index) => {
  apiForm.headers.splice(index, 1)
}

// 保存API
const saveApi = () => {
  // 验证表单
  if (!apiForm.name) {
    ElMessage.error('请输入API名称')
    return
  }
  if (!apiForm.path) {
    ElMessage.error('请输入请求路径')
    return
  }
  
  // 构建API数据
  const apiData = {
    ...apiForm,
    params: apiForm.params.filter(p => p.enabled)
  }
  
  // 模拟保存API到后端
  console.log('保存API:', apiData)
  
  // 显示保存成功消息
  ElMessage.success('API保存成功')
}

// 预览API
const previewApi = () => {
  // 构建预览内容
  const preview = `API名称: ${apiForm.name}\n` +
                `请求方法: ${apiForm.method}\n` +
                `请求路径: ${apiForm.path}\n` +
                `接口描述: ${apiForm.description}\n\n` +
                `Query参数:\n` +
                apiForm.params.filter(p => p.enabled).map(p => `  - ${p.name}: ${p.value} (${p.type})${p.required ? ' *' : ''} - ${p.description}`).join('\n') + '\n\n' +
                `Headers:\n` +
                apiForm.headers.map(h => `  - ${h.name}: ${h.value}`).join('\n') + '\n\n' +
                `请求体类型: ${apiForm.bodyType}\n` +
                `请求体:\n${apiForm.body}\n\n` +
                `响应示例:\n${apiForm.response}`
  
  previewContent.value = preview
  showPreview.value = true
}

// 测试API
const testApi = async () => {
  // 构建测试请求
  const url = `http://localhost:8080${apiForm.path}`
  const method = apiForm.method
  
  // 构建查询参数
  const params = apiForm.params.filter(p => p.enabled)
  const queryString = params.length > 0 ? '?' + params.map(p => `${p.name}=${encodeURIComponent(p.value)}`).join('&') : ''
  const fullUrl = url + queryString
  
  // 构建请求头
  const headers = {}
  apiForm.headers.forEach(h => {
    headers[h.name] = h.value
  })
  
  try {
    // 模拟API测试
    console.log('测试API:', {
      url: fullUrl,
      method,
      headers,
      body: apiForm.bodyType === 'json' ? JSON.parse(apiForm.body) : apiForm.body
    })
    
    // 模拟测试结果
    testResult.value = {
      success: true,
      status: 200,
      statusText: 'OK',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.parse(apiForm.response)
    }
    
    showTest.value = true
  } catch (error) {
    // 模拟测试失败
    testResult.value = {
      success: false,
      error: error.message
    }
    showTest.value = true
    ElMessage.error('测试失败: ' + error.message)
  }
}
</script>

<style scoped>
.api-design-container {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.full-height {
  height: 100%;
}

/* 顶部导航栏 */
.header {
  display: flex;
  align-items: center;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  margin-right: 40px;
}

.menu {
  flex: 1;
  border-bottom: none;
}

.menu .active {
  color: #409eff !important;
  border-bottom: 2px solid #409eff !important;
}

.user {
  margin-left: auto;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: all 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

/* 面包屑 */
.breadcrumb {
  font-size: 14px;
  margin-bottom: 20px;
}

.api-design-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

/* API基本信息 */
.api-basic-info {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e0e0e0;
}

.api-basic-form {
  max-width: 800px;
}

/* 请求配置 */
.request-config {
  margin-top: 20px;
}

.request-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.method-select {
  width: 100px;
  margin-right: 10px;
}

.path-input {
  flex: 1;
  margin-right: 20px;
}

.request-actions {
  display: flex;
  gap: 10px;
}

/* 标签页 */
.api-tabs {
  margin-top: 20px;
}

.request-sub-tabs {
  margin-top: 16px;
}

/* 参数部分 */
.params-section {
  margin-top: 16px;
}

.params-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e0e0e0;
}

.params-list {
  margin-top: 16px;
}

.param-item {
  margin-bottom: 12px;
  padding: 12px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.param-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.param-enabled {
  margin-right: 8px;
}

.param-name {
  width: 150px;
}

.param-value {
  width: 150px;
}

.param-type {
  width: 120px;
}

.param-description {
  flex: 1;
}

.empty-params {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}

/* 请求体部分 */
.body-section {
  margin-top: 16px;
}

.body-type-select {
  width: 200px;
  margin-bottom: 16px;
}

.body-input {
  width: 100%;
  font-family: monospace;
  font-size: 14px;
}

/* Headers部分 */
.headers-section {
  margin-top: 16px;
}

.headers-list {
  margin-bottom: 16px;
}

.header-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  padding: 12px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.header-name {
  width: 200px;
}

.header-value {
  flex: 1;
}

.empty-headers {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}

/* 响应定义部分 */
.response-section {
  margin-top: 16px;
}

/* 接口说明部分 */
.description-section {
  margin-top: 16px;
}

/* 按钮样式 */
.add-api-btn {
  margin-left: 10px;
}

/* 预览内容样式 */
.preview-content {
  font-family: monospace;
  font-size: 14px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-all;
  margin: 0;
}

/* 测试结果样式 */
.test-result {
  font-family: monospace;
  font-size: 14px;
}

.test-result.success {
  color: #67c23a;
}

.test-result.error {
  color: #f56c6c;
}

.test-status {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e0e0e0;
}

.status-code {
  font-size: 24px;
  font-weight: bold;
  margin-right: 10px;
}

.status-text {
  font-size: 16px;
}

.test-section {
  margin-bottom: 20px;
}

.test-section h4 {
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: bold;
  color: #303133;
}

.test-section pre {
  margin: 0;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  overflow-x: auto;
}
</style>
