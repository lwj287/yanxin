<template>
  <view class="page">
    <view class="card form-card">
      <view class="title">简历投递信息</view>
      <input v-model="form.realName" class="ipt" placeholder="请输入姓名" />
      <input v-model="form.phone" class="ipt" placeholder="请输入联系电话" />
      <input v-model="form.age" class="ipt" type="number" placeholder="请输入年龄，如 35" />
      <input v-model="form.educationCode" class="ipt" placeholder="请输入学历，如：大专" />
      <input v-model="form.experienceYears" class="ipt" type="number" placeholder="请输入工作年限，如 5" />
      <input v-model="form.skills" class="ipt" placeholder="请输入技能，多个用顿号分隔，如：做饭、育儿" />
      <textarea v-model="form.selfIntroduction" class="txt" placeholder="请输入个人简介，建议包含经验亮点和服务特长" />

      <view class="upload-row">
        <button class="btn btn-light mini" @click="uploadImage('photoUrl')">上传照片</button>
        <button class="btn btn-light mini" @click="uploadImage('idCardFrontUrl')">身份证正面</button>
        <button class="btn btn-light mini" @click="uploadImage('idCardBackUrl')">身份证反面</button>
      </view>

      <button class="btn btn-primary" @click="submit">确认提交简历</button>
    </view>
  </view>
</template>

<script setup>
import { reactive } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { post } from '../../utils/request'
import { getToken } from '../../utils/auth'

const form = reactive({
  jobId: 0,
  realName: '',
  phone: '',
  age: '',
  educationCode: '',
  experienceYears: 0,
  skills: '',
  selfIntroduction: '',
  photoUrl: '',
  idCardFrontUrl: '',
  idCardBackUrl: ''
})

onLoad((options) => {
  form.jobId = Number(options?.jobId || 0)
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

const uploadImage = (field) => {
  if (!getToken()) {
    softRequireLogin('上传附件', () => {})
    return
  }
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
            form[field] = body.data
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

const submit = async () => {
  if (!getToken()) {
    softRequireLogin('投递简历', () => {})
    return
  }
  if (!form.jobId) {
    uni.showToast({ title: '缺少岗位编号', icon: 'none' })
    return
  }
  await post('/api/candidate/resume/submit', {
    ...form,
    age: Number(form.age || 0),
    experienceYears: Number(form.experienceYears || 0)
  })
  uni.showToast({ title: '投递成功', icon: 'success' })
  setTimeout(() => uni.navigateBack(), 350)
}
</script>

<style scoped>
.page {
  padding: 24rpx 20rpx 30rpx;
}
.card {
  background: #fff;
  border: 1px solid #e7edf5;
  border-radius: 22rpx;
  box-shadow: 0 8rpx 24rpx rgba(31, 54, 88, 0.06);
}
.form-card {
  padding: 24rpx;
}
.title {
  font-size: 36rpx;
  font-weight: 600;
  color: #1f324d;
  margin-bottom: 20rpx;
}
.ipt {
  height: 80rpx;
  border: 1px solid #d8e2f0;
  border-radius: 14rpx;
  padding: 0 18rpx;
  margin-bottom: 14rpx;
  background: #f8fbff;
  font-size: 28rpx;
}
.txt {
  width: 100%;
  height: 180rpx;
  border: 1px solid #d8e2f0;
  border-radius: 14rpx;
  padding: 14rpx 18rpx;
  margin-bottom: 18rpx;
  background: #f8fbff;
  font-size: 28rpx;
}
.upload-row {
  display: flex;
  gap: 10rpx;
  margin-bottom: 18rpx;
}
.btn {
  border-radius: 14rpx;
}
.btn-primary {
  background: linear-gradient(135deg, #4f8ef7 0%, #3b72d9 100%);
  color: #fff;
}
.btn-light {
  background: #f5f8fc;
  color: #3a5575;
}
.mini {
  font-size: 24rpx;
  flex: 1;
}
</style>
