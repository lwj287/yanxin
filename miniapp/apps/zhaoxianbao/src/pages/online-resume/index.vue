<template>
  <view class="page">
    <view v-if="!hasProfile" class="empty-state">
      <view class="empty-icon">📄</view>
      <view class="empty-title">创建你的在线简历</view>
      <view class="empty-desc">完善简历，让好工作主动找你</view>
      <button class="btn btn-primary" @click="openCreate">立即创建</button>
    </view>

    <view v-else class="resume-view">
      <view class="resume-header">
        <view class="resume-main-info">
          <view class="name">{{ profile.realName || '未填写姓名' }}</view>
          <view class="edit-icon" @click="openEdit">编辑</view>
        </view>
        <view class="resume-sub-info">
          <text>{{ profile.experienceYears ? profile.experienceYears + '年' : '经验不限' }}</text>
          <text class="divider">|</text>
          <text>{{ profile.age ? profile.age + '岁' : '年龄不限' }}</text>
          <text class="divider">|</text>
          <text>{{ profile.educationText || '学历不限' }}</text>
        </view>
      </view>

      <view class="resume-section">
        <view class="section-title">求职意向</view>
        <view class="intention-item">
          <view class="intention-label">期望城市</view>
          <view class="intention-val">{{ profile.expectedCityText || '未填写' }}</view>
        </view>
      </view>

      <view class="resume-section">
        <view class="section-title">个人优势</view>
        <view class="skills-list" v-if="profile.skillsText">
          <text class="skill-tag" v-for="skill in (profile.skillsText.split(/[、,]/))" :key="skill">{{ skill }}</text>
        </view>
        <view class="text-content" v-else>未填写技能特长</view>
        
        <view class="text-content mt-10">{{ profile.selfIntroduction || '未填写个人介绍' }}</view>
      </view>

      <view class="resume-section">
        <view class="section-title">联系方式</view>
        <view class="contact-item">
          <text class="contact-label">手机号</text>
          <text class="contact-val">{{ profile.phone || '-' }}</text>
        </view>
      </view>

      <view class="bottom-action">
        <button class="btn btn-outline" @click="removeProfile">删除简历</button>
      </view>
    </view>

    <!-- 弹窗表单 - 全屏显示 -->
    <view v-if="showForm" class="form-modal">
      <view class="form-header">
        <view class="form-title">{{ hasProfile ? '编辑在线简历' : '创建在线简历' }}</view>
        <view class="form-close" @click="cancelEdit">✕</view>
      </view>
      
      <scroll-view scroll-y class="form-scroll">
        <view class="form-group">
          <view class="form-label">基本信息</view>
          <view class="form-item">
            <text class="item-label">姓名</text>
            <input v-model="form.realName" class="item-input" placeholder="请输入姓名" placeholder-class="ph-color" />
          </view>
          <view class="form-item">
            <text class="item-label">手机号</text>
            <input v-model="form.phone" class="item-input" placeholder="请输入手机号" placeholder-class="ph-color" />
          </view>
          <view class="form-item">
            <text class="item-label">年龄</text>
            <input v-model="form.age" class="item-input" type="number" placeholder="请输入年龄" placeholder-class="ph-color" />
          </view>
          <view class="form-item">
            <text class="item-label">工作经验(年)</text>
            <input v-model="form.experienceYears" class="item-input" type="number" placeholder="请输入年限" placeholder-class="ph-color" />
          </view>
          <view class="form-item">
            <text class="item-label">最高学历</text>
            <picker :range="educationOptions" @change="onEducationChange" class="item-picker">
              <view :class="['picker-text', !form.educationText && 'ph-color']">{{ form.educationText || '请选择学历' }}</view>
            </picker>
          </view>
          <view class="form-item">
            <text class="item-label">期望城市</text>
            <picker :range="cityOptions" @change="onCityChange" class="item-picker">
              <view :class="['picker-text', !form.expectedCityText && 'ph-color']">{{ form.expectedCityText || '请选择城市' }}</view>
            </picker>
          </view>
        </view>

        <view class="form-group">
          <view class="form-label">个人优势</view>
          <view class="skills-selector">
            <view
              v-for="name in skillOptions"
              :key="name"
              :class="['skill-choice', selectedSkills.includes(name) && 'active']"
              @click="toggleSkill(name)"
            >
              {{ name }}
            </view>
          </view>
          <textarea v-model="form.selfIntroduction" class="intro-area" placeholder="请简单介绍一下自己，例如：工作认真负责，有耐心..." placeholder-class="ph-color" :maxlength="500"></textarea>
        </view>

        <view class="form-group">
          <view class="form-label">附件信息 (选填)</view>
          <view class="upload-grid">
            <view class="upload-box" @click="uploadImage('photoUrl')">
              <image v-if="form.photoUrl" :src="form.photoUrl" mode="aspectFill" class="upload-img"></image>
              <view v-else class="upload-placeholder">
                <text class="plus">+</text>
                <text class="up-text">个人照片</text>
              </view>
            </view>
            <view class="upload-box" @click="uploadImage('idCardFrontUrl')">
              <image v-if="form.idCardFrontUrl" :src="form.idCardFrontUrl" mode="aspectFill" class="upload-img"></image>
              <view v-else class="upload-placeholder">
                <text class="plus">+</text>
                <text class="up-text">身份证人像</text>
              </view>
            </view>
            <view class="upload-box" @click="uploadImage('idCardBackUrl')">
              <image v-if="form.idCardBackUrl" :src="form.idCardBackUrl" mode="aspectFill" class="upload-img"></image>
              <view v-else class="upload-placeholder">
                <text class="plus">+</text>
                <text class="up-text">身份证国徽</text>
              </view>
            </view>
          </view>
        </view>
        <view class="form-bottom-pad"></view>
      </scroll-view>
      
      <view class="form-footer">
        <button class="btn btn-primary" @click="save">保存</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { del, get, post } from '../../utils/request'
