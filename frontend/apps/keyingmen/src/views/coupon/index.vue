<template>
  <div class="page-wrap">
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="优惠券名称">
          <el-input v-model="searchForm.name" placeholder="请输入名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="success" @click="handleAdd">新增优惠券</el-button>
    </div>

    <div class="table-wrapper">
      <el-table 
        :data="tableData" 
        stripe 
        style="width: 100%"
      >
        <el-table-column prop="couponName" label="优惠券名称" align="center" min-width="150" />
        <el-table-column prop="faceValue" label="面额(元)" align="center" min-width="100" />
        <el-table-column prop="conditionAmount" label="使用条件" align="center" min-width="150">
          <template #default="scope">
            {{ scope.row.conditionAmount === 0 ? '无门槛' : `满${scope.row.conditionAmount}元可用` }}
          </template>
        </el-table-column>
        <el-table-column label="有效期" align="center" min-width="280">
          <template #default="scope">
            {{ scope.row.validStartTime }} ~ {{ scope.row.validEndTime }}
          </template>
        </el-table-column>
        <el-table-column prop="issueCount" label="发放数量" align="center" min-width="100" />
        <el-table-column prop="usedCount" label="使用数量" align="center" min-width="100" />
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增优惠券' : '编辑优惠券'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="优惠券名称">
          <el-input v-model="form.couponName" placeholder="请输入优惠券名称" />
        </el-form-item>
        <el-form-item label="面额(元)">
          <el-input-number v-model="form.faceValue" :min="0" :precision="2" :step="0.1" />
        </el-form-item>
        <el-form-item label="使用条件">
          <el-input-number v-model="form.conditionAmount" :min="0" :precision="2" :step="1" placeholder="0表示无门槛" />
          <span style="margin-left: 10px; color: #999;">0表示无门槛</span>
        </el-form-item>
        <el-form-item label="有效时间">
          <el-date-picker
            v-model="validTimeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="发放数量">
          <el-input-number v-model="form.issueCount" :min="0" :step="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
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
  type: ''
})

const tableData = ref([])

const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchTableData = async () => {
  try {
    const res: any = await request.get('/coupon-template/page', {
      params: {
        page: page.value,
        pageSize: pageSize.value,
        couponName: searchForm.name || undefined
      }
    })
    tableData.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('获取优惠券列表失败:', error)
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
  searchForm.type = ''
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

// 弹窗及表单逻辑
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const validTimeRange = ref<[string, string] | []>([])
const form = reactive({
  couponId: null as number | null,
  couponName: '',
  faceValue: 0,
  conditionAmount: 0,
  validStartTime: '',
  validEndTime: '',
  issueCount: 0
})

const resetForm = () => {
  form.couponId = null
  form.couponName = ''
  form.faceValue = 0
  form.conditionAmount = 0
  form.validStartTime = ''
  form.validEndTime = ''
  form.issueCount = 0
  validTimeRange.value = []
}

const handleAdd = () => {
  dialogType.value = 'add'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  form.couponId = row.couponId
  form.couponName = row.couponName
  form.faceValue = row.faceValue
  form.conditionAmount = row.conditionAmount
  form.issueCount = row.issueCount
  validTimeRange.value = [
    row.validStartTime ? row.validStartTime.replace('T', ' ') : '',
    row.validEndTime ? row.validEndTime.replace('T', ' ') : ''
  ]
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.couponName) {
    ElMessage.warning('请输入优惠券名称')
    return
  }
  if (!validTimeRange.value || validTimeRange.value.length !== 2) {
    ElMessage.warning('请选择有效时间')
    return
  }
  form.validStartTime = validTimeRange.value[0].replace('T', ' ')
  form.validEndTime = validTimeRange.value[1].replace('T', ' ')

  try {
    if (dialogType.value === 'add') {
      await request.post('/coupon-template', form)
      ElMessage.success('新增成功')
    } else {
      await request.put('/coupon-template', form)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    fetchTableData()
  } catch (error) {
    console.error('保存失败:', error)
  }
}

// 逻辑删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该优惠券吗？此操作为逻辑删除。', '警告', {
      type: 'error'
    })
    await request.delete(`/coupon-template/${row.couponId}`)
    ElMessage.success('删除成功')
    fetchTableData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}
</script>

<style scoped lang="scss">
.page-wrap {
  padding: 24px;
}

.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: #ffffff;
  padding: 20px 24px;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
  
  :deep(.el-form-item) {
    margin-bottom: 0;
  }
}

.table-wrapper {
  background: #ffffff;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
  margin-bottom: 24px;

  :deep(.el-table) {
    border-radius: 12px;
    overflow: hidden;
    border: 1px solid #ebeef5;
    
    th.el-table__cell {
      background-color: #f5f7fa;
      color: #606266;
      font-weight: 600;
    }
  }
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  background: #ffffff;
  padding: 16px 24px;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
}
</style>