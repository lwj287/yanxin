<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2 class="title">燕鑫学院</h2>
        <p class="subtitle">员工在线培训与技能认证系统</p>
      </div>
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="login-form" size="large">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入员工账号/手机号" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password :prefix-icon="Lock" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">登录系统</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'

const router = useRouter()
const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [{ required: true, message: '账号不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '密码不能为空', trigger: 'blur' }]
}

const handleLogin = () => {
  loginFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        const token = await login({
          username: loginForm.username,
          password: loginForm.password
        })
        localStorage.setItem('token', token)
        ElMessage.success('登录成功')
        router.push('/')
      } catch (error: any) {
        console.error('登录失败', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f0f2f5 0%, #e6ebf1 100%);
}

.login-card {
  width: 420px;
  padding: 50px 40px;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.05);
  box-sizing: border-box;

  .login-header {
    text-align: center;
    margin-bottom: 40px;

    .title {
      font-size: 28px;
      font-weight: 600;
      color: #1f2d3d;
      margin: 0 0 10px;
      letter-spacing: 2px;
    }

    .subtitle {
      font-size: 14px;
      color: #8c939d;
      margin: 0;
    }
  }

  .login-form {
    :deep(.el-input__wrapper) {
      box-shadow: none;
      background: #f5f7fa;
      border-radius: 12px;
      padding: 0 16px;
      height: 48px;

      &.is-focus {
        background: #fff;
        box-shadow: 0 0 0 1px #409eff inset;
      }
    }

    .login-btn {
      width: 100%;
      height: 48px;
      border-radius: 12px;
      font-size: 16px;
      font-weight: 500;
      letter-spacing: 1px;
      margin-top: 10px;
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
    }
  }
}
</style>