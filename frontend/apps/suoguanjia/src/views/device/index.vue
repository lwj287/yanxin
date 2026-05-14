<template>
  <div class="user-container">
    <div class="header-action">
      <div class="search-area">
        <el-input v-model="searchForm.deviceName" placeholder="搜索设备名称" clearable class="search-input" />
        <el-select v-model="searchForm.deviceType" placeholder="设备类型" clearable class="search-select">
          <el-option label="智能门锁" :value="1" />
          <el-option label="工具箱" :value="2" />
          <el-option label="清洁设备" :value="3" />
        </el-select>
        <el-select v-model="searchForm.status" placeholder="设备状态" clearable class="search-select">
          <el-option label="在线" :value="1" />
          <el-option label="离线" :value="0" />
          <el-option label="故障" :value="2" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>

    <div class="table-wrapper" v-loading="loading">
      <el-table :data="tableData" stripe class="custom-table" style="width: 100%">
        <el-table-column prop="deviceSn" label="序列号(SN)" min-width="160" align="center" />
        <el-table-column prop="deviceName" label="设备名称" min-width="180" align="center" />
        <el-table-column prop="deviceType" label="类型" min-width="140" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.deviceType === 1">智能门锁</el-tag>
            <el-tag v-else-if="row.deviceType === 2" type="warning">工具箱</el-tag>
            <el-tag v-else type="info">清洁设备</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="120" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">在线</el-tag>
            <el-tag v-else-if="row.status === 0" type="info">离线</el-tag>
            <el-tag v-else type="danger">故障</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="batteryLevel" label="电量" min-width="180" align="center">
          <template #default="{ row }">
            <el-progress 
              :percentage="row.batteryLevel" 
              :status="row.batteryLevel < 20 ? 'exception' : 'success'"
            />
          </template>
        </el-table-column>
        <el-table-column prop="location" label="安装位置" min-width="180" align="center" />
        <el-table-column label="录入时间" min-width="180" align="center">
          <template #default="{ row }">
            {{ row.createTime ? row.createTime.replace('T', ' ') : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="260" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleUnlock(row)" v-if="row.deviceType === 1 || row.deviceType === 2">远程开锁</el-button>
            <el-popconfirm title="确定要锁定该设备吗？" @confirm="handleLock(row)">
              <template #reference>
                <el-button link type="danger" size="small">锁定</el-button>
              </template>
            </el-popconfirm>
            <el-popconfirm title="确定要重启该设备吗？" @confirm="handleReboot(row)">
              <template #reference>
                <el-button link type="warning" size="small">重启</el-button>
              </template>
            </el-popconfirm>
            <el-popconfirm title="确定要为该设备申请维修工单吗？" @confirm="handleRepair(row)">
              <template #reference>
                <el-button link type="primary" size="small">报修</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-container">
      <el-pagination
        layout="total, sizes, prev, pager, next, jumper"
        :total="tableData.length"
        background
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDeviceList, remoteUnlock, lockDevice, rebootDevice, applyRepair, DeviceInfo } from '@/api/suoguanjia'

// 绑定在输入框和下拉框上的表单数据
const searchForm = ref({ deviceName: '', deviceType: '' as number | '', status: '' as number | '' })

// 实际用于过滤的查询条件（只有点击查询时才会被赋值）
const activeQuery = ref({ deviceName: '', deviceType: '' as number | '', status: '' as number | '' })

const allData = ref<DeviceInfo[]>([])
const tableData = ref<DeviceInfo[]>([])
const loading = ref(false)
let timer: any = null

const fetchData = async (showLoading = false) => {
  if (showLoading) loading.value = true
  try {
    const res = await getDeviceList()
    allData.value = res || []
    applyFilter()
  } finally {
    if (showLoading) loading.value = false
  }
}

// 点击查询按钮时，将当前表单的值同步给 activeQuery，并执行过滤
const handleSearch = () => {
  activeQuery.value = { ...searchForm.value }
  applyFilter()
}

// 实际的过滤逻辑，依赖于 activeQuery
const applyFilter = () => {
  tableData.value = allData.value.filter(item => {
    const matchName = !activeQuery.value.deviceName || item.deviceName?.includes(activeQuery.value.deviceName)
    const matchType = activeQuery.value.deviceType === '' || item.deviceType === activeQuery.value.deviceType
    const matchStatus = activeQuery.value.status === '' || item.status === activeQuery.value.status
    return matchName && matchType && matchStatus
  })
}

const handleReset = () => {
  searchForm.value = { deviceName: '', deviceType: '', status: '' }
  activeQuery.value = { deviceName: '', deviceType: '', status: '' }
  applyFilter()
}

const handleUnlock = async (row: DeviceInfo) => {
  try {
    await remoteUnlock(row.id, 104) // Mock user 104 (赵经理)
    ElMessage.success('开锁指令下发成功')
    setTimeout(() => fetchData(false), 1000)
  } catch (e: any) {
    ElMessage.error(e.message || '开锁失败')
  }
}

const handleLock = async (row: DeviceInfo) => {
  try {
    await lockDevice(row.id, 104)
    ElMessage.success('设备已锁定')
    setTimeout(() => fetchData(false), 1000)
  } catch (e: any) {
    ElMessage.error(e.message || '锁定失败')
  }
}

const handleReboot = async (row: DeviceInfo) => {
  try {
    await rebootDevice(row.id, 104)
    ElMessage.success('重启指令已下发')
    setTimeout(() => fetchData(false), 1000)
  } catch (e: any) {
    ElMessage.error(e.message || '重启失败')
  }
}

const handleRepair = async (row: DeviceInfo) => {
  try {
    await applyRepair(row.id, 104)
    ElMessage.success('已自动生成报修工单')
    setTimeout(() => fetchData(false), 1000)
  } catch (e: any) {
    ElMessage.error(e.message || '报修失败')
  }
}

onMounted(() => {
  fetchData(true)
  timer = setInterval(() => fetchData(false), 5000) // 每5秒自动刷新
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped lang="scss">
.user-container {
  padding: 24px;
  background: transparent;

  .header-action {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    background: #ffffff;
    padding: 20px 24px;
    border-radius: 16px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);

    .search-area {
      display: flex;
      align-items: center;
      gap: 16px;
      font-size: 16px;
      font-weight: 600;
      color: #303133;

      .search-input { width: 240px; }
      .search-select { width: 140px; }
    }
  }

  .table-wrapper {
    background: #ffffff;
    padding: 24px;
    border-radius: 16px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);

    .custom-table {
      border-radius: 12px;
      overflow: hidden;
      border: 1px solid #ebeef5;
      
      :deep(th.el-table__cell) {
        background-color: #f5f7fa;
        color: #606266;
        font-weight: 600;
      }
    }
  }

  .pagination-container {
    margin-top: 24px;
    display: flex;
    justify-content: flex-end;
    background: #ffffff;
    padding: 16px 24px;
    border-radius: 16px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
  }
}
</style>
