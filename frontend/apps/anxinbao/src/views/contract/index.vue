<template>
  <div class="user-container">
    <div class="header-action">
      <div class="search-area">
        <el-input v-model="searchKeyword" placeholder="搜索姓名或手机号" clearable class="search-input" :prefix-icon="Search" />
        <el-select v-model="searchStatus" placeholder="合同状态" clearable class="search-select">
          <el-option label="待签署" :value="0" />
          <el-option label="已生效" :value="1" />
          <el-option label="已过期" :value="2" />
          <el-option label="已作废" :value="3" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <el-button type="primary" :icon="Plus" class="add-btn" @click="handleCreateTemplate">新建模板</el-button>
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
        <el-table-column prop="contractStatus" label="合同状态" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.contractStatus === 0" type="info" class="level-tag">待签署</el-tag>
            <el-tag v-else-if="row.contractStatus === 1" type="success" class="level-tag">已生效</el-tag>
            <el-tag v-else-if="row.contractStatus === 2" type="warning" class="level-tag">已过期</el-tag>
            <el-tag v-else type="danger" class="level-tag">已作废</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180" align="center" />
        <el-table-column prop="signTime" label="签署时间" min-width="180" align="center" />
        <el-table-column prop="expireTime" label="到期时间" min-width="180" align="center" />
        <el-table-column label="操作" min-width="190" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handlePreview(row)">查看</el-button>
            <el-popconfirm title="确定作废该合同吗?" @confirm="handleVoid(row)">
              <template #reference>
                <el-button link type="warning" size="small">作废</el-button>
              </template>
            </el-popconfirm>
            <el-popconfirm title="确定删除该合同吗?" @confirm="handleDelete(row)">
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

    <el-dialog v-model="previewDialogVisible" title="合同图片预览" width="680px">
      <div class="preview-wrapper">
        <img v-if="previewImageUrl" :src="previewImageUrl" alt="合同存证图片" class="preview-image" />
        <div v-else class="preview-empty">暂无存证图片，以下为合同内容</div>
      </div>
      <div class="preview-meta" v-if="previewContract">
        <div>合同状态：{{ getStatusText(previewContract.contractStatus) }}</div>
        <div>签署时间：{{ previewContract.signTime || '--' }}</div>
        <div>到期时间：{{ previewContract.expireTime || '--' }}</div>
      </div>
      <div class="preview-content">{{ previewContract?.contractContent || '暂无合同正文' }}</div>
    </el-dialog>

    <el-dialog v-model="templateDialogVisible" title="新建模板" width="640px">
      <el-form ref="templateFormRef" :model="templateForm" :rules="templateRules" label-width="90px">
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="templateForm.templateName" placeholder="请输入模板名称" clearable />
        </el-form-item>
        <el-form-item label="合同类型" prop="contractType">
          <el-select v-model="templateForm.contractType" placeholder="请选择合同类型" style="width: 100%">
            <el-option label="标准合同" :value="1" />
            <el-option label="短期合同" :value="2" />
            <el-option label="补充协议" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="模板状态" prop="status">
          <el-switch v-model="templateForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="模板内容" prop="templateContent">
          <el-input
            v-model="templateForm.templateContent"
            type="textarea"
            :rows="8"
            placeholder="请输入模板正文"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="templateDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="templateSubmitting" @click="handleSubmitTemplate">保存模板</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { getContractList, getContractDetail, voidContract, deleteContract, addContractTemplate } from '@/api/contract'
import { getStaffList } from '@/api/staff'
import type { Contract, ContractTemplate, Staff } from '@/api/types'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const loading = ref(false)
const allData = ref<Contract[]>([])
const staffMap = ref<Record<number, Staff>>({})
const searchKeyword = ref('')
const searchStatus = ref<number | undefined>(undefined)
const appliedSearchKeyword = ref('')
const appliedSearchStatus = ref<number | undefined>(undefined)
const currentPage = ref(1)
const pageSize = ref(10)
const pageSizes = [10, 20, 50, 100]
const previewDialogVisible = ref(false)
const previewImageUrl = ref('')
const previewContract = ref<Contract | null>(null)
const templateDialogVisible = ref(false)
const templateSubmitting = ref(false)
const templateFormRef = ref<FormInstance>()
const createDefaultTemplateForm = (): ContractTemplate => ({
  templateName: '',
  contractType: 1,
  templateContent: '',
  status: 1
})
const templateForm = ref<ContractTemplate>(createDefaultTemplateForm())
const templateRules: FormRules = {
  templateName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  contractType: [{ required: true, message: '请选择合同类型', trigger: 'change' }],
  templateContent: [{ required: true, message: '请输入模板内容', trigger: 'blur' }]
}

