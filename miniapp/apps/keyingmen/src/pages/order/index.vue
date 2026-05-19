<template>
  <view class="container">
    <view class="header">
      <text class="title">我的订单</text>
    </view>
    
    <view class="order-list">
      <view class="card order-card" v-for="item in orders" :key="item.orderId">
        <view class="order-header">
          <text class="order-no">订单号: {{ item.orderId }}</text>
          <text class="status" :class="item.payStatus === 1 ? 'paid' : 'unpaid'">
            {{ item.payStatus === 1 ? '已支付' : '未支付' }}
          </text>
        </view>
        <view class="order-body">
          <view class="service-item">{{ item.serviceItem || '家政服务' }}</view>
          <view class="amount-row">
            <text class="label">总金额:</text>
            <text class="value">￥{{ item.totalAmount }}</text>
          </view>
          <view class="amount-row">
            <text class="label">积分抵扣:</text>
            <text class="value">-￥{{ item.pointDiscountAmount }}</text>
          </view>
          <view class="amount-row">
            <text class="label">优惠券抵扣:</text>
            <text class="value">-￥{{ item.couponDiscountAmount }}</text>
          </view>
        </view>
        <view class="order-footer">
          <text class="pay-amount">实付款: <text class="price">￥{{ item.payAmount }}</text></text>
        </view>
      </view>
    </view>
    <view v-if="orders.length === 0" class="empty-state">
      <text>暂无订单</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getOrders } from '@/api/index';

const orders = ref<any[]>([]);

const fetchOrders = async () => {
  try {
    const res: any = await getOrders({ page: 1, pageSize: 10 });
    orders.value = res.records || [];
  } catch (error) {
    console.error('获取订单失败', error);
  }
};

onMounted(() => {
  fetchOrders();
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
.order-card {
  margin-bottom: 16px;
  .order-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 12px;
    border-bottom: 1px solid #ebeef5;
    padding-bottom: 12px;
    .order-no {
      font-size: 14px;
      color: #909399;
    }
    .status {
      font-size: 14px;
      &.paid { color: #67c23a; }
      &.unpaid { color: #e6a23c; }
    }
  }
  .order-body {
    .service-item {
      font-size: 16px;
      font-weight: bold;
      color: #303133;
      margin-bottom: 8px;
    }
    .amount-row {
      display: flex;
      justify-content: space-between;
      font-size: 14px;
      margin-bottom: 4px;
      .label { color: #909399; }
      .value { color: #606266; }
    }
  }
  .order-footer {
    margin-top: 12px;
    border-top: 1px solid #ebeef5;
    padding-top: 12px;
    text-align: right;
    .pay-amount {
      font-size: 14px;
      color: #606266;
      .price {
        font-size: 18px;
        font-weight: bold;
        color: #f56c6c;
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
