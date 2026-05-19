<template>
  <view class="profile-container">
    <view class="user-header" @click="handleHeaderClick">
      <image class="avatar" :src="isLogin && userInfo.avatar ? userInfo.avatar : 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" mode="aspectFill" />
      <view class="info" v-if="isLogin">
        <text class="name">{{ userInfo.memberName || userInfo.username || '微信用户' }}</text>
        <text class="id">会员等级：{{ formatLevel(userInfo.memberLevel) }} | 手机：{{ userInfo.phone || '未绑定' }}</text>
      </view>
      <view class="info" v-else>
        <text class="name">点击登录</text>
        <text class="id">登录后享受更多优惠</text>
      </view>
    </view>

    <view class="points-card card" v-if="isLogin" @click="goTo('/pages/points/index')">
      <view class="points-header">
        <text class="points-title">当前积分</text>
        <text class="points-value">{{ userInfo.accountBalance || 0 }}</text>
      </view>
    </view>

    <view class="menu-list card">
      <view class="menu-item" @click="goTo('/pages/coupon/index')">
        <text class="icon">🎟️</text>
        <text class="title">我的卡券</text>
        <text class="arrow">›</text>
      </view>
      <view class="menu-item" @click="goTo('/pages/order/index')">
        <text class="icon">📋</text>
        <text class="title">我的订单</text>
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
      // 通过 token 获取当前登录会员信息
      const res: any = await getUserInfo();
      if (res) {
        userInfo.value = res;
      } else {
        userInfo.value = { memberName: '演示用户', memberLevel: 0, accountBalance: 100 };
      }
    } catch (e) {
      console.error(e);
      userInfo.value = { memberName: '演示用户', memberLevel: 0, accountBalance: 100 };
    }
  } else {
    userInfo.value = {};
  }
});

const formatLevel = (level: number) => {
  const map: Record<number, string> = { 0: '普通会员', 1: '高级会员' };
  return map[level] || '普通会员';
};

const handleHeaderClick = () => {
  if (!isLogin.value) {
    uni.navigateTo({ url: '/pages/login/index' });
  }
};

const goTo = (url: string) => {
  if (!isLogin.value) {
    uni.navigateTo({ url: '/pages/login/index' });
    return;
  }
  uni.navigateTo({ url });
};

const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        uni.removeStorageSync('token');
        userInfo.value = {};
        uni.reLaunch({ url: '/pages/index/index' });
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
}

.user-header {
  display: flex;
  align-items: center;
  padding: 24px 16px;
  background: #fff;
  border-radius: 20px;
  margin-bottom: 16px;
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

.points-card {
  padding: 20px;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #fdfbfb 0%, #ebedee 100%);
  .points-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .points-title {
      font-size: 16px;
      color: #606266;
    }
    .points-value {
      font-size: 28px;
      font-weight: bold;
      color: #e6a23c;
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
</style>
