<template>
  <div class="api-design-container">
    <el-container class="full-height">
      <el-header height="60px" class="header">
        <div class="logo">API Master</div>
        <el-menu :default-active="activeIndex" class="menu" mode="horizontal">
          <el-menu-item index="1">
            <router-link to="/">首页</router-link>
          </el-menu-item>
          <el-menu-item index="2" :class="{ active: true }">
            <router-link to="/api-design">API设计</router-link>
          </el-menu-item>
          <el-menu-item index="3">
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
        <el-card shadow="hover" class="api-design-card">
          <template #header>
            <div class="card-header">
              <span>API接口设计</span>
              <el-button type="primary" size="small" class="add-api-btn">
                <el-icon><Plus /></el-icon> 新建API
              </el-button>
            </div>
          </template>
          <div class="api-design-content">
            <el-form :model="apiForm" label-width="80px">
              <el-form-item label="API名称">
                <el-input v-model="apiForm.name" placeholder="请输入API名称" />
              </el-form-item>
              <el-form-item label="请求方法">
                <el-select v-model="apiForm.method" placeholder="请选择请求方法">
                  <el-option label="GET" value="GET" />
                  <el-option label="POST" value="POST" />
                  <el-option label="PUT" value="PUT" />
                  <el-option label="DELETE" value="DELETE" />
                </el-select>
              </el-form-item>
              <el-form-item label="请求路径">
                <el-input v-model="apiForm.path" placeholder="请输入请求路径，如 /api/users" />
              </el-form-item>
              <el-form-item label="接口描述">
                <el-input type="textarea" v-model="apiForm.description" placeholder="请输入接口描述" />
              </el-form-item>
              <el-form-item label="请求参数">
                <el-table :data="apiForm.params" style="width: 100%">
                  <el-table-column prop="name" label="参数名" width="180" />
                  <el-table-column prop="type" label="类型" width="120" />
                  <el-table-column prop="required" label="必填" width="80" />
                  <el-table-column prop="description" label="描述" />
                </el-table>
                <el-button type="primary" size="small" class="add-param-btn">
                  <el-icon><Plus /></el-icon> 添加参数
                </el-button>
              </el-form-item>
              <el-form-item label="请求体">
                <el-select v-model="apiForm.bodyType" placeholder="请选择请求体类型">
                  <el-option label="JSON" value="json" />
                  <el-option label="Form Data" value="form-data" />
                  <el-option label="Text" value="text" />
                </el-select>
                <el-input type="textarea" v-model="apiForm.body" placeholder="请输入请求体内容" rows="4" />
              </el-form-item>
              <el-form-item label="响应示例">
                <el-input type="textarea" v-model="apiForm.response" placeholder="请输入响应示例" rows="4" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary">保存API</el-button>
                <el-button>预览</el-button>
                <el-button>测试</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { Plus } from '@element-plus/icons-vue'

const activeIndex = ref('2')

const apiForm = reactive({
  name: '',
  method: 'GET',
  path: '',
  description: '',
  params: [
    {
      name: 'page',
      type: 'number',
      required: false,
      description: '页码'
    },
    {
      name: 'size',
      type: 'number',
      required: false,
      description: '每页条数'
    }
  ],
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
</script>

<style scoped>
.api-design-container {
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

.api-design-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.api-design-content {
  margin-top: 20px;
}

.add-api-btn,
.add-param-btn {
  margin-left: 10px;
}
</style>
