<template>
  <div class="docs-container">
    <el-container class="full-height">
      <el-header height="60px" class="header">
        <div class="logo">API Master</div>
        <el-menu :default-active="activeIndex" class="menu" mode="horizontal">
          <el-menu-item index="1">
            <router-link to="/">首页</router-link>
          </el-menu-item>
          <el-menu-item index="2">
            <router-link to="/api-design">API设计</router-link>
          </el-menu-item>
          <el-menu-item index="3">
            <router-link to="/api-test">API测试</router-link>
          </el-menu-item>
          <el-menu-item index="4">
            <router-link to="/mock">Mock服务</router-link>
          </el-menu-item>
          <el-menu-item index="5" :class="{ active: true }">
            <router-link to="/docs">文档生成</router-link>
          </el-menu-item>
        </el-menu>
        <div class="user">
          <el-button type="primary">
            <router-link to="/login">登录</router-link>
          </el-button>
        </div>
      </el-header>
      <el-main class="main">
        <el-card shadow="hover" class="docs-card">
          <template #header>
            <div class="card-header">
              <span>API文档生成</span>
              <el-button type="primary" size="small" class="generate-btn">
                <el-icon><Document /></el-icon> 生成文档
              </el-button>
            </div>
          </template>
          <div class="docs-content">
            <div class="docs-config">
              <el-form :model="docsConfig" label-width="120px">
                <el-form-item label="文档标题">
                  <el-input v-model="docsConfig.title" placeholder="请输入文档标题" />
                </el-form-item>
                <el-form-item label="文档描述">
                  <el-input type="textarea" v-model="docsConfig.description" placeholder="请输入文档描述" />
                </el-form-item>
                <el-form-item label="API分组">
                  <el-select v-model="docsConfig.group" placeholder="请选择API分组" multiple>
                    <el-option label="用户管理" value="user" />
                    <el-option label="产品管理" value="product" />
                    <el-option label="订单管理" value="order" />
                    <el-option label="支付接口" value="payment" />
                  </el-select>
                </el-form-item>
                <el-form-item label="导出格式">
                  <el-select v-model="docsConfig.format" placeholder="请选择导出格式">
                    <el-option label="HTML" value="html" />
                    <el-option label="Markdown" value="markdown" />
                    <el-option label="PDF" value="pdf" />
                    <el-option label="Swagger JSON" value="swagger" />
                  </el-select>
                </el-form-item>
                <el-form-item label="包含内容">
                  <el-checkbox-group v-model="docsConfig.include">
                    <el-checkbox label="请求参数">请求参数</el-checkbox>
                    <el-checkbox label="请求体">请求体</el-checkbox>
                    <el-checkbox label="响应示例">响应示例</el-checkbox>
                    <el-checkbox label="错误码">错误码</el-checkbox>
                    <el-checkbox label="接口说明">接口说明</el-checkbox>
                  </el-checkbox-group>
                </el-form-item>
                <el-form-item label="API版本">
                  <el-input v-model="docsConfig.version" placeholder="请输入API版本" />
                </el-form-item>
                <el-form-item label="基础URL">
                  <el-input v-model="docsConfig.baseUrl" placeholder="请输入API基础URL" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="generateDocs">生成文档</el-button>
                  <el-button @click="previewDocs">预览文档</el-button>
                  <el-button @click="exportDocs">导出文档</el-button>
                </el-form-item>
              </el-form>
            </div>
            
            <div class="docs-preview" v-if="showPreview">
              <h3>文档预览</h3>
              <div class="preview-content">
                <div class="preview-header">
                  <h1>{{ docsConfig.title }}</h1>
                  <p>{{ docsConfig.description }}</p>
                  <div class="preview-meta">
                    <span>版本: {{ docsConfig.version }}</span>
                    <span>生成时间: {{ generateTime }}</span>
                    <span>基础URL: {{ docsConfig.baseUrl }}</span>
                  </div>
                </div>
                
                <div class="preview-apis">
                  <h2>API接口列表</h2>
                  
                  <div class="api-item">
                    <div class="api-header">
                      <el-tag type="primary" class="api-method">GET</el-tag>
                      <span class="api-path">/api/users</span>
                      <span class="api-name">获取用户列表</span>
                    </div>
                    <div class="api-details">
                      <h4>接口说明</h4>
                      <p>获取系统中的用户列表，支持分页查询</p>
                      
                      <h4>请求参数</h4>
                      <el-table :data="userListParams" style="width: 100%">
                        <el-table-column prop="name" label="参数名" width="150" />
                        <el-table-column prop="type" label="类型" width="100" />
                        <el-table-column prop="required" label="必填" width="80" />
                        <el-table-column prop="description" label="描述" />
                      </el-table>
                      
                      <h4>响应示例</h4>
                      <pre class="response-example">{{ userListResponse }}</pre>
                    </div>
                  </div>
                  
                  <div class="api-item">
                    <div class="api-header">
                      <el-tag type="success" class="api-method">POST</el-tag>
                      <span class="api-path">/api/users</span>
                      <span class="api-name">创建用户</span>
                    </div>
                    <div class="api-details">
                      <h4>接口说明</h4>
                      <p>创建新的用户账号</p>
                      
                      <h4>请求体</h4>
                      <pre class="request-example">{{ createUserRequest }}</pre>
                      
                      <h4>响应示例</h4>
                      <pre class="response-example">{{ createUserResponse }}</pre>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { Document } from '@element-plus/icons-vue'

