<template>
  <el-container class="layout-container">
    <el-aside width="240px" class="aside">
      <div class="logo">
        <el-icon class="logo-icon"><DataAnalysis /></el-icon>
        <h2>燕鑫 BI 系统</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        background-color="#ffffff"
        text-color="#303133"
        active-text-color="#409eff"
        @select="handleSelect"
      >
        <el-menu-item v-for="sys in systems" :key="sys.id" :index="sys.id">
          <el-icon><component :is="sys.icon" /></el-icon>
          <span>{{ sys.name }}大屏</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <span class="page-title">{{ currentSystemName }}聚合看板</span>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click">
            <span class="user-info">
              <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
              <span class="username">Admin</span>
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
        <!-- 通过路由传参或者全局状态控制 dashboard 的显示，这里我们直接保留 router-view -->
        <!-- 并利用路由参数或者 query 将选择的子系统传递给 dashboard -->
        <router-view :key="$route.fullPath" />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { 
  DataAnalysis, 
  TrendCharts, 
  Odometer, 
  PieChart, 
  Histogram, 
  DataLine,
  Platform,
  ArrowDown
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const systems = [
  { id: 'zhaoxianbao', name: '招贤宝', icon: DataAnalysis },
  { id: 'xueyuan', name: '燕鑫学院', icon: TrendCharts },
  { id: 'anxinbao', name: '安心保', icon: Odometer },
  { id: 'keyingmen', name: '客盈门', icon: PieChart },
  { id: 'zhijian', name: '慧质检', icon: Histogram },
  { id: 'zhipaidan', name: '智派单', icon: DataLine },
  { id: 'suoguanjia', name: '锁管家', icon: Platform }
];

const activeMenu = computed(() => {
  return (route.query.system as string) || systems[0].id
})

const currentSystemName = computed(() => {
  const sys = systems.find(s => s.id === activeMenu.value)
  return sys ? sys.name : ''
})

const handleSelect = (index: string) => {
  router.push({
    path: '/dashboard',
    query: { system: index }
  })
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
</style>
