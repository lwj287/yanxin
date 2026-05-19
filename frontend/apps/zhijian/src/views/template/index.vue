<template>
  <div class="user-container">
    <div class="header-action">
      <div class="search-area">
        <el-input v-model="searchForm.keyword" placeholder="搜索模板名称" clearable class="search-input" />
        <el-select v-model="searchForm.serviceType" placeholder="服务类型" clearable class="search-select">
          <el-option 
            v-for="item in serviceTypeList" 
            :key="item.name" 
            :label="item.name" 
            :value="item.name" 
          />
        </el-select>
        <el-select v-model="searchForm.status" placeholder="模板状态" clearable class="search-select">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <el-button type="primary" class="add-btn" @click="openAddDialog">新增模板</el-button>
    </div>

    <div class="table-wrapper" v-loading="loading">
      <el-table :data="tableData" stripe class="custom-table">
        <el-table-column type="index" label="序号" min-width="80" align="center" />
        <el-table-column prop="templateName" label="模板名称" min-width="150" align="center" />
        <el-table-column prop="serviceType" label="服务类型" min-width="150" align="center" />
        <el-table-column label="质检要求 (需提供照片)" min-width="300" align="center">
          <template #default="scope">
            <el-tag v-for="(item, index) in parseRequirements(scope.row.requirements)" :key="index" class="req-tag" size="small" style="margin: 0 4px;">
              {{ item }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" class="status-tag" round>
              {{ scope.row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" min-width="160" align="center" />
        <el-table-column label="操作" min-width="180" fixed="right" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              inline-prompt
              active-text="启"
              inactive-text="停"
              @change="handleStatusChange(scope.row)"
              style="margin-right: 12px;"
            />
            <el-button type="primary" link size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-popconfirm title="确定要删除该模板吗？" @confirm="handleDelete(scope.row)">
              <template #reference>
                <el-button type="danger" link size="small">删除</el-button>
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
        :total="total"
        background
        layout="total, sizes, prev, pager, next, jumper"
      />
    </div>

    <!-- 新增模板弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增质检模板" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="form.templateName" placeholder="例如：日常保洁标准版" />
        </el-form-item>
        <el-form-item label="服务类型" prop="serviceType">
          <el-select v-model="form.serviceType" placeholder="请选择服务类型" style="width: 100%">
            <el-option 
              v-for="item in serviceTypeList" 
              :key="item.name" 
              :label="item.name" 
              :value="item.name" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="质检要求" prop="requirements">
          <div style="width: 100%;">
            <div v-for="(item, index) in form.requirements" :key="index" class="req-item">
              <el-input v-model="form.requirements[index]" placeholder="请输入需要拍照的场景" style="width: 80%" />
              <el-button type="danger" circle plain size="small" @click="removeRequirement(index)" v-if="form.requirements.length > 1" style="margin-left: 10px;">删除</el-button>
            </div>
            <el-button type="primary" plain size="small" @click="addRequirement" style="margin-top: 10px;">+ 添加要求</el-button>
          </div>
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
import { getTemplateList, addTemplate, updateTemplate, deleteTemplate, updateTemplateStatus, getServiceTypeList } from '@/api/zhijian'
import { ElMessage } from 'element-plus'

const searchForm = ref({
  keyword: '',
  serviceType: '',
  status: null
})

const serviceTypeList = ref<any[]>([])

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(3)

const tableData = ref([])
const loading = ref(false)

// 弹窗相关
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref()
const form = ref({
  id: null as number | null,
  templateName: '',
  serviceType: '',
  requirements: [''], // 默认给一个空的要求输入框
  status: 1
})

const rules = {
  templateName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  serviceType: [{ required: true, message: '请输入服务类型', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const params: any = {}
    if (searchForm.value.keyword && searchForm.value.keyword.trim() !== '') {
      params.templateName = searchForm.value.keyword.trim()
    }
    if (searchForm.value.serviceType && searchForm.value.serviceType !== '') {
      params.serviceType = searchForm.value.serviceType
    }
    if (searchForm.value.status !== null && searchForm.value.status !== '') {
      params.status = searchForm.value.status
    }
    
    const res: any = await getTemplateList(params)
    
    // request.ts 拦截器中已经将 response.data.data 提取出来了，所以 res 本身就是数组
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
    console.error("Failed to load data:", error)
    tableData.value = []
  } finally {
    loading.value = false
  }
}

const loadServiceTypes = async () => {
  try {
    const res: any = await getServiceTypeList({ status: 1 })
    if (Array.isArray(res)) {
      serviceTypeList.value = res
    } else if (res && res.records) {
      serviceTypeList.value = res.records
    }
  } catch (error) {
    console.error("Failed to load service types:", error)
  }
}

onMounted(() => {
  loadServiceTypes()
  loadData()
})

const handleSearch = () => {
  loadData()
}

const handleReset = () => {
  searchForm.value = { keyword: '', serviceType: '', status: null }
  loadData()
}

const handleStatusChange = async (row: any) => {
  try {
    await updateTemplateStatus(row.id, row.status)
    ElMessage.success(`已${row.status === 1 ? '启用' : '停用'}该模板`)
  } catch (error) {
    // 恢复状态
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('操作失败')
  }
}

// 解析 JSON 字符串
const parseRequirements = (reqStr: string) => {
  if (!reqStr) return []
  try {
    const parsed = JSON.parse(reqStr)
    // 如果解析出来是对象数组（如 init.sql 中的数据），提取 name 字段
    if (Array.isArray(parsed) && parsed.length > 0 && typeof parsed[0] === 'object') {
      return parsed.map((item: any) => item.name || item.desc || '未命名要求')
    }
    // 如果是纯字符串数组，直接返回
    return Array.isArray(parsed) ? parsed : []
  } catch (e) {
    console.error('Failed to parse requirements:', reqStr, e)
    return []
  }
}

// 表单动态增减要求
const addRequirement = () => {
  form.value.requirements.push('')
}

const removeRequirement = (index: number) => {
  form.value.requirements.splice(index, 1)
}

const openAddDialog = () => {
  form.value = {
    id: null,
    templateName: '',
    serviceType: '',
    requirements: [''],
    status: 1
  }
  dialogVisible.value = true
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const handleEdit = (row: any) => {
  form.value = {
    id: row.id,
    templateName: row.templateName,
    serviceType: row.serviceType,
    requirements: parseRequirements(row.requirements),
    status: row.status
  }
  if (form.value.requirements.length === 0) {
    form.value.requirements = ['']
  }
  dialogVisible.value = true
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const handleDelete = async (row: any) => {
  try {
    const res: any = await deleteTemplate(row.id)
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

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      // 过滤掉空的质检要求
      const validRequirements = form.value.requirements.filter(r => r.trim() !== '')
      if (validRequirements.length === 0) {
        ElMessage.warning('请至少填写一项质检要求')
        return
      }

      submitLoading.value = true
      try {
        const payload = {
          id: form.value.id,
          templateName: form.value.templateName,
          serviceType: form.value.serviceType,
          requirements: JSON.stringify(validRequirements),
          status: form.value.status
        }
        
        let res: any
        if (form.value.id) {
          res = await updateTemplate(payload)
        } else {
          res = await addTemplate(payload)
        }

        if (res && res.id || res === true || res?.code === 200 || res?.data || !res?.code) {
          ElMessage.success(form.value.id ? '修改成功' : '新增成功')
          dialogVisible.value = false
          loadData() // 刷新列表
        } else {
          ElMessage.error(res?.message || (form.value.id ? '修改失败' : '新增失败'))
        }
      } catch (error) {
        console.error('Submit failed:', error)
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
