<template>
  <view class="profile-container">
    <view class="user-header" @click="handleHeaderClick">
      <image class="avatar" :src="isLogin && staffInfo.avatar ? staffInfo.avatar : '/static/tabbar/profile_active.png'" mode="aspectFill" />
      <view class="info" v-if="isLogin">
        <text class="name">{{ staffInfo.staffName || '张阿姨' }}</text>
        <text class="id">工号：{{ staffInfo.id || '1' }} | 手机：{{ staffInfo.staffPhone || '13800138000' }}</text>
      </view>
      <view class="info" v-else>
        <text class="name">点击登录</text>
        <text class="id">登录后享受更多服务</text>
      </view>
    </view>
    
    <view class="status-card" :class="statusClass" v-if="isLogin">
      <text class="status-text">{{ statusText }}</text>
      <text class="status-desc">目前状态</text>
    </view>

    <view class="skills-section" v-if="isLogin">
      <view class="section-header">
        <text class="section-title">我的技能</text>
        <view class="edit-btn" @click="openEditSkills">
          <text class="icon">✎</text>
          <text>编辑</text>
        </view>
      </view>
      <view class="skill-tags" v-if="staffInfo.skills && staffInfo.skills.length">
        <text class="tag" v-for="(skill, index) in staffInfo.skills" :key="index">{{ skill }}</text>
      </view>
      <view class="no-skills" v-else>
        <text>暂无技能，点击编辑添加</text>
      </view>
    </view>

    <view class="menu-list card">
      <view class="menu-item" @click="goToOrder">
        <text class="icon">📝</text>
        <text class="title">我的派单</text>
        <text class="arrow">›</text>
      </view>
      <view class="menu-item" @click="showToast('功能开发中')">
        <text class="icon">⚙️</text>
        <text class="title">设置</text>
        <text class="arrow">›</text>
      </view>
    </view>

    <button class="logout-btn" v-if="isLogin" @click="handleLogout">退出登录</button>

    <!-- 编辑技能弹窗 -->
    <view class="modal-mask" v-if="showEditModal" @click="closeEditSkills">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">编辑技能</text>
          <text class="close-icon" @click="closeEditSkills">×</text>
        </view>
        <view class="modal-body">
          <view class="current-skills">
            <text class="sub-title">已添加技能</text>
            <view class="skill-tags">
              <view class="tag edit-tag" v-for="(skill, index) in editingSkills" :key="index">
                <text>{{ skill }}</text>
                <text class="delete-icon" @click="removeSkill(index)">×</text>
              </view>
              <text class="empty-text" v-if="editingSkills.length === 0">暂无技能</text>
            </view>
          </view>
          <view class="add-skill">
            <text class="sub-title">添加新技能</text>
            <view class="input-group">
              <input class="skill-input" v-model="newSkill" placeholder="输入技能名称" />
              <button class="add-btn" @click="addSkill">添加</button>
            </view>
          </view>
        </view>
        <view class="modal-footer">
          <button class="cancel-btn" @click="closeEditSkills">取消</button>
          <button class="save-btn" @click="saveSkills">保存</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { getStaffById, updateStaff } from '@/api/index';

const staffInfo = ref<any>({});
const isLogin = ref(false);

const showEditModal = ref(false);
const editingSkills = ref<string[]>([]);
const newSkill = ref('');

const statusText = computed(() => {
  if (!isLogin.value) return '未知';
  const status = staffInfo.value.currentStatus;
  if (status === 0) return '空闲';
  if (status === 1) return '服务中';
  if (status === 2) return '休息/请假';
  return '未知';
});

const statusClass = computed(() => {
  if (!isLogin.value) return '';
  const status = staffInfo.value.currentStatus;
  if (status === 0) return 'status-free';
  if (status === 1) return 'status-busy';
  if (status === 2) return 'status-rest';
  return '';
});

onShow(async () => {
  isLogin.value = !!uni.getStorageSync('token');
  if (isLogin.value) {
    try {
      const staffId = uni.getStorageSync('token');
      const res = await getStaffById(staffId); 
      staffInfo.value = res || {};
    } catch (e) {
      console.error(e);
    }
  } else {
    staffInfo.value = {};
  }
});

const handleHeaderClick = () => {
  if (!isLogin.value) {
    uni.navigateTo({ url: '/pages/login/index' });
  }
};

const goToOrder = () => {
  if (!isLogin.value) {
    uni.navigateTo({ url: '/pages/login/index' });
    return;
  }
  uni.switchTab({ url: '/pages/order/index' });
};

const showToast = (title: string) => {
  if (!isLogin.value) {
    uni.navigateTo({ url: '/pages/login/index' });
    return;
  }
  uni.showToast({ title, icon: 'none' });
};

const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        uni.removeStorageSync('token');
        staffInfo.value = {};
        uni.reLaunch({ url: '/pages/login/index' });
      }
    }
  });
};

const openEditSkills = () => {
  editingSkills.value = [...(staffInfo.value.skills || [])];
  newSkill.value = '';
  showEditModal.value = true;
};

const closeEditSkills = () => {
  showEditModal.value = false;
};

const removeSkill = (index: number) => {
  editingSkills.value.splice(index, 1);
};

