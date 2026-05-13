<template>
  <view class="page">
    <view v-if="!list.length" class="empty-state">
      <view class="empty-text">暂无投递记录</view>
    </view>

    <view class="list-container">
      <view v-for="item in list" :key="item.resumeId" class="job-card">
        <view class="job-header">
          <view class="job-title">{{ item.jobTitle || `岗位#${item.jobId || '-'}` }}</view>
          <view :class="['status-tag', getStatusClass(item.resumeStatus)]">
            {{ resumeStatusText(item.resumeStatus) }}
          </view>
        </view>
        
        <view class="meta-row">
          <text>城市：{{ cityMap[item.cityCode] || item.cityCode || '-' }}</text>
          <text>类型：{{ jobTypeMap[item.jobTypeCode] || item.jobTypeCode || '-' }}</text>
        </view>
        
        <view class="meta-row">
          <text class="salary">¥{{ item.salaryMin || 0 }} - ¥{{ item.salaryMax || 0 }}</text>
          <text>筛选：{{ screeningText(item.screeningResult) }}</text>
        </view>
        
        <view v-if="item.screeningReason" class="reason-box">
          筛选说明：{{ item.screeningReason }}
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { get } from '../../utils/request'
import { cityMap, jobTypeMap } from '../../utils/display'

const list = ref([])

const resumeStatusText = (v) => {
  if (v === 1) return '已投递'
  if (v === 2) return '初筛已通过'
  if (v === 3) return '初筛未通过'
  if (v === 4) return '简历已通过'
  if (v === 5) return '简历未通过'
  if (v === 6) return '面试已通过'
  if (v === 7) return '面试未通过'
  if (v === 8) return '已入职'
  return '未知状态'
}

const getStatusClass = (v) => {
  if (v === 1) return 'status-info' // 已投递
  if ([2, 4, 6, 8].includes(v)) return 'status-success' // 通过类
  if ([3, 5, 7].includes(v)) return 'status-warning' // 拒绝类改为黄色
  return 'status-default'
}

const screeningText = (v) => {
  if (v === 0) return '待筛选'
  if (v === 1) return '合格'
  if (v === 2) return '不合格'
  return '-'
}

const load = async () => {
  list.value = (await get('/api/candidate/resume/my')) || []
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
.job-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
}
.job-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}
.job-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}
.status-tag {
  font-size: 24rpx;
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
}
.status-info {
  color: #1890ff;
  background: #e6f7ff;
}
.status-success {
  color: #409eff;
  background: #ecf5ff;
}
.status-warning {
  color: #fa8c16;
  background: #fff7e6;
}
.status-danger {
  color: #ff4d4f;
  background: #fff1f0;
}
.status-default {
  color: #666;
  background: #f5f5f5;
}
.meta-row {
  display: flex;
  justify-content: space-between;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 12rpx;
}
.salary {
  color: #409eff;
  font-weight: 500;
}
.reason-box {
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx dashed #f0f0f0;
  font-size: 24rpx;
  color: #999;
}
</style>
