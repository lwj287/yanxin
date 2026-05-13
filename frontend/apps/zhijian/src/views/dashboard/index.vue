<template>
  <div class="dashboard-container">
    <!-- 数据概览卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon bg-primary">
            <el-icon><List /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-title">总任务数</div>
            <div class="stat-value">{{ statData.totalTasks }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon bg-warning">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-title">待处理任务</div>
            <div class="stat-value">{{ statData.pendingTasks }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon bg-success">
            <el-icon><Check /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-title">今日已通过</div>
            <div class="stat-value">{{ statData.todayPassed }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon bg-danger">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-title">待处理申诉</div>
            <div class="stat-value">{{ statData.pendingAppeals }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-container">
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>近7日任务趋势</span>
            </div>
          </template>
          <div class="chart-placeholder">
            <div ref="chart1Ref" class="chart"></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>质检合格率分布</span>
            </div>
          </template>
          <div class="chart-placeholder">
            <div ref="chart2Ref" class="chart"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { List, Clock, Check, Warning } from '@element-plus/icons-vue'
import { getDashboardStat, getDashboardChart } from '@/api/zhijian'
import * as echarts from 'echarts'

const chart1Ref = ref()
const chart2Ref = ref()

const statData = ref({
  totalTasks: 0,
  pendingTasks: 0,
  todayPassed: 0,
  pendingAppeals: 0
})

const loadStatData = async () => {
  try {
    const res: any = await getDashboardStat()
    if (res) {
      statData.value = res
    }
  } catch (error) {
    console.error('Failed to load stat data:', error)
  }
}

const loadChartData = async () => {
  try {
    const res: any = await getDashboardChart()
    if (res) {
      if (chart1Ref.value && chart2Ref.value) {
        const trendChart = echarts.init(chart1Ref.value)
        const distChart = echarts.init(chart2Ref.value)

        // 渲染折线图
        trendChart.setOption({
          tooltip: { trigger: 'axis' },
          xAxis: {
            type: 'category',
            data: res.trend?.dates || [],
            boundaryGap: false
          },
          yAxis: { type: 'value' },
          series: [
            {
              data: res.trend?.counts || [],
              type: 'line',
              smooth: true,
              areaStyle: {
                opacity: 0.3,
                color: '#409eff'
              },
              itemStyle: { color: '#409eff' }
            }
          ],
          grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true }
        })

        // 渲染饼图
        distChart.setOption({
          tooltip: { trigger: 'item' },
          legend: { top: '5%', left: 'center' },
          series: [
            {
              name: '任务状态',
              type: 'pie',
              radius: ['40%', '70%'],
              avoidLabelOverlap: false,
              itemStyle: {
                borderRadius: 10,
                borderColor: '#fff',
                borderWidth: 2
              },
              label: { show: false, position: 'center' },
              emphasis: {
                label: { show: true, fontSize: '20', fontWeight: 'bold' }
              },
              labelLine: { show: false },
              data: res.statusDist || [],
              color: ['#67c23a', '#f56c6c', '#e6a23c']
            }
          ]
        })
        
        window.addEventListener('resize', () => {
          trendChart.resize()
          distChart.resize()
        })
      }
    }
  } catch (error) {
    console.error('Failed to load chart data:', error)
  }
}

onMounted(() => {
  loadStatData()
  loadChartData()
})
</script>

<style scoped lang="scss">
.dashboard-container {
  padding: 24px;
  
  .stat-cards {
    margin-bottom: 24px;
    
    .stat-card {
      border-radius: 16px;
      
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
        justify-content: center;
        align-items: center;
        margin-right: 16px;
        
        .el-icon {
          font-size: 28px;
          color: #fff;
        }
        
        &.bg-primary { background: linear-gradient(135deg, #409eff 0%, #73b3f9 100%); }
        &.bg-warning { background: linear-gradient(135deg, #e6a23c 0%, #f3c78b 100%); }
        &.bg-success { background: linear-gradient(135deg, #67c23a 0%, #95d475 100%); }
        &.bg-danger { background: linear-gradient(135deg, #f56c6c 0%, #f89898 100%); }
      }
      
      .stat-info {
        flex: 1;
        
        .stat-title {
          font-size: 14px;
          color: #909399;
          margin-bottom: 8px;
        }
        
        .stat-value {
          font-size: 24px;
          font-weight: bold;
          color: #303133;
        }
      }
    }
  }

  .charts-container {
    .chart-card {
      height: 400px;
      border-radius: 16px;
      
      .card-header {
        font-weight: bold;
        color: #303133;
      }
      
      .chart-placeholder {
        height: 320px;
        display: flex;
        justify-content: center;
        align-items: center;
        
        .chart {
          width: 100%;
          height: 100%;
        }
      }
    }
  }
}
</style>
