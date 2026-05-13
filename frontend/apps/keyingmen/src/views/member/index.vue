<template>
  <div class="page-container">
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="会员姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="会员等级">
          <el-select v-model="searchForm.level" placeholder="请选择" clearable style="width: 200px">
            <el-option label="普通会员" :value="0" />
            <el-option label="高级会员" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table 
      :data="tableData" 
      border 
      stripe 
      :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold' }"
      style="width: 100%"
    >
      <el-table-column prop="memberName" label="会员姓名" align="center" />
      <el-table-column prop="phone" label="手机号" align="center" />
      <el-table-column prop="memberLevel" label="等级" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.memberLevel === 1 ? 'warning' : 'info'">
            {{ scope.row.memberLevel === 1 ? '高级会员' : '普通会员' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="memberStatus" label="状态" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.memberStatus === 1 ? 'success' : 'danger'">
            {{ scope.row.memberStatus === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="accountBalance" label="积分余额" align="center" />
      <el-table-column prop="inviteCount" label="邀请人数" align="center" />
      <el-table-column prop="inviterId" label="邀请人ID" align="center" />
      <el-table-column prop="registerTime" label="注册时间" align="center" width="180" />
      <el-table-column label="操作" width="200" align="center">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button link :type="scope.row.memberStatus === 1 ? 'warning' : 'success'" size="small" @click="handleStatusChange(scope.row)">
            {{ scope.row.memberStatus === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button link type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container" style="margin-top: 20px; display: flex; justify-content: flex-end;">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="editDialogVisible" title="编辑会员信息" width="500px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="会员姓名">
          <el-input v-model="editForm.memberName" placeholder="请输入会员姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="会员等级">
          <el-select v-model="editForm.memberLevel" placeholder="请选择" style="width: 100%">
            <el-option label="普通会员" :value="0" />
            <el-option label="高级会员" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="积分余额">
          <el-input-number v-model="editForm.accountBalance" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const searchForm = reactive({
  name: '',
  phone: '',
  level: ''
})

const tableData = ref([])

const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchTableData = async () => {
  try {
    const res: any = await request.get('/member/page', {
      params: {
        page: page.value,
        pageSize: pageSize.value,
        memberName: searchForm.name || undefined,
        phone: searchForm.phone || undefined,
        memberLevel: searchForm.level !== '' ? searchForm.level : undefined
      }
    })
    tableData.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('获取会员列表失败:', error)
  }
}

onMounted(() => {
  fetchTableData()
})

const handleSearch = () => {
  page.value = 1
  fetchTableData()
}

const resetSearch = () => {
  searchForm.name = ''
  searchForm.phone = ''
  searchForm.level = ''
  handleSearch()
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchTableData()
}

const handleCurrentChange = (val: number) => {
  page.value = val
  fetchTableData()
}

// 编辑逻辑
const editDialogVisible = ref(false)
const editForm = reactive({
  memberId: null,
  memberName: '',
  phone: '',
  memberLevel: 0,
  accountBalance: 0
})

const handleEdit = (row: any) => {
  editForm.memberId = row.memberId
  editForm.memberName = row.memberName
  editForm.phone = row.phone
  editForm.memberLevel = row.memberLevel
  editForm.accountBalance = row.accountBalance
  editDialogVisible.value = true
}

const submitEdit = async () => {
  try {
    await request.put('/member', editForm)
    ElMessage.success('更新成功')
    editDialogVisible.value = false
    fetchTableData()
  } catch (error) {
    console.error('更新失败:', error)
  }
}

// 状态切换（启用/禁用）
const handleStatusChange = async (row: any) => {
  const newStatus = row.memberStatus === 1 ? 0 : 1
  const actionText = newStatus === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(`确定要${actionText}该会员吗？`, '提示', {
      type: 'warning'
    })
    await request.put('/member', {
      memberId: row.memberId,
      memberStatus: newStatus
    })
    ElMessage.success(`${actionText}成功`)
    fetchTableData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('状态修改失败:', error)
    }
  }
}

// 逻辑删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该会员吗？此操作为逻辑删除。', '警告', {
      type: 'error'
    })
    await request.delete(`/member/${row.memberId}`)
    ElMessage.success('删除成功')
    fetchTableData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}
</script>