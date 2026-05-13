<template>
  <view class="page">
    <view class="top-wrap">
      <view class="top-row">
        <view class="brand">招贤宝</view>
      </view>
      <view class="search-box">
        <input v-model="q.keyword" class="search-input" placeholder="搜索职位、公司" />
        <button class="search-btn" @click="load">搜索</button>
        <button class="reset-btn" @click="reset">重置</button>
      </view>
    </view>

    <view class="section">
      <view class="cat-scroll">
        <view v-for="cat in displayCategories" :key="cat" class="cat-tag" @click="q.keyword = cat; load()">
          {{ cat }}
        </view>
      </view>
    </view>

    <view class="list-container">
      <view v-if="!records.length" class="empty">暂无相关职位</view>

      <view class="job-list">
        <view v-for="item in records" :key="item.id" class="job-card" @click="goDetail(item.id)">
          <view class="job-header">
            <view class="job-title">{{ item.title }}</view>
            <view class="job-salary">{{ item.salaryMin }}K - {{ item.salaryMax }}K</view>
          </view>
          <view class="job-tags">
            <text class="job-tag">{{ cityMap[item.cityCode] || item.cityCode }}</text>
            <text class="job-tag">{{ item.minExperienceYears || 0 }}年</text>
            <text class="job-tag">{{ educationMap[item.educationCode] || item.educationCode || '学历不限' }}</text>
          </view>
          <view class="job-company">
            <view class="company-name">燕鑫家政 · {{ jobTypeMap[item.jobTypeCode] || item.jobTypeCode }}</view>
            <view class="company-industry">{{ formatCodeList(item.requiredSkills, skillMap) || '综合技能' }}</view>
          </view>
          <view class="job-hr">
            <view class="hr-info">
              <view class="hr-avatar">{{ item.title.slice(0, 1) }}</view>
              <text class="hr-name">招聘专员 · 招聘者</text>
            </view>
            <view class="address">{{ cityMap[item.cityCode] || item.cityCode }}</view>
          </view>
        </view>
      </view>
    </view>

    <view class="pager">
      <button class="pager-btn" :disabled="q.pageNo <= 1" @click="prev">上一页</button>
      <text class="pager-text">{{ q.pageNo }} / {{ totalPage }}</text>
      <button class="pager-btn" :disabled="q.pageNo >= totalPage" @click="next">下一页</button>
    </view>

    <view class="tabbar">
      <view class="tab-item active">
        <view class="tab-icon">
          <image src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iIzQwOWVmZiIgc3Ryb2tlPSIjNDA5ZWZmIiBzdHJva2Utd2lkdGg9IjEuNSIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIiBzdHJva2UtbGluZWpvaW49InJvdW5kIj48cmVjdCB4PSIyIiB5PSI3IiB3aWR0aD0iMjAiIGhlaWdodD0iMTQiIHJ4PSIyIiByeT0iMiI+PC9yZWN0PjxwYXRoIGQ9Ik0xNiAyMVY1YTIgMiAwIDAgMC0yLTJoLTRhMiAyIDAgMCAwLTIgMnYxNiI+PC9wYXRoPjwvc3ZnPg==" style="width: 48rpx; height: 48rpx;" mode="aspectFit"></image>
        </view>
        <view class="tab-text">职位</view>
      </view>
      <view class="tab-item" @click="goMy">
        <view class="tab-icon">
          <image src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjOTk5OTk5IiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCI+PHBhdGggZD0iTTIwIDIxdi0yYTQgNCAwIDAgMC00LTRIOGE0IDQgMCAwIDAtNCA0djIiPjwvcGF0aD48Y2lyY2xlIGN4PSIxMiIgY3k9IjciIHI9IjQiPjwvY2lyY2xlPjwvc3ZnPg==" style="width: 48rpx; height: 48rpx;" mode="aspectFit"></image>
        </view>
        <view class="tab-text">我的</view>
      </view>
    </view>

  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { get } from '../../utils/request'
import { getToken } from '../../utils/auth'
import { cityMap, educationMap, formatCodeList, jobTypeMap, skillMap } from '../../utils/display'

const records = ref([])
const total = ref(0)
const q = reactive({
  pageNo: 1,
  pageSize: 10,
  keyword: '',
  cityCode: '',
  educationCode: '',
  skillCode: '',
  age: null,
  expYears: null
})