const filteredData = computed(() => {
  return allData.value.filter((item) => {
    const staff = staffMap.value[item.staffId]
    const keyword = appliedSearchKeyword.value
    const matchKeyword = !keyword || staff?.realName?.includes(keyword) || staff?.phone?.includes(keyword)
    const matchStatus = appliedSearchStatus.value === undefined || item.contractStatus === appliedSearchStatus.value
    return matchKeyword && matchStatus
  })
})

const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredData.value.slice(start, start + pageSize.value)
})

const handleSearch = () => {
  appliedSearchKeyword.value = searchKeyword.value.trim()
  appliedSearchStatus.value = searchStatus.value
  currentPage.value = 1
}

const handleReset = () => {
  searchKeyword.value = ''
  searchStatus.value = undefined
  appliedSearchKeyword.value = ''
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

const handleCreateTemplate = () => {
  templateForm.value = createDefaultTemplateForm()
  templateDialogVisible.value = true
}

const getStatusText = (status: number) => {
  if (status === 0) return '待签署'
  if (status === 1) return '已生效'
  if (status === 2) return '已过期'
  return '已作废'
}

const handlePreview = async (row: Contract) => {
  try {
    const detail = await getContractDetail(row.contractId)
    previewContract.value = detail || row
    previewImageUrl.value = detail?.storageUrl || ''
    previewDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载合同详情失败')
  }
}

const loadList = async () => {
  loading.value = true
  try {
    const [contractList, staffList] = await Promise.all([getContractList(), getStaffList()])
    allData.value = contractList || []
    staffMap.value = (staffList || []).reduce((acc: Record<number, Staff>, staff) => {
      acc[Number(staff.staffId)] = staff
      return acc
    }, {})
  } catch (error) {
    console.error('Failed to load contract list:', error)
  } finally {
    loading.value = false
  }
}

const handleVoid = async (row: Contract) => {
  try {
    await ElMessageBox.confirm('确定作废该合同吗?', '提示', { type: 'warning' })
    await voidContract(row.contractId)
    ElMessage.success('已作废')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleDelete = async (row: Contract) => {
  try {
    await deleteContract(row.contractId)
    ElMessage.success('删除成功')
    await loadList()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const handleSubmitTemplate = async () => {
  const form = templateFormRef.value
  if (!form) return
  await form.validate()
  templateSubmitting.value = true
  try {
    await addContractTemplate({
      templateName: templateForm.value.templateName.trim(),
      contractType: templateForm.value.contractType,
      templateContent: templateForm.value.templateContent.trim(),
      status: templateForm.value.status
    })
    ElMessage.success('模板创建成功')
    templateDialogVisible.value = false
  } catch (error) {
    ElMessage.error('模板创建失败')
  } finally {
    templateSubmitting.value = false
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

  .preview-wrapper {
    min-height: 280px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f8fafc;
    border-radius: 8px;
    padding: 12px;
  }

  .preview-image {
    max-width: 100%;
    max-height: 70vh;
    object-fit: contain;
  }

  .preview-empty {
    color: #909399;
    font-size: 14px;
  }

  .preview-meta {
    margin-top: 12px;
    display: flex;
    justify-content: space-between;
    color: #606266;
    font-size: 13px;
  }

  .preview-content {
    margin-top: 12px;
    border: 1px solid #ebeef5;
    border-radius: 8px;
    padding: 12px;
    max-height: 220px;
    overflow: auto;
    white-space: pre-wrap;
    color: #303133;
    background: #ffffff;
  }
}
</style>
