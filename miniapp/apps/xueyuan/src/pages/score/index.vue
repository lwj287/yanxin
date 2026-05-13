<template>
  <view class="score-container">
    <view class="header">
      <view class="stat-box card">
        <view class="stat-item">
          <text class="num">{{ exams.length }}</text>
          <text class="label">考试次数</text>
        </view>
        <view class="divider"></view>
        <view class="stat-item">
          <text class="num">{{ averageScore }}</text>
          <text class="label">平均分</text>
        </view>
      </view>
    </view>

    <view class="list-container">
      <view class="section-title">历史成绩</view>
      <view v-if="!exams.length" class="empty">暂无考试记录</view>

      <view class="score-list">
        <view class="score-card card" v-for="item in exams" :key="item.id">
          <view class="score-header">
            <text class="exam-name">{{ item.examName }}</text>
            <text class="score-status pass" v-if="item.score >= 60">及格</text>
            <text class="score-status fail" v-else>不及格</text>
          </view>
          
          <view class="score-info">
            <view class="info-left">
              <view class="info-row">
                <text class="label">交卷时间</text>
                <text class="value">{{ item.submitTime }}</text>
              </view>
              <view class="info-row">
                <text class="label">用时</text>
                <text class="value">{{ item.duration }}分钟</text>
              </view>
            </view>
            <view class="info-right">
              <text class="score-value" :class="{ 'is-fail': item.score < 60 }">{{ item.score }}</text>
              <text class="score-unit">分</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { onShow } from '@dcloudio/uni-app';

const exams = ref<any[]>([]);

const averageScore = computed(() => {
  if (exams.value.length === 0) return 0;
  const total = exams.value.reduce((sum, exam) => sum + exam.score, 0);
  return Math.round(total / exams.value.length);
});

onShow(() => {
  // 生成模拟考试成绩记录
  exams.value = [
    {
      id: 1,
      examName: '保洁服务高级认证考试',
      score: 92,
      duration: 45,
      submitTime: '2026-04-18 14:30:00'
    },
    {
      id: 2,
      examName: '岗前必修安全知识测验',
      score: 100,
      duration: 20,
      submitTime: '2026-04-10 09:15:00'
    },
    {
      id: 3,
      examName: '保姆服务中级认证考试',
      score: 55,
      duration: 60,
      submitTime: '2026-03-25 16:40:00'
    },
    {
      id: 4,
      examName: '保姆服务初级认证考试',
      score: 88,
      duration: 40,
      submitTime: '2026-03-10 11:20:00'
    }
  ];
});
</script>

<style lang="scss" scoped>
.score-container {
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
  background: linear-gradient(135deg, #67C23A 0%, #529b2e 100%);
  color: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 16px rgba(103, 194, 58, 0.2);
  
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

.score-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.score-card {
  padding: 20px;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  
  .score-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 16px;
    border-bottom: 1px dashed #ebeef5;
    
    .exam-name {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      flex: 1;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      margin-right: 12px;
    }
    
    .score-status {
      font-size: 12px;
      padding: 4px 10px;
      border-radius: 10px;
      flex-shrink: 0;
      
      &.pass {
        color: #67c23a;
        background-color: #f0f9eb;
      }
      
      &.fail {
        color: #f56c6c;
        background-color: #fef0f0;
      }
    }
  }
  
  .score-info {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    
    .info-left {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 8px;
      
      .info-row {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .label {
          font-size: 13px;
          color: #909399;
          width: 60px;
        }
        
        .value {
          font-size: 13px;
          color: #606266;
        }
      }
    }
    
    .info-right {
      display: flex;
      align-items: baseline;
      
      .score-value {
        font-size: 36px;
        font-weight: bold;
        color: #409EFF;
        line-height: 1;
        
        &.is-fail {
          color: #F56C6C;
        }
      }
      
      .score-unit {
        font-size: 14px;
        color: #909399;
        margin-left: 2px;
        margin-bottom: 4px;
      }
    }
  }
}
</style>