import { getToken } from '../../utils/auth'

const profile = ref(null)
const showForm = ref(false)
const selectedSkills = ref([])

const educationOptions = ['初中', '高中', '中专', '大专', '本科', '硕士及以上']
const cityOptions = ['深圳', '广州', '东莞', '佛山', '珠海', '中山', '惠州', '其他']
const skillOptions = ['护老', '育儿', '做饭', '保洁', '收纳', '陪护', '月嫂']

const hasProfile = computed(() => Boolean(profile.value?.id))

const buildEmptyForm = () => ({
  realName: '',
  phone: '',
  age: '',
  educationText: '',
  experienceYears: '',
  skillsText: '',
  expectedCityText: '',
  selfIntroduction: '',
  photoUrl: '',
  idCardFrontUrl: '',
  idCardBackUrl: ''
})

const form = reactive(buildEmptyForm())

const resetForm = () => {
  Object.assign(form, buildEmptyForm())
  selectedSkills.value = []
}

const toNullableNumber = (v) => {
  if (v === '' || v === null || v === undefined) return null
  const n = Number(v)
  return Number.isNaN(n) ? null : n
}

const payload = () => ({
  realName: form.realName?.trim() || '',
  phone: form.phone?.trim() || '',
  age: toNullableNumber(form.age),
  educationText: form.educationText?.trim() || '',
  experienceYears: toNullableNumber(form.experienceYears),
  skillsText: form.skillsText?.trim() || '',
  expectedCityText: form.expectedCityText?.trim() || '',
  selfIntroduction: form.selfIntroduction?.trim() || '',
  photoUrl: form.photoUrl?.trim() || '',
  idCardFrontUrl: form.idCardFrontUrl?.trim() || '',
  idCardBackUrl: form.idCardBackUrl?.trim() || ''
})

const shortUrl = (url) => {
  if (!url) return '未上传附件'
  if (url.length <= 36) return url
  return `${url.slice(0, 16)}...${url.slice(-12)}`
}

const previewImage = (url) => {
  if (!url) {
    uni.showToast({ title: '请先上传图片', icon: 'none' })
    return
  }
  uni.previewImage({
    urls: [url],
    current: url
  })
}

const load = async () => {
  profile.value = (await get('/api/candidate/profile/my')) || null
}

const ensureLogin = () => {
  if (getToken()) return true
  uni.showModal({
    title: '提示',
    content: '在线简历需要先登录，是否前往登录？',
    confirmText: '去登录',
    cancelText: '取消',
    success: ({ confirm }) => {
      if (confirm) uni.navigateTo({ url: '/pages/login/index' })
    }
  })
  return false
}

const onEducationChange = (e) => {
  const index = Number(e?.detail?.value ?? -1)
  if (index < 0 || index >= educationOptions.length) return
  form.educationText = educationOptions[index]
}

