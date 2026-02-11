<template>
  <div class="team-project-list-container">
    <el-container class="full-height">
      <el-header height="60px" class="nav-header">
        <div class="logo">API Master</div>
        <el-menu :default-active="activeIndex" class="menu" mode="horizontal">
          <el-menu-item index="1">
            <router-link to="/">首页</router-link>
          </el-menu-item>
          <el-menu-item index="2" :class="{ active: true }">
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
      <el-main class="main-content">
        <!-- 顶部操作栏 -->
        <div class="header">
          <div class="breadcrumb">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item>
                <router-link to="/teams">团队管理</router-link>
              </el-breadcrumb-item>
              <el-breadcrumb-item>{{ team?.name }}</el-breadcrumb-item>
              <el-breadcrumb-item>项目管理</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索项目"
              class="search-input"
              prefix-icon="Search"
            />
            <el-button type="default" class="import-btn">导入项目</el-button>
            <el-button type="primary" class="create-btn" @click="showCreateDialog = true">+ 新建项目</el-button>
          </div>
        </div>

    <!-- 项目列表 -->
    <div class="project-grid">
      <el-card v-for="project in filteredProjects" :key="project.id" class="project-card">
        <div class="project-card-header">
          <div class="project-icon">
            <el-icon class="icon"><Folder /></el-icon>
          </div>
          <h2 class="project-name">{{ project.name }}</h2>
        </div>
        <p class="project-description">{{ project.description }}</p>
        <div class="project-meta">
          <span class="project-status">{{ project.status === 1 ? '活跃' : '归档' }}</span>
          <span class="project-time">{{ formatDate(project.createdAt) }}</span>
        </div>
        <div class="project-actions">
          <el-button type="text" class="action-btn" @click="navigateToApiDesign(project.id)">API设计</el-button>
          <el-button type="text" class="action-btn" @click="navigateToApiTest(project.id)">API测试</el-button>
          <el-button type="text" class="action-btn" @click="navigateToMock(project.id)">Mock服务</el-button>
          <el-button type="text" class="action-btn" @click="navigateToDocs(project.id)">文档生成</el-button>
          <el-button type="text" class="action-btn" @click="editProject(project)">编辑</el-button>
          <el-button type="text" class="action-btn danger" @click="deleteProject(project.id)">删除</el-button>
        </div>
      </el-card>
      
      <!-- 空状态 -->
      <div v-if="filteredProjects.length === 0" class="empty-state">
        <el-empty description="暂无项目" />
        <el-button type="primary" class="create-btn" @click="showCreateDialog = true">+ 新建项目</el-button>
      </div>
    </div>

    <!-- 创建项目对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="创建项目"
      width="500px"
    >
      <el-form :model="formData" :rules="rules" ref="createForm">
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入项目名称"></el-input>
        </el-form-item>
        <el-form-item label="项目描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            rows="4"
            placeholder="请输入项目描述"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button type="primary" @click="submitCreateForm">创建</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑项目对话框 -->
    <el-dialog
      v-model="showEditDialog"
      title="编辑项目"
      width="500px"
    >
      <el-form :model="formData" :rules="rules" ref="editForm">
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入项目名称"></el-input>
        </el-form-item>
        <el-form-item label="项目描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            rows="4"
            placeholder="请输入项目描述"
          ></el-input>
        </el-form-item>
        <el-form-item label="项目状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择项目状态">
            <el-option label="活跃" value="1"></el-option>
            <el-option label="归档" value="0"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" @click="submitEditForm">保存</el-button>
        </span>
      </template>
    </el-dialog>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useProjectStore } from '../stores/project'
import { useTeamStore } from '../stores/team'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { Search, Folder, ArrowDown } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()
const projectStore = useProjectStore()
const teamStore = useTeamStore()

// 导航栏激活项
const activeIndex = ref('2')

// 处理退出登录
const handleLogout = () => {
  userStore.logout()
  ElMessage.success('退出登录成功')
  router.push('/login')
}

const teamId = computed(() => Number(route.params.teamId))
const team = computed(() => teamStore.getCurrentTeam)

const showCreateDialog = ref(false)
const showEditDialog = ref(false)
const currentProjectId = ref<number | null>(null)
const createForm = ref<any>(null)
const editForm = ref<any>(null)
const searchKeyword = ref('')

const formData = ref({
  name: '',
  description: '',
  status: 1
})

const rules = {
  name: [
    { required: true, message: '请输入项目名称', trigger: 'blur' },
    { min: 1, max: 50, message: '项目名称长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入项目描述', trigger: 'blur' }
  ]
}