const activeIndex = ref('5')
const showPreview = ref(false)
const generateTime = ref(new Date().toLocaleString())

const docsConfig = reactive({
  title: 'API接口文档',
  description: '系统API接口详细文档',
  group: ['user'],
  format: 'html',
  include: ['请求参数', '响应示例', '接口说明'],
  version: '1.0.0',
  baseUrl: 'http://localhost:8080/api'
})

const userListParams = [
  {
    name: 'page',
    type: 'number',
    required: false,
    description: '页码，默认1'
  },
  {
    name: 'size',
    type: 'number',
    required: false,
    description: '每页条数，默认10'
  },
  {
    name: 'keyword',
    type: 'string',
    required: false,
    description: '搜索关键词'
  }
]

const userListResponse = `{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "page": 1,
    "size": 10,
    "list": [
      {
        "id": 1,
        "username": "admin",
        "email": "admin@example.com",
        "nickname": "管理员",
        "role": "admin"
      },
      {
        "id": 2,
        "username": "user1",
        "email": "user1@example.com",
        "nickname": "普通用户",
        "role": "user"
      }
    ]
  }
}`

const createUserRequest = `{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "password123",
  "nickname": "新用户"
}`

const createUserResponse = `{
  "code": 200,
  "message": "success",
  "data": {
    "id": 3,
    "username": "newuser",
    "email": "newuser@example.com",
    "nickname": "新用户",
    "role": "user"
  }
}`

const generateDocs = () => {
  // 模拟生成文档
  generateTime.value = new Date().toLocaleString()
  showPreview.value = true
}

const previewDocs = () => {
  showPreview.value = true
}

const exportDocs = () => {
  // 模拟导出文档
  alert(`文档已导出为${docsConfig.format}格式`)
}
</script>

<style scoped>
.docs-container {
  width: 100%;
  height: 100vh;
}

.full-height {
  height: 100%;
}

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

.main {
  padding: 20px;
  background-color: #f5f7fa;
}

.docs-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.docs-content {
  margin-top: 20px;
}

.docs-config {
  margin-bottom: 40px;
}

.docs-preview {
  margin-top: 40px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.docs-preview h3 {
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: bold;
}

.preview-content {
  line-height: 1.6;
}

.preview-header {
  text-align: center;
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e0e0e0;
}

.preview-header h1 {
  margin-bottom: 10px;
  font-size: 24px;
}

.preview-header p {
  margin-bottom: 20px;
  color: #606266;
}

.preview-meta {
  display: flex;
  justify-content: center;
  gap: 30px;
  font-size: 14px;
  color: #909399;
}

.preview-apis {
  margin-top: 40px;
}

.preview-apis h2 {
  margin-bottom: 30px;
  font-size: 20px;
}

.api-item {
  margin-bottom: 40px;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.api-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.api-method {
  margin-right: 15px;
}

.api-path {
  font-family: monospace;
  font-weight: bold;
  margin-right: 15px;
  color: #409eff;
}

.api-name {
  font-weight: bold;
}

.api-details {
  margin-left: 20px;
}

.api-details h4 {
  margin: 20px 0 10px 0;
  font-size: 14px;
  font-weight: bold;
}

.pre-request,
.request-example,
.response-example {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  font-family: monospace;
  white-space: pre-wrap;
  font-size: 14px;
  line-height: 1.4;
}
</style>
