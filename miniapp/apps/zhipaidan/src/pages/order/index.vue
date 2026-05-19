<template>
  <view class="order-container">
    <view class="tabs">
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 0 }" 
        @click="currentTab = 0">
        全部派单
      </view>
    </view>

    <view class="order-list" v-if="orderList.length > 0">
      <view class="order-card" v-for="order in orderList" :key="order.id">
        <view class="order-header">
          <text class="order-no">订单号: {{ order.orderNo }}</text>
          <text class="status" :class="getStatusClass(order.status)">
            {{ getStatusText(order.status) }}
          </text>
        </view>
        <view class="order-body">
          <view class="info-row">
            <text class="label">服务类型：</text>
            <text class="value service-type">{{ order.serviceType || '常规服务' }}</text>
          </view>
          <view class="info-row">
            <text class="label">客户信息：</text>
            <text class="value">{{ order.customerName || '客户' }} {{ order.customerPhone || '' }}</text>
          </view>
          <view class="info-row">
            <text class="label">服务地址：</text>
            <text class="value">{{ order.serviceAddress || '暂无地址' }}</text>
          </view>
          <view class="divider"></view>
          <view class="info-row">
            <text class="label">计划开始：</text>
            <text class="value">{{ formatTime(order.planStartTime) }}</text>
          </view>
          <view class="info-row">
            <text class="label">计划结束：</text>
            <text class="value">{{ formatTime(order.planEndTime) }}</text>
          </view>
          <view class="info-row" v-if="order.estimatedDistance">
            <text class="label">通勤距离：</text>
            <text class="value">{{ (order.estimatedDistance / 1000).toFixed(2) }} km</text>
          </view>
        </view>
        <view class="order-footer" v-if="order.status === 1 || order.status === 2">
          <button class="action-btn primary" v-if="order.status === 1" @click="handleStartService(order)">开始服务</button>
          <button class="action-btn success" v-if="order.status === 2" @click="handleFinishService(order)">完成服务</button>
        </view>
      </view>
    </view>
    
    <view class="empty-state" v-else>
      <text v-if="isLogin">暂无派单记录</text>
      <template v-else>
        <text>请先登录后查看派单记录</text>
        <button class="login-btn-small" @click="goLogin">去登录</button>
      </template>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { getDispatchRecords, getOrderById, startDispatchService, finishDispatchService } from '@/api/index';

const currentTab = ref(0);
const orderList = ref<any[]>([]);
const isLogin = ref(false);

const fetchOrders = async () => {
  try {
    const staffId = uni.getStorageSync('token');
    // 1. 先获取派单记录
    const records: any = await getDispatchRecords(staffId); 
    if (records && records.length > 0) {
      // 2. 根据派单记录中的 orderId，去查询真实的订单详细信息
      const detailedOrders = await Promise.all(
        records.map(async (record: any) => {
          try {
            const orderDetail = await getOrderById(record.orderId);
            // 3. 将订单详情与派单记录合并返回给视图
            // 注意：要确保合并后的对象使用派单记录的 id，因为按钮操作调用的是派单接口
            return {
              ...orderDetail, // 先放订单详情
              ...record,      // 再放派单记录，覆盖同名字段（包括 id）
              status: record.status // 状态以派单记录的状态为准
            };
          } catch (err) {
            return record; // 如果查询订单失败，回退展示基础派单信息
          }
        })
      );
      orderList.value = detailedOrders;
    } else {
      orderList.value = [];
    }
  } catch (e) {
    console.error(e);
  }
};

const getStatusText = (status: number) => {
  if (status === 0) return '待派单';
  if (status === 1) return '待服务';
  if (status === 2) return '服务中';
  if (status === 3) return '已完成';
  if (status === 4) return '已取消';
  return '未知';
};

const getStatusClass = (status: number) => {
  if (status === 0 || status === 1) return 'status-pending';
  if (status === 2) return 'status-processing';
  if (status === 3) return 'status-done';
  if (status === 4) return 'status-cancel';
  return '';
};

