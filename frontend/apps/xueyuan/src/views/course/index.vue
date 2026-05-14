<template>
  <div class="course-container">
    <div class="header-action">
      <div class="search-area">
        <el-input v-model="searchQuery" placeholder="搜索课程名称" clearable class="search-input" :prefix-icon="Search" />
        <el-select v-model="category" placeholder="选择分类" clearable class="search-select">
          <el-option label="保洁服务" value="保洁服务" />
          <el-option label="保姆服务" value="保姆服务" />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <el-button type="primary" :icon="Plus" @click="handleAdd" class="add-btn">发布课程</el-button>
    </div>

    <div class="course-list" v-loading="loading">
      <el-row :gutter="24">
        <el-col :span="8" v-for="item in tableData" :key="item.courseId" style="margin-bottom: 24px;">
          <el-card shadow="hover" class="course-card" :body-style="{ padding: '0px' }">
            <!-- 状态标签 -->
            <div class="status-tag published">
              已发布
            </div>
            
            <!-- 封面图 -->
            <div class="cover-wrapper">
              <img v-if="item.courseCover" :src="item.courseCover" class="cover-img" alt="封面" />
              <div v-else class="cover-placeholder">
                <el-icon size="40" color="#c0c4cc"><Document /></el-icon>
              </div>
            </div>

            <!-- 课程信息 -->
            <div class="course-info">
              <h3 class="course-title">{{ item.courseName }}</h3>
              <p class="course-desc">{{ item.courseContent || '暂无课程简介' }}</p>
              
              <div class="course-meta">
                <div class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  <span>{{ item.createTime }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><Location /></el-icon>
                  <span>{{ item.courseType }}</span>
                </div>
                <div class="meta-item highlight">
                  <el-icon><User /></el-icon>
                  <span>已报名: <strong>{{ item.enrolled || 0 }}</strong> 人</span>
                </div>
              </div>
            </div>

            <!-- 操作按钮区 -->
            <div class="course-actions">
              <el-button class="action-btn main-action" plain @click="handleEnrollment(item)">
                <el-icon><UserFilled /></el-icon>
                <span>报名情况</span>
              </el-button>
              <div class="icon-actions">
                <el-button circle size="small" @click="handleEdit(item)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-popconfirm title="确定删除该课程吗?" @confirm="handleDelete(item)">
                  <template #reference>
                    <el-button circle size="small" type="danger" plain>
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

    <!-- 课程编辑/新增弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '发布课程' : '编辑课程'"
      width="600px"
      :close-on-click-modal="false"
      class="custom-dialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="course-form">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程分类" prop="courseType">
          <el-select v-model="form.courseType" placeholder="请选择课程分类" style="width: 100%;">
            <el-option label="保洁服务" value="保洁服务" />
            <el-option label="保姆服务" value="保姆服务" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度等级" prop="difficulty">
          <el-radio-group v-model="form.difficulty">
            <el-radio label="初级">初级</el-radio>
            <el-radio label="中级">中级</el-radio>
            <el-radio label="高级">高级</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="课程时长" prop="courseDuration">
          <el-input-number v-model="form.courseDuration" :min="1" :max="1000" placeholder="分钟" />
          <span style="margin-left: 10px;">分钟</span>
        </el-form-item>
        <el-form-item label="课程内容" prop="courseContent">
          <el-input v-model="form.courseContent" type="textarea" :rows="3" placeholder="请输入课程内容" />
        </el-form-item>
        <el-form-item label="课程封面" prop="courseCover">
          <el-upload
            class="avatar-uploader"
            action="/api/file/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :headers="uploadHeaders"
          >
            <template v-if="form.courseCover">
              <img :src="form.courseCover" class="avatar" />
              <div class="avatar-uploader-mask" @click.stop>
                <div class="mask-action">
                  <el-icon><Upload /></el-icon>
                  <span>更换封面</span>
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

    <el-dialog v-model="previewVisible" title="图片预览" width="50%" center>
      <img :src="previewUrl" style="width: 100%; display: block;" alt="Preview" />
    </el-dialog>

    <!-- 报名情况弹窗 -->
    <el-dialog
      v-model="enrollmentVisible"
      title="报名情况"
      width="700px"
      :close-on-click-modal="false"
      class="custom-dialog"
    >
      <el-table :data="enrollmentList" stripe border max-height="400px" v-loading="enrollmentLoading">
        <el-table-column prop="id" label="学员ID" width="80" />
        <el-table-column label="头像" width="70">
          <template #default="{ row }">
            <el-avatar :size="30" :src="row.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
          </template>
        </el-table-column>
        <el-table-column prop="realName" label="学员姓名" width="120" />
        <el-table-column prop="phone" label="手机号码" width="140" />
        <el-table-column prop="progress" label="学习进度">
          <template #default="{ row }">
            <el-progress :percentage="row.progress" :color="row.progress === 100 ? '#67c23a' : '#409eff'" />
          </template>
        </el-table-column>
        <el-table-column prop="joinTime" label="报名时间" width="160" />
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="enrollmentVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { Search, Plus, Document, Calendar, Location, User, UserFilled, Edit, Delete, View, Refresh, Upload } from '@element-plus/icons-vue'
import type { FormInstance, FormRules, UploadProps, UploadInstance } from 'element-plus'
import { ElMessage } from 'element-plus'
import { getCoursePage, addCourse, updateCourse, deleteCourse, getCourseEnrollment } from '@/api/course'

const uploadRef = ref<UploadInstance>()

// 报名情况弹窗逻辑
const enrollmentVisible = ref(false)
const enrollmentLoading = ref(false)
const enrollmentList = ref<any[]>([])

const handleEnrollment = async (row: any) => {
  enrollmentVisible.value = true
  enrollmentLoading.value = true
  try {
    const res: any = await getCourseEnrollment(row.courseId)
    enrollmentList.value = res.data || res || []
  } catch (error) {
    console.error('获取报名情况失败:', error)
    ElMessage.error('获取报名情况失败')
  } finally {
    enrollmentLoading.value = false
  }
}

const searchQuery = ref('')
const category = ref('')
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(9)
const total = ref(0)

const tableData = ref<any[]>([])

const loadData = async () => {
  loading.value = true
  try {
    const params: any = {
      current: currentPage.value,
      size: pageSize.value
    }
    if (searchQuery.value) {
      params.title = searchQuery.value
    }
    if (category.value) {
      params.categoryId = category.value
    }

    const res: any = await getCoursePage(params)
    
    // 对接后端数据
    tableData.value = res.records.map((item: any) => ({
      ...item,
      // 后端只返回基础字段，这里通过 mock 填充部分展示字段（实际业务需关联其他表）
      type: 1, 
      createTime: item.createTime.replace('T', ' ')
    }))
    total.value = res.total
  } catch (error) {
    console.error('获取课程列表失败:', error)
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
  category.value = ''
  handleSearch()
}

// 表单相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  courseId: undefined as string | undefined,
  courseName: '',
  courseType: '',
  difficulty: '初级',
  courseDuration: 60,
  courseContent: '',
  courseCover: ''
})

