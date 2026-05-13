<template>
  <div class="user-container">
    <div class="header-action">
      <div class="search-area">
        <el-input v-model="searchForm.staffName" placeholder="搜索服务人员" clearable class="search-input" />
        <el-select v-model="searchForm.status" placeholder="任务状态" clearable class="search-select">
          <el-option label="待审核" value="UPLOADED" />
          <el-option label="已合格" value="PASS" />
          <el-option label="已驳回" value="REJECT" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <el-button type="primary" class="add-btn" @click="handleExport">导出报表</el-button>
    </div>

    <div class="table-wrapper" v-loading="loading">
      <el-table :data="tableData" stripe class="custom-table">
        <el-table-column prop="taskNo" label="质检流水号" min-width="160" align="center" />
        <el-table-column prop="orderNo" label="关联订单号" min-width="160" align="center" />
        <el-table-column prop="serviceType" label="服务类型" min-width="120" align="center" />
        <el-table-column prop="staffName" label="服务人员" min-width="120" align="center" />
        <el-table-column prop="aiScore" label="AI评分" min-width="100" align="center">
          <template #default="scope">
            <span v-if="scope.row.aiScore">{{ scope.row.aiScore }}分</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="manualScore" label="人工评分" min-width="100" align="center">
          <template #default="scope">
            <span v-if="scope.row.manualScore">{{ scope.row.manualScore }}分</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="120" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" class="level-tag">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" min-width="180" align="center" />
        <el-table-column label="操作" min-width="180" fixed="right" align="center">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="openDetailDialog(scope.row)">查看详情</el-button>
            <el-button size="small" type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-popconfirm title="确定要删除该任务吗？" @confirm="handleDelete(scope.row)">
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

    <!-- 编辑任务弹窗 -->
    <el-dialog v-model="dialogVisible" title="编辑质检任务" width="800px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="服务人员" prop="staffName">
          <el-input v-model="form.staffName" placeholder="请输入服务人员名称" />
        </el-form-item>
        <el-form-item label="人工评分" prop="manualScore">
          <el-input-number v-model="form.manualScore" :min="0" :max="100" :precision="1" :step="1" placeholder="请输入评分" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="任务状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="待上传" value="PENDING" />
            <el-option label="待审核" value="UPLOADED" />
            <el-option label="已合格" value="PASS" />
            <el-option label="已驳回" value="REJECT" />
          </el-select>
        </el-form-item>
      </el-form>

      <!-- 图像识别结果展示区域 -->
      <div class="image-gallery">
        <div class="gallery-header" style="margin-bottom: 16px;">
          <h4 class="gallery-title" style="margin: 0;">AI 图像识别结果</h4>
        </div>

        <div class="image-list">
          <div v-for="(img, index) in currentTask.imageRecords" :key="index" class="image-item">
            <div class="image-header">
              <div style="display: flex; align-items: center; width: 100%;">
                <span class="file-name" style="font-weight: bold; margin-right: auto; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">{{ img.fileName }}</span>
                <el-tag :type="img.aiStatus === 'PASSED' ? 'success' : (img.aiStatus === 'FAILED' ? 'danger' : 'info')" size="small" style="margin-right: 12px; flex-shrink: 0;">
                  {{ img.aiStatus === 'PASSED' ? '合格' : (img.aiStatus === 'FAILED' ? '不合格' : (img.aiStatus === 'UNCHECKED' ? '检测中...' : '未检测')) }}
                </el-tag>
                <span v-if="img.aiStatus !== 'UNCHECKED' && img.aiStatus !== 'PENDING_UPLOAD' && img.aiScore !== undefined && img.aiScore !== null" style="font-size: 14px; font-weight: bold; color: #409eff; margin-right: 12px; flex-shrink: 0;">
                  {{ img.aiScore }}分
                </span>
                <el-button type="danger" :icon="Delete" circle size="small" @click="handleDeleteImage(img.id, index)" title="删除图片" style="flex-shrink: 0;" />
              </div>
            </div>
            
            <div class="image-wrapper" v-loading="img.aiStatus === 'UNCHECKED'">
              <img :src="img.imageUrl || img.previewUrl" class="record-img" />
              <!-- 渲染 AI 标注框 -->
              <div v-if="img.aiDetectResult && img.aiStatus !== 'UNCHECKED'">
                <div v-for="(boxObj, bIndex) in parseDetectResult(img.aiDetectResult)" :key="bIndex" 
                     class="ai-box" 
                     :style="getBoxStyle(boxObj)">
                  <span class="ai-label" :style="getLabelStyle(boxObj.label)">{{ boxObj.label }} ({{ Math.round(boxObj.confidence * 100) }}%)</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 上传图片按钮 (矩形加号) -->
          <div class="image-item upload-item">
            <el-upload
              class="upload-area"
              action="#"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleImageUpload"
            >
              <div class="upload-trigger" v-loading="uploadLoading">
                <el-icon class="plus-icon"><Plus /></el-icon>
                <div class="upload-text">上传图片</div>
              </div>
            </el-upload>
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeEditDialog">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确认</el-button>
        </span>
      </template>
    </el-dialog>
    <!-- 查看详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="质检任务详情" width="800px">
      <el-descriptions :column="2" border class="margin-top">
        <el-descriptions-item label="质检流水号">{{ currentTask.taskNo }}</el-descriptions-item>
        <el-descriptions-item label="关联订单号">{{ currentTask.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="服务类型">{{ currentTask.serviceType }}</el-descriptions-item>
        <el-descriptions-item label="服务人员">{{ currentTask.staffName }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ currentTask.submitTime }}</el-descriptions-item>
        <el-descriptions-item label="任务状态">
          <el-tag :type="getStatusType(currentTask.status)" size="small">
            {{ getStatusLabel(currentTask.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="AI评分">
          {{ currentTask.aiScore ? currentTask.aiScore + '分' : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="人工评分">
          {{ currentTask.manualScore ? currentTask.manualScore + '分' : '-' }}
        </el-descriptions-item>
      </el-descriptions>

      <!-- 详情弹窗中的图片展示区域 (只读) -->
      <div class="image-gallery" v-if="currentTask.imageRecords && currentTask.imageRecords.length > 0">
        <div class="gallery-header" style="margin-bottom: 16px;">
          <h4 class="gallery-title" style="margin: 0;">AI 图像识别结果</h4>
        </div>

        <div class="image-list">
          <div v-for="(img, index) in currentTask.imageRecords" :key="index" class="image-item">
            <div class="image-header">
              <div style="display: flex; align-items: center; width: 100%;">
                <span class="file-name" style="font-weight: bold; margin-right: auto; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">{{ img.fileName }}</span>
                <el-tag :type="img.aiStatus === 'PASSED' ? 'success' : (img.aiStatus === 'FAILED' ? 'danger' : 'info')" size="small" style="margin-right: 12px; flex-shrink: 0;">
                  {{ img.aiStatus === 'PASSED' ? '合格' : (img.aiStatus === 'FAILED' ? '不合格' : (img.aiStatus === 'UNCHECKED' ? '检测中...' : '未检测')) }}
                </el-tag>
                <span v-if="img.aiStatus !== 'UNCHECKED' && img.aiScore !== undefined && img.aiScore !== null" style="font-size: 14px; font-weight: bold; color: #409eff; flex-shrink: 0;">
                  {{ img.aiScore }}分
                </span>
              </div>
            </div>
            
            <div class="image-wrapper" v-loading="img.aiStatus === 'UNCHECKED'">
              <img :src="img.imageUrl" class="record-img" />
              <!-- 渲染 AI 标注框 -->
              <div v-if="img.aiDetectResult && img.aiStatus !== 'UNCHECKED'">
                <div v-for="(boxObj, bIndex) in parseDetectResult(img.aiDetectResult)" :key="bIndex" 
                     class="ai-box" 
                     :style="getBoxStyle(boxObj)">
                  <span class="ai-label" :style="getLabelStyle(boxObj.label)">{{ boxObj.label }} ({{ Math.round(boxObj.confidence * 100) }}%)</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'
import { getTaskList, updateTask, deleteTask, uploadTaskImage, getTaskDetail, deleteTaskImage } from '@/api/zhijian'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchForm = ref({
  staffName: '',
  status: ''
})

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(4)

const tableData = ref([])

// 弹窗相关
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref()
const form = ref({
  id: null as number | null,
  staffName: '',
  manualScore: null as number | null,
  status: ''
})

// 记录待上传的图片文件对象
const pendingUploadFiles = ref<any[]>([])

const rules = {
  staffName: [{ required: true, message: '请输入服务人员名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择任务状态', trigger: 'change' }]
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

const handleExport = () => {
  if (tableData.value.length === 0) {
    ElMessage.warning('没有可导出的数据')
    return
  }
  
  let csvContent = '质检流水号,关联订单号,服务类型,服务人员,AI评分,人工评分,状态,提交时间\n'
  
  tableData.value.forEach((row: any) => {
    const taskNo = row.taskNo || ''
    const orderNo = row.orderNo || ''
    const serviceType = row.serviceType || ''
    const staffName = row.staffName || ''
    const aiScore = row.aiScore || ''
    const manualScore = row.manualScore || ''
    const status = getStatusLabel(row.status)
    const submitTime = row.submitTime || ''
    
    csvContent += `${taskNo},${orderNo},${serviceType},${staffName},${aiScore},${manualScore},${status},${submitTime}\n`
  })
  
  const blob = new Blob(['\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `质检任务报表_${new Date().getTime()}.csv`
  link.click()
  URL.revokeObjectURL(link.href)
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

    const res: any = await getTaskList(params)
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
  const map: Record<string, string> = {
    'PENDING': 'info',
    'UPLOADED': 'primary',
    'PASS': 'success',
    'REJECT': 'danger'
  }
  return map[status] || 'info'
}

const getStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    'PENDING': '待上传',
    'UPLOADED': '待审核',
    'PASS': '已合格',
    'REJECT': '已驳回'
  }
  return map[status] || '未知'
}

const handleEdit = (row: any) => {
  currentTask.value = { ...row }
  form.value = {
    id: row.id,
    staffName: row.staffName,
    manualScore: row.manualScore,
    status: row.status
  }
  pendingUploadFiles.value = [] // 清空待上传列表
  dialogVisible.value = true
  if (formRef.value) {
    formRef.value.clearValidate()
  }
  startPolling()
}

const closeEditDialog = () => {
  dialogVisible.value = false
  // 清理生成的对象URL
  pendingUploadFiles.value.forEach(file => {
    if (file.previewUrl) {
      URL.revokeObjectURL(file.previewUrl)
    }
  })
  pendingUploadFiles.value = []
  stopPolling()
}

const submitForm = () => {
  if (!formRef.value) return
  formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true
      try {
        // 先处理待上传的图片
        if (pendingUploadFiles.value.length > 0) {
          const uploadPromises = pendingUploadFiles.value.map(fileObj => {
            const formData = new FormData()
            formData.append('taskId', form.value.id!.toString())
            formData.append('file', fileObj.raw)
            return uploadTaskImage(formData)
          })
          
          await Promise.all(uploadPromises)
          ElMessage.success('图片上传成功，AI正在后台检测中...')
          // 重新拉取数据触发轮询
          const detailRes: any = await getTaskDetail(form.value.id!)
          if (detailRes.code === 200 && detailRes.data) {
            currentTask.value = { ...currentTask.value, imageRecords: detailRes.data.imageRecords }
            // 提交表单后，清空已上传的临时队列
            pendingUploadFiles.value = []
            startPolling()
          } else if (detailRes && detailRes.imageRecords) {
            currentTask.value = { ...currentTask.value, imageRecords: detailRes.imageRecords }
            // 提交表单后，清空已上传的临时队列
            pendingUploadFiles.value = []
            startPolling()
          }
        }

        const payload = {
          id: form.value.id,
          staffName: form.value.staffName,
          manualScore: form.value.manualScore,
          status: form.value.status
        }
        const res: any = await updateTask(payload)
        if (res === true || res?.code === 200 || res?.data === true || !res?.code) {
          ElMessage.success('修改成功')
          closeEditDialog()
          loadData()
        } else {
          ElMessage.error(res?.message || '修改失败')
        }
      } catch (error) {
        console.error('Submit failed:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDelete = async (row: any) => {
  try {
    const res: any = await deleteTask(row.id)
    if (res === true || res === 1 || res === 0 || res?.code === 200 || res?.data === true || res?.data === 1 || res?.data === 0) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res?.message || '删除失败')
    }
  } catch (error) {
    console.error('Delete failed:', error)
  }
}

// 查看详情弹窗相关
const detailDialogVisible = ref(false)
const currentTask = ref<any>({})
const uploadLoading = ref(false)

const openDetailDialog = (row: any) => {
  currentTask.value = { ...row }
  detailDialogVisible.value = true
}

// 模拟上传图片
const handleImageUpload = (file: any) => {
  if (!file || !file.raw) return
  
  // 生成本地预览URL
  const previewUrl = URL.createObjectURL(file.raw)
  
  // 构建一个伪造的图片记录用于前端预览展示
  const pendingImg = {
    id: 'pending_' + Date.now(),
    fileName: file.name,
    imageUrl: '', // 正式URL为空
    previewUrl: previewUrl, // 本地预览URL
    aiStatus: 'PENDING_UPLOAD', // 自定义一个状态表示等待上传
    isPending: true, // 标记为待上传
    raw: file.raw // 保存原始文件用于后续提交
  }
  
  // 将文件记录加入待上传列表
  pendingUploadFiles.value.push(pendingImg)
  
  // 同时加入到 currentTask 的显示列表中，让它能够立即在界面上渲染出来
  if (!currentTask.value.imageRecords) {
    currentTask.value.imageRecords = []
  }
  currentTask.value.imageRecords.push(pendingImg)
}

// 删除图片
const handleDeleteImage = async (imageId: number | string, index: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该图片吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 如果是还没有上传的本地预览图片
    if (typeof imageId === 'string' && imageId.startsWith('pending_')) {
      // 从待上传列表中移除
      const pendingIndex = pendingUploadFiles.value.findIndex(f => f.id === imageId)
      if (pendingIndex > -1) {
        URL.revokeObjectURL(pendingUploadFiles.value[pendingIndex].previewUrl)
        pendingUploadFiles.value.splice(pendingIndex, 1)
      }
      // 从显示列表中移除
      currentTask.value.imageRecords.splice(index, 1)
      ElMessage.success('移除成功')
      return
    }

    // 正式的已上传图片，调用接口删除
    const res: any = await deleteTaskImage(imageId as number)
    // MyBatis-Plus 的 deleteById 在数据不存在时可能返回 false(对应后端 data=0)，
    // 或者 axios 拦截器直接返回了整个 res。我们需要放宽成功条件的判断：
    // 只要 res 不为 null 且 res.code 不为错误码（如果是原始响应），或者直接被拦截器返回了 0/1 这种数字，都算作成功。
    if (res === true || res === 1 || res === 0 || res?.code === 200 || res?.data === true || res?.data === 1 || res?.data === 0) {
      ElMessage.success('删除成功')
      
      // 直接在前端数组中移除，避免依赖后端接口的延迟
      if (currentTask.value.imageRecords) {
        currentTask.value.imageRecords.splice(index, 1)
      }
      
      // 重新拉取当前任务的数据更新弹窗，确保数据一致性
      const detailRes: any = await getTaskDetail(currentTask.value.id)
      if (detailRes.code === 200 && detailRes.data) {
        const newRecords = detailRes.data.imageRecords || []
        const localPendingRecords = currentTask.value.imageRecords?.filter((img: any) => img.isPending) || []
        currentTask.value = { ...currentTask.value, imageRecords: [...newRecords, ...localPendingRecords] }
      } else if (detailRes && detailRes.imageRecords) {
        // 兼容 axios 拦截器直接返回 data 的情况
        const newRecords = detailRes.imageRecords || []
        const localPendingRecords = currentTask.value.imageRecords?.filter((img: any) => img.isPending) || []
        currentTask.value = { ...currentTask.value, imageRecords: [...newRecords, ...localPendingRecords] }
      }
      // 刷新列表更新分数
      loadData()
    } else {
      ElMessage.error(res?.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Delete image failed:', error)
    }
  }
}

// 解析 AI 检测结果
const parseDetectResult = (resultStr: string) => {
  if (!resultStr) return []
  try {
    return JSON.parse(resultStr)
  } catch (e) {
    console.error('Failed to parse detect result', e)
    return []
  }
}

// 预定义一个颜色调色板（模仿 Ultralytics 的多类别颜色）
const colorPalette = [
  '#FF3838', '#FF9D97', '#FF701F', '#FFB21D', '#CFD231', 
  '#48F90A', '#92CC17', '#3DDB86', '#1A9334', '#00D4BB',
  '#2C99A8', '#00C2FF', '#344593', '#6473FF', '#0018EC',
  '#8438FF', '#520085', '#CB38FF', '#FF95C8', '#FF37C7'
]

// 根据标签字符串生成稳定的颜色索引
const getColorByLabel = (label: string) => {
  let hash = 0;
  for (let i = 0; i < label.length; i++) {
    hash = label.charCodeAt(i) + ((hash << 5) - hash);
  }
  const index = Math.abs(hash) % colorPalette.length;
  return colorPalette[index];
}

// 获取 AI 标注框的样式
const getBoxStyle = (boxObj: any) => {
  if (!boxObj || !boxObj.box || boxObj.box.length !== 4) return {}
  // 后端返回的已经是 0.0 ~ 1.0 的相对坐标
  const [xmin, ymin, xmax, ymax] = boxObj.box
  
  const left = xmin * 100
  const top = ymin * 100
  const width = (xmax - xmin) * 100
  const height = (ymax - ymin) * 100

  const color = getColorByLabel(boxObj.label || 'unknown');

  return {
    left: `${left}%`,
    top: `${top}%`,
    width: `${width}%`,
    height: `${height}%`,
    borderColor: color,
    backgroundColor: `${color}33`, // 添加透明度作为背景色
    position: 'absolute',
    borderWidth: '2px',
    borderStyle: 'solid'
  }
}

const getLabelStyle = (label: string) => {
  const color = getColorByLabel(label || 'unknown');
  return {
    backgroundColor: color
  }
}

// 轮询机制
let pollingTimer: any = null

const startPolling = () => {
  if (pollingTimer) return
  
  // 检查是否有状态为 UNCHECKED 的图片，并且不能是本地的伪造记录 (通过是否有 isPending 判断)
  const hasUnchecked = currentTask.value.imageRecords?.some((img: any) => img.aiStatus === 'UNCHECKED' && !img.isPending)
  if (!hasUnchecked) return

  pollingTimer = setInterval(async () => {
    if (!dialogVisible.value) {
      stopPolling()
      return
    }
    
    try {
      const res: any = await getTaskDetail(currentTask.value.id)
      if (res.code === 200 && res.data) {
        // 更新记录时，保留那些还没上传的本地预览图片（如果有的话）
        const newRecords = res.data.imageRecords || []
        const localPendingRecords = currentTask.value.imageRecords?.filter((img: any) => img.isPending) || []
        currentTask.value = { ...currentTask.value, imageRecords: [...newRecords, ...localPendingRecords] }
        
        // 如果没有未检测的图片了，停止轮询并刷新外层列表更新总分
        const stillHasUnchecked = newRecords.some((img: any) => img.aiStatus === 'UNCHECKED')
        if (!stillHasUnchecked) {
          stopPolling()
          loadData()
          ElMessage.success('AI 检测完成')
        }
      } else if (res && res.imageRecords) {
        // 兼容 axios 拦截器
        const newRecords = res.imageRecords || []
        const localPendingRecords = currentTask.value.imageRecords?.filter((img: any) => img.isPending) || []
        currentTask.value = { ...currentTask.value, imageRecords: [...newRecords, ...localPendingRecords] }
        
        const stillHasUnchecked = newRecords.some((img: any) => img.aiStatus === 'UNCHECKED')
        if (!stillHasUnchecked) {
          stopPolling()
          loadData()
          ElMessage.success('AI 检测完成')
        }
      }
    } catch (e) {
      console.error('Polling error:', e)
    }
  }, 2000) // 每2秒轮询一次
}

const stopPolling = () => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }
}

onMounted(() => {
  loadData()
})

onUnmounted(() => {
  stopPolling()
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

.image-gallery {
  margin-top: 24px;
  border-top: 1px solid #ebeef5;
  padding-top: 16px;

  .gallery-title {
    margin: 0 0 16px 0;
    color: #303133;
    font-size: 16px;
  }

  .image-list {
    display: grid;
    grid-template-columns: 1fr; /* 改为单列，一行一张图片 */
    gap: 20px;

    .image-item {
      border: 1px solid #ebeef5;
      border-radius: 8px;
      overflow: hidden;
      background: #fafafa;

      .image-header {
        padding: 8px 12px;
        background: #fff;
        display: flex;
        align-items: center;
        border-bottom: 1px solid #ebeef5;

        .req-name {
          font-weight: 600;
          color: #606266;
        }
      }

      .image-wrapper {
        position: relative;
        width: 100%;
        /* 直接让 wrapper 根据内容撑开，不设固定背景色和 padding */
        display: inline-block;

        .record-img {
          display: block;
          width: 100%;
          height: auto;
        }

        .ai-box {
          position: absolute;
          border: 2px solid transparent; /* 由 inline style 覆盖 */
          background-color: transparent; /* 由 inline style 覆盖 */
          pointer-events: none;
          z-index: 10;

          .ai-label {
            position: absolute;
            top: -22px;
            left: -2px;
            color: #fff;
            padding: 2px 6px;
            font-size: 12px;
            border-radius: 4px;
            white-space: nowrap;
          }
        }
      }

      &.upload-item {
        background: transparent;
        border: 1px dashed #dcdfe6;
        display: flex;
        justify-content: center;
        align-items: center;
        cursor: pointer;
        min-height: 200px;
        transition: all 0.3s;
        
        &:hover {
          border-color: #409eff;
        }
        
        .upload-area {
          width: 100%;
          height: 100%;
          display: flex;
          justify-content: center;
          align-items: center;
          
          :deep(.el-upload) {
            width: 100%;
            height: 100%;
          }
        }

        .upload-trigger {
          width: 100%;
          height: 100%;
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
          color: #8c939d;
          
          &:hover {
            color: #409eff;
          }
          
          .plus-icon {
            font-size: 28px;
            margin-bottom: 8px;
          }
          
          .upload-text {
            font-size: 14px;
          }
        }
      }
    }
  }
}
</style>
