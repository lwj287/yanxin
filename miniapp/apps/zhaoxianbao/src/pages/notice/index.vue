<template>
  <view class="page">
    <view v-if="!list.length" class="empty-state">
      <view class="empty-text">暂无消息通知</view>
    </view>

    <view class="list-container">
      <view v-for="item in list" :key="item.id" class="notice-card">
        <view class="notice-header">
          <view class="notice-title">
            <text v-if="item.readFlag === 0" class="unread-dot"></text>
            {{ item.title }}
          </view>
          <view class="notice-time">{{ (item.createTime || '').slice(0, 16) }}</view>
        </view>
        <view class="notice-content">{{ item.content }}</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { get, post } from '../../utils/request'

const list = ref([])

const load = async () => {
  const notices = (await get('/api/candidate/notice')) || []
  list.value = notices
  
  const hasUnread = notices.some(n => n.readFlag === 0)
  if (hasUnread) {
    try {
      await post('/api/candidate/notice/mark-read')
    } catch (e) {
      console.error('标记已读失败', e)
    }
  }
}

onShow(() => {
  load()
})
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f6f6f8;
  padding-bottom: 40rpx;
}
.empty-state {
  padding: 100rpx 0;
  text-align: center;
}
.empty-text {
  color: #999;
  font-size: 28rpx;
}
.list-container {
  padding: 20rpx;
}
.notice-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
}
.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}
.notice-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  display: flex;
  align-items: center;
}
.unread-dot {
  width: 12rpx;
  height: 12rpx;
  background: #ff4d4f;
  border-radius: 50%;
  margin-right: 12rpx;
}
.notice-time {
  font-size: 24rpx;
  color: #999;
}
.notice-content {
  color: #666;
  font-size: 28rpx;
  line-height: 1.6;
}
</style>
