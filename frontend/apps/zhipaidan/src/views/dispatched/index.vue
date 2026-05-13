<template>
  <div class="workbench-container">
    <div class="main-content">
      <div class="left-panel">
        <div class="panel-header">
          <h3>已派订单池 <el-tag type="info" round>{{ orderList.length }}</el-tag></h3>
          <el-button type="primary" size="small" @click="fetchOrders">刷新</el-button>
        </div>
        
        <div class="order-list" v-loading="loadingOrders">
          <el-empty v-if="orderList.length === 0" description="暂无已派订单" />
          
          <div 
            v-for="order in orderList" 
            :key="order.id"
            :class="['order-card', { active: currentOrder?.id === order.id }]"
            @click="selectOrder(order)"
          >
            <div class="card-header">
              <span class="order-no">{{ order.orderNo }}</span>
              <el-tag size="small" type="info">{{ order.serviceType }}</el-tag>
            </div>
            <div class="card-body">
              <p><el-icon><User /></el-icon> {{ order.customerName }} {{ order.customerPhone }}</p>
              <p class="address"><el-icon><Location /></el-icon> {{ order.serviceAddress }}</p>
              <p class="time">
                <el-icon><Clock /></el-icon> 
                {{ formatTime(order.expectStartTime) }} - {{ formatTime(order.expectEndTime) }}
              </p>
            </div>
          </div>
        </div>
      </div>

      <div class="right-panel">
        <div v-if="!currentOrder" class="empty-state">
          <el-empty description="请在左侧选择一个订单查看详情" />
        </div>
        
        <div v-else class="match-panel">
          <div class="panel-header">
            <h3>派单详情</h3>
            <span class="sub-title">订单：{{ currentOrder.orderNo }}</span>
          </div>
          
          <div class="dispatch-result" v-loading="loadingRecord">
            <el-result icon="success" title="该订单已成功派发">
              <template #extra>
                <el-descriptions border :column="1" class="record-desc">
                  <el-descriptions-item label="服务类型">{{ currentOrder.serviceType }}</el-descriptions-item>
                  <el-descriptions-item label="客户地址">{{ currentOrder.serviceAddress }}</el-descriptions-item>
                  <el-descriptions-item label="接单家政员">
                    <el-tag type="primary" size="large">{{ dispatchRecord?.staffName }}</el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="计划上门时间">{{ formatTime(dispatchRecord?.planStartTime) }}</el-descriptions-item>
                  <el-descriptions-item label="派单时间">{{ formatTime(dispatchRecord?.createTime) }}</el-descriptions-item>
                </el-descriptions>
              </template>
            </el-result>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { User, Location, Clock } from '@element-plus/icons-vue'
import { getOrderList, getDispatchRecordByOrderId } from '@/api/dispatch'

const orderList = ref<any[]>([])
const loadingOrders = ref(false)
const currentOrder = ref<any>(null)

const dispatchRecord = ref<any>(null)
const loadingRecord = ref(false)

const formatTime = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

const fetchOrders = async () => {
  loadingOrders.value = true
  try {
    const res: any = await getOrderList(1) // 1-已派单
    orderList.value = res || []
    if (currentOrder.value && !orderList.value.find(o => o.id === currentOrder.value.id)) {
      currentOrder.value = null
      dispatchRecord.value = null
    }
  } catch (error) {
    console.error(error)
  } finally {
    loadingOrders.value = false
  }
}

const selectOrder = async (order: any) => {
  currentOrder.value = order
  loadingRecord.value = true
  try {
    const res: any = await getDispatchRecordByOrderId(order.id)
    dispatchRecord.value = res
  } catch (error) {
    console.error(error)
  } finally {
    loadingRecord.value = false
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.workbench-container {
  height: calc(100vh - 100px);
  display: flex;
  flex-direction: column;
}

.main-content {
  display: flex;
  gap: 20px;
  flex: 1;
  overflow: hidden;
}

.left-panel, .right-panel {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.left-panel {
  flex: 1;
  max-width: 420px;
}

.right-panel {
  flex: 2;
}

.panel-header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #ffffff;
}

.panel-header h3 {
  margin: 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.sub-title {
  color: #909399;
  font-size: 13px;
}

.order-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
  background-color: #f5f7fa;
}

.order-card {
  background-color: #ffffff;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
}

.order-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transform: translateY(-1px);
}

.order-card.active {
  border-color: #409eff;
  background-color: #f0f7ff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-no {
  font-weight: 600;
  color: #303133;
  font-size: 15px;
}

.card-body p {
  margin: 8px 0 0 0;
  color: #606266;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-body .address {
  color: #909399;
}

.empty-state {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.match-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.dispatch-result {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
  background-color: #f5f7fa;
}

.record-desc {
  margin-top: 20px;
  width: 400px;
  background-color: #ffffff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
}
</style>
