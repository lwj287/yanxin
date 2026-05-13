<template>
  <div class="dashboard-container">
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-title">总设备数</div>
              <div class="stat-value">{{ statData.totalDevices }}</div>
            </div>
            <div class="stat-icon bg-blue">
              <el-icon><Monitor /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-title">在线设备</div>
              <div class="stat-value text-success">{{ statData.onlineDevices }}</div>
            </div>
            <div class="stat-icon bg-green">
              <el-icon><Check /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-title">故障报警</div>
              <div class="stat-value text-danger">{{ statData.faultDevices }}</div>
            </div>
            <div class="stat-icon bg-red">
              <el-icon><Warning /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-title">开锁记录数</div>
              <div class="stat-value">{{ statData.totalLogs }}</div>
            </div>
            <div class="stat-icon bg-purple">
              <el-icon><Document /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表展示区 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>最近7天设备开锁频次趋势</span>
            </div>
          </template>
          <div ref="chartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { Monitor, Check, Warning, Document } from '@element-plus/icons-vue'
import { getDashboardStat, DashboardStat } from '@/api/suoguanjia'
import * as echarts from 'echarts'

const statData = ref<DashboardStat>({
  totalDevices: 0,
  onlineDevices: 0,
  faultDevices: 0,
  totalLogs: 0,
  trendDates: [],
  trendUnlockCounts: []
})

const chartRef = ref<HTMLElement | null>(null)
let chartInstance: echarts.ECharts | null = null

onMounted(async () => {
  const res = await getDashboardStat()
  if (res) {
    statData.value = res
    
    // 初始化 ECharts
    if (chartRef.value) {
      chartInstance = echarts.init(chartRef.value)
      
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: res.trendDates || [],
          axisLine: {
            lineStyle: {
              color: '#E0E6F1'
            }
          },
          axisLabel: {
            color: '#606266'
          }
        },
        yAxis: {
          type: 'value',
          minInterval: 1, // 强制纵坐标刻度只能为整数
          axisLine: {
            show: false
          },
          axisTick: {
            show: false
          },
          splitLine: {
            lineStyle: {
              color: '#EBEEF5',
              type: 'dashed'
            }
          }
        },
        series: [
          {
            name: '开锁频次',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            itemStyle: {
              color: '#409EFF'
            },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [{
                  offset: 0, color: 'rgba(64,158,255,0.3)'
                }, {
                  offset: 1, color: 'rgba(64,158,255,0.05)'
                }]
              }
            },
            data: res.trendUnlockCounts || []
          }
        ]
      }
      
      chartInstance.setOption(option)
    }
  }
  
  // 监听窗口大小变化自动重绘
  window.addEventListener('resize', handleResize)
})

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
  }
})
</script>

<style scoped lang="scss">
.dashboard-container {
  padding: 24px;
  background: transparent;

  .stat-row {
    margin-bottom: 24px;
  }
  
  .stat-card {
    border-radius: 16px;
    border: none;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02) !important;
    
    .stat-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .stat-info {
        .stat-title {
          font-size: 14px;
          color: #909399;
          margin-bottom: 8px;
        }
        
        .stat-value {
          font-size: 28px;
          font-weight: bold;
          color: #303133;
          
          &.text-success { color: #67c23a; }
          &.text-danger { color: #f56c6c; }
        }
      }
      
      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        justify-content: center;
        align-items: center;
        font-size: 24px;
        color: #fff;
        
        &.bg-blue { background-color: #409eff; }
        &.bg-green { background-color: #67c23a; }
        &.bg-red { background-color: #f56c6c; }
        &.bg-purple { background-color: #8a2be2; }
      }
    }
  }

  .chart-row {
    .chart-card {
      border-radius: 16px;
      border: none;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02) !important;
      
      :deep(.el-card__header) {
        border-bottom: 1px solid #ebeef5;
        padding: 20px 24px;
      }
      
      .card-header {
        font-weight: 600;
        font-size: 16px;
        color: #303133;
      }

      :deep(.el-card__body) {
        padding: 24px;
      }

      .chart {
        width: 100%;
        height: 350px;
      }
    }
  }
}
</style>
