<template>
  <div class="page-wrap">
    <div class="header-action" style="margin-bottom: 12px;">
      <div class="page-header" style="margin-bottom: 0;">
        <div>
          <div class="page-title">面试管理</div>
          <div class="page-subtitle">先从合格简历中安排面试，再跟进通知与结果</div>
        </div>
      </div>
    </div>

    <el-row :gutter="12" class="split-panels">
        <el-col :xl="12" :lg="12" :md="24" :sm="24" :xs="24">
          <div class="panel-box">
            <div class="page-subtitle panel-title">待安排面试简历</div>
            <div class="toolbar query-left panel-tools" style="margin-bottom:8px">
              <el-input v-model="q.keyword" placeholder="姓名/手机号/岗位名称" clearable style="width: 180px" />
              <el-button class="query-btn" type="primary" @click="loadResumeList">筛选</el-button>
              <el-button class="query-btn" @click="resetQuery">重置</el-button>
            </div>

            <el-table :data="pagedResumeList" stripe>
              <el-table-column prop="resumeId" label="简历编号" width="90" />
              <el-table-column prop="realName" label="姓名" min-width="100" />
              <el-table-column prop="phone" label="手机号" min-width="120" />
              <el-table-column prop="jobTitle" label="岗位名称" min-width="140" show-overflow-tooltip />
              <el-table-column label="操作" width="110">
                <template #default="{row}">
                  <el-button size="small" type="primary" @click="openArrange(row)">安排</el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-pagination
              v-model:current-page="resumePager.pageNo"
              v-model:page-size="resumePager.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="resumePager.total"
              @size-change="onResumePageSizeChange"
              @current-change="onResumePageChange"
            />
          </div>
        </el-col>

        <el-col :xl="12" :lg="12" :md="24" :sm="24" :xs="24">
          <div class="panel-box">
            <div class="page-subtitle panel-title">已安排面试记录</div>
            <div class="toolbar query-left panel-tools" style="margin-bottom:8px">
              <el-input v-model="q2.keyword" placeholder="简历编号/面试地点" clearable style="width: 180px" />
              <el-select v-model="q2.status" clearable placeholder="状态" style="width: 120px">
                <el-option label="待面试" value="PENDING" />
                <el-option label="面试中" value="IN_PROGRESS" />
                <el-option label="合格" value="PASS" />
                <el-option label="不合格" value="REJECT" />
              </el-select>
              <el-button class="query-btn" type="primary" @click="applyInterviewFilter">筛选</el-button>
              <el-button class="query-btn" @click="resetInterviewFilter">重置</el-button>
            </div>

            <el-table :data="pagedInterviewList" stripe>
              <el-table-column prop="id" label="编号" width="70" />
              <el-table-column prop="resumeId" label="简历编号" width="90" />
              <el-table-column prop="interviewTime" label="面试时间" min-width="150" />
              <el-table-column prop="interviewLocation" label="面试地点" min-width="120" />
              <el-table-column label="状态" min-width="95">
                <template #default="{row}">
                  <span :class="['status-tag', interviewStatusClass(row.interviewStatus)]">
                    {{ interviewStatusMap[row.interviewStatus] || row.interviewStatus }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="170">
                <template #default="{row}">
                  <div class="op-btns">
                    <el-button size="small" type="primary" @click="finish(row,1)">合格</el-button>
                    <el-button size="small" type="danger" @click="finish(row,0)">不合格</el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
            <el-pagination
              v-model:current-page="interviewPager.pageNo"
              v-model:page-size="interviewPager.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="filteredInterviewList.length"
              @size-change="onInterviewPageSizeChange"
              @current-change="onInterviewPageChange"
            />
          </div>
        </el-col>
      </el-row>

      <el-dialog v-model="showArrange" title="安排面试" width="560px">
        <el-form :model="arrange" label-width="90px">
          <el-form-item label="简历信息">
            <el-input :model-value="arrange.resumeLabel" readonly />
          </el-form-item>
          <el-form-item label="面试时间">
            <el-date-picker v-model="arrange.interviewTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" />
          </el-form-item>
          <el-form-item label="面试地点">
            <el-input v-model="arrange.interviewLocation" placeholder="请输入面试地点" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showArrange=false">取消</el-button>
          <el-button type="primary" @click="create">确认安排</el-button>
        </template>
      </el-dialog>
  </div>
</template>

<script setup>
import { computed, reactive, ref, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'
import { interviewStatusMap } from '@/utils/display'

const q = reactive({ keyword: '' })
const q2 = reactive({ keyword: '', status: '' })

const resumeList = ref([])
const resumePager = reactive({ pageNo: 1, pageSize: 10, total: 0 })
const pagedResumeList = computed(() => {
  const start = (resumePager.pageNo - 1) * resumePager.pageSize
  return resumeList.value.slice(start, start + resumePager.pageSize)
})

const interviewList = ref([])
const filteredInterviewList = ref([])
const interviewPager = reactive({ pageNo: 1, pageSize: 10, total: 0 })
const pagedInterviewList = computed(() => {
  const start = (interviewPager.pageNo - 1) * interviewPager.pageSize
  return filteredInterviewList.value.slice(start, start + interviewPager.pageSize)
})

const showArrange = ref(false)
const arrange = reactive({ resumeId: null, resumeLabel: '', interviewTime: '', interviewLocation: '', interviewType: 1, interviewerId: 1 })

const loadResumeList = async () => {
  const params = { keyword: q.keyword || undefined }
  resumeList.value = (await request.get('/api/admin/resume/interview-options', { params })).data || []
  resumePager.total = resumeList.value.length
  resumePager.pageNo = 1
}

const loadInterviewList = async () => {
  interviewList.value = (await request.get('/api/admin/interview/page', { params: { pageNo: 1, pageSize: 1000 } })).data || []
  filteredInterviewList.value = [...interviewList.value]
  interviewPager.pageNo = 1
}

const applyInterviewFilter = () => {
  const kw = (q2.keyword || '').trim()
  filteredInterviewList.value = interviewList.value.filter(i => {
    const matchKeyword = !kw
      || String(i.resumeId || '').includes(kw)
      || String(i.interviewLocation || '').includes(kw)
    const matchStatus = !q2.status || i.interviewStatus === q2.status
    return matchKeyword && matchStatus
  })
  interviewPager.pageNo = 1
}

const resetInterviewFilter = () => {
  q2.keyword = ''
  q2.status = ''
  filteredInterviewList.value = [...interviewList.value]
  interviewPager.pageNo = 1
}

const resetQuery = () => {
  q.keyword = ''
  loadResumeList()
}

const openArrange = (row) => {
  arrange.resumeId = row.resumeId
  arrange.resumeLabel = row.label
  arrange.interviewTime = ''
  arrange.interviewLocation = ''
  showArrange.value = true
}

const create = async () => {
  if (!arrange.resumeId) return ElMessage.warning('请先选择简历')
  if (!arrange.interviewTime) return ElMessage.warning('请填写面试时间')
  await request.post('/api/admin/interview/schedule', {
    resumeId: arrange.resumeId,
    interviewTime: arrange.interviewTime,
    interviewType: 1,
    interviewLocation: arrange.interviewLocation,
    interviewerId: 1
  })
  ElMessage.success('安排成功')
  showArrange.value = false
  loadInterviewList()
}

const finish = async (row, pass) => {
  await request.post(`/api/admin/interview/${row.id}/finish`, { pass, professionalScore:80, communicationScore:80, attitudeScore:80, comments:'面试记录' })
  ElMessage.success('已完成')
  loadInterviewList()
  loadResumeList()
}

const interviewStatusClass = (status) => {
  if (status === 'PASS') return 'status-success'
  if (status === 'REJECT') return 'status-danger'
  if (status === 'IN_PROGRESS') return 'status-warn'
  return 'status-info'
}

const onResumePageSizeChange = (size) => { resumePager.pageSize = size; resumePager.pageNo = 1 }
const onResumePageChange = (pageNo) => { resumePager.pageNo = pageNo }
const onInterviewPageSizeChange = (size) => { interviewPager.pageSize = size; interviewPager.pageNo = 1 }
const onInterviewPageChange = (pageNo) => { interviewPager.pageNo = pageNo }

onMounted(() => {
  loadResumeList()
  loadInterviewList()
})
</script>
<style scoped>
.page-wrap {
  background: transparent;
}
.split-panels {
  margin-top: 4px;
}
.panel-box {
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 16px;
  background: #ffffff;
}
.panel-title {
  margin-bottom: 0;
  font-size: 14px;
  font-weight: 600;
  color: #1d2129;
}
.panel-tools {
  margin-bottom: 0;
  gap: 8px;
}
.op-btns {
  display: flex;
  justify-content: center;
  gap: 8px;
}
</style>