const projects = computed(() => projectStore.getProjects)

const filteredProjects = computed(() => {
  let result = projects.value.filter(project => project.teamId === teamId.value)
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(project => 
      project.name.toLowerCase().includes(keyword) || 
      project.description.toLowerCase().includes(keyword)
    )
  }
  return result
})

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

const navigateToApiDesign = (projectId: number) => {
  router.push(`/api-design?projectId=${projectId}&teamId=${teamId.value}`)
}

const navigateToApiTest = (projectId: number) => {
  router.push(`/api-test?projectId=${projectId}&teamId=${teamId.value}`)
}

const navigateToMock = (projectId: number) => {
  router.push(`/mock?projectId=${projectId}&teamId=${teamId.value}`)
}

const navigateToDocs = (projectId: number) => {
  router.push(`/docs?projectId=${projectId}&teamId=${teamId.value}`)
}

const submitCreateForm = async () => {
  if (!createForm.value) return
  
  try {
    // 检查用户是否登录
    if (!userStore.getIsLoggedIn) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }
    
    await createForm.value.validate()
    await projectStore.createProject({
      name: formData.value.name,
      description: formData.value.description,
      teamId: teamId.value
    })
    ElMessage.success('项目创建成功')
    showCreateDialog.value = false
    formData.value = {
      name: '',
      description: '',
      status: 1
    }
  } catch (error: any) {
    ElMessage.error(error.message || '项目创建失败')
  }
}

const editProject = (project: any) => {
  currentProjectId.value = project.id
  formData.value = {
    name: project.name,
    description: project.description,
    status: project.status
  }
  showEditDialog.value = true
}

const submitEditForm = async () => {
  if (!editForm.value || !currentProjectId.value) return
  
  try {
    await editForm.value.validate()
    await projectStore.updateProject(currentProjectId.value, {
      name: formData.value.name,
      description: formData.value.description,
      status: formData.value.status
    })
    ElMessage.success('项目更新成功')
    showEditDialog.value = false
    currentProjectId.value = null
  } catch (error: any) {
    ElMessage.error(error.message || '项目更新失败')
  }
}

const deleteProject = async (projectId: number) => {
  try {
    await projectStore.deleteProject(projectId)
    ElMessage.success('项目删除成功')
  } catch (error: any) {
    ElMessage.error(error.message || '项目删除失败')
  }
}

onMounted(async () => {
  // 检查用户是否登录
  if (!userStore.getIsLoggedIn) {
    ElMessage.error('请先登录')
    router.push('/login')
    return
  }
  
  // 获取团队详情
  await teamStore.fetchTeam(teamId.value)
  
  // 获取项目列表
  await projectStore.fetchProjects()
})
</script>

<style scoped>
.team-project-list-container {
  width: 100%;
  min-height: 100vh;
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

/* 主要内容 */
.main-content {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

/* 顶部操作栏 */
.header {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 30px;
}

.breadcrumb {
  background-color: #fff;
  padding: 12px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.header-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.search-input {
  width: 240px;
}

.import-btn {
  border-color: #dcdfe6;
  color: #606266;
}

.create-btn {
  background-color: #409eff;
  border-color: #409eff;
  font-weight: 500;
}

/* 项目网格布局 */
.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

/* 项目卡片 */
.project-card {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
  overflow: hidden;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.project-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
  border-color: #c6e2ff;
}

.project-card-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.project-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background-color: #ecf5ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.project-icon .icon {
  font-size: 20px;
  color: #409eff;
}

.project-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  flex: 1;
}

.project-description {
  margin: 0 0 16px 0;
  color: #606266;
  line-height: 1.5;
  font-size: 14px;
  flex: 1;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.project-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 12px;
  color: #909399;
}

.project-status {
  padding: 2px 8px;
  border-radius: 10px;
  background-color: #f0f9eb;
  color: #67c23a;
}

.project-time {
  color: #909399;
}

.project-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #f0f2f5;
}

.action-btn {
  color: #606266;
  font-size: 13px;
  padding: 0 8px;
  height: 28px;
}

.action-btn:hover {
  color: #409eff;
}

.action-btn.danger:hover {
  color: #f56c6c;
}

/* 空状态 */
.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background-color: #fff;
  border-radius: 8px;
  border: 1px dashed #dcdfe6;
}

.empty-state .create-btn {
  margin-top: 20px;
}

/* 对话框样式 */
.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-actions {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    padding: 16px;
  }

  .search-input {
    width: 100%;
  }

  .project-grid {
    grid-template-columns: 1fr;
  }
}
</style>