const onCityChange = (e) => {
  const index = Number(e?.detail?.value ?? -1)
  if (index < 0 || index >= cityOptions.length) return
  form.expectedCityText = cityOptions[index]
}

const toggleSkill = (name) => {
  const set = new Set(selectedSkills.value)
  if (set.has(name)) {
    set.delete(name)
  } else {
    set.add(name)
  }
  selectedSkills.value = Array.from(set)
  form.skillsText = selectedSkills.value.join('、')
}

const openCreate = () => {
  if (!ensureLogin()) return
  if (hasProfile.value) {
    uni.showToast({ title: '你已创建简历，请直接编辑', icon: 'none' })
    return
  }
  resetForm()
  form.realName = uni.getStorageSync('realName') || ''
  form.phone = uni.getStorageSync('loginPhone') || ''
  showForm.value = true
}

const openEdit = () => {
  if (!ensureLogin() || !profile.value) return
  Object.assign(form, {
    realName: profile.value.realName ?? '',
    phone: profile.value.phone ?? '',
    age: profile.value.age ?? '',
    educationText: profile.value.educationText ?? '',
    experienceYears: profile.value.experienceYears ?? '',
    skillsText: profile.value.skillsText ?? '',
    expectedCityText: profile.value.expectedCityText ?? '',
    selfIntroduction: profile.value.selfIntroduction ?? '',
    photoUrl: profile.value.photoUrl ?? '',
    idCardFrontUrl: profile.value.idCardFrontUrl ?? '',
    idCardBackUrl: profile.value.idCardBackUrl ?? ''
  })
  selectedSkills.value = (profile.value.skillsText || '')
    .split(/[、,]/)
    .map((i) => i.trim())
    .filter(Boolean)
  showForm.value = true
}

const save = async () => {
  if (!form.realName?.trim()) {
    uni.showToast({ title: '请填写姓名', icon: 'none' })
    return
  }
  if (!form.phone?.trim()) {
    uni.showToast({ title: '请填写手机号', icon: 'none' })
    return
  }
  await post('/api/candidate/profile/my', payload())
  uni.showToast({ title: hasProfile.value ? '更新成功' : '新增成功', icon: 'success' })
  showForm.value = false
  await load()
}

const cancelEdit = () => {
  showForm.value = false
  resetForm()
}

const removeProfile = () => {
  uni.showModal({
    title: '确认删除',
    content: '删除后不可恢复，是否继续？',
    confirmText: '删除',
    cancelText: '取消',
    success: async ({ confirm }) => {
      if (!confirm) return
      await del('/api/candidate/profile/my')
      uni.showToast({ title: '删除成功', icon: 'success' })
      profile.value = null
      cancelEdit()
    }
  })
}

const uploadImage = (field) => {
  if (!ensureLogin()) return
  uni.chooseImage({
    count: 1,
    success: (res) => {
      const filePath = res.tempFilePaths?.[0]
      if (!filePath) return
      uni.uploadFile({
        url: 'http://localhost:8080/api/file/upload',
        filePath,
        name: 'file',
        header: {
          Authorization: `Bearer ${getToken()}`
        },
        success: (uploadRes) => {
          const body = JSON.parse(uploadRes.data || '{}')
          if (body.code === 200) {
            form[field] = body.data || ''
            uni.showToast({ title: '上传成功', icon: 'success' })
            return
          }
          uni.showToast({ title: body.msg || '上传失败', icon: 'none' })
        },
        fail: () => uni.showToast({ title: '上传失败', icon: 'none' })
      })
    }
  })
}

onShow(() => {
  if (!getToken()) {
    profile.value = null
    showForm.value = false
    return
  }
  load()
})
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f6f6f8;
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 200rpx;
}
.empty-icon {
  font-size: 100rpx;
  margin-bottom: 30rpx;
}
.empty-title {
  font-size: 36rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 16rpx;
}
.empty-desc {
  font-size: 28rpx;
  color: #999;
  margin-bottom: 60rpx;
}
.btn {
  border-radius: 40rpx;
  font-size: 30rpx;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
}
.btn-primary {
  background: #409eff;
  color: #fff;
  width: 400rpx;
  height: 80rpx;
}
.btn-outline {
  background: #fff;
  color: #666;
  border: 1rpx solid #e0e0e0;
  width: 100%;
  height: 88rpx;
}

