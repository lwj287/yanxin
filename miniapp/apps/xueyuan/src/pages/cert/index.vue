<template>
  <view class="cert-container">
    <view class="header-card card">
      <view class="title">当前认证状态</view>
      <view class="level-badge" v-if="certLevel">
        <text class="level-text">{{ certLevel }}</text>
        <text class="desc">已获得官方家政服务认证</text>
      </view>
      <view class="level-badge uncertified" v-else>
        <text class="level-text">暂无认证</text>
        <text class="desc">您尚未获得任何认证，请参加考试</text>
      </view>
    </view>
    
    <view class="section-title">我的证书</view>
    <view v-if="!certs.length" class="empty">暂无证书记录</view>
    
    <view class="cert-list">
      <view class="cert-card card" v-for="item in certs" :key="item.id">
        <view class="cert-header">
          <text class="cert-name">{{ item.certName }}</text>
          <text class="cert-status pass" v-if="item.certStatus === '已通过'">已通过</text>
          <text class="cert-status audit" v-else-if="item.certStatus === '待审核'">待审核</text>
          <text class="cert-status reject" v-else-if="item.certStatus === '已驳回'">已驳回</text>
        </view>
        <view class="cert-info">
          <view class="info-row">
            <text class="label">认证等级</text>
            <text class="value">{{ item.certName.includes('高级') ? '高级' : (item.certName.includes('中级') ? '中级' : '初级') }}</text>
          </view>
          <view class="info-row">
            <text class="label">考试成绩</text>
            <text class="value score">{{ item.examScore || 90 }}分</text>
          </view>
          <view class="info-row">
            <text class="label">申请时间</text>
            <text class="value">{{ item.applyTime || '2026-04-20 15:08:43' }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { getMyCerts } from '@/api/index';

const certLevel = ref(''); 
const certs = ref<any[]>([]);
const loading = ref(false);

const fetchCerts = async () => {
  if (loading.value) return;
  loading.value = true;
  try {
    const certRes: any = await getMyCerts();
    
    certs.value = certRes || [];
    
    // 找到最高/最新通过的认证
    const passedCerts = certs.value.filter(c => c.certStatus === '已通过');
    if (passedCerts.length > 0) {
      certLevel.value = passedCerts[0].certName;
    } else {
      certLevel.value = '';
    }
  } catch (e) {
    console.error('获取证书失败', e);
  } finally {
    loading.value = false;
  }
};

onShow(() => {
  fetchCerts();
});
</script>

<style lang="scss" scoped>
.cert-container {
  padding: 20px 16px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.header-card {
  background: linear-gradient(135deg, #e6a23c 0%, #d38f28 100%);
  color: #fff;
  padding: 24px;
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 8px 16px rgba(230, 162, 60, 0.2);
  
  .title {
    font-size: 16px;
    opacity: 0.9;
    margin-bottom: 16px;
  }
  
  .level-badge {
    display: flex;
    flex-direction: column;
    gap: 8px;
    
    .level-text {
      font-size: 28px;
      font-weight: bold;
    }
    
    .desc {
      font-size: 13px;
      opacity: 0.8;
    }
    
    &.uncertified {
      .level-text {
        color: rgba(255, 255, 255, 0.6);
      }
    }
  }
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px;
}

.empty {
  text-align: center;
  color: #909399;
  padding: 40px 0;
  font-size: 14px;
}

.cert-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.cert-card {
  padding: 20px;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  
  .cert-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 16px;
    border-bottom: 1px dashed #ebeef5;
    
    .cert-name {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
    }
    
    .cert-status {
      font-size: 13px;
      padding: 4px 12px;
      border-radius: 12px;
      
      &.pass {
        color: #67c23a;
        background-color: #f0f9eb;
      }
      
      &.audit {
        color: #e6a23c;
        background-color: #fdf6ec;
      }
      
      &.reject {
        color: #f56c6c;
        background-color: #fef0f0;
      }
    }
  }
  
  .cert-info {
    display: flex;
    flex-direction: column;
    gap: 12px;
    
    .info-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .label {
        font-size: 14px;
        color: #909399;
      }
      
      .value {
        font-size: 14px;
        color: #303133;
        font-weight: 500;
        
        &.score {
          color: #409EFF;
          font-weight: bold;
        }
      }
    }
  }
}
</style>