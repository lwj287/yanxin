<template>
  <div class="statistics-container">
    <el-row :gutter="24">
      <el-col :span="6" v-for="(item, index) in statCards" :key="index">
        <el-card shadow="never" class="stat-card">
          <div class="stat-icon" :style="{ background: item.color + '15', color: item.color }">
            <el-icon><component :is="item.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-title">{{ item.title }}</div>
            <div class="stat-value">{{ item.value }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="24" class="chart-row">
      <el-col :span="16">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">近七日学习进度趋势</div>
          </template>
          <div ref="lineChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">热门课程分类占比</div>
          </template>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { User, VideoCamera, DataAnalysis, Trophy } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getDashboardStat } from '@/api/stat'

const statCards = ref([
  { title: '平台总员工', value: '0', icon: 'User', color: '#409EFF', key: 'userCount' },
  { title: '在线课程数', value: '0', icon: 'VideoCamera', color: '#67C23A', key: 'courseCount' },
  { title: '累计学习进度(总和)', value: '0', icon: 'DataAnalysis', color: '#E6A23C', key: 'totalStudyHours' },
  { title: '认证通过人数', value: '0', icon: 'Trophy', color: '#F56C6C', key: 'certCount' }
])

const loadStatData = async () => {
  try {
    const res: any = await getDashboardStat()
    
    // 更新顶部卡片数据
    statCards.value.forEach(card => {
      if (res.data && res.data[card.key] !== undefined) {
        card.value = Number(res.data[card.key]).toLocaleString()
      } else if (res[card.key] !== undefined) {
        card.value = Number(res[card.key]).toLocaleString()
      }
    })
    
    const realData = res.data || res
    
    // 更新折线图数据
    if (lineChart && realData.trendDates && realData.trendDurations) {
      lineChart.setOption({
        xAxis: {
          data: realData.trendDates
        },
        series: [
          {
            data: realData.trendDurations
          }
        ]
      })
    }
    
    // 更新饼图数据
    if (pieChart && realData.categoryData) {
      pieChart.setOption({
        series: [
          {
            data: realData.categoryData
          }
        ]
      })
    }
  } catch (e) {
    console.error('获取统计数据失败', e)
  }
}

const lineChartRef = ref<HTMLElement | null>(null)
const pieChartRef = ref<HTMLElement | null>(null)

let lineChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null

const initCharts = () => {
  if (lineChartRef.value) {
    lineChart = echarts.init(lineChartRef.value)
    lineChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
        axisLine: { lineStyle: { color: '#E4E7ED' } },
        axisLabel: { color: '#909399' }
      },
      yAxis: {
        type: 'value',
        splitLine: { lineStyle: { color: '#EBEEF5', type: 'dashed' } },
        axisLabel: { color: '#909399' }
      },
      series: [
        {
          name: '学习进度(累加)',
          type: 'line',
          smooth: true,
          symbol: 'none',
          lineStyle: { color: '#409EFF', width: 3 },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(64,158,255,0.3)' },
              { offset: 1, color: 'rgba(64,158,255,0.05)' }
            ])
          },
          data: []
        }
      ]
    })
  }

  if (pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value)
    pieChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: '0', left: 'center', itemWidth: 10, itemHeight: 10, textStyle: { color: '#606266' } },
      color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C'],
      series: [
        {
          name: '课程分类',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 8,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: { show: false },
          data: []
        }
      ]
    })
  }
}

const handleResize = () => {
  lineChart?.resize()
  pieChart?.resize()
}

onMounted(() => {
  nextTick(() => {
    initCharts()
    loadStatData()
    window.addEventListener('resize', handleResize)
  })
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  lineChart?.dispose()
  pieChart?.dispose()
})
</script>

<style scoped lang="scss">
.statistics-container {
  .stat-card {
    border: none;
    border-radius: 16px;
    display: flex;
    align-items: center;
    padding: 24px;
    background: #ffffff;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
    transition: transform 0.3s;
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.04);
    }
    
    :deep(.el-card__body) {
      padding: 0;
      display: flex;
      align-items: center;
      width: 100%;
    }
    
    .stat-icon {
      width: 56px;
      height: 56px;
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 28px;
      margin-right: 20px;
    }
    
    .stat-info {
      flex: 1;
      
      .stat-title {
        font-size: 14px;
        color: #909399;
        margin-bottom: 8px;
      }
      
      .stat-value {
        font-size: 28px;
        font-weight: 600;
        color: #303133;
        line-height: 1;
      }
    }
  }

  .chart-row {
    margin-top: 24px;
  }

  .chart-card {
    border: none;
    border-radius: 16px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);

    :deep(.el-card__header) {
      border-bottom: 1px solid #f0f2f5;
      padding: 16px 24px;
    }

    .card-header {
      font-size: 16px;
      font-weight: 500;
      color: #303133;
    }

    .chart-container {
      height: 320px;
      width: 100%;
    }
  }
}
</style>