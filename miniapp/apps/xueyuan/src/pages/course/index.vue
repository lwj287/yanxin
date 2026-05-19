<template>
  <view class="page">
    <view class="top-wrap">
      <view class="top-row">
        <view class="brand">燕鑫学院</view>
      </view>
      <view class="search-box">
        <input v-model="queryParams.title" class="search-input" placeholder="搜索课程名称" @confirm="onSearch" />
        <button class="search-btn" @click="onSearch">搜索</button>
        <button class="reset-btn" @click="onReset">重置</button>
      </view>
    </view>

    <view class="list-container">
      <view v-if="!courses.length" class="empty">暂无相关课程</view>       

      <view class="course-list">
        <view class="course-card" v-for="item in courses" :key="item.id" @click="goToCourse(item.id)">
          <video v-if="item.coverUrl" class="cover" :src="item.coverUrl" :controls="false" :show-center-play-btn="false" object-fit="cover"></video>
          <view v-else class="cover" style="display: flex; align-items: center; justify-content: center; color: #909399; font-size: 14px;">
            <text>暂无视频</text>
          </view>
          <view class="info">
            <text class="title">{{ item.title }}</text>
            <view class="tags">
              <text class="tag">{{ item.category }}</text>
              <text class="tag level" v-if="item.level">{{ item.level }}</text> 
            </view>
            <text class="desc">{{ item.description }}</text>
          </view>
        </view>
      </view>

      <view class="pager" v-if="courses.length > 0 || queryParams.current > 1"> 
        <button class="pager-btn" :disabled="queryParams.current <= 1" @click="prev">上一页</button>
        <text class="pager-text">{{ queryParams.current }}</text>
        <button class="pager-btn" :disabled="!hasMore" @click="next">下一页</button>
      </view>
    </view>

  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { getUserInfo, getCourses } from '@/api/index';

const userInfo = ref<any>({});
const courses = ref<any[]>([]);
const isLogin = computed(() => !!uni.getStorageSync('token'));

const queryParams = ref({
  current: 1,
  size: 10,
  title: ''
});
const hasMore = ref(true);
const loading = ref(false);

const fetchCourses = async (reset = false) => {
  if (reset) {
    queryParams.value.current = 1;
    courses.value = [];
    hasMore.value = true;
  }
  if (!hasMore.value || loading.value) return;

  loading.value = true;
  try {
    const params = { ...queryParams.value };
    if (!params.title) {
      delete params.title;
    }
    const res: any = await getCourses(params);
    const newRecords = res.records || [];

    const mappedRecords = newRecords.map((item: any) => {
      // 处理相对路径，拼接完整的后端地址
      let videoUrl = item.videoUrl;
      if (videoUrl && videoUrl.startsWith('/api')) {
        videoUrl = 'http://localhost:8082' + videoUrl;
      }
      return {
        id: item.courseId,
        title: item.courseName,
        category: item.courseType || '其他',
        level: item.difficulty || '初级',
        description: item.courseContent,
        coverUrl: videoUrl,
        createTime: item.createTime
      };
    });

    courses.value = reset ? mappedRecords : [...courses.value, ...mappedRecords];
    if (newRecords.length < queryParams.value.size) {
      hasMore.value = false;
    }
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
};

const onSearch = () => {
  fetchCourses(true);
};

const onReset = () => {
  queryParams.value.title = '';
  fetchCourses(true);
};

const prev = () => {
  if (queryParams.value.current <= 1) return;
  queryParams.value.current -= 1;
  fetchCourses(true);
};

const next = () => {
  if (!hasMore.value) return;
  queryParams.value.current += 1;
  fetchCourses(true);
};

onShow(async () => {
  try {
    if (isLogin.value) {
      const userRes = await getUserInfo();
      userInfo.value = userRes;
    } else {
      userInfo.value = {};
    }

    fetchCourses(true);
  } catch (e) {
    console.error(e);
  }
});

const goMy = () => {
  uni.redirectTo({ url: '/pages/profile/index' })
}

const goToCourse = (id: string) => {
  uni.navigateTo({
    url: `/pages/course/detail?id=${id}`
  });
};
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: calc(100rpx + env(safe-area-inset-bottom) + 20px);
}
.top-wrap {
  padding: 20px 16px;
  background: #ffffff;
}
.top-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.brand {
  font-size: 22px;
  font-weight: 700;
  color: #409eff;
}
.user-status {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
  background: #f5f7fa;
  padding: 4px 12px 4px 4px;
  border-radius: 20px;

  .mini-avatar {
    width: 24px;
    height: 24px;
    border-radius: 12px;
    background-color: #e4e7ed;
  }
}
.search-box {
  display: flex;
  gap: 8px;
  align-items: center;
}
.search-input {
  flex: 1;
  height: 36px;
  background: #f5f7fa;
  border-radius: 18px;
  padding: 0 16px;
  font-size: 14px;
  color: #303133;
}
.search-btn {
  height: 36px;
  line-height: 36px;
  padding: 0 16px;
  border-radius: 18px;
  background: #409eff;
  color: #fff;
  font-size: 14px;
  margin: 0;
}
.reset-btn {
  height: 36px;
  line-height: 36px;
  padding: 0 12px;
  border-radius: 18px;
  background: #f5f7fa;
  color: #606266;
  font-size: 14px;
  margin: 0;
}

.list-container {
  padding: 12px 16px;
}

.empty {
  text-align: center;
  color: #909399;
  padding: 40px 0;
  font-size: 14px;
}

.course-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.course-card {
  padding: 0;
  overflow: hidden;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);

  .cover {
    width: 100%;
    height: 140px;
    background-color: #ebeef5;
  }

  .info {
    padding: 16px;

    .title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 8px;
      display: block;
    }

    .tags {
      display: flex;
      gap: 8px;
      margin-bottom: 8px;

      .tag {
        font-size: 12px;
        color: #409EFF;
        background-color: #ecf5ff;
        padding: 4px 8px;
        border-radius: 4px;

        &.level {
          color: #E6A23C;
          background-color: #fdf6ec;
        }
      }
    }

    .desc {
      font-size: 13px;
      color: #909399;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
      overflow: hidden;
    }
  }
}

.pager {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
}
.pager-btn {
  width: 90px;
  height: 36px;
  line-height: 36px;
  background: #fff;
  border-radius: 18px;
  font-size: 14px;
  color: #303133;
  border: 1px solid #ebeef5;
  margin: 0;
}
.pager-btn[disabled] {
  color: #c0c4cc;
  background: #f5f7fa;
}
.pager-text {
  font-size: 14px;
  color: #606266;
}

.tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 100rpx;
  background: #fff;
  border-top: 1rpx solid #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 0 100rpx;
  padding-bottom: env(safe-area-inset-bottom);
  z-index: 99;
}
.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #999;
}
.tab-item.active {
  color: #409eff;
}
.tab-icon {
  margin-bottom: 4rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
.tab-text {
  font-size: 22rpx;
  font-weight: 500;
  margin-top: 4rpx;
}
</style>