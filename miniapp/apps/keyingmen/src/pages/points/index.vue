<template>
  <view class="container">
    <view class="header">
      <text class="title">积分明细</text>
    </view>
    
    <view class="record-list">
      <view class="card record-card" v-for="item in records" :key="item.recordId">
        <view class="record-left">
          <view class="record-title">{{ item.pointsChange > 0 ? '订单奖励积分' : '订单抵扣消耗' }}</view>
          <view class="record-time">{{ formatDate(item.createTime) }}</view>
          <view class="record-order">关联订单: {{ item.orderId }}</view>
        </view>
        <view class="record-right" :class="item.pointsChange > 0 ? 'plus' : 'minus'">
          {{ item.pointsChange > 0 ? '+' : '' }}{{ item.pointsChange }}
        </view>
      </view>
    </view>
    <view v-if="records.length === 0" class="empty-state">
      <text>暂无积分记录</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getPointRecords } from '@/api/index';

const records = ref<any[]>([]);

const fetchRecords = async () => {
  try {
    const res: any = await getPointRecords({ page: 1, pageSize: 50 });
    records.value = res.records || [];
  } catch (error) {
    console.error('获取积分记录失败', error);
  }
};

const formatDate = (dateStr: string) => {
  if (!dateStr) return '';
  return dateStr.replace('T', ' ');
};

onMounted(() => {
  fetchRecords();
});
</script>

<style lang="scss" scoped>
.container {
  padding: 20px;
}
.header {
  margin-bottom: 20px;
  .title {
    font-size: 24px;
    font-weight: bold;
    color: #303133;
  }
}
.record-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 16px;
  
  .record-left {
    .record-title {
      font-size: 16px;
      font-weight: bold;
      color: #303133;
      margin-bottom: 8px;
    }
    .record-time {
      font-size: 12px;
      color: #909399;
      margin-bottom: 4px;
    }
    .record-order {
      font-size: 12px;
      color: #c0c4cc;
    }
  }
  
  .record-right {
    font-size: 24px;
    font-weight: bold;
    
    &.plus {
      color: #67c23a;
    }
    &.minus {
      color: #f56c6c;
    }
  }
}
.empty-state {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}
</style>
