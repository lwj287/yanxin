<template>
  <view class="profile-container">
    <view class="user-header" @click="handleHeaderClick">
      <image class="avatar" :src="isLogin && userInfo.avatar ? userInfo.avatar : 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" mode="aspectFill" />
      <view class="info" v-if="isLogin">
        <text class="name">{{ userInfo.realName || userInfo.username || '家政员工' }}</text>
        <text class="id">工号：{{ userInfo.id || '10001' }} | 手机：{{ userInfo.phone || '未绑定' }}</text>
      </view>
      <view class="info" v-else>
        <text class="name">点击登录</text>
        <text class="id">登录后享受更多服务</text>
      </view>
    </view>

    <view class="menu-list card">
      <view class="menu-item" @click="goTo('/pages/cert/index')">
        <text class="icon">🏆</text>
        <text class="title">我的认证</text>
        <text class="arrow">›</text>
      </view>
      <view class="menu-item" @click="goTo('/pages/record/index')">
        <text class="icon">📖</text>
        <text class="title">学习记录</text>
        <text class="arrow">›</text>
      </view>
      <view class="menu-item" @click="goTo('/pages/score/index')">
        <text class="icon">📘</text>
        <text class="title">考试成绩</text>
        <text class="arrow">›</text>
      </view>
    </view>

    <button class="logout-btn" v-if="isLogin" @click="handleLogout">退出登录</button>

  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { getUserInfo } from '@/api/index';

const userInfo = ref<any>({});
const isLogin = computed(() => !!uni.getStorageSync('token'));

onShow(async () => {
  if (isLogin.value) {
    try {
      const res = await getUserInfo();
      userInfo.value = res || {};
    } catch (e) {
      console.error(e);
    }
  } else {
    userInfo.value = {};
  }
});

const handleHeaderClick = () => {
  if (!isLogin.value) {
    uni.navigateTo({ url: '/pages/login/index' });
  }
};

const goCourse = () => {
  uni.redirectTo({ url: '/pages/course/index' });
};

const goTo = (url: string) => {
  if (!isLogin.value) {
    uni.navigateTo({ url: '/pages/login/index' });
    return;
  }
  uni.navigateTo({ url });
};

const showToast = (title: string) => {
  if (!isLogin.value) {
    uni.navigateTo({ url: '/pages/login/index' });
    return;
  }
  uni.showToast({ title, icon: 'none' });
};

const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        uni.removeStorageSync('token');
        userInfo.value = {};
        uni.reLaunch({ url: '/pages/course/index' });
      }
    }
  });
};
</script>

<style lang="scss" scoped>
.profile-container {
  padding: 20px 16px;
  background-color: #f5f7fa;
  min-height: 100vh;
  padding-bottom: calc(100rpx + env(safe-area-inset-bottom) + 20px);
}

.user-header {
  display: flex;
  align-items: center;
  padding: 24px 16px;
  background: #fff;
  border-radius: 20px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);

  .avatar {
    width: 64px;
    height: 64px;
    border-radius: 32px;
    background-color: #e4e7ed;
    margin-right: 16px;
  }

  .info {
    display: flex;
    flex-direction: column;

    .name {
      font-size: 20px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 4px;
    }

    .id {
      font-size: 13px;
      color: #909399;
    }
  }
}

.menu-list {
  padding: 0 16px;
  margin-bottom: 32px;

  .menu-item {
    display: flex;
    align-items: center;
    padding: 16px 0;
    border-bottom: 1px solid #ebeef5;

    &:last-child {
      border-bottom: none;
    }

    .icon {
      font-size: 20px;
      margin-right: 12px;
    }

    .title {
      flex: 1;
      font-size: 15px;
      color: #303133;
    }

    .arrow {
      font-size: 24px;
      color: #c0c4cc;
      margin-left: 8px;
    }
  }
}

.logout-btn {
  background-color: #fff;
  color: #F56C6C;
  font-size: 16px;
  height: 48px;
  line-height: 48px;
  border-radius: 12px;
  border: none;

  &::after {
    border: none;
  }
}

.tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 100rpx;
  background: #fff;
  border-top: 1rpx solid #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 0 100rpx;
  padding-bottom: env(safe-area-inset-bottom);
  z-index: 99;
}
.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #999;
}
.tab-item.active {
  color: #409eff;
}
.tab-icon {
  margin-bottom: 4rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
.tab-text {
  font-size: 22rpx;
  font-weight: 500;
  margin-top: 4rpx;
}
</style>