const addSkill = () => {
  const val = newSkill.value.trim();
  if (!val) {
    uni.showToast({ title: '请输入技能名称', icon: 'none' });
    return;
  }
  if (editingSkills.value.includes(val)) {
    uni.showToast({ title: '技能已存在', icon: 'none' });
    return;
  }
  editingSkills.value.push(val);
  newSkill.value = '';
};

const saveSkills = async () => {
  try {
    uni.showLoading({ title: '保存中...' });
    const params = {
      id: staffInfo.value.id, // 这里需要传后端需要的更新参数，如果是根据 id 更新
      staffId: staffInfo.value.staffId,
      skills: editingSkills.value
    };
    await updateStaff(params);
    uni.hideLoading();
    uni.showToast({ title: '保存成功', icon: 'success' });
    staffInfo.value.skills = [...editingSkills.value];
    showEditModal.value = false;
  } catch (e: any) {
    uni.hideLoading();
    uni.showToast({ title: e.message || '保存失败', icon: 'none' });
  }
};
</script>

<style lang="scss" scoped>
.profile-container {
  padding: 20px 16px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.user-header {
  display: flex;
  align-items: center;
  padding: 24px 16px;
  background: #fff;
  border-radius: 20px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);

  .avatar {
    width: 64px;
    height: 64px;
    border-radius: 32px;
    background-color: #e4e7ed;
    margin-right: 16px;
  }

  .info {
    display: flex;
    flex-direction: column;

    .name {
      font-size: 20px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 4px;
    }

    .id {
      font-size: 13px;
      color: #909399;
    }
  }
}

.status-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px;
  border-radius: 16px;
  margin-bottom: 24px;
  
  .status-text {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 4px;
  }

  .status-desc {
    font-size: 12px;
    opacity: 0.8;
  }

  &.status-free {
    background-color: #e1f3d8;
    color: #67c23a;
  }
  &.status-busy {
    background-color: #fde2e2;
    color: #f56c6c;
  }
  &.status-rest {
    background-color: #e9e9eb;
    color: #909399;
  }
}

.skills-section {
  background: #fff;
  padding: 20px;
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }

    .edit-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 4px 12px;
      background-color: #f0f7ff;
      border-radius: 14px;
      color: #409eff;
      font-size: 13px;
      font-weight: 500;
      transition: all 0.2s;

      .icon {
        font-size: 14px;
        margin-right: 4px;
      }

      &:active {
        background-color: #d9ecff;
        opacity: 0.8;
      }
    }
  }

  .no-skills {
    padding: 16px 0;
    text-align: center;
    
    text {
      font-size: 14px;
      color: #909399;
    }
  }
}

.skill-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;

  .tag {
    background-color: #ecf5ff;
    color: #409eff;
    padding: 6px 16px;
    border-radius: 20px;
    font-size: 14px;
  }
}

.menu-list {
  background: #fff;
  border-radius: 16px;
  padding: 0 16px;
  margin-bottom: 32px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);

  .menu-item {
    display: flex;
    align-items: center;
    padding: 16px 0;
    border-bottom: 1px solid #ebeef5;

    &:last-child {
      border-bottom: none;
    }

    .icon {
      font-size: 20px;
      margin-right: 12px;
    }

    .title {
      flex: 1;
      font-size: 15px;
      color: #303133;
    }

    .arrow {
      font-size: 24px;
      color: #c0c4cc;
      margin-left: 8px;
    }
  }
}

.logout-btn {
  background-color: #fff;
  color: #F56C6C;
  font-size: 16px;
  height: 48px;
  line-height: 48px;
  border-radius: 12px;
  border: none;

  &::after {
    border: none;
  }
}

.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.modal-content {
  background: #ffffff;
  border-radius: 20px;
  width: 80%;
  max-width: 320px;
  padding: 24px;
  box-sizing: border-box;

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .modal-title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
    }

    .close-icon {
      font-size: 24px;
      color: #909399;
      line-height: 1;
    }
  }

  .modal-body {
    .sub-title {
      font-size: 14px;
      color: #606266;
      margin-bottom: 12px;
      display: block;
    }

    .current-skills {
      margin-bottom: 20px;

      .skill-tags {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;

        .edit-tag {
          display: flex;
          align-items: center;
          background-color: #f4f4f5;
          color: #909399;
          padding: 4px 12px;
          border-radius: 16px;
          font-size: 13px;

          .delete-icon {
            margin-left: 6px;
            font-size: 16px;
            color: #f56c6c;
            line-height: 1;
          }
        }

        .empty-text {
          font-size: 13px;
          color: #c0c4cc;
        }
      }
    }

    .add-skill {
      .input-group {
        display: flex;
        gap: 12px;

        .skill-input {
          flex: 1;
          height: 36px;
          background-color: #f5f7fa;
          border-radius: 8px;
          padding: 0 12px;
          font-size: 14px;
        }

        .add-btn {
          width: 60px;
          height: 36px;
          line-height: 36px;
          font-size: 14px;
          background-color: #409eff;
          color: #fff;
          border-radius: 8px;
          margin: 0;
          padding: 0;
        }
      }
    }
  }

  .modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 24px;

    button {
      margin: 0;
      padding: 0 20px;
      height: 36px;
      line-height: 36px;
      font-size: 14px;
      border-radius: 18px;

      &::after {
        border: none;
      }
    }

    .cancel-btn {
      background-color: #f4f4f5;
      color: #909399;
    }

    .save-btn {
      background-color: #409eff;
      color: #fff;
    }
  }
}
</style>