<template>
  <el-container class="layout-container">
    <el-aside width="240px" class="aside">
      <div class="logo">
        <el-icon class="logo-icon"><Camera /></el-icon>
        <h2>燕鑫慧质检</h2>
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
        <el-menu-item index="/service-type">
            <el-icon><Menu /></el-icon>
            <span>服务类型管理</span>
          </el-menu-item>
          <el-menu-item index="/template">
            <el-icon><Document /></el-icon>
            <span>质检模板配置</span>
          </el-menu-item>
          <el-menu-item index="/task">
            <el-icon><Monitor /></el-icon>
            <span>质检任务处理</span>
          </el-menu-item>
          <el-menu-item index="/appeal">
            <el-icon><Warning /></el-icon>
            <span>申诉处理</span>
          </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <span class="page-title">{{ route.meta.title || '首页' }}</span>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click">
            <span class="user-info">
              <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
              <span class="username">管理员</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main">
        <router-view v-slot="{ Component }">
          <component :is="Component" />
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { User, Stamp, Document, Briefcase, Tickets, Warning, DataAnalysis, ArrowDown, Camera, Monitor, Menu } from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)
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

</style>
