<template>
  <div class="user-container">
    <div class="header-action">
      <div class="search-area">
        <el-input v-model="searchForm.staffName" placeholder="搜索申诉人" clearable class="search-input" />
        <el-select v-model="searchForm.status" placeholder="处理状态" clearable class="search-select">
          <el-option label="待处理" value="PENDING" />
          <el-option label="申诉通过" value="APPROVED" />
          <el-option label="申诉驳回" value="REJECTED" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>

    <div class="table-wrapper" v-loading="loading">
      <el-table :data="tableData" stripe class="custom-table">
        <el-table-column prop="appealNo" label="申诉工单号" min-width="160" align="center" />
        <el-table-column prop="taskNo" label="质检流水号" min-width="160" align="center" />
        <el-table-column prop="staffName" label="申诉人" min-width="120" align="center" />
        <el-table-column prop="reason" label="申诉理由" min-width="200" align="center" show-overflow-tooltip />
        <el-table-column prop="handleResult" label="处理意见" min-width="200" align="center" show-overflow-tooltip>
          <template #default="scope">
            <span>{{ scope.row.handleResult || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="handlerName" label="处理人" min-width="100" align="center">
          <template #default="scope">
            <span>{{ scope.row.handlerName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="处理状态" min-width="120" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" class="status-tag" round>
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申诉时间" min-width="160" align="center" />
        <el-table-column label="操作" min-width="180" fixed="right" align="center">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="openHandleDialog(scope.row)">
              {{ scope.row.status === 'PENDING' ? '处理申诉' : '重新处理' }}
            </el-button>
            <el-popconfirm title="确定要删除该申诉记录吗？" @confirm="handleDelete(scope.row)">
              <template #reference>
                <el-button size="small" type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        background
      />
    </div>

    <!-- 处理申诉弹窗 -->
    <el-dialog v-model="dialogVisible" title="处理申诉" width="500px">
      <div style="margin-bottom: 20px; padding: 15px; background: #f8f9fa; border-radius: 8px;">
        <p style="margin: 0 0 10px 0; color: #606266;"><strong>申诉人：</strong>{{ currentAppeal?.staffName }}</p>
        <p style="margin: 0; color: #606266; line-height: 1.5;"><strong>申诉理由：</strong>{{ currentAppeal?.reason }}</p>
      </div>
      
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="处理结果" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="APPROVED">通过 (撤销不合格判定)</el-radio>
            <el-radio label="REJECTED">驳回 (维持原判)</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理意见" prop="handleResult">
          <el-input 
            v-model="form.handleResult" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入处理意见，此意见将展示给家政人员" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确认提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAppealList, handleAppeal, deleteAppeal } from '@/api/zhijian'
import { ElMessage } from 'element-plus'

const searchForm = ref({
  staffName: '',
  status: ''
})

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const tableData = ref([])

// 弹窗相关
const dialogVisible = ref(false)
const submitLoading = ref(false)
const currentAppeal = ref<any>(null)
const formRef = ref()
const form = ref({
  status: 'APPROVED',
  handleResult: ''
})

const rules = {
  status: [{ required: true, message: '请选择处理结果', trigger: 'change' }],
  handleResult: [{ required: true, message: '请填写处理意见', trigger: 'blur' }]
}

const handleSearch = () => {
  currentPage.value = 1
  loadData()
}

const handleReset = () => {
  searchForm.value = {
    staffName: '',
    status: ''
  }
  handleSearch()
}

const loadData = async () => {
  loading.value = true
  try {
    const params: any = {}
    if (searchForm.value.staffName && searchForm.value.staffName.trim() !== '') {
      params.staffName = searchForm.value.staffName.trim()
    }
    if (searchForm.value.status && searchForm.value.status !== '') {
      params.status = searchForm.value.status
    }
    
    const res: any = await getAppealList(params)
    if (Array.isArray(res)) {
      tableData.value = res
      total.value = res.length
    } else if (res && res.records) {
      tableData.value = res.records
      total.value = res.total
    } else {
      tableData.value = []
      total.value = 0
    }
  } catch (error) {
    console.error(error)
    tableData.value = []
  } finally {
    loading.value = false
  }
}

const getStatusType = (status: string) => {
  switch (status) {
    case 'APPROVED': return 'success'
    case 'REJECTED': return 'danger'
    case 'PENDING': return 'warning'
    default: return 'info'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'APPROVED': return '申诉通过'
    case 'REJECTED': return '申诉驳回'
    case 'PENDING': return '待处理'
    default: return '未知'
  }
}

const openHandleDialog = (row: any) => {
  currentAppeal.value = row
  // 如果是重新处理，带上原有的处理结果和意见
  form.value = {
    status: row.status !== 'PENDING' ? row.status : 'APPROVED',
    handleResult: row.handleResult || ''
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true
      try {
        await handleAppeal({
          id: currentAppeal.value.id,
          status: form.value.status,
          handleResult: form.value.handleResult
        })
        ElMessage.success('处理成功')
        dialogVisible.value = false
        loadData()
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDelete = async (row: any) => {
  try {
    const res: any = await deleteAppeal(row.id)
    if (res === true || res?.code === 200 || res?.data === true) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res?.message || '删除失败')
    }
  } catch (error) {
    console.error('Delete failed:', error)
  }
}

onMounted(() => {
  loadData()
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
