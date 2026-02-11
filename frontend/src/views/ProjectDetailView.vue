<template>
  <div class="project-detail-container">
    <el-container class="full-height">
      <el-header height="60px" class="nav-header">
        <div class="logo">API Master</div>
        <el-menu :default-active="activeIndex" class="menu" mode="horizontal">
          <el-menu-item index="1">
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
      <el-main class="main-content">
        <div class="header">
          <h1>项目详情</h1>
          <el-button type="default" @click="router.push('/projects')">返回列表</el-button>
        </div>

    <el-card v-if="project" class="project-detail-card">
      <div class="project-info">
        <h2>{{ project.name }}</h2>
        <p class="project-description">{{ project.description }}</p>
        <div class="project-meta">
          <span>创建时间: {{ formatDate(project.createdAt) }}</span>
          <span>更新时间: {{ formatDate(project.updatedAt) }}</span>
          <span>状态: {{ project.status === 1 ? '活跃' : '归档' }}</span>
        </div>
      </div>
    </el-card>

    <!-- 项目成员管理 -->
    <el-card class="members-card">
      <template #header>
        <div class="card-header">
          <h3>成员管理</h3>
          <el-button type="primary" @click="showAddMemberDialog = true">添加成员</el-button>
        </div>
      </template>

      <el-table :data="project?.members || []" style="width: 100%">
        <el-table-column prop="user.username" label="用户名" width="180" />
        <el-table-column prop="user.nickname" label="昵称" width="180" />
        <el-table-column prop="user.email" label="邮箱" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="scope">
            {{ getRoleText(scope.row.role) }}
          </template>
        </el-table-column>
        <el-table-column prop="joinedAt" label="加入时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.joinedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button type="danger" size="small" @click="removeMember(scope.row.userId)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 项目API列表 -->
    <el-card class="apis-card">
      <template #header>
        <div class="card-header">
          <h3>API列表</h3>
          <el-button type="primary" @click="navigateToApiDesign">创建API</el-button>
        </div>
      </template>

      <el-empty v-if="!apis || apis.length === 0" description="暂无API" />
      <el-table v-else :data="apis" style="width: 100%">
        <el-table-column prop="name" label="API名称" width="180" />
        <el-table-column prop="path" label="路径" />
        <el-table-column prop="method" label="方法" width="100">
          <template #default="scope">
            <el-tag :type="getMethodType(scope.row.method)">{{ scope.row.method }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            {{ scope.row.status === 1 ? '活跃' : '归档' }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="default" size="small" @click="navigateToApiTest(scope.row.id)">测试</el-button>
            <el-button type="default" size="small" @click="editApi(scope.row.id)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加成员对话框 -->
    <el-dialog
      v-model="showAddMemberDialog"
      title="添加项目成员"
      width="500px"
    >
      <el-form :model="memberForm" :rules="memberRules" ref="memberFormRef">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="memberForm.userId" placeholder="请输入用户ID"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="memberForm.role" placeholder="请选择角色">
            <el-option label="所有者" value="1"></el-option>
            <el-option label="管理员" value="2"></el-option>
            <el-option label="成员" value="3"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddMemberDialog = false">取消</el-button>
          <el-button type="primary" @click="submitAddMember">添加</el-button>
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
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const projectStore = useProjectStore()
const userStore = useUserStore()

// 导航栏激活项
const activeIndex = ref('2')

// 处理退出登录
const handleLogout = () => {
  userStore.logout()
  ElMessage.success('退出登录成功')
  router.push('/login')
}

const projectId = computed(() => Number(route.params.id))
const project = computed(() => projectStore.getCurrentProject)
const apis = ref<any[]>([])

const showAddMemberDialog = ref(false)
const memberFormRef = ref<any>(null)
const memberForm = ref({
  userId: '',
  role: 3
})

const memberRules = {
  userId: [
    { required: true, message: '请输入用户ID', trigger: 'blur' },
    { type: 'number', message: '用户ID必须是数字', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

const getRoleText = (role: number) => {
  switch (role) {
    case 1:
      return '所有者'
    case 2:
      return '管理员'
    case 3:
      return '成员'
    default:
      return '未知'
  }
}

const getMethodType = (method: string) => {
  switch (method.toUpperCase()) {
    case 'GET':
      return 'success'
    case 'POST':
      return 'primary'
    case 'PUT':
      return 'warning'
    case 'DELETE':
      return 'danger'
    default:
      return 'info'
  }
}

const navigateToApiDesign = () => {
  router.push(`/api-design?projectId=${projectId.value}`)
}

const navigateToApiTest = (apiId: number) => {
  router.push(`/api-test?apiId=${apiId}&projectId=${projectId.value}`)
}

const editApi = (apiId: number) => {
  router.push(`/api-design?id=${apiId}&projectId=${projectId.value}`)
}

const submitAddMember = async () => {
  if (!memberFormRef.value) return
  
  try {
    await memberFormRef.value.validate()
    await projectStore.addProjectMember(projectId.value, {
      userId: Number(memberForm.value.userId),
      role: memberForm.value.role
    })
    ElMessage.success('成员添加成功')
    showAddMemberDialog.value = false
    memberForm.value = {
      userId: '',
      role: 3
    }
  } catch (error) {
    ElMessage.error('成员添加失败')
  }
}

const removeMember = async (userId: number) => {
  try {
    await projectStore.removeProjectMember(projectId.value, userId)
    ElMessage.success('成员移除成功')
  } catch (error) {
    ElMessage.error('成员移除失败')
  }
}

onMounted(async () => {
  await projectStore.fetchProject(projectId.value)
  await projectStore.fetchProjectMembers(projectId.value)
  // 这里可以添加获取项目API列表的逻辑
})
</script>

<style scoped>
.project-detail-container {
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
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
}

.project-detail-card {
  margin-bottom: 30px;
}

.project-info h2 {
  margin: 0 0 10px 0;
  font-size: 20px;
  font-weight: bold;
}

.project-description {
  margin: 0 0 15px 0;
  color: #666;
  line-height: 1.5;
}

.project-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #999;
}

.members-card,
.apis-card {
  margin-bottom: 30px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
}

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}
</style>
