<template>
  <div class="api-test-container">
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
          <el-menu-item index="3" :class="{ active: true }">
            <router-link to="/api-test">API测试</router-link>
          </el-menu-item>
          <el-menu-item index="4">
            <router-link to="/mock">Mock服务</router-link>
          </el-menu-item>
          <el-menu-item index="5">
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
        <el-card shadow="hover" class="api-test-card">
          <template #header>
            <div class="card-header">
              <span>API测试</span>
              <el-button type="primary" size="small" class="save-test-btn">
                <el-icon><Check /></el-icon> 保存测试用例
              </el-button>
            </div>
          </template>
          <div class="api-test-content">
            <div class="request-section">
              <el-select v-model="testForm.method" placeholder="请求方法" style="width: 120px; margin-right: 10px;">
                <el-option label="GET" value="GET" />
                <el-option label="POST" value="POST" />
                <el-option label="PUT" value="PUT" />
                <el-option label="DELETE" value="DELETE" />
              </el-select>
              <el-input v-model="testForm.url" placeholder="请求URL" style="flex: 1; margin-right: 10px;" />
              <el-button type="primary" @click="sendRequest">
                <el-icon><Send /></el-icon> 发送
              </el-button>
            </div>
            
            <el-tabs v-model="activeTab" class="tabs">
              <el-tab-pane label="参数" name="params">
                <el-table :data="testForm.params" style="width: 100%">
                  <el-table-column prop="name" label="参数名" width="180">
                    <template #default="scope">
                      <el-input v-model="scope.row.name" placeholder="参数名" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="value" label="值">
                    <template #default="scope">
                      <el-input v-model="scope.row.value" placeholder="值" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="description" label="描述" width="200">
                    <template #default="scope">
                      <el-input v-model="scope.row.description" placeholder="描述" />
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="100">
                    <template #default="scope">
                      <el-button type="danger" size="small" @click="removeParam(scope.$index)">
                        <el-icon><Delete /></el-icon>
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <el-button type="primary" size="small" class="add-param-btn">
                  <el-icon><Plus /></el-icon> 添加参数
                </el-button>
              </el-tab-pane>
              
              <el-tab-pane label="请求头" name="headers">
                <el-table :data="testForm.headers" style="width: 100%">
                  <el-table-column prop="name" label="头部名称" width="180">
                    <template #default="scope">
                      <el-input v-model="scope.row.name" placeholder="头部名称" />
                    </template>
                  </el-table-column>
                  <el-table-column prop="value" label="值">
                    <template #default="scope">
                      <el-input v-model="scope.row.value" placeholder="值" />
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="100">
                    <template #default="scope">
                      <el-button type="danger" size="small" @click="removeHeader(scope.$index)">
                        <el-icon><Delete /></el-icon>
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <el-button type="primary" size="small" class="add-header-btn">
                  <el-icon><Plus /></el-icon> 添加请求头
                </el-button>
              </el-tab-pane>
              
              <el-tab-pane label="请求体" name="body">
                <el-select v-model="testForm.bodyType" placeholder="请求体类型" style="width: 150px; margin-bottom: 10px;">
                  <el-option label="JSON" value="json" />
                  <el-option label="Form Data" value="form-data" />
                  <el-option label="Text" value="text" />
                </el-select>
                <el-input type="textarea" v-model="testForm.body" placeholder="请输入请求体内容" rows="6" />
              </el-tab-pane>
            </el-tabs>
            
            <div class="response-section">
              <h3>响应结果</h3>
              <el-input type="textarea" v-model="testForm.response" placeholder="响应结果将显示在这里" rows="8" readonly />
              <div class="response-info">
                <span>状态码: {{ testForm.statusCode }}</span>
                <span>响应时间: {{ testForm.responseTime }}ms</span>
                <span>响应大小: {{ testForm.responseSize }}B</span>
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
import { Check, Send, Delete, Plus } from '@element-plus/icons-vue'

const activeIndex = ref('3')
const activeTab = ref('params')

const testForm = reactive({
  method: 'GET',
  url: '',
  params: [
    {
      name: '',
      value: '',
      description: ''
    }
  ],
  headers: [
    {
      name: 'Content-Type',
      value: 'application/json'
    }
  ],
  bodyType: 'json',
  body: '',
  response: '',
  statusCode: '',
  responseTime: '',
  responseSize: ''
})

const sendRequest = () => {
  // 模拟发送请求
  setTimeout(() => {
    testForm.response = '{"code": 200, "message": "success", "data": {"id": 1, "name": "Test Data", "created_at": "2026-02-11T00:00:00Z"}}'
    testForm.statusCode = '200'
    testForm.responseTime = '150'
    testForm.responseSize = '120'
  }, 500)
}

const removeParam = (index: number) => {
  testForm.params.splice(index, 1)
}

const removeHeader = (index: number) => {
  testForm.headers.splice(index, 1)
}
</script>

<style scoped>
.api-test-container {
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

.api-test-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.api-test-content {
  margin-top: 20px;
}

.request-section {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.tabs {
  margin-bottom: 20px;
}

.add-param-btn,
.add-header-btn {
  margin-top: 10px;
}

.response-section {
  margin-top: 30px;
}

.response-section h3 {
  margin-bottom: 10px;
  font-size: 16px;
  font-weight: bold;
}

.response-info {
  display: flex;
  gap: 20px;
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
}
</style>
