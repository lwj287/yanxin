<template>
  <view class="exam-container">
    <view class="header-card card" @click="handleHeaderClick">
      <view class="title">我的认证</view>
      <view class="level-badge" v-if="isLogin && certLevel">
        <text class="level-text">{{ certLevel }}</text>
        <text class="desc">已获得官方家政服务认证</text>
      </view>
      <view class="level-badge uncertified" v-else-if="isLogin">
        <text class="level-text">未认证</text>
        <text class="desc">请完成下方考试以获取认证</text>
      </view>
      <view class="level-badge uncertified" v-else>
        <text class="level-text">点击登录</text>
        <text class="desc">登录后查看认证状态</text>
      </view>
    </view>
    
    <view class="section-title">可用考试</view>
    <view class="exam-list">
      <view class="exam-card card" v-for="item in exams" :key="item.id">
        <view class="info">
          <text class="title">{{ item.title }}</text>
          <text class="desc">总分：{{ item.totalScore }}分 | 及格：{{ item.passScore }}分</text>
        </view>
        <button class="start-btn" @click="startExam(item.id)">开始考试</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { getExams, getCerts } from '@/api/index';

const isLogin = computed(() => !!uni.getStorageSync('token'));
const certLevel = ref(''); 
const exams = ref<any[]>([]);
const loading = ref(false);

const fetchExamsAndCerts = async () => {
  if (loading.value) return;
  loading.value = true;
  try {
    // 1. 获取考试列表
    const examRes: any = await getExams({
      current: 1,
      size: 50,
      status: 1 // 只查询进行中的考试
    });
    exams.value = examRes.records || [];

    // 2. 如果已登录，获取该用户的最高认证（简易模拟：查询该用户的已通过认证）
    if (isLogin.value) {
      const certRes: any = await getCerts({
        current: 1,
        size: 1,
        auditStatus: 1 // 只查已通过
      });
      if (certRes.records && certRes.records.length > 0) {
        certLevel.value = certRes.records[0].certName || '认证服务员';
      } else {
        certLevel.value = '';
      }
    }
  } catch (e) {
    console.error('获取数据失败', e);
  } finally {
    loading.value = false;
  }
};

onShow(() => {
  fetchExamsAndCerts();
});

const handleHeaderClick = () => {
  if (!isLogin.value) {
    uni.navigateTo({ url: '/pages/login/index' });
  }
};

const startExam = (id: number) => {
  if (!isLogin.value) {
    uni.navigateTo({ url: '/pages/login/index' });
    return;
  }
  uni.showToast({
    title: '考试系统升级中，敬请期待',
    icon: 'none'
  });
};
</script>

<style lang="scss" scoped>
.exam-container {
  padding: 20px 16px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.header-card {
  background: linear-gradient(135deg, #e6a23c 0%, #d38f28 100%);
  color: #fff;
  padding: 24px;
  
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
  margin: 24px 0 16px;
}

.exam-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.exam-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  
  .info {
    flex: 1;
    
    .title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 8px;
      display: block;
    }
    
    .desc {
      font-size: 13px;
      color: #909399;
    }
  }
  
  .start-btn {
    background-color: #409EFF;
    color: #fff;
    font-size: 13px;
    height: 32px;
    line-height: 32px;
    padding: 0 16px;
    border-radius: 16px;
    margin: 0;
  }
}
</style>