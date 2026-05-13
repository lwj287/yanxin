<template>
  <div class="user-container">
    <div class="header-action">
      <div class="search-area">
        <el-input v-model="searchForm.name" placeholder="搜索服务类型名称" clearable class="search-input" />
        <el-select v-model="searchForm.status" placeholder="状态" clearable class="search-select">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <el-button type="primary" class="add-btn" @click="openAddDialog">新增服务类型</el-button>
    </div>

    <div class="table-wrapper" v-loading="loading">
      <el-table :data="tableData" stripe class="custom-table">
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="name" label="服务类型名称" min-width="150" align="center" />
        <el-table-column prop="description" label="描述" min-width="200" align="center" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" min-width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" class="status-tag" round>
              {{ scope.row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" align="center" />
        <el-table-column label="操作" min-width="150" fixed="right" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              inline-prompt
              active-text="启"
              inactive-text="停"
              style="margin-right: 12px"
              @change="handleStatusChange(scope.row)"
            />
            <el-button type="primary" link @click="openEditDialog(scope.row)">编辑</el-button>
            <el-popconfirm title="确定要删除该服务类型吗？" @confirm="handleDelete(scope.row)">
              <template #reference>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑服务类型' : '新增服务类型'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="服务名称" prop="name">
          <el-input v-model="form.name" placeholder="例如：日常保洁" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入服务类型的描述说明" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getServiceTypeList, addServiceType, updateServiceType, updateServiceTypeStatus, deleteServiceType } from '@/api/zhijian'
import { ElMessage } from 'element-plus'

const searchForm = ref({
  name: '',
  status: null
})

const tableData = ref([])
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = ref({
  id: undefined,
  name: '',
  description: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入服务名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const params: any = {}
    if (searchForm.value.name && searchForm.value.name.trim() !== '') {
      params.name = searchForm.value.name.trim()
    }
    if (searchForm.value.status !== null && searchForm.value.status !== '') {
      params.status = searchForm.value.status
    }
    
    const res: any = await getServiceTypeList(params)
    
    if (Array.isArray(res)) {
      tableData.value = res
    } else if (res && res.records) {
      tableData.value = res.records
    } else {
      tableData.value = []
    }
  } catch (error) {
    console.error("Failed to load data:", error)
    tableData.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})

const handleSearch = () => {
  loadData()
}

const handleReset = () => {
  searchForm.value = { name: '', status: null }
  loadData()
}

const handleStatusChange = async (row: any) => {
  try {
    await updateServiceTypeStatus(row.id, row.status)
    ElMessage.success(`已${row.status === 1 ? '启用' : '停用'}该服务类型`)
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row: any) => {
  try {
    await deleteServiceType(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const openAddDialog = () => {
  isEdit.value = false
  form.value = {
    id: undefined,
    name: '',
    description: '',
    status: 1
  }
  dialogVisible.value = true
}

const openEditDialog = (row: any) => {
  isEdit.value = true
  form.value = {
    id: row.id,
    name: row.name,
    description: row.description || '',
    status: row.status
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateServiceType(form.value)
          ElMessage.success('更新成功')
        } else {
          await addServiceType(form.value)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        loadData()
      } finally {
        submitLoading.value = false
      }
    }
  })
}
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
    }
  }
}
</style>
