<template>
  <view class="record-container">
    <view class="header">
      <view class="stat-box card">
        <view class="stat-item">
          <text class="num">{{ totalCourses }}</text>
          <text class="label">已学课程</text>
        </view>
        <view class="divider"></view>
        <view class="stat-item">
          <text class="num">{{ totalHours }}</text>
          <text class="label">累计学时(h)</text>
        </view>
      </view>
    </view>

    <view class="list-container">
      <view class="section-title">最近学习</view>
      <view v-if="!records.length" class="empty">暂无学习记录</view>

      <view class="record-list">
        <view class="record-card card" v-for="item in records" :key="item.id" @click="goToCourse(item.courseId)">
          <image class="cover" :src="item.coverUrl || 'https://via.placeholder.com/150x150?text=Course'" mode="aspectFill" />
          <view class="info">
            <text class="title">{{ item.courseName || '加载中...' }}</text>
            <view class="progress-box">
              <view class="progress-bar">
                <view class="progress-inner" :style="{ width: (item.progress || 0) + '%' }"></view>
              </view>
              <text class="progress-text">已学 {{ item.progress || 0 }}%</text>
            </view>
            <text class="time">上次学习: {{ item.lastLearnTime || '2026-04-20 10:00:00' }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { getCourses } from '@/api/index'; // 临时借用课程接口生成模拟数据

const records = ref<any[]>([]);
const totalCourses = ref(0);
const totalHours = ref(0);
const loading = ref(false);

const fetchRecords = async () => {
  if (loading.value) return;
  loading.value = true;
  try {
    // 这里暂时使用获取课程列表接口模拟用户的学习记录数据
    const res: any = await getCourses({ current: 1, size: 10 });
    const courseList = res.records || [];
    
    // 生成模拟学习记录
    records.value = courseList.slice(0, 5).map((c: any, index: number) => ({
      id: index + 1,
      courseId: c.id,
      courseName: c.title,
      coverUrl: c.coverUrl,
      progress: Math.floor(Math.random() * 60) + 40, // 40-100 随机进度
      lastLearnTime: '2026-04-' + (20 - index) + ' 10:00:00'
    }));
    
    totalCourses.value = records.value.length;
    totalHours.value = Math.floor(totalCourses.value * 2.5);
  } catch (e) {
    console.error('获取学习记录失败', e);
  } finally {
    loading.value = false;
  }
};

onShow(() => {
  fetchRecords();
});

const goToCourse = (id: number) => {
  uni.navigateTo({
    url: `/pages/course/detail?id=${id}`
  });
};
</script>

<style lang="scss" scoped>
.record-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header {
  padding: 20px 16px;
  background-color: #fff;
  border-radius: 0 0 24px 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
  margin-bottom: 16px;
}

.stat-box {
  display: flex;
  align-items: center;
  padding: 24px 0;
  background: linear-gradient(135deg, #409EFF 0%, #3a8ee6 100%);
  color: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 16px rgba(64, 158, 255, 0.2);
  
  .stat-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    
    .num {
      font-size: 28px;
      font-weight: bold;
      margin-bottom: 4px;
    }
    
    .label {
      font-size: 13px;
      opacity: 0.9;
    }
  }
  
  .divider {
    width: 1px;
    height: 40px;
    background-color: rgba(255, 255, 255, 0.3);
  }
}

.list-container {
  padding: 0 16px 20px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
}

.empty {
  text-align: center;
  color: #909399;
  padding: 40px 0;
  font-size: 14px;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.record-card {
  display: flex;
  padding: 16px;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  
  .cover {
    width: 100px;
    height: 75px;
    border-radius: 8px;
    background-color: #ebeef5;
    margin-right: 16px;
    flex-shrink: 0;
  }
  
  .info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    overflow: hidden;
    
    .title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    
    .progress-box {
      display: flex;
      align-items: center;
      gap: 8px;
      
      .progress-bar {
        flex: 1;
        height: 6px;
        background-color: #ebeef5;
        border-radius: 3px;
        overflow: hidden;
        
        .progress-inner {
          height: 100%;
          background-color: #67c23a;
          border-radius: 3px;
        }
      }
      
      .progress-text {
        font-size: 12px;
        color: #67c23a;
        font-weight: 500;
      }
    }
    
    .time {
      font-size: 12px;
      color: #909399;
    }
  }
}
</style>