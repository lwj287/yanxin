<template>
  <div class="user-container">
    <div class="header-action">
      <div class="search-area">
        <el-input v-model="searchForm.userName" placeholder="搜索授权用户" style="width: 200px" />
        <el-select v-model="searchForm.authType" placeholder="授权类型" style="width: 160px" clearable>
          <el-option label="永久授权" :value="1" />
          <el-option label="临时授权" :value="2" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <el-button type="primary" @click="dialogVisible = true">新增授权</el-button>
    </div>

    <div class="table-wrapper" v-loading="loading">
      <el-table :data="tableData" stripe class="custom-table" style="width: 100%">
        <el-table-column prop="userName" label="授权用户" min-width="140" align="center" />
        <el-table-column label="设备名称" min-width="180" align="center">
          <template #default="{ row }">
            {{ getDeviceName(row.deviceId) }}
          </template>
        </el-table-column>
        <el-table-column label="序列号(SN)" min-width="160" align="center">
          <template #default="{ row }">
            {{ getDeviceSn(row.deviceId) }}
          </template>
        </el-table-column>
        <el-table-column prop="authType" label="授权类型" min-width="120" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.authType === 1" type="success">永久授权</el-tag>
            <el-tag v-else type="warning">临时授权</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="生效时间" min-width="180" align="center">
          <template #default="{ row }">
            {{ row.validStartTime ? row.validStartTime.replace('T', ' ') : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="失效时间" min-width="180" align="center">
          <template #default="{ row }">
            {{ row.validEndTime ? row.validEndTime.replace('T', ' ') : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-popconfirm title="确定要撤销该用户的授权吗？" @confirm="handleRevoke(row)">
              <template #reference>
                <el-button link type="danger" size="small">撤销</el-button>
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
        :page-sizes="[10, 20, 50]"
      />
    </div>

    <!-- 新增授权弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增授权" width="500px">
      <el-form :model="addForm" label-width="100px">
        <el-form-item label="授权设备">
          <el-select v-model="addForm.deviceId" placeholder="请选择设备" style="width: 100%">
            <el-option v-for="d in deviceList" :key="d.id" :label="d.deviceName + ' (' + d.deviceSn + ')'" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="授权用户">
          <!-- 简单模拟，实际应该是下拉选择用户 -->
          <el-input v-model="addForm.userName" placeholder="输入用户姓名" />
        </el-form-item>
        <el-form-item label="授权类型">
          <el-radio-group v-model="addForm.authType">
            <el-radio :label="1">永久授权</el-radio>
            <el-radio :label="2">临时授权</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="有效时间段" v-if="addForm.authType === 2">
          <el-date-picker
            v-model="addForm.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="生效时间"
            end-placeholder="失效时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAddAuth" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAuthList, addAuth, revokeAuth, DeviceUserRel, getDeviceList, DeviceInfo } from '@/api/suoguanjia'

const searchForm = ref({ userName: '', authType: '' as number | '' })
const activeQuery = ref({ userName: '', authType: '' as number | '' })

const allData = ref<DeviceUserRel[]>([])
const tableData = ref<DeviceUserRel[]>([])
const loading = ref(false)
let timer: any = null

// 新增授权相关
const dialogVisible = ref(false)
const submitLoading = ref(false)
const deviceList = ref<DeviceInfo[]>([])
const addForm = ref({
  deviceId: null as number | null,
  userName: '',
  authType: 1,
  timeRange: [] as string[]
})

const fetchData = async (showLoading = false) => {
  if (showLoading) loading.value = true
  try {
    const res = await getAuthList()
    allData.value = res || []
    applyFilter()
  } finally {
    if (showLoading) loading.value = false
  }
}

const fetchDevices = async () => {
  const res = await getDeviceList()
  deviceList.value = res || []
}

const getDeviceName = (deviceId: number) => {
  const device = deviceList.value.find(d => d.id === deviceId)
  return device ? device.deviceName : '未知设备'
}

const getDeviceSn = (deviceId: number) => {
  const device = deviceList.value.find(d => d.id === deviceId)
  return device ? device.deviceSn : '-'
}

const handleSearch = () => {
  activeQuery.value = { ...searchForm.value }
  applyFilter()
}

const applyFilter = () => {
  tableData.value = allData.value.filter(item => {
    const matchName = !activeQuery.value.userName || item.userName?.includes(activeQuery.value.userName)
    const matchType = activeQuery.value.authType === '' || item.authType === activeQuery.value.authType
    return matchName && matchType
  })
}

const handleReset = () => {
  searchForm.value = { userName: '', authType: '' }
  activeQuery.value = { userName: '', authType: '' }
  applyFilter()
}

const submitAddAuth = async () => {
  if (!addForm.value.deviceId || !addForm.value.userName) {
    ElMessage.warning('请填写完整的设备和人员信息')
    return
  }
  
  const payload: Partial<DeviceUserRel> = {
    deviceId: addForm.value.deviceId,
    userId: 999, // 模拟生成的固定ID
    userName: addForm.value.userName,
    authType: addForm.value.authType,
  }
  
  if (addForm.value.authType === 2) {
    if (!addForm.value.timeRange || addForm.value.timeRange.length !== 2) {
      ElMessage.warning('临时授权请选择时间段')
      return
    }
    payload.validStartTime = addForm.value.timeRange[0]
    payload.validEndTime = addForm.value.timeRange[1]
  }
  
  submitLoading.value = true
  try {
    await addAuth(payload)
    ElMessage.success('授权添加成功')
    dialogVisible.value = false
    // 重置表单
    addForm.value = { deviceId: null, userName: '', authType: 1, timeRange: [] }
    fetchData(true)
  } catch (e: any) {
    ElMessage.error(e.message || '添加失败')
  } finally {
    submitLoading.value = false
  }
}

const handleRevoke = async (row: DeviceUserRel) => {
  try {
    await revokeAuth(row.id)
    ElMessage.success('撤销授权成功')
    fetchData(true)
  } catch (e: any) {
    ElMessage.error(e.message || '撤销失败')
  }
}

onMounted(() => {
  fetchData(true)
  fetchDevices()
  timer = setInterval(() => fetchData(false), 5000)
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
