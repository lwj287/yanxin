<template>
  <div class="certification-container">
    <div class="header-action">
      <div class="search-area">
        <el-input v-model="searchQuery" placeholder="搜索学员姓名或证书名称" clearable class="search-input" :prefix-icon="Search" />
        <el-select v-model="status" placeholder="审核状态" clearable class="search-select">
          <el-option label="待审核" value="待审核" />
          <el-option label="已通过" value="已通过" />
          <el-option label="已驳回" value="已驳回" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>

    <div class="table-wrapper" v-loading="loading">
      <el-table :data="tableData" stripe class="custom-table">
        <el-table-column label="审批单号" min-width="120">
          <template #default="{ row }">
            <el-tooltip :content="row.certId" placement="top" effect="light">
              <span class="truncate-id">{{ row.certId.substring(0, 8) }}...</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="申请学员" min-width="150" align="center" />
        <el-table-column prop="examName" label="关联考试" min-width="220" align="center" show-overflow-tooltip />
        <el-table-column label="认证项目及等级" min-width="200" align="center">
          <template #default="{ row }">
            <span style="margin-right: 8px;">{{ row.certName }}</span>
            <el-tag :type="getLevelType(row.level)" size="small" effect="light" class="level-tag">{{ row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" min-width="180" align="center" />
        <el-table-column label="审核状态" min-width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.certStatus)" size="small" effect="dark">
              {{ row.certStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button v-if="row.certStatus === '待审核'" link type="primary" size="small" @click="handleAudit(row)">审批</el-button>
            <el-button v-if="row.certStatus === '已通过'" link type="success" size="small" @click="handleCert(row)">查看证书</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 审批弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="审批认证申请"
      width="500px"
      :close-on-click-modal="false"
      class="custom-dialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="审批结果" prop="certStatus">
          <el-radio-group v-model="form.certStatus">
            <el-radio label="已通过">通过</el-radio>
            <el-radio label="已驳回">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="电子证书" prop="certUrl" v-if="form.certStatus === '已通过'">
          <el-upload
            class="avatar-uploader"
            action="/api/file/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :headers="uploadHeaders"
          >
            <template v-if="form.certUrl">
              <img :src="form.certUrl" class="avatar" />
              <div class="avatar-uploader-mask" @click.stop>
                <div class="mask-action">
                  <el-icon><Upload /></el-icon>
                  <span>上传</span>
                </div>
              </div>
            </template>
            <div v-else class="avatar-uploader-icon">
              <el-icon><Plus /></el-icon>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看证书弹窗 -->
    <el-dialog v-model="certVisible" title="电子证书" width="500px" center class="custom-dialog">
      <div style="text-align: center;">
        <el-upload
          class="avatar-uploader"
          action="/api/file/upload"
          :show-file-list="false"
          :on-success="handleCertUpdateSuccess"
          :before-upload="beforeAvatarUpload"
          :headers="uploadHeaders"
        >
          <template v-if="currentCertUrl">
            <img :src="currentCertUrl" class="avatar" style="width: 400px; height: 280px;" />
            <div class="avatar-uploader-mask" @click.stop>
              <div class="mask-action" @click="previewImage(currentCertUrl)">
                <el-icon><View /></el-icon>
                <span>大图预览</span>
              </div>
              <div class="mask-action">
                <el-icon><Refresh /></el-icon>
                <span>更换证书</span>
              </div>
            </div>
          </template>
          <div v-else class="avatar-uploader-icon" style="width: 400px; height: 280px; display: flex; align-items: center; justify-content: center;">
            <el-icon><Plus /></el-icon>
          </div>
        </el-upload>
      </div>
    </el-dialog>

    <el-dialog v-model="previewVisible" title="大图预览" width="50%" center>
      <img :src="previewUrl" style="width: 100%; display: block;" alt="Preview" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { Search, Plus, View, Refresh, Upload } from '@element-plus/icons-vue'
import type { FormInstance, FormRules, UploadProps } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCertPage, updateCert, deleteCert } from '@/api/cert'

const searchQuery = ref('')
const status = ref('')
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const tableData = ref<any[]>([])

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await getCertPage({
      current: currentPage.value,
      size: pageSize.value,
      certName: searchQuery.value,
      auditStatus: status.value === '' ? undefined : status.value
    })
    
    tableData.value = res.records.map((item: any) => ({
      ...item,
      certLevel: item.certLevel || item.level || '未知等级',
      userName: item.userName || '未知学员', 
      examName: item.examName || '无关联考试',
      applyTime: item.applyTime ? item.applyTime.replace('T', ' ') : '-'
    }))
    total.value = res.total
  } catch (error) {
    console.error('获取认证列表失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})

const getLevelType = (level: string) => {
  if (!level || typeof level !== 'string') return 'info'
  if (level.includes('高级')) return 'danger'
  if (level.includes('中级')) return 'warning'
  if (level.includes('初级')) return 'success'
  if (level.includes('保洁')) return 'primary'
  if (level.includes('保姆')) return 'success'
  return 'info'
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = { '待审核': 'warning', '已通过': 'success', '已驳回': 'danger' }
  return map[status] || 'info'
}

const handleSearch = () => {
  currentPage.value = 1
  loadData()
}

const handleReset = () => {
  searchQuery.value = ''
  status.value = ''
  handleSearch()
}

// 审批相关
const dialogVisible = ref(false)
const certVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const currentAuditId = ref<string>()
const currentCertUrl = ref('')

const form = reactive({
  certStatus: '已通过',
  certUrl: ''
})

const uploadHeaders = {
  Authorization: localStorage.getItem('token') || ''
}

const handleAvatarSuccess: UploadProps['onSuccess'] = (response) => {
  if (response.code === 200) {
    form.certUrl = response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.size / 1024 / 1024 > 5) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

const previewVisible = ref(false)
const previewUrl = ref('')

const previewImage = (url: string) => {
  previewUrl.value = url
  previewVisible.value = true
}

const triggerUpload = () => {
  // empty
}

const rules = reactive<FormRules>({
  certStatus: [{ required: true, message: '请选择审批结果', trigger: 'change' }],
  certUrl: [{ required: true, message: '请上传电子证书', trigger: 'change' }]
})

const handleAudit = (row: any) => {
  currentAuditId.value = row.certId
  dialogVisible.value = true
  if (formRef.value) formRef.value.resetFields()
  Object.assign(form, {
    certStatus: '已通过',
    certUrl: ''
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid && currentAuditId.value) {
      submitLoading.value = true
      try {
        await updateCert({
          certId: currentAuditId.value,
          certStatus: form.certStatus,
          certUrl: form.certStatus === '已通过' ? form.certUrl : undefined
        })
        ElMessage.success('审批完成')
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('审批失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleView = (row: any) => ElMessage.info(`查看详情: 申请单号 ${row.certId}`)
const handleCert = (row: any) => {
  currentAuditId.value = row.certId
  currentCertUrl.value = row.certUrl || ''
  certVisible.value = true
}

const handleCertUpdateSuccess: UploadProps['onSuccess'] = async (response) => {
  if (response.code === 200 && currentAuditId.value) {
    try {
      await updateCert({
        certId: currentAuditId.value,
        certUrl: response.data
      })
      currentCertUrl.value = response.data
      ElMessage.success('证书更新成功')
      loadData()
    } catch (error) {
      console.error('更新证书地址失败:', error)
      ElMessage.error('更新证书地址失败')
    }
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除这条认证记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteCert(row.certId)
    ElMessage.success('删除成功')
    if (tableData.value.length === 1 && currentPage.value > 1) {
      currentPage.value -= 1
    }
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
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
.truncate-id {
  font-family: monospace;
  color: #606266;
  cursor: pointer;
}

.certification-container {
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
        width: 280px;
      }
      
      .search-select {
        width: 160px;
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

      .text-success {
        color: #67c23a;
        font-weight: 600;
      }

      .text-warning {
        color: #e6a23c;
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

  .avatar-uploader {
    :deep(.el-upload) {
      border: 1px dashed var(--el-border-color);
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);

      &:hover {
        border-color: var(--el-color-primary);
        .avatar-uploader-mask {
          opacity: 1;
        }
      }
    }

    .avatar-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 178px;
      height: 178px;
      text-align: center;
      line-height: 178px;
    }

    .avatar {
      width: 178px;
      height: 178px;
      display: block;
      object-fit: cover;
    }

    .avatar-uploader-mask {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 16px;
      opacity: 0;
      transition: opacity 0.3s;
      color: #fff;

      .mask-action {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 4px;
        cursor: pointer;
        
        .el-icon {
          font-size: 20px;
        }

        span {
          font-size: 12px;
        }

        &:hover {
          color: var(--el-color-primary);
        }
      }
    }
  }
}
</style>