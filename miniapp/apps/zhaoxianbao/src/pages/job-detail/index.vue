<template>
  <view class="page">
    <view class="job-header-card">
      <view class="title-row">
        <view class="title">{{ job.title }}</view>
        <view class="salary">{{ job.salaryMin }}K - {{ job.salaryMax }}K</view>
      </view>
      <view class="meta-row">
        <view class="meta-item">
          <text class="icon">📍</text>
          <text>{{ cityMap[job.cityCode] || job.cityCode }}</text>
        </view>
        <view class="meta-item">
          <text class="icon">💼</text>
          <text>{{ job.minExperienceYears || 0 }}年</text>
        </view>
        <view class="meta-item">
          <text class="icon">🎓</text>
          <text>{{ educationMap[job.educationCode] || job.educationCode || '学历不限' }}</text>
        </view>
      </view>
    </view>

    <view class="job-company-card">
      <view class="company-avatar">{{ (job.title || '').slice(0, 1) }}</view>
      <view class="company-info">
        <view class="company-name">燕鑫家政 · {{ jobTypeMap[job.jobTypeCode] || job.jobTypeCode }}</view>
        <view class="company-tags">100-499人 | 家政服务</view>
      </view>
    </view>

    <view class="job-detail-card">
      <view class="section-title">职位描述</view>
      <view class="tags-container">
        <view class="tag" v-for="skill in skillsList" :key="skill">{{ skill }}</view>
      </view>
      <view class="desc-content">{{ job.description || '暂无职位描述' }}</view>
    </view>

    <view class="bottom-bar">
      <button class="apply-btn" @click="handleApply">投递在线简历</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { get, post } from '../../utils/request'
import { getToken } from '../../utils/auth'
import { cityMap, educationMap, jobTypeMap, skillMap } from '../../utils/display'

const job = ref({})
const jobId = ref(null)

const skillsList = computed(() => {
  if (!job.value.requiredSkills) return []
  return job.value.requiredSkills.split(',').map(code => skillMap[code] || code)
})

const loadJob = async () => {
  try {
    const data = await get(`/api/candidate/job/${jobId.value}`)
    if (data) {
      job.value = data
    }
  } catch (err) {
    uni.showToast({ title: '获取职位信息失败', icon: 'none' })
  }
}

onLoad((options) => {
  if (options.id) {
    jobId.value = options.id
    loadJob()
  }
})

const softRequireLogin = (actionText, next) => {
  if (getToken()) {
    next()
    return
  }
  uni.showModal({
    title: '提示',
    content: `${actionText}需要先登录，是否前往登录？`,
    confirmText: '去登录',
    cancelText: '先逛逛',
    success: ({ confirm }) => {
      if (confirm) {
        uni.navigateTo({ url: '/pages/login/index' })
      }
    }
  })
}

const handleApply = () => {
  softRequireLogin('投递简历', async () => {
    uni.showModal({
      title: '投递确认',
      content: `是否确认投递【${job.value.title}】？`,
      success: async ({ confirm }) => {
        if (confirm) {
          checkAndSubmit()
        }
      }
    })
  })
}

const checkAndSubmit = async () => {
  try {
    uni.showLoading({ title: '处理中' })
    const profile = await get('/api/candidate/profile/my')
    if (!profile || !profile.realName || !profile.phone) {
      uni.hideLoading()
      uni.showModal({
        title: '提示',
        content: '您还没有在线简历或信息不完整，请先前往新增完善。',
        confirmText: '去完善',
        cancelText: '取消',
        success: ({ confirm }) => {
          if (confirm) {
            uni.navigateTo({ url: '/pages/online-resume/index' })
          }
        }
      })
      return
    }

    await post('/api/candidate/resume/submit', { jobId: jobId.value })
    uni.hideLoading()
    uni.showToast({ title: '投递成功', icon: 'success' })
  } catch (err) {
    uni.hideLoading()
    // The interceptor might have already shown a toast, but we can handle it if needed
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f6f6f8;
  padding-bottom: 140rpx;
}
.job-header-card {
  background: #ffffff;
  padding: 40rpx 30rpx;
  margin-bottom: 16rpx;
}
.title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24rpx;
}
.title {
  font-size: 44rpx;
  font-weight: bold;
  color: #333;
  flex: 1;
  margin-right: 20rpx;
}
.salary {
  font-size: 36rpx;
  font-weight: bold;
  color: #409eff;
  white-space: nowrap;
}
.meta-row {
  display: flex;
  gap: 30rpx;
}
.meta-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 26rpx;
  color: #666;
}
.icon {
  font-size: 24rpx;
}

.job-company-card {
  background: #ffffff;
  padding: 30rpx;
  margin-bottom: 16rpx;
  display: flex;
  align-items: center;
  gap: 24rpx;
}
.company-avatar {
  width: 80rpx;
  height: 80rpx;
  background: #409eff;
  color: #fff;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  font-weight: bold;
}
.company-info {
  flex: 1;
}
.company-name {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 8rpx;
}
.company-tags {
  font-size: 24rpx;
  color: #999;
}

.job-detail-card {
  background: #ffffff;
  padding: 40rpx 30rpx;
  min-height: 400rpx;
}
.section-title {
  font-size: 34rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 24rpx;
}
.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-bottom: 30rpx;
}
.tag {
  background: #f8f8f8;
  color: #666;
  padding: 8rpx 20rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
}
.desc-content {
  font-size: 28rpx;
  color: #333;
  line-height: 1.8;
  white-space: pre-wrap;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 120rpx;
  background: #ffffff;
  border-top: 1rpx solid #f5f5f5;
  display: flex;
  align-items: center;
  padding: 0 40rpx;
  padding-bottom: env(safe-area-inset-bottom);
  z-index: 100;
}
.apply-btn {
  flex: 1;
  height: 80rpx;
  background: #409eff;
  color: #fff;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  font-weight: bold;
}
</style>