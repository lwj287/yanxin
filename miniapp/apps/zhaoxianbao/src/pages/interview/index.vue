<template>
  <view class="page">
    <view v-for="item in list" :key="item.id" class="card">
      <view class="title">面试安排</view>
      <view class="line"><text class="label">简历编号</text>{{ item.resumeId }}</view>
      <view class="line"><text class="label">面试时间</text>{{ item.interviewTime || '-' }}</view>
      <view class="line"><text class="label">面试地点</text>{{ item.interviewLocation || '-' }}</view>
      <view class="line"><text class="label">当前状态</text>{{ interviewStatusMap[item.interviewStatus] || item.interviewStatus }}</view>
    </view>
    <view v-if="!list.length" class="empty card">暂时没有面试通知</view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { get } from '../../utils/request'
import { interviewStatusMap } from '../../utils/display'

const list = ref([])

const load = async () => {
  list.value = (await get('/api/candidate/interview/notice')) || []
}

onShow(() => {
  load()
})
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f6f6f8;
  padding: 24rpx 20rpx 30rpx;
}
.card {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
}
.title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}
.line {
  line-height: 1.95;
  color: #333;
  font-size: 28rpx;
}
.label {
  display: inline-block;
  width: 140rpx;
  color: #666;
}
.empty {
  margin-top: 100rpx;
  padding: 30rpx 24rpx;
  text-align: center;
  color: #999;
  background: transparent;
  box-shadow: none;
  border: none;
}
</style>
