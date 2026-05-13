<template>
  <div class="user-container">
    <div class="header-action">
      <div class="search-area">
        <el-input v-model="searchOrderNo" placeholder="搜索订单号/订单ID" clearable class="search-input" :prefix-icon="Search" />
        <el-select v-model="searchStatus" placeholder="理赔状态" clearable class="search-select">
          <el-option label="待审核" :value="0" />
          <el-option label="处理中" :value="1" />
          <el-option label="已赔付" :value="2" />
          <el-option label="已驳回" :value="3" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>

    <div class="table-wrapper" v-loading="loading">
      <el-table :data="pagedData" stripe class="custom-table">
        <el-table-column label="订单号" min-width="150" align="center">
          <template #default="{ row }">
            {{ getDisplayOrderNo(row) }}
          </template>
        </el-table-column>
        <el-table-column label="申请人" min-width="100" align="center">
          <template #default="{ row }">
            {{ getStaffName(row.staffId) }}
          </template>
        </el-table-column>
        <el-table-column label="手机号" min-width="130" align="center">
          <template #default="{ row }">
            {{ getStaffPhone(row.staffId) }}
          </template>
        </el-table-column>
        <el-table-column label="保险产品" min-width="160" align="center">
          <template #default="{ row }">
            {{ getProductName(row.orderId) }}
          </template>
        </el-table-column>
        <el-table-column label="保单状态" min-width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="getOrderProtectStatus(row.orderId) === 1" type="success" class="level-tag">保障中</el-tag>
            <el-tag v-else-if="getOrderProtectStatus(row.orderId) === 2" type="info" class="level-tag">已过期</el-tag>
            <el-tag v-else-if="getOrderProtectStatus(row.orderId) === 3" type="danger" class="level-tag">已退保</el-tag>
            <el-tag v-else type="info" class="level-tag">未知</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="claimAmount" label="理赔金额(元)" min-width="120" align="center" />
        <el-table-column prop="claimStatus" label="状态" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.claimStatus === 0" type="warning" class="level-tag">待审核</el-tag>
            <el-tag v-else-if="row.claimStatus === 1" type="primary" class="level-tag">处理中</el-tag>
            <el-tag v-else-if="row.claimStatus === 2" type="success" class="level-tag">已赔付</el-tag>
            <el-tag v-else type="danger" class="level-tag">已驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="理赔材料" min-width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.materialUrl" type="success" class="level-tag">已上传</el-tag>
            <el-tag v-else type="info" class="level-tag">未上传</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" min-width="180" align="center" />
        <el-table-column prop="claimTime" label="结案时间" min-width="180" align="center" />
        <el-table-column prop="createTime" label="创建时间" min-width="180" align="center" />
        <el-table-column label="操作" min-width="320" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleViewMaterial(row)">查看材料</el-button>
            <el-popconfirm v-if="row.claimStatus === 0" title="确认受理该理赔申请吗?" @confirm="handleProcess(row)">
              <template #reference>
                <el-button link type="primary" size="small">受理</el-button>
              </template>
            </el-popconfirm>
            <el-popconfirm v-if="row.claimStatus === 0 || row.claimStatus === 1" title="确认驳回该理赔申请吗?" @confirm="handleReject(row)">
              <template #reference>
                <el-button link type="danger" size="small">驳回</el-button>
              </template>
            </el-popconfirm>
            <el-popconfirm v-if="row.claimStatus === 1" title="确认标记为已赔付吗?" @confirm="handleComplete(row)">
              <template #reference>
                <el-button link type="success" size="small">标记赔付</el-button>
              </template>
            </el-popconfirm>
            <el-popconfirm title="确认删除该理赔记录吗?" @confirm="handleDelete(row)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
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

    <el-dialog v-model="materialDialogVisible" title="理赔材料预览" width="760px">
      <div class="material-preview">
        <el-image
          :src="materialPreviewUrl"
          :preview-src-list="[materialPreviewUrl]"
          fit="contain"
          class="material-image"
        >
          <template #error>
            <div class="material-empty">图片加载失败，请确认材料地址是否可访问</div>
          </template>
        </el-image>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { completeClaim, deleteClaim, getClaimList, processClaim, rejectClaim } from '@/api/claim'
import { getOrderList } from '@/api/order'
import { getStaffList } from '@/api/staff'
import { getInsuranceList } from '@/api/insurance'
import type { ClaimRecord, InsuranceOrder, InsuranceProduct, Staff } from '@/api/types'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const allData = ref<ClaimRecord[]>([])
const orderMap = ref<Record<number, InsuranceOrder>>({})
const staffMap = ref<Record<number, Staff>>({})
const productMap = ref<Record<number, InsuranceProduct>>({})
const searchOrderNo = ref('')
const searchStatus = ref<number | undefined>(undefined)
const appliedSearchOrderNo = ref('')
const appliedSearchStatus = ref<number | undefined>(undefined)
const currentPage = ref(1)
const pageSize = ref(10)
const pageSizes = [10, 20, 50, 100]
const materialDialogVisible = ref(false)
const materialPreviewUrl = ref('')