const totalPage = computed(() => Math.max(1, Math.ceil((total.value || 0) / q.pageSize)))
const isLogin = computed(() => Boolean(getToken()))
const cityCount = computed(() => {
  const set = new Set(records.value.map((i) => i.cityCode).filter(Boolean))
  return set.size || 0
})
const averageSalaryText = computed(() => {
  if (!records.value.length) return '--'
  const avg =
    records.value.reduce((sum, i) => {
      const min = Number(i.salaryMin || 0)
      const max = Number(i.salaryMax || 0)
      return sum + (min + max) / 2
    }, 0) / records.value.length
  return Math.round(avg).toString()
})
const displayCategories = computed(() => {
  const fromData = records.value
    .map((i) => jobTypeMap[i.jobTypeCode] || i.jobTypeCode)
    .filter(Boolean)
  const fallback = Object.values(jobTypeMap)
  return [...new Set([...fromData, ...fallback])].slice(0, 8)
})

const softRequireLogin = (actionText, next) => {
  if (isLogin.value) {
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

const load = async () => {
  const data = await get('/api/candidate/job/page', q)
  records.value = data.records || []
  total.value = data.total || 0
}

const reset = () => {
  q.keyword = ''
  q.cityCode = ''
  q.educationCode = ''
  q.skillCode = ''
  q.age = null
  q.expYears = null
  q.pageNo = 1
  load()
}

const prev = () => {
  if (q.pageNo <= 1) return
  q.pageNo -= 1
  load()
}

const next = () => {
  if (q.pageNo >= totalPage.value) return
  q.pageNo += 1
  load()
}

const goDetail = (jobId) => {
  uni.navigateTo({ url: `/pages/job-detail/index?id=${jobId}` })
}
const goMy = () => {
  uni.redirectTo({ url: '/pages/my/index' })
}

onShow(() => {
  load()
})
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f6f6f8;
  padding-bottom: 150rpx;
}
.top-wrap {
  padding: 20rpx 30rpx;
  background: #ffffff;
}
.brand {
  font-size: 44rpx;
  font-weight: 700;
  color: #409eff;
}
.search-box {
  display: flex;
  gap: 16rpx;
  align-items: center;
  margin-top: 10rpx;
}
.search-input {
  flex: 1;
  height: 68rpx;
  background: #f8f8f8;
  border-radius: 34rpx;
  padding: 0 30rpx;
  font-size: 26rpx;
  color: #333;
}
.search-btn {
  height: 68rpx;
  line-height: 68rpx;
  padding: 0 32rpx;
  border-radius: 34rpx;
  background: #409eff;
  color: #fff;
  font-size: 26rpx;
  font-weight: 500;
}
.reset-btn {
  height: 68rpx;
  line-height: 68rpx;
  padding: 0 24rpx;
  border-radius: 34rpx;
  background: #f8f8f8;
  color: #666;
  font-size: 26rpx;
}
.section {
  background: #ffffff;
  padding: 0 30rpx 20rpx;
  margin-bottom: 16rpx;
}
.cat-scroll {
  display: flex;
  gap: 16rpx;
  overflow-x: auto;
  white-space: nowrap;
}
.cat-tag {
  background: #f8f8f8;
  color: #666;
  font-size: 24rpx;
  padding: 10rpx 24rpx;
  border-radius: 24rpx;
  flex-shrink: 0;
}
.list-container {
  padding: 0 20rpx;
}
.job-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}
.job-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
}
.job-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}
.job-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #333;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.job-salary {
  font-size: 32rpx;
  font-weight: 700;
  color: #409eff;
  margin-left: 20rpx;
}
.job-tags {
  display: flex;
  gap: 12rpx;
  margin-bottom: 20rpx;
}
.job-tag {
  background: #f8f8f8;
  color: #666;
  font-size: 22rpx;
  padding: 6rpx 12rpx;
  border-radius: 6rpx;
}
.job-company {
  display: flex;
  gap: 16rpx;
  align-items: center;
  margin-bottom: 24rpx;
}
.company-name {
  font-size: 26rpx;
  color: #333;
}
.company-industry {
  font-size: 26rpx;
  color: #666;
}
.job-hr {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20rpx;
  border-top: 1rpx solid #f5f5f5;
}
.hr-info {
  display: flex;
  align-items: center;
  gap: 12rpx;
}
.hr-avatar {
  width: 44rpx;
  height: 44rpx;
  border-radius: 50%;
  background: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20rpx;
}
.hr-name {
  font-size: 24rpx;
  color: #333;
}
.address {
  font-size: 24rpx;
  color: #999;
}
.empty {
  text-align: center;
  color: #999;
  padding: 60rpx 0;
  font-size: 28rpx;
}
.pager {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
}
.pager-btn {
  width: 180rpx;
  height: 64rpx;
  line-height: 64rpx;
  background: #fff;
  border-radius: 32rpx;
  font-size: 26rpx;
  color: #333;
  border: 1px solid #eee;
}
.pager-btn[disabled] {
  color: #ccc;
  background: #fafafa;
}
.pager-text {
  font-size: 26rpx;
  color: #666;
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
