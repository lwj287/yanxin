<template>
  <div class="exam-container">
    <div class="header-action">
      <div class="search-area">
        <el-input v-model="searchQuery" placeholder="搜索考试名称" clearable class="search-input" :prefix-icon="Search" />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <el-button type="primary" :icon="Plus" @click="handleAdd" class="add-btn">发布考试</el-button>
    </div>

    <div class="exam-list" v-loading="loading">
      <el-row :gutter="24">
        <el-col :span="8" v-for="item in tableData" :key="item.examId" style="margin-bottom: 24px;">
          <el-card shadow="hover" class="exam-card" :body-style="{ padding: '0px' }">
            <!-- 顶部装饰与状态 -->
            <div class="card-header-deco">
              <div class="course-link">
                <el-icon><Reading /></el-icon>
                <span>关联课程：{{ getCourseName(item.courseId) }}</span>
              </div>
            </div>

            <!-- 考试信息 -->
            <div class="exam-info">
              <h3 class="exam-title">
                {{ item.examName }}
              </h3>
              
              <div class="exam-meta">
                <div class="meta-row">
                  <div class="meta-item">
                    <el-icon><Timer /></el-icon>
                    <span>时长: <strong>{{ item.examDuration }}</strong> 分钟</span>
                  </div>
                  <div class="meta-item">
                    <el-icon><Trophy /></el-icon>
                    <span>及格: <strong>{{ item.passScore }}</strong> 分</span>
                  </div>
                </div>
                <div class="meta-item time-range">
                  <el-icon><Calendar /></el-icon>
                  <span>{{ item.examTime }}</span>
                </div>
              </div>
            </div>

            <!-- 操作按钮区 -->
            <div class="exam-actions">
              <el-button class="action-btn main-action" plain @click="handleScore(item)">
                <el-icon><DataAnalysis /></el-icon>
                <span>考试成绩</span>
              </el-button>
              <div class="icon-actions">
                <el-button circle size="small" @click="handleEdit(item)" title="编辑考试">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button circle size="small" @click="handleQuestions(item)" title="题库管理">
                  <el-icon><Document /></el-icon>
                </el-button>
                <el-popconfirm title="确定删除该考试吗?" @confirm="handleDelete(item)">
                  <template #reference>
                    <el-button circle size="small" type="danger" plain title="删除考试">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </template>
                </el-popconfirm>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[9, 18, 54, 108]"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 考试编辑/新增弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '发布考试' : '编辑考试'"
      width="600px"
      :close-on-click-modal="false"
      class="custom-dialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="exam-form">
        <el-form-item label="考试名称" prop="examName">
          <el-input v-model="form.examName" placeholder="请输入考试名称" />
        </el-form-item>
        <el-form-item label="关联课程" prop="courseId">
          <el-select v-model="form.courseId" placeholder="请选择关联课程" style="width: 100%;">
            <el-option v-for="course in courseList" :key="course.courseId" :label="course.courseName" :value="course.courseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="及格分" prop="passScore">
          <el-input-number v-model="form.passScore" :min="1" :max="1000" />
        </el-form-item>
        <el-form-item label="考试时长" prop="examDuration">
          <el-input-number v-model="form.examDuration" :min="1" :max="300" placeholder="分钟" />
          <span style="margin-left: 10px;">分钟</span>
        </el-form-item>
        <el-form-item label="考试时间" prop="examTime">
          <el-date-picker v-model="form.examTime" type="datetime" placeholder="选择考试时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DDTHH:mm:ss" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 考试成绩明细弹窗 -->
    <el-dialog
      v-model="scoreVisible"
      title="考试成绩明细"
      width="750px"
      :close-on-click-modal="false"
      class="custom-dialog"
    >
      <el-table :data="scoreList" stripe border max-height="400px" v-loading="scoreLoading">
        <el-table-column prop="id" label="学员ID" width="80" />
        <el-table-column label="头像" width="70">
          <template #default="{ row }">
            <el-avatar :size="30" :src="row.avatar" />
          </template>
        </el-table-column>
        <el-table-column prop="realName" label="学员姓名" width="120" />
        <el-table-column prop="phone" label="手机号码" width="140" />
        <el-table-column prop="score" label="考试得分" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.isPassed ? '#67c23a' : '#f56c6c', fontWeight: 'bold' }">{{ row.score }} 分</span>
          </template>
        </el-table-column>
        <el-table-column prop="isPassed" label="是否及格" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isPassed ? 'success' : 'danger'" size="small">
              {{ row.isPassed ? '及格' : '不及格' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="交卷时间" width="160" />
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="scoreVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { Search, Plus, Calendar, Timer, Trophy, Reading, DataAnalysis, Edit, Document, Delete } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { getExamPage, addExam, updateExam, deleteExam, getExamRecords } from '@/api/exam'
import { getCoursePage } from '@/api/course'

const searchQuery = ref('')
const status = ref('')
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(9)
const total = ref(0)

const tableData = ref<any[]>([])
const courseList = ref<any[]>([])

const getLevelType = (code: number) => {
  const map: Record<number, string> = { 1: 'info', 2: 'warning', 3: 'danger' }
  return map[code] || 'info'
}

const getStatusText = (status: string) => {
  return status || '未知'
}

const getStatusClass = (status: string) => {
  return 'processing'
}

const getCourseName = (courseId: string) => {
  if (!courseId) return '未知课程'
  const course = courseList.value.find(c => c.courseId === courseId)
  return course ? course.courseName : courseId
}

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await getExamPage({
      current: currentPage.value,
      size: pageSize.value,
      title: searchQuery.value,
      status: undefined
    })
    
    tableData.value = res.records.map((item: any) => ({
      ...item,
      examTime: item.examTime ? item.examTime.replace('T', ' ') : '-'
    }))
    total.value = res.total
  } catch (error) {
    console.error('获取考试列表失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  loadData()
  try {
    const res: any = await getCoursePage({ current: 1, size: 1000 })
    courseList.value = res.records || []
  } catch (e) {
    console.error('获取课程列表失败', e)
  }
})

const handleSearch = () => {
  currentPage.value = 1
  loadData()
}

const handleReset = () => {
  searchQuery.value = ''
  status.value = ''
  handleSearch()
}

// 表单相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  examId: undefined as string | undefined,
  examName: '',
  courseId: '',
  passScore: 60,
  examDuration: 60,
  examTime: ''
})

