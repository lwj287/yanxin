<template>
  <div class="dashboard-container">
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6" v-for="item in statCards" :key="item.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-header">
            <span>{{ item.title }}</span>
            <el-icon :class="item.iconClass"><component :is="item.icon" /></el-icon>
          </div>
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-footer">
            <span>较昨日 </span>
            <span :class="item.trend > 0 ? 'trend-up' : 'trend-down'">
              {{ item.trend > 0 ? '+' : '' }}{{ item.trend }}%
            </span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">订单趋势</div>
          </template>
          <div ref="orderChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">活动转化率</div>
          </template>
          <div ref="conversionChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, markRaw } from 'vue'
import { User, List, Ticket, Money } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import request from '@/utils/request'

const statCards = ref([
  { title: '总会员数', value: '0', trend: 0, icon: markRaw(User), iconClass: 'icon-blue' },
  { title: '总订单数', value: '0', trend: 0, icon: markRaw(List), iconClass: 'icon-green' },
  { title: '发券数量', value: '0', trend: 0, icon: markRaw(Ticket), iconClass: 'icon-orange' },
  { title: '总收入(元)', value: '￥ 0', trend: 0, icon: markRaw(Money), iconClass: 'icon-red' },
])

const orderChartRef = ref()
const conversionChartRef = ref()

let orderChartInstance: echarts.ECharts | null = null
let conversionChartInstance: echarts.ECharts | null = null

const fetchDashboardData = async () => {
  try {
    const res: any = await request.get('/dashboard/stat')
    
    // 更新顶部卡片
    statCards.value[0].value = res.totalMembers?.toString() || '0'
    statCards.value[1].value = res.totalOrders?.toString() || '0'
    statCards.value[2].value = res.issuedCoupons?.toString() || '0'
    statCards.value[3].value = `￥ ${res.totalIncome || '0'}`

    // 更新订单图表
    if (orderChartInstance) {
      orderChartInstance.setOption({
        xAxis: { data: res.trendDates || [] },
        series: [{ data: res.trendOrders || [] }]
      })
    }

    // 更新漏斗图
    if (conversionChartInstance) {
      conversionChartInstance.setOption({
        series: [{
          data: [
            { value: res.couponReceived || 0, name: '领券' },
            { value: res.couponUsed || 0, name: '核销' },
            { value: res.couponExpired || 0, name: '过期' }
          ]
        }]
      })
    }

  } catch (error) {
    console.error('获取看板数据失败:', error)
  }
}

onMounted(() => {
  initOrderChart()
  initConversionChart()
  fetchDashboardData()
})

const initOrderChart = () => {
  orderChartInstance = echarts.init(orderChartRef.value)
  orderChartInstance.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '订单数',
        data: [0, 0, 0, 0, 0, 0, 0],
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.1 },
        itemStyle: { color: '#409EFF' }
      }
    ]
  })
}

const initConversionChart = () => {
  conversionChartInstance = echarts.init(conversionChartRef.value)
  conversionChartInstance.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '0', left: 'center' },
    series: [
      {
        name: '转化漏斗',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false, position: 'center' },
        emphasis: {
          label: { show: true, fontSize: 20, fontWeight: 'bold' }
        },
        labelLine: { show: false },
        data: [
          { value: 0, name: '领券' },
          { value: 0, name: '核销' },
          { value: 0, name: '过期' }
        ]
      }
    ]
  })
}
</script>

<style scoped lang="scss">
.dashboard-container {
  padding: 24px;
  
  .stat-cards {
    margin-bottom: 24px;
    
    .stat-card {
      border-radius: 16px;
      .stat-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        color: #909399;
        font-size: 14px;
        
        .el-icon {
          font-size: 20px;
          
          &.icon-blue { color: #409EFF; }
          &.icon-green { color: #67C23A; }
          &.icon-orange { color: #E6A23C; }
          &.icon-red { color: #F56C6C; }
        }
      }
      
      .stat-value {
        font-size: 24px;
        font-weight: bold;
        margin: 10px 0;
        color: #303133;
      }
      
      .stat-footer {
        font-size: 12px;
        color: #909399;
        
        .trend-up { color: #F56C6C; }
        .trend-down { color: #67C23A; }
      }
    }
  }
  
  .chart-row {
    :deep(.el-card) {
      border-radius: 16px;
    }
    .card-header {
      font-weight: bold;
      color: #303133;
    }
  }
}
</style>