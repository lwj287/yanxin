<template>
  <div class="user-container">
    <div class="header-action">
      <div class="search-area">
        <el-input v-model="searchOrderNo" placeholder="搜索订单号/订单ID" clearable class="search-input" :prefix-icon="Search" />
        <el-select v-model="searchStatus" placeholder="保障状态" clearable class="search-select">
          <el-option label="保障中" :value="1" />
          <el-option label="已过期" :value="2" />
          <el-option label="已退保" :value="3" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>

    <div class="table-wrapper" v-loading="loading">
      <el-table :data="pagedData" stripe class="custom-table">
        <el-table-column label="订单号" min-width="180" align="center">
          <template #default="{ row }">
            {{ getDisplayOrderNo(row) }}
          </template>
        </el-table-column>
        <el-table-column label="投保人" min-width="120" align="center">
          <template #default="{ row }">
            {{ getStaffName(row.staffId) }}
          </template>
        </el-table-column>
        <el-table-column label="手机号" min-width="140" align="center">
          <template #default="{ row }">
            {{ getStaffPhone(row.staffId) }}
          </template>
        </el-table-column>
        <el-table-column label="保险产品" min-width="160" align="center">
          <template #default="{ row }">
            {{ getProductName(row.productId) }}
          </template>
        </el-table-column>
        <el-table-column prop="premium" label="实付保费(元)" min-width="120" align="center" />
        <el-table-column prop="status" label="保障状态" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success" class="level-tag">保障中</el-tag>
            <el-tag v-else-if="row.status === 2" type="info" class="level-tag">已过期</el-tag>
            <el-tag v-else type="danger" class="level-tag">已退保</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="claimStatus" label="理赔状态" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.claimStatus === 0" type="info" class="level-tag">无理赔</el-tag>
            <el-tag v-else-if="row.claimStatus === 1" type="warning" class="level-tag">理赔中</el-tag>
            <el-tag v-else-if="row.claimStatus === 2" type="success" class="level-tag">已理赔</el-tag>
            <el-tag v-else-if="row.claimStatus === 3" type="danger" class="level-tag">已驳回</el-tag>
            <el-tag v-else type="danger" class="level-tag">状态值{{ row.claimStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="insureTime" label="投保时间" min-width="180" align="center" />
        <el-table-column prop="expireTime" label="到期时间" min-width="180" align="center" />
        <el-table-column prop="createTime" label="下单时间" min-width="180" align="center" />
        <el-table-column label="操作" min-width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该订单吗?" @confirm="handleDelete(row)">
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

    <el-dialog v-model="dialogVisible" title="编辑订单" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="formModel" :rules="formRules" label-width="100px">
        <el-form-item label="订单号">
          <el-input :model-value="getDisplayOrderNo(formModel)" disabled />
        </el-form-item>
        <el-form-item label="投保人">
          <el-input :model-value="getStaffName(formModel.staffId || 0)" disabled />
        </el-form-item>
        <el-form-item label="保险产品">
          <el-input :model-value="getProductName(formModel.productId || 0)" disabled />
        </el-form-item>
        <el-form-item label="保费(元)" prop="premium">
          <el-input-number v-model="formModel.premium" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="保障状态" prop="status">
          <el-select v-model="formModel.status" placeholder="请选择保障状态" style="width: 100%">
            <el-option label="保障中" :value="1" />
            <el-option label="已过期" :value="2" />
            <el-option label="已退保" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="理赔状态" prop="claimStatus">
          <el-select v-model="formModel.claimStatus" placeholder="请选择理赔状态" style="width: 100%">
            <el-option label="无理赔" :value="0" />
            <el-option label="理赔中" :value="1" />
            <el-option label="已理赔" :value="2" />
            <el-option label="已驳回" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { deleteOrder, getOrderList, updateOrder } from '@/api/order'
import { getStaffList } from '@/api/staff'
import { getInsuranceList } from '@/api/insurance'
import type { InsuranceOrder, InsuranceProduct, Staff } from '@/api/types'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const loading = ref(false)
const allData = ref<InsuranceOrder[]>([])
const staffMap = ref<Record<number, Staff>>({})
const productMap = ref<Record<number, InsuranceProduct>>({})
const searchOrderNo = ref('')
const searchStatus = ref<number | undefined>(undefined)
const appliedSearchOrderNo = ref('')
const appliedSearchStatus = ref<number | undefined>(undefined)
const currentPage = ref(1)
const pageSize = ref(10)
const pageSizes = [10, 20, 50, 100]
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()
const createDefaultForm = () => ({
  orderId: undefined as number | undefined,
  orderNo: '',
  staffId: 0,
  productId: 0,
  premium: 0,
  claimStatus: 0,
  status: 1
})
const formModel = ref(createDefaultForm())
const formRules: FormRules = {
  premium: [{ required: true, message: '请输入保费', trigger: 'change' }],
  status: [{ required: true, message: '请选择保障状态', trigger: 'change' }],
  claimStatus: [{ required: true, message: '请选择理赔状态', trigger: 'change' }]
}

const getDisplayOrderNo = (order: Pick<InsuranceOrder, 'orderNo' | 'orderId'>) => {
  return order.orderNo || `${order.orderId ?? '--'}`
}

const filteredData = computed(() => {
  return allData.value.filter((item) => {
    const keyword = appliedSearchOrderNo.value
    const matchOrderNo = !keyword
      || (item.orderNo || '').includes(keyword)
      || String(item.orderId ?? '').includes(keyword)
    const matchStatus = appliedSearchStatus.value === undefined || item.status === appliedSearchStatus.value
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

const getStaffName = (staffId: number) => {
  return staffMap.value[staffId]?.realName || '--'
}

const getStaffPhone = (staffId: number) => {
  return staffMap.value[staffId]?.phone || '--'
}

const getProductName = (productId: number) => {
  return productMap.value[productId]?.productName || '--'
}

const openEditDialog = (row: InsuranceOrder) => {
  formModel.value = {
    orderId: row.orderId,
    orderNo: row.orderNo || '',
    staffId: row.staffId,
    productId: row.productId,
    premium: row.premium ?? 0,
    claimStatus: row.claimStatus ?? 0,
    status: row.status ?? 1
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const form = formRef.value
  if (!form) return
  await form.validate()
  submitting.value = true
  try {
    await updateOrder({
      orderId: formModel.value.orderId,
      premium: Number(formModel.value.premium),
      claimStatus: formModel.value.claimStatus,
      status: formModel.value.status
    })
    ElMessage.success('编辑成功')
    dialogVisible.value = false
    await loadList()
  } catch (error) {
    ElMessage.error('编辑失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row: InsuranceOrder) => {
  try {
    await deleteOrder(row.orderId)
    ElMessage.success('删除成功')
    await loadList()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const loadList = async () => {
  loading.value = true
  try {
    const [orderList, staffList, productList] = await Promise.all([
      getOrderList(),
      getStaffList(),
      getInsuranceList()
    ])
    allData.value = orderList || []
    staffMap.value = (staffList || []).reduce((acc: Record<number, Staff>, staff) => {
      acc[Number(staff.staffId)] = staff
      return acc
    }, {})
    productMap.value = (productList || []).reduce((acc: Record<number, InsuranceProduct>, product) => {
      acc[Number(product.productId)] = product
      return acc
    }, {})
  } catch (error) {
    console.error('Failed to load order list:', error)
  } finally {
    loading.value = false
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
