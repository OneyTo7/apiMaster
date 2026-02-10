<template>
  <div class="home-container">
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
        <div class="hero">
          <h1>API Master</h1>
          <p>一体化的API设计、测试和协作平台</p>
          <div class="actions">
            <el-button type="primary" size="large">
              <router-link to="/api-design">开始使用</router-link>
            </el-button>
            <el-button size="large">
              <router-link to="/docs">了解更多</router-link>
            </el-button>
          </div>
        </div>
        <div class="features">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>API设计</span>
                  </div>
                </template>
                <p>可视化API编辑，参数管理，请求体设计</p>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>API测试</span>
                  </div>
                </template>
                <p>发送请求，响应分析，测试用例管理</p>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>Mock服务</span>
                  </div>
                </template>
                <p>自动生成Mock数据，自定义Mock规则</p>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>文档生成</span>
                  </div>
                </template>
                <p>自动生成API文档，支持多格式导出</p>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const activeIndex = ref('1')
</script>

<style scoped>
.home-container {
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

.user {
  margin-left: auto;
}

.main {
  padding: 20px;
  background-color: #f5f7fa;
}

.hero {
  text-align: center;
  padding: 60px 0;
  background-color: #fff;
  border-radius: 8px;
  margin-bottom: 40px;
}

.hero h1 {
  font-size: 36px;
  margin-bottom: 20px;
  color: #303133;
}

.hero p {
  font-size: 18px;
  color: #606266;
  margin-bottom: 40px;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.features {
  margin-top: 40px;
}

.card-header {
  font-weight: bold;
}
</style>