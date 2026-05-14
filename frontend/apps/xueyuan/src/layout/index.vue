<template>
  <el-container class="layout-container">
    <el-aside width="240px" class="aside">
      <div class="logo">
        <el-icon class="logo-icon"><Reading /></el-icon>
        <h2>燕鑫学院</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        router
        :collapse="false"
        background-color="#ffffff"
        text-color="#303133"
        active-text-color="#409eff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>首页概览</span>
        </el-menu-item>
        <el-menu-item index="/course">
          <el-icon><VideoCamera /></el-icon>
          <span>在线课程</span>
        </el-menu-item>
        <el-menu-item index="/exam">
          <el-icon><Document /></el-icon>
          <span>考试认证</span>
        </el-menu-item>
        <el-menu-item index="/certification">
          <el-icon><CircleCheck /></el-icon>
          <span>认证审批</span>
        </el-menu-item>
        <el-menu-item index="/user">
          <el-icon><User /></el-icon>
          <span>学员管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <span class="page-title">{{ $route.meta.title }}</span>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
              <span class="username">管理员</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { VideoCamera, Document, CircleCheck, User, DataAnalysis, ArrowDown, Reading } from '@element-plus/icons-vue'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => route.path)

onMounted(() => {
  // 页面加载时请求当前用户信息，若 Token 失效（如后端重启），拦截器会自动跳转到登录页
  // 为支持大屏免密演示，暂时注释掉 info 请求
  // request.get('/auth/info').catch(() => {
  //   // 失败已在拦截器中处理
  // })
})

const handleCommand = (command: string) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    router.push('/login')
  }
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
  background-color: #f5f7fa;

  .aside {
    background-color: #ffffff;
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.02);
    z-index: 10;
    transition: width 0.3s;
    
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
      
      h2 {
        margin: 0;
        font-size: 20px;
        font-weight: 700;
        letter-spacing: 1px;
      }
    }
    
    .el-menu-vertical {
      border-right: none;
      
      :deep(.el-menu-item) {
        margin: 8px 16px;
        border-radius: 12px;
        height: 50px;
        line-height: 50px;
        
        &.is-active {
          background-color: #ecf5ff;
          font-weight: 600;
        }
        
        &:hover {
          background-color: #f5f7fa;
        }
      }
    }
  }

  .main-container {
    display: flex;
    flex-direction: column;

    .header {
      height: 60px;
      background-color: #ffffff;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 24px;
      box-shadow: 0 1px 4px rgba(0, 0, 0, 0.02);
      z-index: 9;

      .page-title {
        font-size: 18px;
        font-weight: 500;
        color: #303133;
      }

      .user-info {
        display: flex;
        align-items: center;
        cursor: pointer;
        
        .username {
          margin: 0 8px;
          font-size: 14px;
          color: #606266;
        }
      }
    }

    .main {
      padding: 24px;
      overflow-x: hidden;
      overflow-y: auto;
    }
  }
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s cubic-bezier(0.55, 0, 0.1, 1);
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>