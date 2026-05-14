<template>
  <div class="user-container">
    <div class="header-action">
      <div class="search-area">
        <el-input v-model="searchForm.action" placeholder="搜索具体动作" clearable class="search-input" />
        <el-select v-model="searchForm.logType" placeholder="日志类型" clearable class="search-select">
          <el-option label="状态上报" :value="1" />
          <el-option label="远程指令" :value="2" />
          <el-option label="设备报警" :value="3" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>

    <div class="table-wrapper" v-loading="loading">
      <el-table :data="tableData" stripe class="custom-table" style="width: 100%">
        <el-table-column prop="logType" label="日志类型" min-width="150" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.logType === 1" type="info">状态上报</el-tag>
            <el-tag v-else-if="row.logType === 2" type="success">远程指令</el-tag>
            <el-tag v-else type="danger">设备报警</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="action" label="具体动作" min-width="180" align="center" />
        <el-table-column label="设备名称" min-width="160" align="center">
          <template #default="{ row }">
            {{ getDeviceName(row.deviceId) }}
          </template>
        </el-table-column>
        <el-table-column label="设备序列号(SN)" min-width="160" align="center">
          <template #default="{ row }">
            {{ getDeviceSn(row.deviceId) }}
          </template>
        </el-table-column>
        <el-table-column prop="content" label="详细内容" min-width="400">
          <template #default="{ row }">
            <span v-if="row.content && row.content.msg">{{ row.content.msg }}</span>
            <span v-else-if="row.content && row.content.faultCode">检测到硬件故障，错误码: {{ row.content.faultCode }}</span>
            <span v-else class="text-gray">操作成功</span>
          </template>
        </el-table-column>
        <el-table-column label="记录时间" min-width="180" align="center">
          <template #default="{ row }">
            {{ row.createTime ? row.createTime.replace('T', ' ') : '-' }}
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
import { getLogList, DeviceLog, getDeviceList, DeviceInfo } from '@/api/suoguanjia'

// 绑定在输入框和下拉框上的表单数据
const searchForm = ref({ action: '', logType: '' as number | '' })

// 实际用于过滤的查询条件（只有点击查询时才会被赋值）
const activeQuery = ref({ action: '', logType: '' as number | '' })

const allData = ref<DeviceLog[]>([])
const tableData = ref<DeviceLog[]>([])
const deviceList = ref<DeviceInfo[]>([])
const loading = ref(false)
let timer: any = null

const fetchData = async (showLoading = false) => {
  if (showLoading) loading.value = true
  try {
    const res = await getLogList()
    let data = res || []
    // 按照记录时间倒序排列，最新产生的日志在最上面
    data.sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())
    allData.value = data
    applyFilter()
  } finally {
    if (showLoading) loading.value = false
  }
}

const fetchDevices = async () => {
  const res = await getDeviceList()
  deviceList.value = res || []
}

const getDeviceSn = (deviceId: number) => {
  const device = deviceList.value.find(d => d.id === deviceId)
  return device ? device.deviceSn : '-'
}

const getDeviceName = (deviceId: number) => {
  const device = deviceList.value.find(d => d.id === deviceId)
  return device ? device.deviceName : '-'
}

// 点击查询按钮时，将当前表单的值同步给 activeQuery，并执行过滤
const handleSearch = () => {
  activeQuery.value = { ...searchForm.value }
  applyFilter()
}

// 实际的过滤逻辑，依赖于 activeQuery
const applyFilter = () => {
  tableData.value = allData.value.filter(item => {
    const matchAction = !activeQuery.value.action || item.action?.includes(activeQuery.value.action)
    const matchType = activeQuery.value.logType === '' || item.logType === activeQuery.value.logType
    return matchAction && matchType
  })
}

const handleReset = () => {
  searchForm.value = { action: '', logType: '' }
  activeQuery.value = { action: '', logType: '' }
  applyFilter()
}

onMounted(() => {
  fetchData(true)
  fetchDevices()
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

  .json-content {
    margin: 0;
    padding: 8px;
    background-color: #f5f7fa;
    border-radius: 4px;
    font-size: 12px;
    color: #909399;
    white-space: pre-wrap;
    word-wrap: break-word;
  }
}
</style>
