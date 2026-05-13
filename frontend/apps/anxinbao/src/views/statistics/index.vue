<template>
  <div class="statistics-container" v-loading="loading">
    <el-row :gutter="24">
      <el-col :span="6" v-for="item in statCards" :key="item.key">
        <el-card shadow="never" class="stat-card">
          <div class="stat-icon" :style="{ background: `${item.color}15`, color: item.color }">
            <el-icon><component :is="item.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-title">{{ item.title }}</div>
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-subtitle">{{ item.subtitle }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="24" class="chart-row">
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">保障状态分布</div>
          </template>
          <div ref="orderStatusChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">理赔状态分布</div>
          </template>
          <div ref="claimStatusChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">最近投保订单</div>
      </template>
      <el-table :data="recentOrders" stripe>
        <el-table-column label="订单号" min-width="120" align="center">
          <template #default="{ row }">
            {{ row.orderNo || row.orderId }}
          </template>
        </el-table-column>
        <el-table-column label="投保人" min-width="120" align="center">
          <template #default="{ row }">
            {{ getStaffName(row.staffId) }}
          </template>
        </el-table-column>
        <el-table-column label="实付保费(元)" prop="premium" min-width="120" align="center" />
        <el-table-column label="保障状态" min-width="120" align="center">
          <template #default="{ row }">
            {{ getOrderStatusText(row.status) }}
          </template>
        </el-table-column>
        <el-table-column label="理赔状态" min-width="120" align="center">
          <template #default="{ row }">
            {{ getClaimStatusText(row.claimStatus) }}
          </template>
        </el-table-column>
        <el-table-column label="下单时间" min-width="180" align="center">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, ref } from 'vue'
import { User, Tickets, Warning, DataAnalysis } from '@element-plus/icons-vue'
import { getStaffList } from '@/api/staff'
import { getOrderList } from '@/api/order'
import type { InsuranceOrder, Staff } from '@/api/types'
import * as echarts from 'echarts'

const loading = ref(false)
const staffs = ref<Staff[]>([])
const orders = ref<InsuranceOrder[]>([])
const orderStatusChartRef = ref<HTMLElement | null>(null)
const claimStatusChartRef = ref<HTMLElement | null>(null)
let orderStatusChart: echarts.ECharts | null = null
let claimStatusChart: echarts.ECharts | null = null

const toPercent = (count: number, total: number) => {
  if (!total) return 0
  return Number(((count / total) * 100).toFixed(1))
}

const getOrderStatusText = (status: number) => {
  if (status === 1) return '保障中'
  if (status === 2) return '已过期'
  if (status === 3) return '已退保'
  return `状态值${status}`
}

const getClaimStatusText = (status: number) => {
  if (status === 0) return '无理赔'
  if (status === 1) return '理赔中'
  if (status === 2) return '已理赔'
  if (status === 3) return '已驳回'
  return `状态值${status}`
}

const formatDateTime = (value?: string) => {
  if (!value) return '--'
  return value.replace('T', ' ').slice(0, 19)
}

const staffMap = computed(() => {
  return staffs.value.reduce((acc: Record<number, Staff>, item) => {
    acc[Number(item.staffId)] = item
    return acc
  }, {})
})

const getStaffName = (staffId: number) => {
  return staffMap.value[staffId]?.realName || '--'
}

const activeStaffCount = computed(() => staffs.value.filter((item) => item.status === 1).length)
const orderCount = computed(() => orders.value.length)
const insuredOrderCount = computed(() => orders.value.filter((item) => item.status === 1).length)
const claimOrderCount = computed(() => orders.value.filter((item) => item.claimStatus > 0).length)

const insuredCoverage = computed(() => toPercent(insuredOrderCount.value, orderCount.value))
const claimRate = computed(() => toPercent(claimOrderCount.value, orderCount.value))

