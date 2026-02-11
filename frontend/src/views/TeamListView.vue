<template>
  <div class="team-list-container">
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
          <h1>团队管理</h1>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索团队"
              class="search-input"
              prefix-icon="Search"
            />
            <el-button type="primary" class="create-btn" @click="showCreateDialog = true">+ 新建团队</el-button>
          </div>
        </div>

    <!-- 团队列表 -->
    <div class="team-grid">
      <el-card v-for="team in filteredTeams" :key="team.id" class="team-card">
        <div class="team-card-header">
          <div class="team-icon">
            <el-icon class="icon"><UserFilled /></el-icon>
          </div>
          <h2 class="team-name">{{ team.name }}</h2>
        </div>
        <p class="team-description">{{ team.description }}</p>
        <div class="team-meta">
          <span class="team-status">{{ team.status === 1 ? '活跃' : '归档' }}</span>
          <span class="team-time">{{ formatDate(team.createdAt) }}</span>
        </div>
        <div class="team-actions">
          <el-button type="text" class="action-btn" @click="navigateToProjects(team.id)">项目管理</el-button>
          <el-button type="text" class="action-btn" @click="editTeam(team)">编辑</el-button>
          <el-button type="text" class="action-btn danger" @click="deleteTeam(team.id)">删除</el-button>
        </div>
      </el-card>
      
      <!-- 空状态 -->
      <div v-if="filteredTeams.length === 0" class="empty-state">
        <el-empty description="暂无团队" />
        <el-button type="primary" class="create-btn" @click="showCreateDialog = true">+ 新建团队</el-button>
      </div>
    </div>

    <!-- 创建团队对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="创建团队"
      width="500px"
    >
      <el-form :model="formData" :rules="rules" ref="createForm">
        <el-form-item label="团队名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入团队名称"></el-input>
        </el-form-item>
        <el-form-item label="团队描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            rows="4"
            placeholder="请输入团队描述"
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

    <!-- 编辑团队对话框 -->
    <el-dialog
      v-model="showEditDialog"
      title="编辑团队"
      width="500px"
    >
      <el-form :model="formData" :rules="rules" ref="editForm">
        <el-form-item label="团队名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入团队名称"></el-input>
        </el-form-item>
        <el-form-item label="团队描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            rows="4"
            placeholder="请输入团队描述"
          ></el-input>
        </el-form-item>
        <el-form-item label="团队状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择团队状态">
            <el-option label="活跃" :value="1"></el-option>
            <el-option label="归档" :value="0"></el-option>
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
import { useRouter } from 'vue-router'
import { useTeamStore } from '../stores/team'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { Search, UserFilled, ArrowDown } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()
const teamStore = useTeamStore()

// 导航栏激活项
const activeIndex = ref('2')

// 处理退出登录
const handleLogout = () => {
  userStore.logout()
  ElMessage.success('退出登录成功')
  router.push('/login')
}

const showCreateDialog = ref(false)
const showEditDialog = ref(false)
const currentTeamId = ref<number | null>(null)
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
    { required: true, message: '请输入团队名称', trigger: 'blur' },
    { min: 1, max: 50, message: '团队名称长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入团队描述', trigger: 'blur' }
  ]
}

const teams = computed(() => teamStore.getTeams)

const filteredTeams = computed(() => {
  if (!searchKeyword.value) {
    return teams.value
  }
  const keyword = searchKeyword.value.toLowerCase()
  return teams.value.filter(team => 
    team.name.toLowerCase().includes(keyword) || 
    team.description.toLowerCase().includes(keyword)
  )
})

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

const navigateToProjects = (teamId: number) => {
  router.push(`/teams/${teamId}/projects`)
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
    await teamStore.createTeam({
      name: formData.value.name,
      description: formData.value.description
    })
    ElMessage.success('团队创建成功')
    showCreateDialog.value = false
    formData.value = {
      name: '',
      description: '',
      status: 1
    }
  } catch (error: any) {
    ElMessage.error(error.message || '团队创建失败')
  }
}

const editTeam = (team: any) => {
  currentTeamId.value = team.id
  formData.value = {
    name: team.name,
    description: team.description,
    status: team.status
  }
  showEditDialog.value = true
}

const submitEditForm = async () => {
  if (!editForm.value || !currentTeamId.value) return
  
  try {
    await editForm.value.validate()
    await teamStore.updateTeam(currentTeamId.value, {
      name: formData.value.name,
      description: formData.value.description,
      status: formData.value.status
    })
    ElMessage.success('团队更新成功')
    showEditDialog.value = false
    currentTeamId.value = null
  } catch (error: any) {
    ElMessage.error(error.message || '团队更新失败')
  }
}

const deleteTeam = async (teamId: number) => {
  try {
    await teamStore.deleteTeam(teamId)
    ElMessage.success('团队删除成功')
  } catch (error: any) {
    ElMessage.error(error.message || '团队删除失败')
  }
}

onMounted(async () => {
  // 检查用户是否登录
  if (!userStore.getIsLoggedIn) {
    ElMessage.error('请先登录')
    router.push('/login')
    return
  }
  
  await teamStore.fetchTeams()
})
</script>

<style scoped>
.team-list-container {
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
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 16px 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.header h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 240px;
}

.create-btn {
  background-color: #409eff;
  border-color: #409eff;
  font-weight: 500;
}

/* 团队网格布局 */
.team-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

/* 团队卡片 */
.team-card {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
  overflow: hidden;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.team-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
  border-color: #c6e2ff;
}

.team-card-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.team-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background-color: #ecf5ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.team-icon .icon {
  font-size: 20px;
  color: #409eff;
}

.team-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  flex: 1;
}

.team-description {
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

.team-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 12px;
  color: #909399;
}

.team-status {
  padding: 2px 8px;
  border-radius: 10px;
  background-color: #f0f9eb;
  color: #67c23a;
}

.team-time {
  color: #909399;
}

.team-actions {
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
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    padding: 16px;
  }

  .header-actions {
    width: 100%;
    justify-content: space-between;
  }

  .search-input {
    flex: 1;
  }

  .team-grid {
    grid-template-columns: 1fr;
  }
}
</style>
