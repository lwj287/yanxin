<template>
  <el-container class="admin-layout">
    <el-aside width="240px" class="aside-panel">
      <div class="logo">
        <el-icon class="logo-icon"><Briefcase /></el-icon>
        <span>燕鑫招贤宝</span>
      </div>
      <el-menu
        router
        :default-active="$route.path"
        class="menu-panel"
        background-color="#ffffff"
        text-color="#303133"
        active-text-color="#409eff"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon class="menu-icon"><DataAnalysis /></el-icon>
          <span>首页概览</span>
        </el-menu-item>
        <el-menu-item index="/admin/jobs">
          <el-icon class="menu-icon"><Briefcase /></el-icon>
          <span>岗位管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/resumes">
          <el-icon class="menu-icon"><Document /></el-icon>
          <span>简历管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/interviews">
          <el-icon class="menu-icon"><ChatDotRound /></el-icon>
          <span>面试管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/onboarding">
          <el-icon class="menu-icon"><UserFilled /></el-icon>
          <span>入职管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header-bar">
        <div class="header-left">
          <div class="title">招聘管理后台</div>
          <div class="sub">提升招聘效率，闭环管理入职流程</div>
        </div>
        <div class="header-right">
          <div class="user-chip">
            <span class="user-dot">{{ (realName || '管').slice(0, 1) }}</span>
            <span>{{ realName || '管理员' }}</span>
          </div>
          <el-button type="danger" plain @click="logout" size="small">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main-area">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Briefcase, ChatDotRound, DataAnalysis, Document, UserFilled } from '@element-plus/icons-vue'
const router = useRouter()
const realName = sessionStorage.getItem('realName')
const logout = () => {
  sessionStorage.clear()
  router.push('/login')
}
</script>

<style scoped lang="scss">
.admin-layout { height: 100vh; }
.aside-panel {
  background: #ffffff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.02);
  z-index: 10;
  display: flex;
  flex-direction: column;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #f0f2f5;
  gap: 8px;
  color: #409eff;
  
  .logo-icon {
    font-size: 24px;
  }
  
  span {
    font-size: 20px;
    font-weight: 700;
    letter-spacing: 1px;
  }
}
.menu-panel {
  border-right: none;
  background: transparent;
  flex: 1;
}
.menu-panel :deep(.el-menu-item) {
  display: flex;
  align-items: center;
  color: #303133;
  font-size: 14px;
  margin: 8px 16px;
  border-radius: 12px;
  height: 50px;
  line-height: 50px;
  padding: 0 16px;
}
.menu-icon {
  font-size: 16px;
  margin-right: 12px;
}
.menu-panel :deep(.el-menu-item:hover) {
  background: #f7f8fa;
  color: #1d2129;
}
.menu-panel :deep(.el-menu-item.is-active) {
  color: #409eff;
  font-weight: 600;
  background: #ecf5ff;
}
.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #ffffff;
  height: 60px;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.02);
  z-index: 9;
}
.header-left {
  display: flex;
  align-items: baseline;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.user-chip {
  color: #4e5969;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}
.user-dot {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 12px;
}
.title { font-size: 16px; font-weight: 600; color: #1d2129; }
.sub { color: #86909c; font-size: 12px; margin-left: 12px; }
.main-area { background: #f6f6f8; padding: 24px; }
</style>