const getDisplayOrderNo = (item: ClaimRecord) => {
  const orderNo = item.orderNo || orderMap.value[item.orderId]?.orderNo
  return orderNo || `${item.orderId}`
}

const getStaffName = (staffId: number) => {
  return staffMap.value[staffId]?.realName || '--'
}

const getStaffPhone = (staffId: number) => {
  return staffMap.value[staffId]?.phone || '--'
}

const getProductName = (orderId: number) => {
  const order = orderMap.value[orderId]
  if (!order) return '--'
  return productMap.value[order.productId]?.productName || '--'
}

const getOrderProtectStatus = (orderId: number) => {
  return orderMap.value[orderId]?.status
}

const filteredData = computed(() => {
  return allData.value.filter((item) => {
    const keyword = appliedSearchOrderNo.value
    const matchOrderNo = !keyword
      || getDisplayOrderNo(item).includes(keyword)
      || String(item.orderId).includes(keyword)
    const matchStatus = appliedSearchStatus.value === undefined || item.claimStatus === appliedSearchStatus.value
    return matchOrderNo && matchStatus
  })
})

const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredData.value.slice(start, start + pageSize.value)
})

const handleSearch = () => {
  appliedSearchOrderNo.value = searchOrderNo.value.trim()
  appliedSearchStatus.value = searchStatus.value
  currentPage.value = 1
}

const handleReset = () => {
  searchOrderNo.value = ''
  searchStatus.value = undefined
  appliedSearchOrderNo.value = ''
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

const loadList = async () => {
  loading.value = true
  try {
    const [claimList, orderList, staffList, productList] = await Promise.all([
      getClaimList(),
      getOrderList(),
      getStaffList(),
      getInsuranceList()
    ])
    allData.value = claimList || []
    orderMap.value = (orderList || []).reduce((acc: Record<number, InsuranceOrder>, order) => {
      acc[Number(order.orderId)] = order
      return acc
    }, {})
    staffMap.value = (staffList || []).reduce((acc: Record<number, Staff>, staff) => {
      acc[Number(staff.staffId)] = staff
      return acc
    }, {})
    productMap.value = (productList || []).reduce((acc: Record<number, InsuranceProduct>, product) => {
      acc[Number(product.productId)] = product
      return acc
    }, {})
  } catch (error) {
    console.error('Failed to load claim list:', error)
  } finally {
    loading.value = false
  }
}

const handleProcess = async (row: ClaimRecord) => {
  try {
    await processClaim(row.claimId)
    ElMessage.success('受理成功')
    loadList()
  } catch (error) {
    ElMessage.error('受理失败')
  }
}

const handleReject = async (row: ClaimRecord) => {
  try {
    await rejectClaim(row.claimId)
    ElMessage.success('驳回成功')
    loadList()
  } catch (error) {
    ElMessage.error('驳回失败')
  }
}

const handleComplete = async (row: ClaimRecord) => {
  try {
    await completeClaim(row.claimId)
    ElMessage.success('已标记为已赔付')
    loadList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row: ClaimRecord) => {
  try {
    await deleteClaim(row.claimId)
    ElMessage.success('删除成功')
    await loadList()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const resolveMaterialUrl = (rawUrl: string) => {
  const url = rawUrl.trim()
  if (/^https?:\/\//i.test(url)) {
    return url
  }
  if (url.startsWith('/')) {
    // 开发环境前端端口是 3000，材料文件由后端 8080 提供
    if (window.location.port === '3000') {
      return `${window.location.protocol}//${window.location.hostname}:8080${url}`
    }
    return `${window.location.origin}${url}`
  }
  return `${window.location.origin}/${url}`
}

const isImageUrl = (url: string) => {
  return /\.(png|jpe?g|gif|webp|bmp|svg)$/i.test(url.split('?')[0])
}

const handleViewMaterial = (row: ClaimRecord) => {
  if (!row.materialUrl) {
    ElMessage.warning('未上传材料')
    return
  }
  const materialUrl = resolveMaterialUrl(row.materialUrl)
  if (!isImageUrl(materialUrl)) {
    ElMessage.warning('当前材料不是图片，请上传图片后再预览')
    return
  }
  materialPreviewUrl.value = materialUrl
  materialDialogVisible.value = true
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

  .material-preview {
    width: 100%;
    height: 520px;
    border: 1px solid #ebeef5;
    border-radius: 8px;
    overflow: hidden;
    background: #f5f7fa;
  }

  .material-image {
    width: 100%;
    height: 100%;
    background: #ffffff;
  }

  .material-empty {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #909399;
    font-size: 14px;
  }
}
</style>
