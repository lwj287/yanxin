<template>
  <view class="container">
    <view class="header">
      <text class="title">慧质检 AI 验收</text>
      <text class="subtitle">拍摄服务交付照片，实时获取 AI 评分</text>
    </view>

    <view class="card upload-section">
      <view class="image-container" v-if="imageUrl || (result && result.imageUrl)">
        <view class="image-box">
          <view class="image-wrapper">
            <image :src="(result && result.imageUrl) ? result.imageUrl : imageUrl" mode="widthFix" class="preview-img"></image>    
            
            <!-- 渲染 AI 标注框 -->
            <view v-if="result && result.aiDetectResult">
              <view v-for="(boxObj, bIndex) in parseDetectResult(result.aiDetectResult)" :key="bIndex" 
                    class="ai-box" 
                    :style="getBoxStyle(boxObj)">
                <text class="ai-label" :style="getLabelStyle(boxObj.label)">{{ boxObj.label }} ({{ Math.round(boxObj.confidence * 100) }}%)</text>
              </view>
            </view>
          </view>
        </view>
      </view>
      <view class="upload-placeholder" @tap="chooseImage" v-else>
        <text class="icon">+</text>
        <text class="text">点击拍摄/上传照片</text>
      </view>
    </view>

    <view class="action-section">
      <button 
        v-if="(imageUrl || (result && result.imageUrl)) && !isUploading"
        class="re-upload-btn" 
        @tap="chooseImage"
      >
        重新拍摄
      </button>
      <button 
        class="submit-btn" 
        :class="{ 'disabled': !imageUrl || isUploading }"
        :disabled="!imageUrl || isUploading"
        @tap="startAnalysis"
      >
        {{ isUploading ? 'AI 正在检测中...' : '开始 AI 检测' }}
      </button>
    </view>

    <view class="card result-section" v-if="result && result.aiStatus !== 'UNCHECKED'">
      <view class="score-row">
        <text class="label">AI 评分：</text>
        <text class="score" :class="result.aiStatus === 'PASSED' ? 'good' : 'bad'">{{ result.aiScore || 0 }} 分</text>
      </view>
      <view class="status-row">
        <text class="label">验收状态：</text>
        <text class="status">{{ result.aiStatus === 'PASSED' ? '通过' : '需返工' }}</text>
      </view>
      <view class="detail-row" v-if="getDefects(result.aiDetectResult).length > 0">
        <text class="label">检测到缺陷：</text>
        <view class="defect-list">
          <text class="defect-tag" v-for="(item, index) in getDefects(result.aiDetectResult)" :key="index">{{ item }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { createQualityTask, uploadTaskImage, getTaskDetail } from '@/api/index'

const imageUrl = ref('')
const isUploading = ref(false)
const result = ref<any>(null)
let pollingTimer: any = null

const chooseImage = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['camera', 'album'],
    success: (res) => {
      imageUrl.value = res.tempFilePaths[0]
      result.value = null // 清除上次的结果
    }
  })
}

const startAnalysis = async () => {
  if (!imageUrl.value || isUploading.value) return
  
  isUploading.value = true
  result.value = null

  try {
    uni.showLoading({ title: '创建任务中...' })
    // 1. 创建任务
    const taskRes: any = await createQualityTask({
      serviceType: '日常保洁',
      staffName: '小程序测试人员'
    })
    
    if (!taskRes || !taskRes.id) {
      throw new Error('创建任务失败')
    }
    const taskId = taskRes.id

    uni.showLoading({ title: '上传图片中...' })
    // 2. 上传图片
    await uploadTaskImage(taskId, imageUrl.value)

    uni.showLoading({ title: 'AI 分析中...' })
    // 3. 开始轮询结果
    startPolling(taskId)
  } catch (error) {
    console.error(error)
    uni.hideLoading()
    isUploading.value = false
    uni.showToast({ title: '分析失败，请重试', icon: 'none' })
  }
}

const startPolling = (taskId: number) => {
  if (pollingTimer) clearInterval(pollingTimer)
  
  pollingTimer = setInterval(async () => {
    try {
      const res: any = await getTaskDetail(taskId)
      if (res && res.imageRecords && res.imageRecords.length > 0) {
        const imageRecord = res.imageRecords[0]
        
        // 更新图片URL为后端返回的真实URL
        if (imageRecord.imageUrl) {
          result.value = imageRecord
        }

        if (imageRecord.aiStatus !== 'UNCHECKED') {
          // 检测完成
          clearInterval(pollingTimer)
          pollingTimer = null
          isUploading.value = false
          uni.hideLoading()
          
          if (imageRecord.aiStatus === 'PASSED') {
            uni.showToast({ title: '验收通过', icon: 'success' })
          } else {
            uni.showToast({ title: '发现问题，请返工', icon: 'error' })        
          }
        }
      }
    } catch (error) {
      console.error('Polling error:', error)
      clearInterval(pollingTimer)
      pollingTimer = null
      isUploading.value = false
      uni.hideLoading()
      uni.showToast({ title: '获取结果失败', icon: 'none' })
    }
  }, 2000)
}