const statCards = computed(() => [
  {
    key: 'staffTotal',
    title: '员工总数',
    value: staffs.value.length.toLocaleString(),
    subtitle: `在职 ${activeStaffCount.value} 人`,
    icon: User,
    color: '#409EFF'
  },
  {
    key: 'orderTotal',
    title: '投保订单',
    value: orderCount.value.toLocaleString(),
    subtitle: `保障中 ${insuredOrderCount.value} 单`,
    icon: Tickets,
    color: '#67C23A'
  },
  {
    key: 'claimTotal',
    title: '理赔订单',
    value: claimOrderCount.value.toLocaleString(),
    subtitle: `理赔率 ${claimRate.value}%`,
    icon: Warning,
    color: '#E6A23C'
  },
  {
    key: 'coverage',
    title: '在保覆盖率',
    value: `${insuredCoverage.value}%`,
    subtitle: '按订单保障状态计算',
    icon: DataAnalysis,
    color: '#F56C6C'
  }
])

const orderStatusStats = computed(() => {
  const total = orderCount.value
  const protectedCount = orders.value.filter((item) => item.status === 1).length
  const expiredCount = orders.value.filter((item) => item.status === 2).length
  const cancelledCount = orders.value.filter((item) => item.status === 3).length
  return [
    { label: '保障中', count: protectedCount, percent: toPercent(protectedCount, total), color: '#67C23A' },
    { label: '已过期', count: expiredCount, percent: toPercent(expiredCount, total), color: '#909399' },
    { label: '已退保', count: cancelledCount, percent: toPercent(cancelledCount, total), color: '#F56C6C' }
  ]
})

const claimStatusStats = computed(() => {
  const total = orderCount.value
  const noneCount = orders.value.filter((item) => item.claimStatus === 0).length
  const processingCount = orders.value.filter((item) => item.claimStatus === 1).length
  const doneCount = orders.value.filter((item) => item.claimStatus === 2).length
  const rejectedCount = orders.value.filter((item) => item.claimStatus === 3).length
  return [
    { label: '无理赔', count: noneCount, percent: toPercent(noneCount, total), color: '#909399' },
    { label: '理赔中', count: processingCount, percent: toPercent(processingCount, total), color: '#E6A23C' },
    { label: '已理赔', count: doneCount, percent: toPercent(doneCount, total), color: '#67C23A' },
    { label: '已驳回', count: rejectedCount, percent: toPercent(rejectedCount, total), color: '#F56C6C' }
  ]
})

const buildPieOption = (data: Array<{ label: string; count: number; color: string }>) => ({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {c} 单 ({d}%)'
  },
  legend: {
    bottom: 0,
    left: 'center',
    itemWidth: 10,
    itemHeight: 10
  },
  series: [
    {
      type: 'pie',
      radius: ['42%', '70%'],
      center: ['50%', '45%'],
      data: data.map((item) => ({
        name: item.label,
        value: item.count,
        itemStyle: { color: item.color }
      })),
      label: {
        show: true,
        formatter: '{d}%',
        color: '#606266',
        fontSize: 12
      },
      labelLine: {
        length: 8,
        length2: 10
      }
    }
  ]
})

const recentOrders = computed(() => {
  return [...orders.value]
    .sort((a, b) => new Date(b.createTime || '').getTime() - new Date(a.createTime || '').getTime())
    .slice(0, 10)
})

const renderCharts = () => {
  if (orderStatusChartRef.value) {
    orderStatusChart = orderStatusChart || echarts.init(orderStatusChartRef.value)
    orderStatusChart.setOption(buildPieOption(orderStatusStats.value))
  }
  if (claimStatusChartRef.value) {
    claimStatusChart = claimStatusChart || echarts.init(claimStatusChartRef.value)
    claimStatusChart.setOption(buildPieOption(claimStatusStats.value))
  }
}

const handleResize = () => {
  orderStatusChart?.resize()
  claimStatusChart?.resize()
}

const loadData = async () => {
  loading.value = true
  try {
    const [staffList, orderList] = await Promise.all([getStaffList(), getOrderList()])
    staffs.value = staffList || []
    orders.value = orderList || []
    await nextTick()
    renderCharts()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  orderStatusChart?.dispose()
  claimStatusChart?.dispose()
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

      .stat-subtitle {
        margin-top: 8px;
        font-size: 12px;
        color: #909399;
      }
    }
  }

  .chart-row {
    margin-top: 24px;
  }

  .chart-card,
  .table-card {
    margin-top: 24px;
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
  }

  .chart-container {
    width: 100%;
    height: 320px;
  }
}
</style>
