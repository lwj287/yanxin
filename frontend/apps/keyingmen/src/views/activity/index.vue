<template>
  <div class="page-wrap">
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="活动名称">
          <el-input v-model="searchForm.name" placeholder="请输入活动名称" clearable />
        </el-form-item>
        <el-form-item label="活动状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 200px">
            <el-option label="未开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已结束" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="success" @click="handleAdd">创建活动</el-button>
    </div>

    <div class="table-wrapper">
      <el-table 
        :data="tableData" 
        stripe 
        style="width: 100%"
      >
        <el-table-column prop="activityName" label="活动名称" align="center" min-width="150" />
        <el-table-column prop="activityType" label="类型" min-width="100" align="center">
          <template #default="scope">
            {{ scope.row.activityType === 1 ? '赠送' : '抽奖' }}
          </template>
        </el-table-column>
        <el-table-column prop="activityStatus" label="状态" min-width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.activityStatus === 0" type="info">未开始</el-tag>
            <el-tag v-else-if="scope.row.activityStatus === 1" type="success">进行中</el-tag>
            <el-tag v-else type="danger">已结束</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="活动时间" min-width="280" align="center">
          <template #default="scope">
            {{ scope.row.startTime }} ~ {{ scope.row.endTime }}
          </template>
        </el-table-column>
        <el-table-column prop="issueCount" label="券发放数" min-width="100" align="center" />
        <el-table-column prop="redeemCount" label="券核销数" min-width="100" align="center" />
        <el-table-column label="操作" min-width="120" align="center" fixed="right">
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

    <!-- 创建/编辑活动弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '创建活动' : '编辑活动'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="活动名称">
          <el-input v-model="form.activityName" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动类型">
          <el-radio-group v-model="form.activityType">
            <el-radio :label="1">赠送</el-radio>
            <el-radio :label="2">抽奖</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="关联优惠券">
          <el-select v-model="form.couponId" placeholder="请选择关联优惠券" filterable style="width: 100%;">
            <el-option
              v-for="item in couponList"
              :key="item.couponId"
              :label="item.couponName"
              :value="item.couponId"
            >
              <span style="float: left">{{ item.couponName }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">面额: {{ item.faceValue }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="活动时间">
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
  status: ''
})

const tableData = ref([])

const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const couponList = ref<any[]>([])

const fetchCouponList = async () => {
  try {
    const res: any = await request.get('/coupon-template/page', {
      params: {
        page: 1,
        pageSize: 1000 // 获取所有优惠券
      }
    })
    couponList.value = res.records || []
  } catch (error) {
    console.error('获取优惠券列表失败:', error)
  }
}

const fetchTableData = async () => {
  try {
    const res: any = await request.get('/activity-template/page', {
      params: {
        page: page.value,
        pageSize: pageSize.value,
        activityName: searchForm.name || undefined,
        activityStatus: searchForm.status !== '' ? searchForm.status : undefined
      }
    })
    const records = res.records || []
    
    // 动态计算活动状态并在前端进行过滤
    const now = new Date().getTime()
    let filteredRecords = records.map((item: any) => {
      const startTime = new Date(item.startTime).getTime()
      const endTime = new Date(item.endTime).getTime()
      
      if (now < startTime) {
        item.activityStatus = 0 // 未开始
      } else if (now > endTime) {
        item.activityStatus = 2 // 已结束
      } else {
        item.activityStatus = 1 // 进行中
      }
      return item
    })
    
    // 如果用户选择了活动状态，在前端进行二次过滤
    if (searchForm.status !== '') {
      filteredRecords = filteredRecords.filter((item: any) => item.activityStatus === searchForm.status)
    }
    
    tableData.value = filteredRecords
    total.value = searchForm.status !== '' ? filteredRecords.length : (res.total || 0)
  } catch (error) {
    console.error('获取活动列表失败:', error)
  }
}

onMounted(() => {
  fetchTableData()
  fetchCouponList()
})

const handleSearch = () => {
  page.value = 1
  fetchTableData()
}

const resetSearch = () => {
  searchForm.name = ''
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

// 弹窗及表单逻辑
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const validTimeRange = ref<[string, string] | []>([])
const form = reactive({
  activityId: null as number | null,
  activityName: '',
  activityType: 1,
  couponId: null as number | null,
  startTime: '',
  endTime: '',
  issueCount: 0
})

const resetForm = () => {
  form.activityId = null
  form.activityName = ''
  form.activityType = 1
  form.couponId = null
  form.startTime = ''
  form.endTime = ''
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
  form.activityId = row.activityId
  form.activityName = row.activityName
  form.activityType = row.activityType
  form.couponId = row.couponId
  form.issueCount = row.issueCount
  validTimeRange.value = [
    row.startTime ? row.startTime.replace('T', ' ') : '',
    row.endTime ? row.endTime.replace('T', ' ') : ''
  ]
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.activityName) {
    ElMessage.warning('请输入活动名称')
    return
  }
  if (!form.couponId) {
    ElMessage.warning('请选择关联优惠券')
    return
  }
  if (!validTimeRange.value || validTimeRange.value.length !== 2) {
    ElMessage.warning('请选择活动时间')
    return
  }
  form.startTime = validTimeRange.value[0].replace('T', ' ')
  form.endTime = validTimeRange.value[1].replace('T', ' ')

  try {
    if (dialogType.value === 'add') {
      await request.post('/activity-template', form)
      ElMessage.success('新增成功')
    } else {
      await request.put('/activity-template', form)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    fetchTableData()
  } catch (error) {
    console.error('保存失败:', error)
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该活动吗？此操作为逻辑删除。', '警告', {
      type: 'error'
    })
    await request.delete(`/activity-template/${row.activityId}`)
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