<template>
  <div class="workbench-container">
    <div class="main-content">
      <div class="left-panel">
        <div class="panel-header">
          <h3>待派订单池 <el-tag type="danger" round>{{ orderList.length }}</el-tag></h3>
          <el-button type="primary" size="small" @click="fetchOrders">刷新</el-button>
        </div>
        
        <div class="order-list" v-loading="loadingOrders">
          <el-empty v-if="orderList.length === 0" description="暂无待派订单" />
          
          <div 
            v-for="order in orderList" 
            :key="order.id"
            :class="['order-card', { active: currentOrder?.id === order.id }]"
            @click="selectOrder(order)"
          >
            <div class="card-header">
              <span class="order-no">{{ order.orderNo }}</span>
              <el-tag size="small" type="success">{{ order.serviceType }}</el-tag>
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
          <el-empty description="请在左侧选择一个订单进行智能匹配" />
        </div>
        
        <div v-else class="match-panel">
          <div class="panel-header">
            <h3>智能匹配推荐</h3>
            <span class="sub-title">订单：{{ currentOrder.orderNo }} ({{ currentOrder.serviceType }})</span>
          </div>

          <div class="map-container" id="amap-container"></div>

          <div class="staff-list" v-loading="loadingMatch">
            <el-empty v-if="matchedStaffs.length === 0" description="没有找到符合技能和时间要求的家政员" />
            
            <div 
              v-for="(staff, index) in matchedStaffs" 
              :key="staff.staffId" 
              :class="['staff-card', { 'active-staff': selectedStaff?.staffId === staff.staffId }]"
              @click="previewRoute(staff)"
            >
              <div class="staff-info">
                <div class="avatar-wrap">
                  <el-avatar :size="50" style="background-color: #409EFF">
                    {{ staff.staffName.substring(0, 1) }}
                  </el-avatar>
                  <div v-if="index === 0" class="recommend-badge">最优</div>
                </div>
                <div class="details">
                  <h4>{{ staff.staffName }} <span class="phone">{{ staff.staffPhone }}</span></h4>
                  <div class="skills">
                    <el-tag v-for="skill in staff.skills" :key="skill" size="small" class="skill-tag">
                      {{ skill }}
                    </el-tag>
                  </div>
                  <div class="distance">
                    <el-icon><Position /></el-icon> 
                    <span v-if="staff.realDistance">
                      驾车：<strong>{{ staff.realDistance }} km</strong> (约 {{ staff.realTime }} 分钟)
                    </span>
                    <span v-else>
                      距客户直线距离：<strong>{{ (staff.distance / 1000).toFixed(2) }} km</strong>
                    </span>
                  </div>
                </div>
              </div>
              
              <div class="action-area">
                <div class="score">匹配分：<span class="score-num">{{ staff.matchScore }}</span></div>
                <el-button 
                  type="primary" 
                  :icon="Check"
                  @click.stop="handleDispatch(staff)"
                  :loading="dispatchingId === staff.staffId"
                >
                  确认派单
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, shallowRef } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Location, Clock, Position, Check } from '@element-plus/icons-vue'
import { getOrderList, matchStaffForOrder, dispatchOrder } from '@/api/dispatch'
import AMapLoader from '@amap/amap-jsapi-loader'

const orderList = ref<any[]>([])
const loadingOrders = ref(false)
const currentOrder = ref<any>(null)

const matchedStaffs = ref<any[]>([])
const loadingMatch = ref(false)
const dispatchingId = ref<number | null>(null)
const selectedStaff = ref<any>(null)

const map = shallowRef<any>(null)
const amap = shallowRef<any>(null)
const drivingRoute = ref<any>(null)

const formatTime = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

const initMap = async () => {
  try {
    ;(window as any)._AMapSecurityConfig = {
      securityJsCode: '041a868f7c9e05e55b62b1b3b1df2e73',
    }
    amap.value = await AMapLoader.load({
      key: '608d75903d29ad471362f8c58c550daf',
      version: '2.0',
      plugins: ['AMap.Driving']
    })
    map.value = new amap.value.Map('amap-container', {
      zoom: 11,
      center: [116.397428, 39.90923]
    })
  } catch (e) {
    console.error('高德地图加载失败', e)
  }
}

