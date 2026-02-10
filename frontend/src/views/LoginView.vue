<template>
  <div class="login-container">
    <div class="login-form-wrapper">
      <div class="login-header">
        <h1>API Master</h1>
        <p>欢迎登录 API 管理平台</p>
      </div>
      <el-card shadow="hover" class="login-card">
        <el-tabs v-model="activeTab" class="login-tabs">
          <el-tab-pane label="登录" name="login">
            <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" label-width="80px">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
              </el-form-item>
              <el-form-item>
                <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
                <el-link type="primary" :underline="false" style="float: right">忘记密码?</el-link>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" class="login-btn" @click="handleLogin" :loading="loading">登录</el-button>
              </el-form-item>
              <div class="login-footer">
                <span>还没有账号?</span>
                <el-link type="primary" @click="activeTab = 'register'">立即注册</el-link>
              </div>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="注册" name="register">
            <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" label-width="100px">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="registerForm.username" placeholder="请输入用户名" prefix-icon="User" />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="registerForm.email" placeholder="请输入邮箱" prefix-icon="Message" />
              </el-form-item>
              <el-form-item label="昵称" prop="nickname">
                <el-input v-model="registerForm.nickname" placeholder="请输入昵称" prefix-icon="Avatar" />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码" prefix-icon="Check" show-password />
              </el-form-item>
              <el-form-item>
                <el-checkbox v-model="registerForm.agree">我已阅读并同意 <el-link type="primary">用户协议</el-link> 和 <el-link type="primary">隐私政策</el-link></el-checkbox>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" class="register-btn" @click="handleRegister" :loading="registerLoading">注册</el-button>
              </el-form-item>
              <div class="login-footer">
                <span>已有账号?</span>
                <el-link type="primary" @click="activeTab = 'login'">立即登录</el-link>
              </div>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </el-card>
      <div class="login-footer-info">
        <p>© 2026 API Master. 保留所有权利</p>
        <div class="login-links">
          <el-link type="info">帮助中心</el-link>
          <el-link type="info">API文档</el-link>
          <el-link type="info">联系我们</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Message, Avatar, Check } from '@element-plus/icons-vue'

const router = useRouter()
const activeTab = ref('login')
const loading = ref(false)
const registerLoading = ref(false)
const loginFormRef = ref()
const registerFormRef = ref()

const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

const registerForm = reactive({
  username: '',
  email: '',
  nickname: '',
  password: '',
  confirmPassword: '',
  agree: false
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }, { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码长度至少6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }, {
    validator: (rule, value, callback) => {
      if (value !== registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    },
    trigger: 'blur'
  }],
  agree: [{ validator: (rule, value, callback) => {
    if (!value) {
      callback(new Error('请阅读并同意用户协议和隐私政策'))
    } else {
      callback()
    }
  }, trigger: 'change' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    // 模拟登录请求
    setTimeout(() => {
      loading.value = false
      console.log('登录成功:', loginForm)
      router.push('/')
    }, 1000)
  } catch (error) {
    console.error('登录验证失败:', error)
  }
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    await registerFormRef.value.validate()
    registerLoading.value = true
    
    // 模拟注册请求
    setTimeout(() => {
      registerLoading.value = false
      console.log('注册成功:', registerForm)
      activeTab.value = 'login'
      loginForm.username = registerForm.username
    }, 1000)
  } catch (error) {
    console.error('注册验证失败:', error)
  }
}
</script>

<style scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-form-wrapper {
  width: 400px;
  max-width: 90%;
  padding: 40px 20px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  color: #fff;
  font-size: 32px;
  margin-bottom: 10px;
  font-weight: bold;
}

.login-header p {
  color: rgba(255, 255, 255, 0.8);
  font-size: 16px;
}

.login-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.login-tabs {
  padding: 0 20px;
}

.login-btn,
.register-btn {
  width: 100%;
  height: 40px;
  font-size: 16px;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
}

.login-footer span {
  color: #606266;
  margin-right: 5px;
}

.login-footer-info {
  text-align: center;
  margin-top: 30px;
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
}

.login-footer-info p {
  margin-bottom: 10px;
}

.login-links {
  display: flex;
  justify-content: center;
  gap: 30px;
}

.login-links .el-link {
  color: rgba(255, 255, 255, 0.8);
}

.login-links .el-link:hover {
  color: #fff;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-form-wrapper {
    padding: 20px 10px;
  }
  
  .login-header h1 {
    font-size: 24px;
  }
  
  .login-header p {
    font-size: 14px;
  }
  
  .login-tabs {
    padding: 0 10px;
  }
}
</style>
