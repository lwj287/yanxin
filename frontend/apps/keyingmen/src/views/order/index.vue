<template>
  <div class="page-container">
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="订单ID">
          <el-input v-model="searchForm.orderId" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="会员姓名">
          <el-input v-model="searchForm.memberName" placeholder="请输入会员姓名" clearable />
        </el-form-item>
        <el-form-item label="支付状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 200px">
            <el-option label="待支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="已取消" :value="2" />
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
      <el-table-column prop="orderId" label="订单ID" min-width="100" align="center" />
      <el-table-column prop="memberName" label="会员姓名" min-width="120" align="center" />
      <el-table-column prop="serviceItem" label="服务项目" min-width="150" align="center" />
      <el-table-column prop="totalAmount" label="总金额(元)" min-width="100" align="center" />
      <el-table-column prop="pointDiscountAmount" label="积分抵扣(元)" min-width="120" align="center" />
      <el-table-column prop="couponDiscountAmount" label="优惠券抵扣(元)" min-width="120" align="center" />
      <el-table-column prop="payAmount" label="实际支付(元)" min-width="100" align="center" />
      <el-table-column prop="payMethod" label="支付方式" min-width="100" align="center">
        <template #default="scope">
          <span v-if="scope.row.payMethod === 1">微信</span>
          <span v-else-if="scope.row.payMethod === 2">支付宝</span>
          <span v-else-if="scope.row.payMethod === 3">余额</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="payStatus" label="状态" min-width="100" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.payStatus === 0" type="warning">待支付</el-tag>
          <el-tag v-else-if="scope.row.payStatus === 1" type="success">已支付</el-tag>
          <el-tag v-else type="info">已取消</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="payTime" label="支付时间" min-width="180" align="center" />
      <el-table-column label="操作" min-width="120" fixed="right" align="center">
        <template #default="scope">
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const searchForm = reactive({
  orderId: '',
  memberName: '',
  status: ''
})

const tableData = ref([])

const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchTableData = async () => {
  try {
    const res: any = await request.get('/orders/page', {
      params: {
        page: page.value,
        pageSize: pageSize.value,
        orderId: searchForm.orderId || undefined,
        memberName: searchForm.memberName || undefined,
        payStatus: searchForm.status !== '' ? searchForm.status : undefined
      }
    })
    tableData.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('获取订单列表失败:', error)
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
  searchForm.orderId = ''
  searchForm.memberName = ''
  searchForm.status = ''
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

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该订单记录吗？此操作为逻辑删除。', '警告', {
      type: 'error'
    })
    await request.delete(`/orders/${row.orderId}`)
    ElMessage.success('删除成功')
    fetchTableData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}
</script>