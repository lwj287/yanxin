<template>
  <view class="detail-container">
    <video 
      v-if="isLogin"
      class="video-player"
      :src="course.videoUrl || 'https://www.w3schools.com/html/mov_bbb.mp4'"
      controls
      autoplay
      @timeupdate="onTimeUpdate"
    ></video>
    <view v-else class="video-placeholder" @click="goToLogin">
      <text class="icon">🔒</text>
      <text class="text">登录后可观看课程视频</text>
      <button class="login-btn">去登录</button>
    </view>
    
    <view class="info-section">
      <text class="title">{{ course.title || '加载中...' }}</text>
      <view class="tags" v-if="course.id">
        <text class="tag">{{ course.category }}</text>
        <text class="tag level" v-if="course.level">{{ course.level }}</text>
      </view>
      
      <view class="stat">
        <text class="stat-item">学习人数: {{ course.studentCount || 0 }}</text>
        <text class="stat-item">学时: 2h</text>
      </view>
      
      <view class="desc-box">
        <text class="desc-title">课程简介</text>
        <text class="desc-content">{{ course.description || '暂无简介' }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { getCourseDetail } from '@/api/index';

const course = ref<any>({});
const courseId = ref<number | null>(null);
const isLogin = computed(() => !!uni.getStorageSync('token'));

onMounted(() => {
  const pages = getCurrentPages();
  const options = (pages[pages.length - 1] as any).options;
  if (options && options.id) {
    courseId.value = parseInt(options.id, 10);
    fetchDetail();
  }
});

const fetchDetail = async () => {
  if (!courseId.value) return;
  try {
    const res = await getCourseDetail(courseId.value);
    if (res) {
      const categoryMap: Record<number, string> = { 1: '保洁服务', 2: '保姆服务' };
      const levelMap: Record<number, string> = { 1: '初级', 2: '中级', 3: '高级' };
      course.value = {
        ...res,
        category: categoryMap[res.categoryId] || '其他',
        level: levelMap[res.certLevel] || '初级'
      };
    }
  } catch (e) {
    console.error(e);
  }
};

const goToLogin = () => {
  uni.navigateTo({ url: '/pages/login/index' });
};

const onTimeUpdate = (e: any) => {
  // 可以在这里记录学习进度
};
</script>

<style lang="scss" scoped>
.detail-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.video-player {
  width: 100%;
  height: 225px; // 16:9 比例
}

.video-placeholder {
  width: 100%;
  height: 225px;
  background-color: #2c2c2c;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  
  .icon {
    font-size: 32px;
    margin-bottom: 12px;
  }
  
  .text {
    font-size: 14px;
    margin-bottom: 16px;
    color: #e4e7ed;
  }
  
  .login-btn {
    width: 120px;
    height: 36px;
    line-height: 36px;
    font-size: 14px;
    background-color: #409EFF;
    color: #fff;
    border-radius: 18px;
    margin: 0;
  }
}

.info-section {
  padding: 20px;
  background: #fff;
  border-radius: 24px 24px 0 0;
  margin-top: -16px;
  position: relative;
  z-index: 10;
  min-height: calc(100vh - 209px);
  
  .title {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
    display: block;
    margin-bottom: 12px;
  }
  
  .tags {
    display: flex;
    gap: 8px;
    margin-bottom: 16px;
    
    .tag {
      font-size: 12px;
      color: #409EFF;
      background-color: #ecf5ff;
      padding: 4px 10px;
      border-radius: 12px;
      
      &.level {
        color: #E6A23C;
        background-color: #fdf6ec;
      }
    }
  }
  
  .stat {
    display: flex;
    gap: 24px;
    margin-bottom: 24px;
    padding-bottom: 24px;
    border-bottom: 1px solid #ebeef5;
    
    .stat-item {
      font-size: 13px;
      color: #909399;
    }
  }
  
  .desc-box {
    .desc-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 12px;
      display: block;
    }
    
    .desc-content {
      font-size: 14px;
      color: #606266;
      line-height: 1.6;
    }
  }
}
</style>