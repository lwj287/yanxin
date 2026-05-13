<template>
  <div class="user-container">
    <div class="header-action">
      <div class="search-area">
        <el-input v-model="searchIdCard" placeholder="搜索身份证号" clearable class="search-input" :prefix-icon="Search" />
        <el-select v-model="searchStatus" placeholder="审核状态" clearable class="search-select">
          <el-option label="待审核" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已驳回" :value="2" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>

    <div class="table-wrapper" v-loading="loading">
      <el-table :data="pagedData" stripe class="custom-table">
        <el-table-column label="姓名" min-width="120" align="center">
          <template #default="{ row }">
            {{ getStaffName(row.staffId) }}
          </template>
        </el-table-column>
        <el-table-column label="手机号" min-width="140" align="center">
          <template #default="{ row }">
            {{ getStaffPhone(row.staffId) }}
          </template>
        </el-table-column>
        <el-table-column label="员工状态" min-width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStaffStatus(row.staffId) === 1 ? 'success' : 'info'">
              {{ getStaffStatus(row.staffId) === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="idCard" label="身份证号" min-width="180" align="center" />
        <el-table-column prop="staffType" label="用工类型" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.staffType === 1">全职</el-tag>
            <el-tag v-else-if="row.staffType === 2" type="warning">兼职</el-tag>
            <el-tag v-else type="info">临时</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="authStatus" label="审核状态" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.authStatus === 0" type="warning" class="level-tag">待审核</el-tag>
            <el-tag v-else-if="row.authStatus === 1" type="success" class="level-tag">已通过</el-tag>
            <el-tag v-else type="danger" class="level-tag">已驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" min-width="180" align="center">
          <template #default="{ row }">
            {{ getStaffCreateTime(row.staffId) }}
          </template>
        </el-table-column>
        <el-table-column prop="authTime" label="提交时间" min-width="180" align="center" />
        <el-table-column label="操作" min-width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-button v-if="row.authStatus === 0" link type="success" size="small" @click="handleApprove(row)">通过</el-button>
            <el-button v-if="row.authStatus === 0" link type="danger" size="small" @click="handleReject(row)">驳回</el-button>
            <el-button v-else-if="row.authStatus === 1" link type="danger" size="small" @click="handleReject(row)">驳回</el-button>
            <el-tag v-else type="info" size="small">已驳回</el-tag>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-container">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="filteredData.length"
        :current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="pageSizes"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getAuthList, approveAuth, rejectAuth, deleteAuth } from '@/api/auth'
import { getStaffList } from '@/api/staff'
import type { RealnameAuth, Staff } from '@/api/types'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const allData = ref<RealnameAuth[]>([])
const staffMap = ref<Record<number, Staff>>({})
const searchIdCard = ref('')
const searchStatus = ref<number | undefined>(undefined)
const appliedSearchIdCard = ref('')
const appliedSearchStatus = ref<number | undefined>(undefined)
const currentPage = ref(1)
const pageSize = ref(10)
const pageSizes = [10, 20, 50, 100]

const filteredData = computed(() => {
  return allData.value.filter((item) => {
    const matchIdCard = !appliedSearchIdCard.value || item.idCard?.includes(appliedSearchIdCard.value)
    const matchStatus = appliedSearchStatus.value === undefined || item.authStatus === appliedSearchStatus.value
    return matchIdCard && matchStatus
  })
})

const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredData.value.slice(start, start + pageSize.value)
})

const handleSearch = () => {
  appliedSearchIdCard.value = searchIdCard.value.trim()
  appliedSearchStatus.value = searchStatus.value
  currentPage.value = 1
}

const handleReset = () => {
  searchIdCard.value = ''
  searchStatus.value = undefined
  appliedSearchIdCard.value = ''
  appliedSearchStatus.value = undefined
  currentPage.value = 1
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
}

const getStaffName = (staffId: number) => {
  return staffMap.value[staffId]?.realName || '--'
}

const getStaffPhone = (staffId: number) => {
  return staffMap.value[staffId]?.phone || '--'
}

const getStaffStatus = (staffId: number) => {
  return staffMap.value[staffId]?.status
}

const getStaffCreateTime = (staffId: number) => {
  return staffMap.value[staffId]?.createTime || '--'
}

const loadList = async () => {
  loading.value = true
  try {
    const [authList, staffList] = await Promise.all([getAuthList(), getStaffList()])
    allData.value = authList || []
    staffMap.value = (staffList || []).reduce((acc: Record<number, Staff>, staff) => {
      acc[Number(staff.staffId)] = staff
      return acc
    }, {})
  } catch (error) {
    console.error('Failed to load auth list:', error)
  } finally {
    loading.value = false
  }
}

const handleApprove = async (row: RealnameAuth) => {
  try {
    await ElMessageBox.confirm('确定通过该实名认证吗?', '提示', { type: 'warning' })
    await approveAuth(row.authId)
    row.authStatus = 1
    ElMessage.success('审核通过')
    await loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleReject = async (row: RealnameAuth) => {
  try {
    await ElMessageBox.confirm('确定驳回该实名认证吗?', '提示', { type: 'warning' })
    await rejectAuth(row.authId)
    row.authStatus = 2
    ElMessage.success('已驳回')
    await loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleDelete = async (row: RealnameAuth) => {
  try {
    await ElMessageBox.confirm('确定删除该实名认证记录吗?', '提示', { type: 'warning' })
    await deleteAuth(row.authId)
    ElMessage.success('删除成功')
    await loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadList()
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
      gap: 16px;

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

      .level-tag {
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
