<template>
  <div class="user-container">
    <div class="header-action">
      <div class="search-area">
        <el-input v-model="searchName" placeholder="搜索姓名或手机号" clearable class="search-input" :prefix-icon="Search" />
        <el-select v-model="searchRole" placeholder="角色" clearable class="search-select">
          <el-option label="超级管理员" :value="1" />
          <el-option label="家政员" :value="2" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <el-button type="primary" :icon="Plus" class="add-btn" @click="openAddDialog">新增员工</el-button>
    </div>

    <div class="table-wrapper" v-loading="loading">
      <el-table :data="pagedData" stripe class="custom-table">
        <el-table-column prop="realName" label="姓名" min-width="120" align="center" />
        <el-table-column prop="phone" label="手机号码" min-width="150" align="center" />
        <el-table-column label="角色" min-width="140" align="center">
          <template #default="{ row }">
            <el-tag :type="row.roleId === 1 ? 'danger' : 'info'" size="small" effect="light" class="level-tag">
              {{ row.roleId === 1 ? '超级管理员' : '家政员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="100" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row, $event)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" min-width="180" align="center" />
        <el-table-column label="操作" min-width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该员工吗?" @confirm="handleDelete(row)">
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
      :title="dialogMode === 'add' ? '新增员工' : '编辑员工'"
      width="520px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="formModel" :rules="formRules" label-width="90px">
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="formModel.realName" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formModel.phone" placeholder="请输入11位手机号" maxlength="11" clearable />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="formModel.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option label="超级管理员" :value="1" />
            <el-option label="家政员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="formModel.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="formModel.password"
            type="password"
            show-password
            :placeholder="dialogMode === 'add' ? '请输入登录密码' : '留空则不修改密码'"
            clearable
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { getStaffList, addStaff, updateStaff, deleteStaff, updateStaffStatus } from '@/api/staff'
import type { Staff } from '@/api/types'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const loading = ref(false)
const allData = ref<Staff[]>([])
const searchName = ref('')
const searchRole = ref<number | undefined>(undefined)
const appliedSearchName = ref('')
const appliedSearchRole = ref<number | undefined>(undefined)
const currentPage = ref(1)
const pageSize = ref(10)
const pageSizes = [10, 20, 50, 100]
const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit'>('add')
const submitting = ref(false)
const formRef = ref<FormInstance>()
const createDefaultForm = () => ({
  staffId: undefined as number | undefined,
  realName: '',
  phone: '',
  roleId: 2,
  status: 1,
  password: ''
})
const formModel = ref(createDefaultForm())
const formRules: FormRules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '请输入正确的11位手机号', trigger: 'blur' }
  ],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }],
  password: [
    {
      validator: (_rule, value, callback) => {
        if (dialogMode.value === 'add' && !value) {
          callback(new Error('新增员工必须设置密码'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
}

const filteredData = computed(() => {
  return allData.value.filter((item) => {
    const matchName = !appliedSearchName.value || item.realName?.includes(appliedSearchName.value) || item.phone?.includes(appliedSearchName.value)
    const matchRole = appliedSearchRole.value === undefined || item.roleId === appliedSearchRole.value
    return matchName && matchRole
  })
})

const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredData.value.slice(start, start + pageSize.value)
})

const handleSearch = () => {
  appliedSearchName.value = searchName.value.trim()
  appliedSearchRole.value = searchRole.value
  currentPage.value = 1
}

const handleReset = () => {
  searchName.value = ''
  searchRole.value = undefined
  appliedSearchName.value = ''
  appliedSearchRole.value = undefined
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
    const list = await getStaffList()
    allData.value = list || []
  } catch (error) {
    console.error('Failed to load staff list:', error)
  } finally {
    loading.value = false
  }
}

const handleStatusChange = async (row: Staff, newStatus: number) => {
  try {
    await updateStaffStatus(row.staffId, newStatus)
    ElMessage.success('状态更新成功')
  } catch (error) {
    row.status = newStatus === 1 ? 0 : 1
    ElMessage.error('状态更新失败')
  }
}

const handleDelete = async (row: Staff) => {
  try {
    await ElMessageBox.confirm('确定删除该员工吗?', '提示', { type: 'warning' })
    await deleteStaff(row.staffId)
    ElMessage.success('删除成功')
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const openAddDialog = () => {
  dialogMode.value = 'add'
  formModel.value = createDefaultForm()
  dialogVisible.value = true
}

const openEditDialog = (row: Staff) => {
  dialogMode.value = 'edit'
  formModel.value = {
    staffId: row.staffId,
    realName: row.realName || '',
    phone: row.phone || '',
    roleId: row.roleId ?? 2,
    status: row.status ?? 1,
    password: ''
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const form = formRef.value
  if (!form) return
  await form.validate()
  submitting.value = true
  try {
    const payload: Partial<Staff> = {
      staffId: formModel.value.staffId,
      realName: formModel.value.realName.trim(),
      phone: formModel.value.phone.trim(),
      roleId: formModel.value.roleId,
      status: formModel.value.status
    }
    if (formModel.value.password) {
      payload.password = formModel.value.password
    }
    if (dialogMode.value === 'add') {
      await addStaff(payload)
      ElMessage.success('新增成功')
    } else {
      await updateStaff(payload)
      ElMessage.success('编辑成功')
    }
    dialogVisible.value = false
    loadList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(dialogMode.value === 'add' ? '新增失败' : '编辑失败')
    }
  } finally {
    submitting.value = false
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