const drawMarkers = () => {
  if (!map.value || !amap.value) return
  map.value.clearMap()
  if (drivingRoute.value) drivingRoute.value.clear()

  const orderMarker = new amap.value.Marker({
    position: [currentOrder.value.longitude, currentOrder.value.latitude],
    icon: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_r.png',
    title: '客户地址：' + currentOrder.value.serviceAddress,
    label: { content: `<div class="marker-label order-label">终点: ${currentOrder.value.customerName}</div>`, direction: 'top' }
  })
  map.value.add(orderMarker)

  matchedStaffs.value.forEach((staff: any) => {
    const staffMarker = new amap.value.Marker({
      position: [staff.lastLongitude, staff.lastLatitude],
      icon: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png',
      title: staff.staffName,
      label: { content: `<div class="marker-label staff-label">${staff.staffName}</div>`, direction: 'right' }
    })
    map.value.add(staffMarker)
  })
  map.value.setFitView()
}

const previewRoute = (staff: any) => {
  selectedStaff.value = staff
  if (!map.value || !amap.value) return
  if (drivingRoute.value) drivingRoute.value.clear()
  drawMarkers()

  drivingRoute.value = new amap.value.Driving({
    map: map.value,
    hideMarkers: true,
    showTraffic: true,
  })

  drivingRoute.value.search(
    [staff.lastLongitude, staff.lastLatitude],
    [currentOrder.value.longitude, currentOrder.value.latitude],
    (status: string, result: any) => {
      if (status === 'complete') {
        staff.realDistance = (result.routes[0].distance / 1000).toFixed(1)
        staff.realTime = Math.round(result.routes[0].time / 60)
      } else {
        ElMessage.error('路径规划失败')
      }
    }
  )
}

const fetchOrders = async () => {
  loadingOrders.value = true
  try {
    const res: any = await getOrderList(0)
    orderList.value = res || []
    if (currentOrder.value && !orderList.value.find(o => o.id === currentOrder.value.id)) {
      currentOrder.value = null
      matchedStaffs.value = []
      if (map.value) {
        map.value.destroy()
        map.value = null
      }
    }
  } catch (error) {
    console.error(error)
  } finally {
    loadingOrders.value = false
  }
}

const selectOrder = async (order: any) => {
  currentOrder.value = order
  selectedStaff.value = null
  loadingMatch.value = true
  try {
    const res: any = await matchStaffForOrder(order.id)
    matchedStaffs.value = res || []
    setTimeout(() => {
      if (!map.value) initMap().then(() => drawMarkers())
      else drawMarkers()
    }, 100)
  } catch (error) {
    console.error(error)
    matchedStaffs.value = []
  } finally {
    loadingMatch.value = false
  }
}

const handleDispatch = (staff: any) => {
  ElMessageBox.confirm(`确认将订单 ${currentOrder.value.orderNo} 派发给 【${staff.staffName}】 吗？`, '派单确认', {
    confirmButtonText: '确定派发',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    dispatchingId.value = staff.staffId
    try {
      await dispatchOrder(currentOrder.value.id, staff.staffId)
      ElMessage.success('派单成功！')
      fetchOrders()
    } finally {
      dispatchingId.value = null
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchOrders()
})

onUnmounted(() => {
  if (map.value) {
    map.value.destroy()
    map.value = null
  }
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

.map-container {
  height: 320px;
  width: 100%;
  border-bottom: 1px solid #ebeef5;
}

.staff-list {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background-color: #f5f7fa;
}

.staff-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background-color: #ffffff;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
}

.staff-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border-color: #c6e2ff;
}

.staff-card.active-staff {
  border-color: #409eff;
  background-color: #f0f7ff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.staff-info {
  display: flex;
  gap: 20px;
  align-items: center;
}

.avatar-wrap {
  position: relative;
}

.recommend-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background: #f56c6c;
  color: white;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
  font-weight: 600;
  box-shadow: 0 2px 4px rgba(245, 108, 108, 0.3);
}

.details h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
  font-weight: 600;
}

.phone {
  font-size: 13px;
  color: #909399;
  font-weight: normal;
  margin-left: 8px;
}

.skills {
  margin-bottom: 10px;
}

.skill-tag {
  margin-right: 8px;
}

.distance {
  font-size: 13px;
  color: #e6a23c;
  display: flex;
  align-items: center;
  gap: 6px;
  background-color: #fdf6ec;
  padding: 4px 8px;
  border-radius: 4px;
  display: inline-flex;
}

.action-area {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

.score {
  color: #909399;
  font-size: 13px;
}

.score-num {
  font-size: 20px;
  color: #67c23a;
  font-weight: 600;
}

:deep(.marker-label) {
  padding: 4px 8px;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.15);
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

:deep(.order-label) {
  color: #f56c6c;
  border: 1px solid #f56c6c;
}

:deep(.staff-label) {
  color: #409eff;
  border: 1px solid #409eff;
}
</style>
