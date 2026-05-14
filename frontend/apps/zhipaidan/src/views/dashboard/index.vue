<template>
  <div class="dashboard-container">
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card dispatch" v-loading="loading">
          <div class="stat-icon"><el-icon><Timer /></el-icon></div>
          <div class="stat-info">
            <div class="title">待派订单</div>
            <div class="value">{{ statistics.dispatchOrders || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card dispatched" v-loading="loading">
          <div class="stat-icon"><el-icon><Finished /></el-icon></div>
          <div class="stat-info">
            <div class="title">今日已派发</div>
            <div class="value">{{ statistics.todayDispatched || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card free" v-loading="loading">
          <div class="stat-icon"><el-icon><Avatar /></el-icon></div>
          <div class="stat-info">
            <div class="title">当前空闲家政员</div>
            <div class="value">{{ statistics.freeStaff || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card working" v-loading="loading">
          <div class="stat-icon"><el-icon><Briefcase /></el-icon></div>
          <div class="stat-info">
            <div class="title">服务中家政员</div>
            <div class="value">{{ statistics.workingStaff || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="charts-area">
      <el-col :span="24">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>近期派单趋势 (过去7天)</span>
            </div>
          </template>
          <div class="chart-container" ref="chartRef"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { Timer, Finished, Avatar, Briefcase } from '@element-plus/icons-vue'
import { getDashboardStatistics } from '@/api/dispatch'
import * as echarts from 'echarts'

const statistics = ref<any>({})
const loading = ref(false)
const chartRef = ref<HTMLElement | null>(null)
let chartInstance: echarts.ECharts | null = null

const initChart = () => {
  if (!chartRef.value) return
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }

  const trendData = statistics.value.trendData || { dates: [], counts: [] }

  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#ebeef5',
      textStyle: { color: '#303133' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: trendData.dates,
      axisLine: { lineStyle: { color: '#ebeef5' } },
      axisLabel: { color: '#909399' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#ebeef5', type: 'dashed' } },
      axisLabel: { color: '#909399' }
    },
    series: [
      {
        name: '派发订单数',
        type: 'line',
        smooth: true,
        data: trendData.counts,
        itemStyle: { color: '#409EFF' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64,158,255,0.3)' },
            { offset: 1, color: 'rgba(64,158,255,0.0)' }
          ])
        },
        lineStyle: { width: 3 }
      }
    ]
  }

  chartInstance.setOption(option)
}

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

const fetchStatistics = async () => {
  loading.value = true
  try {
    const res = await getDashboardStatistics()
    statistics.value = res || {}
    await nextTick()
    initChart()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchStatistics()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
  }
})
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
}

.stat-cards {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 16px;
  border: none;
}

:deep(.el-card__body) {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  margin-right: 20px;
}

.stat-info .title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-info .value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

/* 各个卡片的颜色主题 */
.dispatch .stat-icon { background: #fef0f0; color: #f56c6c; }
.dispatched .stat-icon { background: #f0f9eb; color: #67c23a; }
.free .stat-icon { background: #ecf5ff; color: #409EFF; }
.working .stat-icon { background: #fdf6ec; color: #e6a23c; }

.charts-area {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 16px;
  height: 350px;
  display: flex;
  flex-direction: column;
}

:deep(.chart-card .el-card__body) {
  flex: 1;
  padding: 10px;
}

.card-header {
  font-weight: bold;
}

.chart-container {
  width: 100%;
  height: 100%;
}
</style>