<template>
  <div class="api-manager-container">
    <el-container class="full-height">
      <!-- 顶部导航 -->
      <el-header height="60px" class="nav-header">
        <div class="logo">API Master</div>
        <el-menu :default-active="activeMenu" class="menu" mode="horizontal" @select="handleMenuSelect">
          <el-menu-item index="home">
            <el-icon><HomeFilled /></el-icon> 首页
          </el-menu-item>
          <el-menu-item index="teams">
            <el-icon><UserFilled /></el-icon> 团队管理
          </el-menu-item>
          <el-menu-item index="api-manager">
            <el-icon><DocumentChecked /></el-icon> 接口管理
          </el-menu-item>
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

      <!-- 主内容区 -->
      <el-main class="main-content">
        <!-- 项目选择器 -->
        <div class="project-selector">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>
              <router-link to="/teams">团队管理</router-link>
            </el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentTeam">
              <router-link :to="`/teams/${currentTeam.id}/projects`">{{ currentTeam.name }}</router-link>
            </el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentProject">
              <el-dropdown trigger="click">
                <span class="project-dropdown">
                  {{ currentProject.name }} <el-icon><arrow-down /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item
                      v-for="project in projects"
                      :key="project.id"
                      @click="switchProject(project)"
                    >
                      {{ project.name }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!-- API 管理区域 -->
        <div class="api-manager-body">
          <!-- 左侧目录树 -->
          <ApiFolderTree
            v-if="currentProject"
            :project-id="currentProject.id"
            @select-api="handleSelectApi"
            @create-api="handleCreateApi"
          />

          <!-- 右侧编辑器 -->
          <ApiEditor
            v-if="currentProject"
            ref="apiEditorRef"
            :project-id="currentProject.id"
            @save="handleSaveApi"
          />

          <!-- 未选择项目时的提示 -->
          <div v-else class="no-project">
            <el-empty description="请先选择一个项目">
              <el-button type="primary" @click="goToProjects">选择项目</el-button>
            </el-empty>
          </div>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  HomeFilled,
  UserFilled,
  DocumentChecked,
  ArrowDown
} from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'
import { useProjectStore } from '../stores/project'
import ApiFolderTree from '../components/ApiFolderTree.vue'
import ApiEditor from '../components/ApiEditor.vue'
import type { Api } from '../stores/api'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const projectStore = useProjectStore()

const activeMenu = ref('api-manager')
const apiEditorRef = ref<InstanceType<typeof ApiEditor>>()

const currentTeam = computed(() => {
  // 从 store 或路由获取当前团队
  return null
})

const currentProject = computed(() => projectStore.getCurrentProject)
const projects = computed(() => projectStore.getProjects)

const handleMenuSelect = (index: string) => {
  switch (index) {
    case 'home':
      router.push('/')
      break
    case 'teams':
      router.push('/teams')
      break
    case 'api-manager':
      // 已在当前页面
      break
  }
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('退出登录成功')
  router.push('/login')
}

const switchProject = (project: any) => {
  projectStore.setCurrentProject(project)
  router.push({
    path: '/api-manager',
    query: { projectId: project.id }
  })
}

const goToProjects = () => {
  router.push('/projects')
}

const handleSelectApi = (api: Api) => {
  apiEditorRef.value?.openApi(api)
}

const handleCreateApi = (folderId?: number) => {
  apiEditorRef.value?.createNewApi(folderId)
}

const handleSaveApi = (api: any) => {
  console.log('保存API:', api)
}

// 初始化
onMounted(async () => {
  userStore.checkLogin()

  const projectId = route.query.projectId
  if (projectId) {
    await projectStore.fetchProject(Number(projectId))
  } else if (projects.value.length === 0) {
    await projectStore.fetchProjects()
  }
})
</script>

<style scoped>
.api-manager-container {
  width: 100%;
  height: 100vh;
  background-color: #f5f7fa;
}

.full-height {
  height: 100%;
}

/* 导航栏样式 */
.nav-header {
  display: flex;
  align-items: center;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 0 24px;
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

/* 主内容区 */
.main-content {
  padding: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 项目选择器 */
.project-selector {
  padding: 12px 24px;
  background-color: #fff;
  border-bottom: 1px solid #e0e0e0;
}

.project-dropdown {
  cursor: pointer;
  color: #409eff;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* API 管理主体 */
.api-manager-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.no-project {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
