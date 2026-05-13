<template>
  <div class="login-page">
    <div class="login-left">
      <div class="brand-box">
        <h1>燕鑫招贤宝</h1>
        <p>保洁 / 保姆招聘、简历筛选与入职管理系统</p>
        <ul>
          <li>岗位发布与多条件筛选</li>
          <li>智能筛选与面试流程管理</li>
          <li>入职进度与招聘数据看板</li>
        </ul>
      </div>
    </div>
    <div class="login-right">
      <el-card class="login-card">
        <template #header>
          <div class="login-title">系统登录</div>
        </template>
        <el-form :model="form" label-position="top">
          <el-form-item label="账号">
            <el-input v-model="form.username" placeholder="请输入账号" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input type="password" v-model="form.password" show-password placeholder="请输入密码" />
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="form.role" style="width:100%">
              <el-option label="管理员" value="ADMIN" />
              <el-option label="HR" value="HR" />
            </el-select>
          </el-form-item>
          <el-button type="primary" @click="login" style="width:100%">登录</el-button>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = reactive({ username: '', password: '', role: '' })
const login = async () => {
  const res = await request.post('/api/auth/login', form)
  const d = res.data
  sessionStorage.setItem('token', d.token)
  sessionStorage.setItem('role', d.role)
  sessionStorage.setItem('realName', d.realName)
  ElMessage.success('登录成功')
  router.push('/admin/jobs')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  background: linear-gradient(135deg, #dbeafe 0%, #f8fafc 45%, #eff6ff 100%);
}

.login-left {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.brand-box {
  width: min(520px, 90%);
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border: 1px solid #e5e7eb;
  border-radius: 20px;
  padding: 28px;
}

.brand-box h1 { margin: 0 0 8px; font-size: 34px; color: #1d4ed8; }
.brand-box p { margin: 0 0 12px; color: #475569; }
.brand-box ul { margin: 0; padding-left: 18px; color: #334155; line-height: 1.9; }

.login-right {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.login-card {
  width: min(420px, 90%);
  border-radius: 16px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.12);
}

.login-title { font-size: 18px; font-weight: 600; text-align: center; }

@media (max-width: 900px) {
  .login-page { grid-template-columns: 1fr; }
  .login-left { display: none; }
}
</style>