// 解析 AI 检测结果
const parseDetectResult = (resultStr: string) => {
  if (!resultStr) return []
  try {
    return JSON.parse(resultStr)
  } catch (e) {
    console.error('Failed to parse detect result', e)
    return []
  }
}

// 提取缺陷标签（这里简化处理，将所有识别出的 label 作为缺陷展示）
const getDefects = (resultStr: string) => {
  const boxes = parseDetectResult(resultStr)
  const labels = boxes.map((box: any) => box.label)
  return [...new Set(labels)] // 去重
}

// 预定义颜色调色板
const colorPalette = [
  '#FF3838', '#FF9D97', '#FF701F', '#FFB21D', '#CFD231', 
  '#48F90A', '#92CC17', '#3DDB86', '#1A9334', '#00D4BB',
  '#2C99A8', '#00C2FF', '#344593', '#6473FF', '#0018EC',
  '#8438FF', '#520085', '#CB38FF', '#FF95C8', '#FF37C7'
]

const getColorByLabel = (label: string) => {
  let hash = 0;
  for (let i = 0; i < label.length; i++) {
    hash = label.charCodeAt(i) + ((hash << 5) - hash);
  }
  const index = Math.abs(hash) % colorPalette.length;
  return colorPalette[index];
}

const getBoxStyle = (boxObj: any) => {
  if (!boxObj || !boxObj.box || boxObj.box.length !== 4) return ''
  const [xmin, ymin, xmax, ymax] = boxObj.box
  
  const left = xmin * 100
  const top = ymin * 100
  const width = (xmax - xmin) * 100
  const height = (ymax - ymin) * 100

  const color = getColorByLabel(boxObj.label || 'unknown');

  return `left: ${left}%; top: ${top}%; width: ${width}%; height: ${height}%; border-color: ${color}; background-color: ${color}33;`
}

const getLabelStyle = (label: string) => {
  const color = getColorByLabel(label || 'unknown');
  return `background-color: ${color};`
}
</script>

<style>
.container {
  padding: 40rpx 40rpx;
  background-color: #f5f7fa;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  margin-top: 40rpx;
  margin-bottom: 80rpx;
  text-align: center;
}

.title {
  font-size: 64rpx;
  font-weight: 600;
  color: #303133;
  display: block;
  margin-bottom: 24rpx;
}

.subtitle {
  font-size: 32rpx;
  color: #909399;
}

.upload-placeholder {
  height: 400rpx;
  background-color: #f5f7fa;
  border-radius: 24rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.icon {
  font-size: 80rpx;
  color: #c0c4cc;
  margin-bottom: 20rpx;
}

.text {
  font-size: 30rpx;
  color: #c0c4cc;
}

.image-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.image-box {
  position: relative;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #000;
  border-radius: 24rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
}

.image-wrapper {
  position: relative;
  width: 100%;
}

.preview-img {
  width: 100%;
  display: block;
}

.ai-box {
  position: absolute;
  border: 4rpx solid transparent;
  background-color: transparent;
  pointer-events: none;
  z-index: 10;
}

.ai-label {
  position: absolute;
  top: -40rpx;
  left: -4rpx;
  color: #fff;
  padding: 4rpx 12rpx;
  font-size: 24rpx;
  border-radius: 8rpx;
  white-space: nowrap;
}

.re-upload-btn {
  background-color: #fdf6ec;
  color: #e6a23c;
  border-radius: 24rpx;
  font-size: 32rpx;
  font-weight: 500;
  height: 96rpx;
  line-height: 96rpx;
  margin-bottom: 24rpx;
  border: none;
}
.re-upload-btn::after {
  border: none;
}

.submit-btn {
  background-color: #409EFF;
  color: #ffffff;
  border-radius: 24rpx;
  font-size: 32rpx;
  font-weight: 500;
  height: 96rpx;
  line-height: 96rpx;
  border: none;
}
.submit-btn::after {
  border: none;
}
.submit-btn:active {
  background-color: #3a8ee6;
}

.submit-btn.disabled {
  background-color: #a0cfff;
  color: #ffffff;
}

.score-row, .status-row {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
}

.label {
  font-size: 28rpx;
  color: #606266;
  width: 160rpx;
  font-weight: 500;
}

.score {
  font-size: 36rpx;
  font-weight: bold;
}

.score.good {
  color: #67c23a;
}

.score.bad {
  color: #f56c6c;
}

.status {
  font-size: 32rpx;
  color: #303133;
  font-weight: 500;
}

.detail-row {
  margin-top: 32rpx;
  border-top: 2rpx solid #ebeef5;
  padding-top: 24rpx;
}

.defect-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 16rpx;
}

.defect-tag {
  background-color: #fef0f0;
  color: #f56c6c;
  font-size: 24rpx;
  padding: 8rpx 20rpx;
  border-radius: 8rpx;
}
</style>