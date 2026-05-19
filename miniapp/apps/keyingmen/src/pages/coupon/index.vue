<template>
  <view class="container">
    <view class="header">
      <text class="title">我的卡券</text>
    </view>
    
    <view class="coupon-list">
      <view class="card coupon-card" v-for="item in coupons" :key="item.id">
        <view class="coupon-left">
          <text class="amount">￥{{ item.faceValue || 50 }}</text>
          <text class="condition">{{ formatCondition(item.conditionAmount) }}</text>
        </view>
        <view class="coupon-right">
          <view class="coupon-title">{{ item.couponName || '优惠券' }}</view>
          <view class="coupon-time">有效期至: {{ formatDate(item.useTime || item.receiveTime) }}</view>
          <view class="coupon-status" :class="'status-' + item.status">
            {{ formatStatus(item.status) }}
          </view>
        </view>
      </view>
    </view>
    <view v-if="coupons.length === 0" class="empty-state">
      <text>暂无卡券</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getCoupons } from '@/api/index';

const coupons = ref<any[]>([]);

const fetchCoupons = async () => {
  try {
    const res: any = await getCoupons({ page: 1, pageSize: 20 });
    coupons.value = res.records || [];
  } catch (error) {
    console.error('获取卡券失败', error);
  }
};

const formatStatus = (status: number) => {
  const map: Record<number, string> = { 0: '未使用', 1: '已使用', 2: '已过期' };
  return map[status] || '未知';
};

const formatDate = (dateStr: string) => {
  if (!dateStr) return '2026-12-31';
  return dateStr.substring(0, 10);
};

const formatCondition = (amount: any) => {
  if (amount === undefined || amount === null) return '满100可用';
  if (Number(amount) === 0) return '无门槛';
  return `满${amount}可用`;
};

onMounted(() => {
  fetchCoupons();
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
.coupon-card {
  display: flex;
  padding: 0;
  overflow: hidden;
  margin-bottom: 16px;
  .coupon-left {
    background: #ff6b6b;
    color: white;
    width: 100px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 16px 0;
    .amount {
      font-size: 24px;
      font-weight: bold;
    }
    .condition {
      font-size: 12px;
      margin-top: 4px;
    }
  }
  .coupon-right {
    flex: 1;
    padding: 16px;
    position: relative;
    .coupon-title {
      font-size: 16px;
      font-weight: bold;
      color: #303133;
      margin-bottom: 8px;
    }
    .coupon-time {
      font-size: 12px;
      color: #909399;
    }
    .coupon-status {
      position: absolute;
      right: 16px;
      top: 16px;
      font-size: 14px;
      &.status-0 { color: #ff6b6b; }
      &.status-1 { color: #909399; }
      &.status-2 { color: #c0c4cc; }
    }
  }
}
.empty-state {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}
</style>