const rules = reactive<FormRules>({
  examName: [{ required: true, message: '请输入考试名称', trigger: 'blur' }],
  courseId: [{ required: true, message: '请选择关联课程', trigger: 'change' }],
  passScore: [{ required: true, message: '请输入及格分', trigger: 'blur' }],
  examDuration: [{ required: true, message: '请输入考试时长', trigger: 'blur' }]
})

const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  if (formRef.value) formRef.value.resetFields()
  Object.assign(form, {
    examId: undefined,
    examName: '',
    courseId: '',
    passScore: 60,
    examDuration: 60,
    examTime: ''
  })
}

const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  if (formRef.value) formRef.value.resetFields()
  Object.assign(form, {
    examId: row.examId,
    examName: row.examName,
    courseId: row.courseId,
    passScore: row.passScore,
    examDuration: row.examDuration,
    examTime: row.examTime ? row.examTime.replace(' ', 'T') : ''
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (dialogType.value === 'add') {
          await addExam(form)
          ElMessage.success('发布成功')
        } else {
          await updateExam(form)
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
    await deleteExam(row.examId)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

const handleQuestions = (row: any) => ElMessage.info(`管理题库: ${row.examName}`)

// 考试成绩明细弹窗逻辑
const scoreVisible = ref(false)
const scoreLoading = ref(false)
const scoreList = ref<any[]>([])

const handleScore = async (row: any) => {
  scoreVisible.value = true
  scoreLoading.value = true
  try {
    const res: any = await getExamRecords(row.examId)
    scoreList.value = res.data || res || []
  } catch (error) {
    console.error('获取成绩明细失败:', error)
    ElMessage.error('获取成绩明细失败')
  } finally {
    scoreLoading.value = false
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
.exam-container {
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

  .exam-list {
    .exam-card {
      border-radius: 16px;
      overflow: hidden;
      transition: all 0.3s;
      background: #ffffff;
      border: 1px solid #f0f2f5 !important;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04) !important;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08) !important;
      }

      .card-header-deco {
        height: 48px;
        background: linear-gradient(90deg, #f5f7fa 0%, #ffffff 100%);
        border-bottom: 1px solid #ebeef5;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 16px;

        .course-link {
          display: flex;
          align-items: center;
          font-size: 13px;
          color: #606266;
          
          .el-icon {
            margin-right: 6px;
            color: #409eff;
            font-size: 16px;
          }
          
          span {
            max-width: 180px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }

        .status-tag {
          padding: 4px 10px;
          border-radius: 12px;
          font-size: 12px;
          font-weight: 600;
          
          &.pending {
            background-color: #f4f4f5;
            color: #909399;
          }
          
          &.processing {
            background-color: #ecf5ff;
            color: #409eff;
          }

          &.finished {
            background-color: #fdf6ec;
            color: #e6a23c;
          }
        }
      }

      .exam-info {
        padding: 20px;

        .exam-title {
          margin: 0 0 12px;
          font-size: 18px;
          font-weight: 600;
          color: #303133;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          display: flex;
          align-items: center;

          .level-tag {
            margin-left: 8px;
            font-weight: 600;
          }
        }

        .exam-desc {
          margin: 0 0 20px;
          font-size: 13px;
          color: #909399;
          height: 36px;
          line-height: 18px;
          overflow: hidden;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }

        .exam-meta {
          background-color: #f8f9fb;
          padding: 12px;
          border-radius: 12px;

          .meta-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 8px;
          }

          .meta-item {
            display: flex;
            align-items: center;
            font-size: 13px;
            color: #606266;

            .el-icon {
              margin-right: 6px;
              color: #909399;
              font-size: 14px;
            }

            strong {
              color: #303133;
              font-size: 14px;
              margin: 0 2px;
            }

            &.time-range {
              color: #909399;
              font-size: 12px;
            }
          }
        }
      }

      .exam-actions {
        padding: 16px 20px;
        border-top: 1px solid #f5f7fa;
        display: flex;
        justify-content: space-between;
        align-items: center;
        background-color: #fafbfc;

        .main-action {
          flex: 1;
          margin-right: 16px;
          border-radius: 16px;
          background-color: #f0f5ff;
          border-color: transparent;
          color: #409eff;
          
          &:hover {
            background-color: #409eff;
            color: #ffffff;
          }
        }

        .icon-actions {
          display: flex;
          gap: 8px;
          
          .el-button.is-circle {
            padding: 8px;
          }
        }
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