const rules = reactive<FormRules>({
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  courseType: [{ required: true, message: '请选择课程分类', trigger: 'change' }],
  courseCover: [{ required: true, message: '请上传课程封面', trigger: 'change' }]
})

const uploadHeaders = {
  Authorization: localStorage.getItem('token') || ''
}

const handleAvatarSuccess: UploadProps['onSuccess'] = (response) => {
  if (response.code === 200) {
    form.courseCover = response.data
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

const triggerUpload = () => {
  // empty
}

const handleFileChange = (e: Event) => {
  const target = e.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    const file = target.files[0]
    // 清空 input 值，允许重复选择同一个文件
    target.value = ''
    
    if (beforeAvatarUpload(file)) {
      // 构建 FormData
      const formData = new FormData()
      formData.append('file', file)
      
      // 手动触发上传
      fetch('/api/file/upload', {
        method: 'POST',
        headers: {
          'Authorization': uploadHeaders.Authorization
        },
        body: formData
      })
      .then(res => res.json())
      .then(res => {
        handleAvatarSuccess(res, file as any, [])
      })
      .catch(err => {
        ElMessage.error('上传失败')
      })
    }
  }
}

const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  if (formRef.value) formRef.value.resetFields()
  Object.assign(form, {
    courseId: undefined,
    courseName: '',
    courseType: '',
    difficulty: '初级',
    courseDuration: 60,
    courseContent: '',
    courseCover: ''
  })
}

const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  if (formRef.value) formRef.value.resetFields()
  Object.assign(form, {
    courseId: row.courseId,
    courseName: row.courseName,
    courseType: row.courseType,
    difficulty: row.difficulty,
    courseDuration: row.courseDuration,
    courseContent: row.courseContent,
    courseCover: row.courseCover || ''
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (dialogType.value === 'add') {
          await addCourse(form)
          ElMessage.success('发布成功')
        } else {
          await updateCourse(form)
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
    await deleteCourse(row.courseId)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error('删除失败:', error)
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
.course-container {
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

  .course-list {
    .course-card {
      border-radius: 16px;
      overflow: hidden;
      position: relative;
      transition: all 0.3s;
      background: #ffffff;
      border: 1px solid #f0f2f5 !important;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04) !important;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08) !important;
      }

      .status-tag {
        position: absolute;
        top: 16px;
        right: 16px;
        padding: 4px 12px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 600;
        z-index: 10;
        
        &.published {
          background-color: #e1f3d8;
          color: #67c23a;
        }
        
        &.draft {
          background-color: #fdf6ec;
          color: #e6a23c;
        }
      }

      .cover-wrapper {
        height: 180px;
        width: 100%;
        background-color: #f5f7fa;
        display: flex;
        align-items: center;
        justify-content: center;
        overflow: hidden;

        .cover-img {
          width: 100%;
          height: 100%;
          transition: transform 0.3s;
        }

        .cover-placeholder {
          color: #dcdfe6;
        }
      }

      &:hover .cover-img {
        transform: scale(1.05);
      }

      .course-info {
        padding: 20px;

        .course-title {
          margin: 0 0 8px;
          font-size: 18px;
          font-weight: 600;
          color: #303133;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        .course-desc {
          margin: 0 0 16px;
          font-size: 13px;
          color: #909399;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        .course-meta {
          .meta-item {
            display: flex;
            align-items: center;
            font-size: 13px;
            color: #606266;
            margin-bottom: 8px;

            .el-icon {
              margin-right: 8px;
              color: #909399;
              font-size: 15px;
            }

            &.highlight {
              color: #409eff;
              .el-icon { color: #409eff; }
              strong { font-size: 14px; margin: 0 2px; }
            }
          }
        }
      }

      .course-actions {
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

.avatar-uploader {
  :deep(.el-upload) {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
    width: 178px;
    height: 178px;
    display: flex;
    justify-content: center;
    align-items: center;

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
    display: flex;
    justify-content: center;
    align-items: center;
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
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    flex-direction: column;
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
        font-size: 13px;
      }
      
      &:hover {
        color: var(--el-color-primary);
      }
    }
  }
}
</style>