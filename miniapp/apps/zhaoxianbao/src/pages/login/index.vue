<template>
  <view class="page">
    <view class="header">
      <view class="title">燕鑫招贤宝</view>
      <view class="subtitle">提升招聘效率，闭环管理入职流程</view>
    </view>
    <view class="card login-card">
      <view class="form-item">
        <text class="label">账号</text>
        <input v-model="form.phone" class="input" placeholder="请输入手机号" placeholder-class="placeholder-style" />
      </view>
      <view class="form-item">
        <text class="label">密码</text>
        <input v-model="form.password" class="input" password placeholder="请输入密码" placeholder-class="placeholder-style" />
      </view>
      <button class="login-btn" @click="login">登录</button>
      <button class="register-btn" @click="goRegister">没有账号？去注册</button>
    </view>
  </view>
</template>

<script setup>
import { reactive } from 'vue'
import { post } from '../../utils/request'
import { setToken } from '../../utils/auth'

const form = reactive({
  phone: '',
  password: ''
})

const login = async () => {
  if (!form.phone || !form.password) {
    uni.showToast({ title: '请输入手机号和密码', icon: 'none' })
    return
  }
  const res = await post('/api/auth/login', { username: form.phone, password: form.password, role: 'CANDIDATE' })
  setToken(res.token)
  uni.setStorageSync('realName', res.realName || '')
  uni.setStorageSync('loginPhone', form.phone || '')
  uni.reLaunch({ url: '/pages/jobs/index' })
}

const goRegister = () => {
  uni.navigateTo({ url: '/pages/register/index' })
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
}
.header {
  margin-top: 60px;
  margin-bottom: 40px;
  text-align: center;
  
  .title {
    font-size: 32px;
    font-weight: 600;
    color: #303133;
    display: block;
    margin-bottom: 12px;
  }
  
  .subtitle {
    font-size: 16px;
    color: #909399;
  }
}
.login-card {
  padding: 32px 24px;
  border-radius: 20px;
  background: #ffffff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.04);
}
.form-item {
  margin-bottom: 24px;
  
  .label {
    display: block;
    font-size: 14px;
    color: #606266;
    margin-bottom: 8px;
    font-weight: 500;
  }
  
  .input {
    height: 48px;
    background-color: #f5f7fa;
    border-radius: 12px;
    padding: 0 16px;
    font-size: 15px;
    color: #303133;
  }
  
  .placeholder-style {
    color: #c0c4cc;
  }
}
.login-btn {
  margin-top: 32px;
  background-color: #409eff;
  color: #ffffff;
  border-radius: 12px;
  height: 48px;
  line-height: 48px;
  font-size: 16px;
  font-weight: 500;
  border: none;
  
  &::after {
    border: none;
  }
  
  &:active {
    background-color: #3a8ee6;
  }
}
.register-btn {
  margin-top: 16px;
  background-color: transparent;
  color: #606266;
  font-size: 14px;
  border: none;
  
  &::after {
    border: none;
  }
}
</style>
