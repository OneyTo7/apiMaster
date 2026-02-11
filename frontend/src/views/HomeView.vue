<template>
  <div class="home-container">
    <el-container class="full-height">
      <el-header height="60px" class="header">
        <div class="logo">API Master</div>
        <el-menu :default-active="activeIndex" class="menu" mode="horizontal">
          <el-menu-item index="1" :class="{ active: true }">
            <router-link to="/">首页</router-link>
          </el-menu-item>
          <el-menu-item index="2">
            <router-link to="/teams">团队管理</router-link>
          </el-menu-item>
          <el-sub-menu index="3">
            <template #title>API管理</template>
            <el-menu-item index="3-1">
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
        <div class="hero">
          <h1>API Master</h1>
          <p>一体化的API设计、测试和协作平台</p>
          <div class="actions">
            <el-button type="primary" size="large">
              <router-link to="/teams">开始使用</router-link>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const activeIndex = ref('1')

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