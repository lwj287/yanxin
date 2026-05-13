<template>
  <div class="user-container">
    <div class="header-action">
      <div class="search-area">
        <el-input v-model="searchName" placeholder="搜索产品名称" clearable class="search-input" :prefix-icon="Search" />
        <el-select v-model="searchStatus" placeholder="状态" clearable class="search-select">
          <el-option label="上架" :value="1" />
          <el-option label="下架" :value="0" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <el-button type="primary" :icon="Plus" class="add-btn" @click="openAddDialog">新增保险产品</el-button>
    </div>

    <div class="table-wrapper" v-loading="loading">
      <el-table :data="pagedData" stripe class="custom-table">
        <el-table-column prop="productName" label="保险名称" min-width="150" align="center" />
        <el-table-column prop="insuranceType" label="类型" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.insuranceType === 1" class="level-tag">家政意外险</el-tag>
            <el-tag v-else-if="row.insuranceType === 2" type="success" class="level-tag">责任险</el-tag>
            <el-tag v-else type="warning" class="level-tag">短期临时工险</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="premium" label="保费(元)" min-width="120" align="center" />
        <el-table-column prop="coverageAmount" label="保额(元)" min-width="120" align="center" />
        <el-table-column prop="durationDays" label="保障期限(天)" min-width="120" align="center" />
        <el-table-column prop="status" label="状态" min-width="100" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row, $event)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该保险产品吗?" @confirm="handleDelete(row)">
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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'add' ? '新增保险产品' : '编辑保险产品'"
      width="620px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="formModel" :rules="formRules" label-width="100px">
        <el-form-item label="保险名称" prop="productName">
          <el-input v-model="formModel.productName" placeholder="请输入保险名称" clearable />
        </el-form-item>
        <el-form-item label="保险类型" prop="insuranceType">
          <el-select v-model="formModel.insuranceType" placeholder="请选择保险类型" style="width: 100%">
            <el-option label="家政意外险" :value="1" />
            <el-option label="责任险" :value="2" />
            <el-option label="短期临时工险" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="保费(元)" prop="premium">
          <el-input-number v-model="formModel.premium" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="保额(元)" prop="coverageAmount">
          <el-input-number v-model="formModel.coverageAmount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="保障天数" prop="durationDays">
          <el-input-number v-model="formModel.durationDays" :min="1" :precision="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="保障条件" prop="conditions">
          <el-input v-model="formModel.conditions" type="textarea" :rows="4" placeholder="请输入保障说明" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="formModel.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { getInsuranceList, addInsurance, updateInsurance, updateInsuranceStatus, deleteInsurance } from '@/api/insurance'
import type { InsuranceProduct } from '@/api/types'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const loading = ref(false)
const allData = ref<InsuranceProduct[]>([])
const searchName = ref('')
const searchStatus = ref<number | undefined>(undefined)
const appliedSearchName = ref('')
const appliedSearchStatus = ref<number | undefined>(undefined)
const currentPage = ref(1)
const pageSize = ref(10)
const pageSizes = [10, 20, 50, 100]
const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit'>('add')
const submitting = ref(false)
const formRef = ref<FormInstance>()
const createDefaultForm = () => ({
  productId: undefined as number | undefined,
  productName: '',
  insuranceType: 1,
  premium: 0,
  coverageAmount: 0,
  durationDays: 1,
  conditions: '',
  status: 1
})
const formModel = ref(createDefaultForm())
const formRules: FormRules = {
  productName: [{ required: true, message: '请输入保险名称', trigger: 'blur' }],
  insuranceType: [{ required: true, message: '请选择保险类型', trigger: 'change' }],
  premium: [{ required: true, message: '请输入保费', trigger: 'change' }],
  coverageAmount: [{ required: true, message: '请输入保额', trigger: 'change' }],
  durationDays: [{ required: true, message: '请输入保障天数', trigger: 'change' }]
}

const filteredData = computed(() => {
  return allData.value.filter((item) => {
    const matchName = !appliedSearchName.value || item.productName?.includes(appliedSearchName.value)
    const matchStatus = appliedSearchStatus.value === undefined || item.status === appliedSearchStatus.value
    return matchName && matchStatus
  })
})

const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredData.value.slice(start, start + pageSize.value)
})

const handleSearch = () => {
  appliedSearchName.value = searchName.value.trim()
  appliedSearchStatus.value = searchStatus.value
  currentPage.value = 1
}

const handleReset = () => {
  searchName.value = ''
  searchStatus.value = undefined
  appliedSearchName.value = ''
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

const openAddDialog = () => {
  dialogMode.value = 'add'
  formModel.value = createDefaultForm()
  dialogVisible.value = true
}

const openEditDialog = (row: InsuranceProduct) => {
  dialogMode.value = 'edit'
  formModel.value = {
    productId: row.productId,
    productName: row.productName || '',
    insuranceType: row.insuranceType ?? 1,
    premium: row.premium ?? 0,
    coverageAmount: row.coverageAmount ?? 0,
    durationDays: row.durationDays ?? 1,
    conditions: row.conditions || '',
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
    const payload: Partial<InsuranceProduct> = {
      productId: formModel.value.productId,
      productName: formModel.value.productName.trim(),
      insuranceType: formModel.value.insuranceType,
      premium: Number(formModel.value.premium),
      coverageAmount: Number(formModel.value.coverageAmount),
      durationDays: Number(formModel.value.durationDays),
      conditions: formModel.value.conditions?.trim() || '',
      status: formModel.value.status
    }
    if (dialogMode.value === 'add') {
      await addInsurance(payload)
      ElMessage.success('新增成功')
    } else {
      await updateInsurance(payload)
      ElMessage.success('编辑成功')
    }
    dialogVisible.value = false
    await loadList()
  } catch (error) {
    ElMessage.error(dialogMode.value === 'add' ? '新增失败' : '编辑失败')
  } finally {
    submitting.value = false
  }
}

const loadList = async () => {
  loading.value = true
  try {
    const list = await getInsuranceList()
    allData.value = list || []
  } catch (error) {
    console.error('Failed to load insurance list:', error)
  } finally {
    loading.value = false
  }
}

const handleStatusChange = async (row: InsuranceProduct, newStatus: number) => {
  try {
    await updateInsuranceStatus(row.productId, newStatus)
    ElMessage.success('状态更新成功')
  } catch (error) {
    row.status = newStatus === 1 ? 0 : 1
    ElMessage.error('状态更新失败')
  }
}

const handleDelete = async (row: InsuranceProduct) => {
  try {
    await ElMessageBox.confirm('确定删除该保险产品吗?', '提示', { type: 'warning' })
    await deleteInsurance(row.productId)
    ElMessage.success('删除成功')
    loadList()
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

    .add-btn {
      background-color: #1a1a1a;
      border-color: #1a1a1a;
      border-radius: 20px;
      padding: 8px 24px;
      
      &:hover {
        background-color: #333333;
        border-color: #333333;
      }
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
