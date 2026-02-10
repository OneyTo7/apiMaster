<template>
  <div class="mock-container">
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
          <el-menu-item index="4" :class="{ active: true }">
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
        <el-card shadow="hover" class="mock-card">
          <template #header>
            <div class="card-header">
              <span>Mock服务</span>
              <el-button type="primary" size="small" class="add-mock-btn">
                <el-icon><Plus /></el-icon> 新建Mock接口
              </el-button>
            </div>
          </template>
          <div class="mock-content">
            <div class="mock-list">
              <el-table :data="mockList" style="width: 100%">
                <el-table-column prop="path" label="接口路径" width="200" />
                <el-table-column prop="method" label="请求方法" width="120" />
                <el-table-column prop="name" label="接口名称" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="scope">
                    <el-tag :type="scope.row.status === 'enabled' ? 'success' : 'info'">
                      {{ scope.row.status === 'enabled' ? '启用' : '禁用' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createdAt" label="创建时间" width="180" />
                <el-table-column label="操作" width="200">
                  <template #default="scope">
                    <el-button type="primary" size="small" @click="editMock(scope.row)">
                      <el-icon><Edit /></el-icon> 编辑
                    </el-button>
                    <el-button type="danger" size="small" @click="deleteMock(scope.row.id)">
                      <el-icon><Delete /></el-icon> 删除
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            
            <div class="mock-editor" v-if="currentMock">
              <h3>编辑Mock接口</h3>
              <el-form :model="currentMock" label-width="100px">
                <el-form-item label="接口名称">
                  <el-input v-model="currentMock.name" placeholder="请输入接口名称" />
                </el-form-item>
                <el-form-item label="请求路径">
                  <el-input v-model="currentMock.path" placeholder="请输入请求路径，如 /api/users" />
                </el-form-item>
                <el-form-item label="请求方法">
                  <el-select v-model="currentMock.method" placeholder="请选择请求方法">
                    <el-option label="GET" value="GET" />
                    <el-option label="POST" value="POST" />
                    <el-option label="PUT" value="PUT" />
                    <el-option label="DELETE" value="DELETE" />
                  </el-select>
                </el-form-item>
                <el-form-item label="状态码">
                  <el-input v-model="currentMock.statusCode" placeholder="请输入状态码，如 200" type="number" />
                </el-form-item>
                <el-form-item label="响应头">
                  <el-input type="textarea" v-model="currentMock.headers" placeholder="请输入响应头，JSON格式" rows="3" />
                </el-form-item>
                <el-form-item label="响应体">
                  <el-input type="textarea" v-model="currentMock.response" placeholder="请输入响应体，JSON格式" rows="6" />
                </el-form-item>
                <el-form-item label="延迟(ms)">
                  <el-input v-model="currentMock.delay" placeholder="请输入响应延迟，单位毫秒" type="number" />
                </el-form-item>
                <el-form-item label="状态">
                  <el-switch v-model="currentMock.statusEnabled" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary">保存</el-button>
                  <el-button @click="cancelEdit">取消</el-button>
                </el-form-item>
              </el-form>
            </div>
            
            <div class="mock-info">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>Mock服务信息</span>
                  </div>
                </template>
                <div class="info-content">
                  <p><strong>Mock基础URL:</strong> <code>{{ mockBaseUrl }}</code></p>
                  <p><strong>已创建接口:</strong> {{ mockList.length }}</p>
                  <p><strong>启用状态:</strong> {{ enabledCount }} 个接口已启用</p>
                  <p><strong>使用说明:</strong></p>
                  <ul>
                    <li>创建Mock接口后，使用基础URL + 接口路径访问</li>
                    <li>支持自定义响应体、状态码和响应延迟</li>
                    <li>可以通过状态开关启用或禁用接口</li>
                  </ul>
                </div>
              </el-card>
            </div>
          </div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

const activeIndex = ref('4')
const currentMock = ref<any>(null)

const mockBaseUrl = 'http://localhost:8080/api/mock'

const mockList = ref([
  {
    id: 1,
    name: '用户列表',
    path: '/users',
    method: 'GET',
    status: 'enabled',
    statusCode: 200,
    createdAt: '2026-02-11 00:00:00'
  },
  {
    id: 2,
    name: '用户详情',
    path: '/users/{id}',
    method: 'GET',
    status: 'enabled',
    statusCode: 200,
    createdAt: '2026-02-11 00:00:00'
  },
  {
    id: 3,
    name: '创建用户',
    path: '/users',
    method: 'POST',
    status: 'disabled',
    statusCode: 201,
    createdAt: '2026-02-11 00:00:00'
  }
])

const enabledCount = computed(() => {
  return mockList.value.filter(item => item.status === 'enabled').length
})

const editMock = (mock: any) => {
  currentMock.value = { ...mock, statusEnabled: mock.status === 'enabled' }
}

const deleteMock = (id: number) => {
  mockList.value = mockList.value.filter(item => item.id !== id)
}

const cancelEdit = () => {
  currentMock.value = null
}
</script>

<style scoped>
.mock-container {
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

.mock-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.mock-content {
  margin-top: 20px;
}

.mock-list {
  margin-bottom: 40px;
}

.mock-editor {
  margin-bottom: 40px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.mock-editor h3 {
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: bold;
}

.mock-info {
  margin-top: 40px;
}

.info-content {
  line-height: 1.6;
}

.info-content code {
  background-color: #f0f0f0;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: monospace;
}

.info-content ul {
  margin-top: 10px;
  padding-left: 20px;
}

.info-content li {
  margin-bottom: 5px;
}
</style>
