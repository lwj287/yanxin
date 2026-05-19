<template>
  <view class="container" v-if="activity">
    <view class="header-card">
      <view class="title-row">
        <text class="title">{{ activity.activityName }}</text>
        <text class="status" :class="statusInfo.class">{{ statusInfo.text }}</text>
      </view>
      <view class="info-row">
        <text class="label">活动类型：</text>
        <text class="value">{{ activity.activityType === 1 ? '赠送活动' : '抽奖活动' }}</text>
      </view>
      <view class="info-row">
        <text class="label">活动时间：</text>
        <text class="value">{{ formatDate(activity.startTime) }} 至 {{ formatDate(activity.endTime) }}</text>
      </view>
      <view class="info-row">
        <text class="label">奖品名额：</text>
        <text class="value">{{ activity.redeemCount || 0 }} / {{ activity.issueCount || '不限' }}</text>
      </view>
    </view>

    <view class="desc-card">
      <view class="desc-title">活动奖品</view>
      <view class="coupon-card" v-if="activity.couponName">
        <view class="coupon-left">
          <view class="face-value">
            <text class="symbol">￥</text>
            <text class="amount">{{ activity.faceValue }}</text>
          </view>
          <view class="condition">
            {{ formatCondition(activity.conditionAmount) }}
          </view>
        </view>
        <view class="coupon-right">
          <view class="coupon-name">{{ activity.couponName }}</view>
          <view class="coupon-type">
            {{ activity.activityType === 1 ? '参与即得' : '概率抽取' }}
          </view>
        </view>
      </view>
      <view class="desc-content" v-else>暂无关联奖品信息</view>
    </view>

    <view class="desc-card" style="margin-top: 16px;">
      <view class="desc-title">活动规则</view>
      <view class="desc-content">
        <text v-if="activity.activityType === 1">点击下方按钮即可直接领取专属优惠券，每人限领一次，数量有限，先到先得！</text>
        <text v-if="activity.activityType === 2">点击下方按钮参与抽奖，有概率获得专属优惠券，每人限抽一次，快来试试手气吧！</text>
      </view>
    </view>

    <view class="footer-bar">
      <button 
        class="action-btn" 
        :class="{ disabled: statusInfo.text !== '进行中' || loading }" 
        @click="handleParticipate"
        :disabled="statusInfo.text !== '进行中' || loading"
      >
        {{ loading ? '处理中...' : (activity.activityType === 1 ? '立即领取' : '立即抽奖') }}
      </button>
    </view>
  </view>
  <view class="loading-state" v-else>
    <text>加载中...</text>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { getActivityDetail, participateActivity } from '@/api/index';

const activityId = ref('');
const activity = ref<any>(null);
const loading = ref(false);

const statusInfo = computed(() => {
  if (!activity.value) return { text: '', class: '' };
  
  const now = new Date().getTime();
  const startTime = new Date(activity.value.startTime).getTime();
  const endTime = new Date(activity.value.endTime).getTime();

  if (now < startTime) {
    return { text: '未开始', class: '' };
  } else if (now >= startTime && now <= endTime) {
    return { text: '进行中', class: 'active' };
  } else {
    return { text: '已结束', class: '' };
  }
});

const formatDate = (dateStr: string) => {
  if (!dateStr) return '';
  return dateStr.substring(0, 16); // yyyy-MM-dd HH:mm
};

const formatCondition = (amount: number | string) => {
  if (Number(amount) === 0) return '无门槛';
  return `满${amount}元可用`;
};

const fetchDetail = async () => {
  try {
    const res: any = await getActivityDetail(activityId.value);
    activity.value = res;
  } catch (error) {
    uni.showToast({ title: '获取详情失败', icon: 'none' });
  }
};

const handleParticipate = async () => {
  if (statusInfo.value.text !== '进行中') {
    uni.showToast({ title: '活动未在进行中', icon: 'none' });
    return;
  }
  
  if (loading.value) return;
  loading.value = true;
  
  try {
    const res: any = await participateActivity(activityId.value);
    // 这里因为后端做了统一的Result包装，并且我们在request.ts里直接返回了res.data.data (字符串) 或 抛出异常
    uni.showModal({
      title: '提示',
      content: res,
      showCancel: false,
      success: () => {
        // 刷新一下详情数据，比如更新redeemCount
        fetchDetail();
      }
    });
  } catch (error: any) {
    // 错误在request.ts里已经被抛出
  } finally {
    loading.value = false;
  }
};

onLoad((options) => {
  if (options && options.id) {
    activityId.value = options.id;
    fetchDetail();
  } else {
    uni.showToast({ title: '参数错误', icon: 'none' });
    setTimeout(() => {
      uni.navigateBack();
    }, 1500);
  }
});
</script>

<style lang="scss" scoped>
.container {
  padding: 20px;
  padding-bottom: 100px;
  background-color: #f5f7fa;
  min-height: 100vh;
}
.header-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);

  .title-row {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20px;
    border-bottom: 1px solid #ebeef5;
    padding-bottom: 16px;

    .title {
      font-size: 20px;
      font-weight: bold;
      color: #303133;
      flex: 1;
      margin-right: 12px;
    }
    .status {
      font-size: 12px;
      padding: 4px 10px;
      border-radius: 12px;
      background: #f4f4f5;
      color: #909399;
      white-space: nowrap;
      &.active {
        background: #e1f3d8;
        color: #67c23a;
      }
    }
  }

  .info-row {
    margin-bottom: 12px;
    font-size: 14px;
    display: flex;
    .label {
      color: #909399;
      width: 80px;
    }
    .value {
      color: #303133;
      flex: 1;
    }
  }
}

.desc-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);

  .desc-title {
    font-size: 16px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 12px;
  }
  .desc-content {
    font-size: 14px;
    color: #606266;
    line-height: 1.6;
  }

  .coupon-card {
    display: flex;
    background: linear-gradient(135deg, #fff5f5, #ffe0e0);
    border-radius: 8px;
    padding: 16px;
    align-items: center;
    border: 1px solid #ffcccc;
    
    .coupon-left {
      flex: 0 0 100px;
      text-align: center;
      border-right: 1px dashed #ff9999;
      padding-right: 12px;
      margin-right: 12px;
      
      .face-value {
        color: #f56c6c;
        .symbol {
          font-size: 14px;
          font-weight: bold;
        }
        .amount {
          font-size: 28px;
          font-weight: bold;
        }
      }
      .condition {
        font-size: 12px;
        color: #909399;
        margin-top: 4px;
      }
    }
    
    .coupon-right {
      flex: 1;
      .coupon-name {
        font-size: 16px;
        font-weight: bold;
        color: #303133;
        margin-bottom: 8px;
      }
      .coupon-type {
        font-size: 12px;
        color: #f56c6c;
        background: #fff;
        padding: 2px 6px;
        border-radius: 4px;
        display: inline-block;
      }
    }
  }
}

.footer-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 20px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  background: #ffffff;
  box-shadow: 0 -2px 10px 0 rgba(0, 0, 0, 0.05);

  .action-btn {
    background: linear-gradient(90deg, #409eff, #66b1ff);
    color: white;
    border-radius: 24px;
    font-size: 16px;
    font-weight: bold;
    height: 48px;
    line-height: 48px;
    border: none;
    
    &::after {
      border: none;
    }

    &.disabled {
      background: #a0cfff;
      opacity: 0.8;
    }
  }
}

.loading-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  color: #909399;
  font-size: 14px;
}
</style>