.resume-view {
  padding-bottom: 60rpx;
}
.resume-header {
  background: #fff;
  padding: 40rpx 30rpx;
  margin-bottom: 16rpx;
}
.resume-main-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}
.name {
  font-size: 48rpx;
  font-weight: bold;
  color: #333;
}
.edit-icon {
  font-size: 28rpx;
  color: #409eff;
  padding: 10rpx 20rpx;
  background: #ecf5ff;
  border-radius: 30rpx;
}
.resume-sub-info {
  display: flex;
  align-items: center;
  color: #666;
  font-size: 26rpx;
}
.divider {
  margin: 0 16rpx;
  color: #e0e0e0;
}
.resume-section {
  background: #fff;
  padding: 40rpx 30rpx;
  margin-bottom: 16rpx;
}
.section-title {
  font-size: 34rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
}
.intention-item {
  display: flex;
  align-items: center;
}
.intention-label {
  width: 140rpx;
  color: #666;
  font-size: 28rpx;
}
.intention-val {
  color: #333;
  font-size: 28rpx;
  flex: 1;
}
.skills-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}
.skill-tag {
  background: #f8f8f8;
  color: #666;
  padding: 10rpx 24rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
}
.text-content {
  font-size: 28rpx;
  color: #333;
  line-height: 1.6;
}
.mt-10 {
  margin-top: 20rpx;
}
.contact-item {
  display: flex;
  align-items: center;
}
.contact-label {
  width: 140rpx;
  color: #666;
  font-size: 28rpx;
}
.contact-val {
  color: #333;
  font-size: 28rpx;
  font-weight: 500;
}
.bottom-action {
  padding: 40rpx 30rpx;
}

.form-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #f6f6f8;
  z-index: 100;
  display: flex;
  flex-direction: column;
}
.form-header {
  height: 88rpx;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  border-bottom: 1rpx solid #f5f5f5;
}
.form-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}
.form-close {
  position: absolute;
  right: 30rpx;
  font-size: 40rpx;
  color: #999;
  padding: 10rpx;
}
.form-scroll {
  flex: 1;
  height: 0;
}
.form-group {
  background: #fff;
  padding: 0 30rpx;
  margin-bottom: 16rpx;
}
.form-label {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  padding: 40rpx 0 20rpx;
}
.form-item {
  display: flex;
  align-items: center;
  min-height: 100rpx;
  border-bottom: 1rpx solid #f5f5f5;
}
.form-item:last-child {
  border-bottom: none;
}
.item-label {
  width: 180rpx;
  font-size: 30rpx;
  color: #333;
}
.item-input {
  flex: 1;
  height: 100rpx;
  font-size: 30rpx;
  color: #333;
  text-align: right;
}
.item-picker {
  flex: 1;
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}
.picker-text {
  font-size: 30rpx;
  color: #333;
  text-align: right;
  width: 100%;
}
.ph-color {
  color: #ccc;
}
.skills-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
  padding-bottom: 30rpx;
}
.skill-choice {
  padding: 12rpx 30rpx;
  background: #f8f8f8;
  color: #666;
  border-radius: 30rpx;
  font-size: 26rpx;
  border: 1rpx solid transparent;
}
.skill-choice.active {
  background: #ecf5ff;
  color: #409eff;
  border-color: #409eff;
}
.intro-area {
  width: 100%;
  height: 200rpx;
  background: #f8f8f8;
  border-radius: 12rpx;
  padding: 20rpx;
  font-size: 28rpx;
  color: #333;
  box-sizing: border-box;
  margin-bottom: 40rpx;
}
.upload-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20rpx;
  padding-bottom: 40rpx;
}
.upload-box {
  aspect-ratio: 1;
  background: #f8f8f8;
  border-radius: 12rpx;
  overflow: hidden;
  position: relative;
}
.upload-img {
  width: 100%;
  height: 100%;
}
.upload-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
}
.plus {
  font-size: 40rpx;
  margin-bottom: 10rpx;
}
.up-text {
  font-size: 22rpx;
}
.form-bottom-pad {
  height: 40rpx;
}
.form-footer {
  padding: 20rpx 30rpx;
  background: #fff;
  border-top: 1rpx solid #f5f5f5;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}
.form-footer .btn-primary {
  width: 100%;
  height: 88rpx;
  border-radius: 12rpx;
}
</style>
