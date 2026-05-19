<template>
  <view class="container">
    <view class="header">
      <text class="title">热门活动</text>
      <text class="subtitle">参与活动获取更多优惠</text>
    </view>
    
    <view class="activity-list">
      <view class="card activity-card" v-for="item in activities" :key="item.id">
        <view class="activity-header">
          <text class="activity-title">{{ item.activityName || '营销活动' }}</text>
          <text class="status" :class="getActivityStatus(item).class">
            {{ getActivityStatus(item).text }}
          </text>
        </view>
        <view class="activity-body">
          <view class="info-row">
            <text class="label">活动类型：</text>
            <text class="value">{{ formatType(item.activityType) }}</text>
          </view>
          <view class="info-row">
            <text class="label">时间：</text>
            <text class="value">{{ formatDate(item.startTime) }} - {{ formatDate(item.endTime) }}</text>
          </view>
        </view>
        <view class="activity-footer">
          <button class="join-btn" @click="handleJoin(item)">活动详情</button>
        </view>
      </view>
    </view>
    <view v-if="activities.length === 0" class="empty-state">
      <text>暂无活动</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getActivities } from '@/api/index';

const activities = ref<any[]>([]);

const fetchActivities = async () => {
  try {
    const res: any = await getActivities({ page: 1, pageSize: 10 });
    activities.value = res.records || [];
  } catch (error) {
    console.error('获取活动失败', error);
  }
};

const formatType = (type: number) => {
  const map: Record<number, string> = { 1: '赠送', 2: '抽奖' };
  return map[type] || '其他';
};

const formatDate = (dateStr: string) => {
  if (!dateStr) return '';
  return dateStr.substring(0, 10);
};

const handleJoin = (item: any) => {
  uni.navigateTo({
    url: `/pages/activity/detail?id=${item.activityId}`
  });
};

const getActivityStatus = (item: any) => {
  const now = new Date().getTime();
  const startTime = new Date(item.startTime).getTime();
  const endTime = new Date(item.endTime).getTime();

  if (now < startTime) {
    return { text: '未开始', class: '' };
  } else if (now >= startTime && now <= endTime) {
    return { text: '进行中', class: 'active' };
  } else {
    return { text: '已结束', class: '' };
  }
};

onMounted(() => {
  fetchActivities();
});
</script>

<style lang="scss" scoped>
.container {
  padding: 20px;
  padding-bottom: 80px;
}
.header {
  margin-bottom: 20px;
  .title {
    font-size: 24px;
    font-weight: bold;
    color: #303133;
    display: block;
  }
  .subtitle {
    font-size: 14px;
    color: #909399;
    margin-top: 4px;
    display: block;
  }
}
.activity-card {
  margin-bottom: 16px;
  .activity-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    border-bottom: 1px solid #ebeef5;
    padding-bottom: 12px;
    .activity-title {
      font-size: 18px;
      font-weight: bold;
      color: #303133;
    }
    .status {
      font-size: 12px;
      padding: 4px 8px;
      border-radius: 4px;
      background: #f4f4f5;
      color: #909399;
      &.active {
        background: #e1f3d8;
        color: #67c23a;
      }
    }
  }
  .activity-body {
    .info-row {
      margin-bottom: 8px;
      font-size: 14px;
      .label {
        color: #909399;
      }
      .value {
        color: #606266;
      }
    }
  }
  .activity-footer {
    margin-top: 16px;
    .join-btn {
      background: #409eff;
      color: white;
      border-radius: 20px;
      font-size: 14px;
      &::after {
        border: none;
      }
    }
  }
}
.empty-state {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}
</style>
