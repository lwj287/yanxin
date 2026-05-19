<template>
  <view class="login-container">
    <view class="nav-bar">
      <view class="back-btn" @click="handleBack">
        <text class="icon">‹</text>
        <text>返回</text>
      </view>
    </view>
    <view class="header">
      <text class="title">燕鑫智派单</text>
      <text class="subtitle">家政服务接单与日程管理平台</text>
    </view>
    
    <view class="card login-card">
      <view class="form-item">
        <text class="label">手机号</text>
        <input class="input" type="text" v-model="form.username" placeholder="请输入绑定的手机号" placeholder-class="placeholder-style" />
      </view>
      <view class="form-item">
        <text class="label">验证码</text>
        <view class="code-input-wrap">
          <input class="input code-input" type="text" v-model="form.password" placeholder="请任意输入" placeholder-class="placeholder-style" />
          <text class="get-code-text" @click="handleGetCode">{{ codeText }}</text>
        </view>
      </view>
      <button class="login-btn" @click="handleLogin" :disabled="loading">
        {{ loading ? '登录中...' : '登录' }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { login } from '@/api/index';

const form = ref({
  username: '',
  password: ''
});
const loading = ref(false);
const codeText = ref('获取验证码');
const isCounting = ref(false);

const handleGetCode = () => {
  if (isCounting.value) return;
  if (!form.value.username) {
    uni.showToast({ title: '请先输入手机号', icon: 'none' });
    return;
  }
  
  // 模拟发送验证码
  uni.showToast({ title: '验证码 123456 已发送', icon: 'none' });
  form.value.password = '123456'; // 自动帮用户填上
  
  isCounting.value = true;
  let count = 60;
  codeText.value = `${count}s后重发`;
  
  const timer = setInterval(() => {
    count--;
    if (count > 0) {
      codeText.value = `${count}s后重发`;
    } else {
      clearInterval(timer);
      codeText.value = '获取验证码';
      isCounting.value = false;
    }
  }, 1000);
};

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    uni.showToast({ title: '请输入手机号和验证码', icon: 'none' });
    return;
  }
  loading.value = true;
  try {
    const staffInfo = await login(form.value.username);
    // 登录成功后，应当保存员工真实的 staffId，而不是主键 id
    uni.setStorageSync('token', staffInfo.staffId); 
    uni.setStorageSync('staffInfo', staffInfo);
    
    uni.showToast({ title: '登录成功', icon: 'success' });
    setTimeout(() => {
      uni.switchTab({ url: '/pages/order/index' });
    }, 1000);
  } catch (error: any) {
    uni.showToast({ title: error.message || '登录失败', icon: 'none' });
  } finally {
    loading.value = false;
  }
};

const handleBack = () => {
  const pages = getCurrentPages();
  if (pages.length > 1) {
    uni.navigateBack({ delta: 1 });
  } else {
    uni.switchTab({ url: '/pages/order/index' });
  }
};
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
}

.nav-bar {
  position: absolute;
  top: 40px; // 适配状态栏高度
  left: 20px;
  z-index: 10;
  
  .back-btn {
    display: flex;
    align-items: center;
    color: #606266;
    font-size: 16px;
    padding: 8px 12px;
    border-radius: 20px;
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(4px);
    
    .icon {
      font-size: 24px;
      margin-right: 4px;
      margin-top: -2px;
    }
  }
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
  border-radius: 20px; // 符合极简风格和大圆角偏好
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
  
  .code-input-wrap {
    position: relative;
    display: flex;
    align-items: center;
    
    .code-input {
      flex: 1;
      padding-right: 100px;
    }
    
    .get-code-text {
      position: absolute;
      right: 16px;
      font-size: 14px;
      color: #409EFF;
      z-index: 10;
      padding: 10px 0;
      
      &:active {
        opacity: 0.7;
      }
    }
  }
  
  .placeholder-style {
    color: #c0c4cc;
  }
}

.login-btn {
  margin-top: 32px;
  background-color: #409EFF;
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
  
  &[disabled] {
    background-color: #a0cfff;
    color: #ffffff;
  }
}
</style>