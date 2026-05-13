<template>
  <view class="profile-container">
    <view class="user-header" @click="!isLogin && goLogin()">
      <image class="avatar" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" mode="aspectFill" />
      <view class="info">
        <text class="name">{{ isLogin ? displayName : '点击登录/注册' }}</text>
        <text class="id" v-if="isLogin">手机号：{{ displayPhone }}</text>
        <text class="id" v-else>登录后体验更多求职服务</text>
      </view>
    </view>

    <view class="menu-list card">
      <view class="menu-item" @click="goOnlineResume">
        <text class="icon">📄</text>
        <text class="title">在线简历</text>
        <text class="menu-hint" style="margin-right: 8px;">管理个人优势与意向</text>
        <text class="arrow">›</text>
      </view>
      
      <view class="menu-item" @click="goResume">
        <text class="icon">📮</text>
        <text class="title">投递记录</text>
        <text class="arrow">›</text>
      </view>

      <view class="menu-item" @click="goNotice">
        <text class="icon">🔔</text>
        <text class="title">消息通知</text>
        <view v-if="unreadCount > 0" class="red-dot">{{ unreadCount > 99 ? '99+' : unreadCount }}</view>
        <text class="arrow">›</text>
      </view>
    </view>

    <button class="logout-btn" v-if="isLogin" @click="logout">退出登录</button>

    <view class="tabbar">
      <view class="tab-item" @click="goJobs">
        <view class="tab-icon">
          <image src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjOTk5OTk5IiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCI+PHJlY3QgeD0iMiIgeT0iNyIgd2lkdGg9IjIwIiBoZWlnaHQ9IjE0IiByeD0iMiIgcnk9IjIiPjwvcmVjdD48cGF0aCBkPSJNMTYgMjFWNWEyIDIgMCAwIDAtMi0yaC00YTIgMiAwIDAgMC0yIDJ2MTYiPjwvcGF0aD48L3N2Zz4=" style="width: 48rpx; height: 48rpx;" mode="aspectFit"></image>
        </view>
        <view class="tab-text">职位</view>
      </view>
      <view class="tab-item active">
        <view class="tab-icon">
          <image src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iIzQwOWVmZiIgc3Ryb2tlPSIjNDA5ZWZmIiBzdHJva2Utd2lkdGg9IjEuNSIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIiBzdHJva2UtbGluZWpvaW49InJvdW5kIj48cGF0aCBkPSJNMjAgMjF2LTJhNCA0IDAgMCAwLTQtNEg4YTQgNCAwIDAgMC00IDR2MiI+PC9wYXRoPjxjaXJjbGUgY3g9IjEyIiBjeT0iNyIgcj0iNCI+PC9jaXJjbGU+PC9zdmc+" style="width: 48rpx; height: 48rpx;" mode="aspectFit"></image>
        </view>
        <view class="tab-text">我的</view>
      </view>
    </view>

  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { clearToken, getToken } from '../../utils/auth'
import { get } from '../../utils/request'

const isLogin = computed(() => Boolean(getToken()))
const displayName = ref('已登录用户')
const displayPhone = ref('已登录')

const unreadCount = ref(0)

const loadUnreadCount = async () => {
  if (!isLogin.value) {
    unreadCount.value = 0
    return
  }
  try {
    const res = await get('/api/candidate/notice/unread-count')
    unreadCount.value = res || 0
  } catch (e) {
    console.error('获取未读消息数失败', e)
  }
}

const maskPhone = (phone) => {
  if (!phone || phone.length < 7) return phone || '未绑定手机号'
  return `${phone.slice(0, 3)}****${phone.slice(-4)}`
}

const syncProfile = () => {
  if (!isLogin.value) return
  const realName = uni.getStorageSync('realName') || ''
  const phone = uni.getStorageSync('loginPhone') || ''
  displayName.value = realName || '已登录用户'
  displayPhone.value = phone ? maskPhone(phone) : '已登录'
}

const requireLogin = (actionText, next) => {
  if (isLogin.value) {
    next()
    return
  }
  uni.showModal({
    title: '提示',
    content: `${actionText}需要先登录，是否前往登录？`,
    confirmText: '去登录',
    cancelText: '取消',
    success: ({ confirm }) => {
      if (confirm) {
        goLogin()
      }
    }
  })
}

const goLogin = () => uni.navigateTo({ url: '/pages/login/index' })
const goResume = () =>
  requireLogin('查看我的投递', () => {
    uni.navigateTo({ url: '/pages/my-resume/index' })
  })
const goOnlineResume = () =>
  requireLogin('管理在线简历', () => {
    uni.navigateTo({ url: '/pages/online-resume/index' })
  })
const goNotice = () =>
  requireLogin('查看消息通知', () => {
    uni.navigateTo({ url: '/pages/notice/index' })
  })
const goJobs = () => {
  uni.redirectTo({ url: '/pages/jobs/index' })
}

const logout = () => {
  clearToken()
  uni.removeStorageSync('realName')
  uni.removeStorageSync('loginPhone')
  uni.showToast({ title: '已退出登录', icon: 'none' })
  setTimeout(() => {
    uni.reLaunch({ url: '/pages/jobs/index' })
  }, 200)
}

onShow(() => {
  syncProfile()
  loadUnreadCount()
})
</script>

<style scoped lang="scss">
.profile-container {
  padding: 20px 16px;
  background-color: #f5f7fa;
  min-height: 100vh;
  padding-bottom: 150rpx;
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

.card {
  background: #ffffff;
  border-radius: 16px;
  padding: 0 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.menu-list {
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
    
    .menu-hint {
      font-size: 13px;
      color: #909399;
    }
  }
}

.red-dot {
  background: #ff4d4f;
  color: #fff;
  font-size: 10px;
  height: 16px;
  min-width: 16px;
  line-height: 16px;
  text-align: center;
  border-radius: 8px;
  padding: 0 4px;
  font-weight: bold;
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
