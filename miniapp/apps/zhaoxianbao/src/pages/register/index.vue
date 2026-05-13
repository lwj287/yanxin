<template>
  <view class="page">
    <view class="header">
      <view class="title">燕鑫招贤宝</view>
      <view class="subtitle">提升招聘效率，闭环管理入职流程</view>
    </view>
    <view class="card login-card">
      <view class="form-item">
        <text class="label">手机号</text>
        <input v-model="form.phone" class="input" placeholder="请输入手机号" placeholder-class="placeholder-style" />
      </view>
      <view class="form-item">
        <text class="label">密码</text>
        <input v-model="form.password" class="input" password placeholder="请输入登录密码" placeholder-class="placeholder-style" />
      </view>
      <view class="form-item">
        <text class="label">姓名</text>
        <input v-model="form.realName" class="input" placeholder="请输入姓名" placeholder-class="placeholder-style" />
      </view>
      <button class="login-btn" @click="register">提交注册</button>
      <button class="register-btn" @click="goLogin">返回登录</button>
    </view>
  </view>
</template>

<script setup>
import { reactive } from 'vue'
import { post } from '../../utils/request'

const form = reactive({
  password: '',
  realName: '',
  phone: ''
})

const register = async () => {
  if (!form.phone || !form.password || !form.realName) {
    uni.showToast({ title: '请完整填写注册信息', icon: 'none' })
    return
  }
  await post('/api/auth/register/candidate', { ...form, username: form.phone })
  uni.showToast({ title: '注册成功', icon: 'success' })
  setTimeout(() => uni.navigateBack(), 400)
}

const goLogin = () => uni.navigateBack()
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
