<template>
  <view class="login-container">
    <view class="nav-bar">
      <view class="back-btn" @click="handleBack">
        <text class="icon">‹</text>
        <text>返回</text>
      </view>
    </view>
    <view class="header">
      <text class="title">燕鑫客盈门</text>
      <text class="subtitle">会员营销与裂变工具</text>
    </view>
    
    <view class="card login-card">
      <view class="form-item">
        <text class="label">手机号</text>
        <input class="input" type="number" v-model="form.phone" placeholder="请输入手机号" placeholder-class="placeholder-style" />
      </view>
      <view class="form-item code-item">
        <text class="label">验证码</text>
        <view class="code-input-wrap">
          <input class="input" type="number" v-model="form.code" placeholder="请输入验证码" placeholder-class="placeholder-style" />
          <button class="send-btn" :disabled="countdown > 0" @click="handleSendSms">
            {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
          </button>
        </view>
      </view>
      <button class="login-btn" @click="handleLogin" :disabled="loading">
        {{ loading ? '登录中...' : '登录 / 注册' }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onUnmounted } from 'vue';
import { login, sendSms } from '@/api/index';

const form = ref({
  phone: '',
  code: ''
});
const loading = ref(false);
const countdown = ref(0);
let timer: number | null = null;

const handleSendSms = async () => {
  if (!form.value.phone || form.value.phone.length !== 11) {
    uni.showToast({ title: '请输入正确的手机号', icon: 'none' });
    return;
  }
  
  try {
    const res: any = await sendSms({ phone: form.value.phone });
    uni.showToast({ title: `发送成功(测试验证码:${res})`, icon: 'none', duration: 3000 });
    
    // 开始倒计时
    countdown.value = 60;
    timer = setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0) {
        clearInterval(timer as number);
      }
    }, 1000);
  } catch (error) {
    // 错误在 request 拦截器中已提示
  }
};

onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
  }
});

const handleLogin = async () => {
  if (!form.value.phone) {
    uni.showToast({ title: '请输入手机号', icon: 'none' });
    return;
  }
  if (!form.value.code) {
    uni.showToast({ title: '请输入验证码', icon: 'none' });
    return;
  }
  loading.value = true;
  try {
    const res: any = await login(form.value);
    uni.setStorageSync('token', res); 
    uni.showToast({ title: '登录成功', icon: 'success' });
    setTimeout(() => {
      uni.reLaunch({ url: '/pages/index/index' });
    }, 1000);
  } catch (error) {
    // 错误在request里已提示
  } finally {
    loading.value = false;
  }
};

const handleBack = () => {
  const pages = getCurrentPages();
  if (pages.length > 1) {
    uni.navigateBack({ delta: 1 });
  } else {
    uni.switchTab({ url: '/pages/index/index' });
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
  
  &.code-item {
    .code-input-wrap {
      display: flex;
      align-items: center;
      .input {
        flex: 1;
      }
      .send-btn {
        margin-left: 12px;
        width: 100px;
        height: 44px;
        line-height: 44px;
        font-size: 14px;
        background: #f0f9eb;
        color: #67c23a;
        border-radius: 8px;
        padding: 0;
        &::after {
          border: none;
        }
        &[disabled] {
          background: #f5f7fa;
          color: #c0c4cc;
        }
      }
    }
  }

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