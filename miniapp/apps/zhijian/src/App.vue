<template>
  <view class="app">
  </view>
</template>

<script setup lang="ts">
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'

onLaunch(() => {
  console.log('App Launch')
})

onShow(() => {
  console.log('App Show')
})

onHide(() => {
  console.log('App Hide')
})
</script>

<style>
/* 全局样式 */
page {
  background-color: #f5f7fa;
  font-family: -apple-system, BlinkMacSystemFont, "Helvetica Neue", Helvetica, Segoe UI, Arial, Roboto, "PingFang SC", "miui", "Hiragino Sans GB", "Microsoft Yahei", sans-serif;
}

.card {
  background: #ffffff;
  border-radius: 40rpx;
  padding: 40rpx 32rpx;
  box-shadow: 0 16rpx 48rpx rgba(0, 0, 0, 0.04);
  margin-bottom: 32rpx;
}
</style>