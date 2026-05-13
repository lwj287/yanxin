<template>
  <view class="page">
    <view v-for="item in list" :key="item.id" class="card">
      <view class="title">入职进度</view>
      <view class="line"><text class="label">简历编号</text>{{ item.resumeId }}</view>
      <view class="line"><text class="label">当前阶段</text>{{ onboardStatusMap[item.onboardStatus] || item.onboardStatus }}</view>
      <view class="line"><text class="label">材料核验</text>{{ yesNo(item.docsVerified) }}</view>
      <view class="line"><text class="label">入职日期</text>{{ item.onboardingDate || '-' }}</view>
      <view class="line"><text class="label">合同编号</text>{{ item.contractNo || '-' }}</view>
    </view>
    <view v-if="!list.length" class="empty card">暂时没有入职进度信息</view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { get } from '../../utils/request'
import { onboardStatusMap, yesNo } from '../../utils/display'

const list = ref([])

const load = async () => {
  list.value = (await get('/api/candidate/onboarding/progress')) || []
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