const formatTime = (timeStr: string) => {
  if (!timeStr) return '';
  return timeStr.replace('T', ' ');
};

const handleStartService = (order: any) => {
  uni.showModal({
    title: '提示',
    content: '确认要开始该服务吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({ title: '提交中...' });
          await startDispatchService(order.id);
          uni.hideLoading();
          uni.showToast({ title: '服务已开始', icon: 'success' });
          fetchOrders(); // 刷新列表
        } catch (e: any) {
          uni.hideLoading();
          uni.showToast({ title: e.message || '操作失败', icon: 'none' });
        }
      }
    }
  });
};

const handleFinishService = (order: any) => {
  uni.showModal({
    title: '提示',
    content: '确认已完成该服务吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({ title: '提交中...' });
          await finishDispatchService(order.id);
          uni.hideLoading();
          uni.showToast({ title: '服务已完成', icon: 'success' });
          fetchOrders(); // 刷新列表
        } catch (e: any) {
          uni.hideLoading();
          uni.showToast({ title: e.message || '操作失败', icon: 'none' });
        }
      }
    }
  });
};

const goLogin = () => {
  uni.navigateTo({ url: '/pages/login/index' });
};

onShow(() => {
  isLogin.value = !!uni.getStorageSync('token');
  if (isLogin.value) {
    fetchOrders();
  } else {
    orderList.value = [];
  }
});
</script>

<style lang="scss" scoped>
.order-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.tabs {
  display: flex;
  background: #ffffff;
  padding: 0 20px;
  margin-bottom: 12px;

  .tab-item {
    padding: 16px 0;
    margin-right: 32px;
    font-size: 15px;
    color: #606266;
    position: relative;

    &.active {
      color: #409EFF;
      font-weight: 600;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 24px;
        height: 3px;
        background-color: #409EFF;
        border-radius: 2px;
      }
    }
  }
}

.order-list {
  padding: 12px 20px;
}

.order-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);

  .order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 16px;
    border-bottom: 1px solid #ebeef5;

    .order-no {
      font-size: 15px;
      font-weight: 600;
      color: #303133;
    }

    .status {
      font-size: 14px;
      font-weight: 500;
      padding: 4px 12px;
      border-radius: 12px;

      &.status-pending { background: #fdf6ec; color: #e6a23c; }
      &.status-processing { background: #ecf5ff; color: #409eff; }
      &.status-done { background: #e1f3d8; color: #67c23a; }
      &.status-cancel { background: #f4f4f5; color: #909399; }
    }
  }

  .order-body {
    .info-row {
      display: flex;
      margin-bottom: 12px;
      font-size: 14px;
      line-height: 1.5;

      &:last-child {
        margin-bottom: 0;
      }

      .label {
        color: #909399;
        width: 80px;
        flex-shrink: 0;
      }

      .value {
        color: #303133;
        flex: 1;
        word-break: break-all;
      }
      
      .service-type {
        color: #409EFF;
        font-weight: 500;
      }
    }
    
    .divider {
      height: 1px;
      background-color: #f2f6fc;
      margin: 16px 0;
    }
  }

  .order-footer {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px dashed #ebeef5;
    display: flex;
    justify-content: flex-end;

    .action-btn {
      margin: 0;
      padding: 0 24px;
      height: 36px;
      line-height: 36px;
      font-size: 14px;
      border-radius: 18px;

      &::after {
        border: none;
      }

      &.primary {
        background-color: #409EFF;
        color: #ffffff;
      }

      &.success {
        background-color: #67C23A;
        color: #ffffff;
      }
    }
  }
}

.empty-state {
  padding-top: 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  text {
    font-size: 15px;
    color: #909399;
    margin-bottom: 24px;
  }
  
  .login-btn-small {
    background-color: #409EFF;
    color: #ffffff;
    font-size: 14px;
    padding: 0 32px;
    height: 40px;
    line-height: 40px;
    border-radius: 20px;
    
    &::after {
      border: none;
    }
  }
}
</style>