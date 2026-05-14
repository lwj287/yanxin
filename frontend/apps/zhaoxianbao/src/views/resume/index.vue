<template>
  <div class="page-wrap">
    <div class="header-action">
      <div class="page-header">
        <div>
          <div class="page-title">简历管理</div>
          <div class="page-subtitle">智能筛选 + 人工判定</div>
        </div>
        <el-button type="primary" @click="openAdd">新增简历</el-button>
      </div>

      <div class="toolbar query-left">
        <el-input v-model="q.keyword" placeholder="姓名/手机号" clearable style="width: 240px" />
        <el-select v-model="q.screeningResult" clearable placeholder="筛选结果" style="width: 130px">
          <el-option label="待筛选" :value="0" />
          <el-option label="合格" :value="1" />
          <el-option label="不合格" :value="2" />
        </el-select>
        <el-button class="query-btn" type="primary" @click="load">查询简历</el-button>
        <el-button class="query-btn" @click="q.keyword=''; q.screeningResult=null; load()">重置条件</el-button>
      </div>
    </div>

    <div class="list-container">
      <el-table :data="page.records" stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column label="岗位类型" min-width="100">
          <template #default="{row}">{{ jobTypeText(row.jobId) }}</template>
        </el-table-column>
        <el-table-column label="岗位名称" min-width="160" show-overflow-tooltip>
          <template #default="{row}">{{ jobTitleText(row.jobId) }}</template>
        </el-table-column>
        <el-table-column prop="realName" label="姓名" min-width="120" />
        <el-table-column prop="phone" label="手机号" min-width="140" />
        <el-table-column prop="screeningScore" label="评分" min-width="90" />
        <el-table-column label="筛选结果" min-width="110">
          <template #default="{row}">
            <span :class="['status-tag', row.screeningResult===1 ? 'status-success' : row.screeningResult===2 ? 'status-danger' : 'status-info']">{{ row.screeningResult===1 ? '合格' : row.screeningResult===2 ? '不合格' : '待筛选' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="edit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="del(row)">删除</el-button>
            <el-button size="small" @click="smart(row)">智能筛选</el-button>
            <el-dropdown class="manual-screen-dropdown">
              <el-button size="small" type="primary">
                人工筛选
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="screen(row,1)">标记合格</el-dropdown-item>
                  <el-dropdown-item @click="screen(row,0)">标记不合格</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="q.pageNo"
        v-model:page-size="q.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="page.total || 0"
        @size-change="onPageSizeChange"
        @current-change="onPageChange"
      />
    </div>

    <el-dialog v-model="show" :title="form.id ? '编辑简历' : '新增简历'" width="720px">
        <el-form :model="form" label-width="90px">
          <el-row :gutter="12">
            <el-col :span="12"><el-form-item label="岗位ID"><el-input-number v-model="form.jobId" :min="1" style="width:100%" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="候选人ID"><el-input-number v-model="form.candidateId" :min="0" style="width:100%" /></el-form-item></el-col>
          </el-row>
          <el-row :gutter="12">
            <el-col :span="12"><el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item></el-col>
          </el-row>
          <el-row :gutter="12">
            <el-col :span="12"><el-form-item label="年龄"><el-input-number v-model="form.age" :min="18" :max="65" style="width:100%" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="学历"><el-select v-model="form.educationCode" style="width:100%"><el-option label="初中" value="MIDDLE" /><el-option label="高中" value="HIGH" /><el-option label="大专" value="COLLEGE" /></el-select></el-form-item></el-col>
          </el-row>
          <el-row :gutter="12">
            <el-col :span="12"><el-form-item label="经验年限"><el-input-number v-model="form.experienceYears" :min="0" :max="40" style="width:100%" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="技能编码"><el-input v-model="form.skills" placeholder="COOKING,BABYCARE" /></el-form-item></el-col>
          </el-row>
          <el-form-item label="自我介绍"><el-input type="textarea" :rows="3" v-model="form.selfIntroduction" /></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="show=false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </template>
      </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { jobTypeMap } from '@/utils/display'
const page = ref({records:[], total:0})
const q = reactive({pageNo:1,pageSize:10,screeningResult:null,keyword:''})
const show = ref(false)
const form = reactive({ id: null, candidateId: 0, jobId: null, realName: '', phone: '', age: 25, educationCode: 'HIGH', experienceYears: 1, skills: 'COOKING', selfIntroduction: '' })
const jobMetaMap = ref({})

const loadJobs = async () => {
  const res = (await request.get('/api/admin/job/page', { params: { pageNo: 1, pageSize: 2000 } })).data
  const map = {}
  ;(res.records || []).forEach(j => {
    map[j.id] = j
  })
  jobMetaMap.value = map
}

const load = async () => {
  page.value = (await request.get('/api/admin/resume/page',{params:q})).data
  if (!Object.keys(jobMetaMap.value).length) {
    await loadJobs()
  }
}
const smart = async (row) => { await request.post(`/api/admin/resume/${row.id}/smart-screen`); ElMessage.success('筛选完成'); load() }
const screen = async (row, pass) => { await request.put(`/api/admin/resume/${row.id}/screen`,null,{params:{pass,reason:pass===1?'人工通过':'人工不通过'}}); ElMessage.success('操作成功'); load() }

const jobTypeText = (jobId) => {
  const job = jobMetaMap.value[jobId]
  return jobTypeMap[job?.jobTypeCode] || job?.jobTypeCode || '-'
}

const jobTitleText = (jobId) => {
  return jobMetaMap.value[jobId]?.title || '-'
}

const resetForm = () => {
  Object.assign(form, { id: null, candidateId: 0, jobId: null, realName: '', phone: '', age: 25, educationCode: 'HIGH', experienceYears: 1, skills: 'COOKING', selfIntroduction: '' })
}

const openAdd = () => {
  resetForm()
  show.value = true
}

const edit = (row) => {
  Object.assign(form, row)
  show.value = true
}

const save = async () => {
  if (form.id) await request.put(`/api/admin/resume/${form.id}`, form)
  else await request.post('/api/admin/resume', form)
  ElMessage.success('保存成功')
  show.value = false
  load()
}

const del = async (row) => {
  await ElMessageBox.confirm('确认删除该简历?')
  await request.delete(`/api/admin/resume/${row.id}`)
  ElMessage.success('删除成功')
  load()
}

const onPageSizeChange = (size) => {
  q.pageSize = size
  q.pageNo = 1
  load()
}

const onPageChange = (pageNo) => {
  q.pageNo = pageNo
  load()
}

onMounted(load)
</script>

<style scoped>
.page-wrap {
  background: transparent;
}
.manual-screen-dropdown {
  margin-left: 8px;
}
</style>
