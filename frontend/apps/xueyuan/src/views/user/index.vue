<template>
  <div class="user-container">
    <div class="header-action">
      <div class="search-area">
        <el-input v-model="searchQuery" placeholder="搜索学员姓名或手机号" clearable class="search-input" :prefix-icon="Search" />
        <el-select v-model="searchRoleId" placeholder="角色" clearable class="search-select">
          <el-option label="管理员" :value="1" />
          <el-option label="讲师" :value="2" />
          <el-option label="员工" :value="3" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <el-button type="primary" :icon="Plus" @click="handleAdd" class="add-btn">新增学员</el-button>
    </div>

    <div class="table-wrapper" v-loading="loading">
      <el-table :data="tableData" stripe class="custom-table">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="32" :src="row.avatar" />
          </template>
        </el-table-column>
        <el-table-column prop="username" label="登录账号" min-width="150" align="center" />
        <el-table-column prop="realName" label="学员姓名" min-width="120" align="center" />
        <el-table-column prop="phone" label="手机号码" min-width="150" align="center" />
        
        <!-- 角色字段展示 -->
        <el-table-column label="角色" min-width="140" align="center">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.roleId)" size="small" effect="light" class="level-tag">
              {{ row.roleName }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="状态" min-width="100" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" min-width="180" align="center" />
        <el-table-column label="操作" min-width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除该学员吗?" @confirm="handleDelete(row)">
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
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 用户编辑/新增弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增学员' : '编辑学员'"
      width="500px"
      :close-on-click-modal="false"
      class="custom-dialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="登录账号" prop="username">
          <el-input v-model="form.username" placeholder="请输入登录账号" :disabled="dialogType === 'edit'" />
        </el-form-item>
        <el-form-item label="登录密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="form.password" placeholder="不填默认123456" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-radio-group v-model="form.roleId">
            <el-radio :label="1">管理员</el-radio>
            <el-radio :label="2">讲师</el-radio>
            <el-radio :label="3">学员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="正常" inactive-text="停用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { getUserPage, addUser, updateUser, deleteUser } from '@/api/user'

const searchQuery = ref('')
const searchRoleId = ref<number | ''>('')
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const tableData = ref<any[]>([])

const getRoleName = (roleId: number) => {
  const map: Record<number, string> = { 1: '管理员', 2: '讲师', 3: '员工' }
  return map[roleId] || '未知角色'
}

const getRoleType = (roleId: number) => {
  const map: Record<number, string> = { 1: 'danger', 2: 'warning', 3: 'info' }
  return map[roleId] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await getUserPage({
      current: currentPage.value,
      size: pageSize.value,
      realName: searchQuery.value || undefined,
      roleId: searchRoleId.value || undefined
    })
    
    tableData.value = res.records.map((item: any) => ({
      ...item,
      roleName: getRoleName(item.roleId),
      createTime: item.createTime ? item.createTime.replace('T', ' ') : '-'
    }))
    total.value = res.total
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})

const handleSearch = () => {
  currentPage.value = 1
  loadData()
}

const handleReset = () => {
  searchQuery.value = ''
  searchRoleId.value = ''
  handleSearch()
}

// 表单相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  id: undefined as number | undefined,
  username: '',
  password: '',
  realName: '',
  phone: '',
  roleId: 3,
  status: 1
})

const rules = reactive<FormRules>({
  username: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号码', trigger: 'blur' }],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
})

const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  if (formRef.value) formRef.value.resetFields()
  Object.assign(form, {
    id: undefined,
    username: '',
    password: '',
    realName: '',
    phone: '',
    roleId: 3,
    status: 1
  })
}

const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  if (formRef.value) formRef.value.resetFields()
  Object.assign(form, {
    id: row.id,
    username: row.username,
    password: '',
    realName: row.realName,
    phone: row.phone,
    roleId: row.roleId,
    status: row.status
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (dialogType.value === 'add') {
          await addUser(form)
          ElMessage.success('新增成功')
        } else {
          await updateUser(form)
          ElMessage.success('编辑成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('提交失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDelete = async (row: any) => {
  try {
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

const handleStatusChange = async (row: any) => {
  try {
    await updateUser({ id: row.id, status: row.status })
    ElMessage.success(`状态已切换为: ${row.status === 1 ? '正常' : '停用'}`)
  } catch (error) {
    console.error('状态修改失败:', error)
    row.status = row.status === 1 ? 0 : 1 // 回滚
  }
}

const handleSizeChange = () => {
  currentPage.value = 1
  loadData()
}
const handleCurrentChange = () => {
  loadData()
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

      .search-input {
        width: 240px;
      }
      
      .search-select {
        width: 140px;
      }
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

      .empty-text {
        color: #c0c4cc;
        font-size: 13px;
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