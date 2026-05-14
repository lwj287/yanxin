<template>
  <div class="page-wrap">
    <div class="header-action">
      <div class="page-header">
        <div>
          <div class="page-title">岗位管理</div>
          <div class="page-subtitle">岗位发布、上下架与筛选管理</div>
        </div>
        <div class="header-actions">
          <el-button type="primary" plain @click="showTypeDialog=true">新增岗位分类</el-button>
          <el-button type="primary" @click="openAdd">新增岗位</el-button>
        </div>
      </div>

      <div class="toolbar query-left">
        <el-input v-model="q.keyword" placeholder="岗位关键字（岗位名称/描述）" clearable style="width: 260px" />
        <el-input v-model="q.cityCode" placeholder="城市（支持模糊）" clearable style="width: 140px" />
        <el-input v-model="q.areaCode" placeholder="区域（支持模糊）" clearable style="width: 140px" />
        <el-input-number v-model="q.salaryMin" :min="0" :step="500" placeholder="最低薪资" style="width: 140px" />
        <el-input-number v-model="q.salaryMax" :min="0" :step="500" placeholder="最高薪资" style="width: 140px" />
        <el-select v-model="q.publishStatus" clearable placeholder="上下架状态" style="width: 130px">
          <el-option label="上架" :value="1" />
          <el-option label="下架" :value="0" />
        </el-select>
        <el-button class="query-btn" type="primary" @click="load">查询岗位</el-button>
        <el-button class="query-btn" @click="resetQuery">重置条件</el-button>
      </div>
    </div>

    <div class="list-container">
      <el-row v-if="(page.records || []).length" :gutter="24" class="job-card-list">
        <el-col v-for="row in page.records" :key="row.id" :xl="8" :lg="8" :md="12" :sm="24" :xs="24">
          <el-card :class="['job-card', row.publishStatus===1 ? 'job-card-up' : 'job-card-down']" shadow="hover">
            <div class="job-card-head">
              <div class="job-head-left">
                <div class="job-icon-wrap">
                  <el-icon class="job-icon"><Briefcase /></el-icon>
                </div>
                <div>
                  <div class="job-title-row">
                    <div class="job-title">{{ row.title }}</div>
                    <span :class="['status-tag', row.publishStatus===1 ? 'status-success' : 'status-warn']">
                      {{ row.publishStatus===1 ? '已上架' : '已下架' }}
                    </span>
                  </div>
                  <div class="job-meta">{{ jobTypeText(row.jobTypeCode) }}</div>
                </div>
              </div>
            </div>

            <div class="job-city-line">
              <span class="city-dot"></span>
              <span>{{ cityMap[row.cityCode] || row.cityCode }} / {{ cityMap[row.areaCode] || row.areaCode || '-' }}</span>
            </div>

            <div class="job-panel">
              <div class="panel-block">
                <div class="panel-title">岗位要求</div>
                <div class="job-info-grid">
                  <div class="info-item info-item-edu">
                    <span class="info-label">学历</span>
                    <span class="info-value">{{ educationMap[row.educationCode] || row.educationCode }}</span>
                  </div>
                  <div class="info-item info-item-exp">
                    <span class="info-label">经验</span>
                    <span class="info-value">{{ row.minExperienceYears || 0 }}年+</span>
                  </div>
                  <div class="info-item info-item-full info-item-age">
                    <span class="info-label">年龄</span>
                    <span class="info-value">{{ row.minAge || '-' }} - {{ row.maxAge || '-' }}</span>
                  </div>
                  <div class="info-item info-item-salary">
                    <span class="info-label">薪资</span>
                    <span class="salary-value">￥{{ row.salaryMin }}-{{ row.salaryMax }}</span>
                  </div>
                  <div class="info-item info-item-skill info-item-skill-center">
                  <span class="info-label">技能</span>
                  <span class="info-value">{{ formatCodeList(row.requiredSkills, skillMap) }}</span>
                  </div>
                </div>
              </div>

              <div class="panel-block">
                <div class="panel-title">岗位说明</div>
                <div :class="['job-desc', isDescExpanded(row.id) ? 'job-desc-expanded' : '']">
                  {{ row.description || '暂无岗位描述' }}
                </div>
                <button
                  v-if="(row.description || '').length > 36"
                  class="desc-toggle"
                  @click="toggleDesc(row.id)"
                >
                  {{ isDescExpanded(row.id) ? '收起' : '展开' }}
                </button>
              </div>
            </div>

            <div class="job-actions">
              <div class="action-fab">
                <el-button class="fab-main" circle type="primary" @click="toggleActionMenu(row.id)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <div v-show="activeActionId === row.id" class="fab-panel">
                  <button class="fab-item fab-edit" @click="edit(row)">
                    <el-icon><Edit /></el-icon>
                    <span>编辑</span>
                  </button>
                  <button :class="['fab-item', row.publishStatus===1 ? 'fab-publish-down' : 'fab-publish-up']" @click="pub(row)">
                    <el-icon><SwitchButton /></el-icon>
                    <span>{{ row.publishStatus===1 ? '下架' : '上架' }}</span>
                  </button>
                  <button class="fab-item fab-delete" @click="del(row)">
                    <el-icon><Delete /></el-icon>
                    <span>删除</span>
                  </button>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-else description="暂无岗位数据" />
    </div>

    <el-dialog v-model="show" title="岗位信息" width="720px">
        <el-form :model="form" label-width="100px">
          <el-row :gutter="12">
            <el-col :span="12"><el-form-item label="岗位名称"><el-input v-model="form.title" /></el-form-item></el-col>
            <el-col :span="12">
              <el-form-item label="岗位类型">
                <el-select v-model="form.jobTypeCode" style="width:100%">
                  <el-option v-for="item in jobTypeOptions" :key="item.code" :label="item.name" :value="item.code" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="12">
            <el-col :span="12"><el-form-item label="城市"><el-input v-model="form.cityCode" placeholder="请直接填写，如：深圳" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="区域"><el-input v-model="form.areaCode" placeholder="请直接填写，如：南山" /></el-form-item></el-col>
          </el-row>
          <el-row :gutter="12">
            <el-col :span="12"><el-form-item label="学历"><el-select v-model="form.educationCode" style="width:100%"><el-option label="初中" value="MIDDLE" /><el-option label="高中" value="HIGH" /><el-option label="大专" value="COLLEGE" /></el-select></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="经验年限"><el-input-number v-model="form.minExperienceYears" :min="0" :max="30" style="width:100%" /></el-form-item></el-col>
          </el-row>
          <el-row :gutter="12">
            <el-col :span="12"><el-form-item label="最低薪资"><el-input-number v-model="form.salaryMin" :min="0" style="width:100%" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="最高薪资"><el-input-number v-model="form.salaryMax" :min="0" style="width:100%" /></el-form-item></el-col>
          </el-row>
          <el-row :gutter="12">
            <el-col :span="12"><el-form-item label="最小年龄"><el-input-number v-model="form.minAge" :min="18" :max="60" style="width:100%" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="最大年龄"><el-input-number v-model="form.maxAge" :min="18" :max="60" style="width:100%" /></el-form-item></el-col>
          </el-row>
          <el-form-item label="技能要求">
            <el-select v-model="skillValues" multiple filterable style="width:100%" placeholder="请选择技能">
              <el-option label="做饭" value="COOKING" />
              <el-option label="育儿" value="BABYCARE" />
              <el-option label="护老" value="ELDERCARE" />
            </el-select>
          </el-form-item>
          <el-form-item label="岗位描述"><el-input type="textarea" :rows="3" v-model="form.description" /></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="show=false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </template>
      </el-dialog>

      <el-dialog v-model="showTypeDialog" title="新增岗位分类" width="520px">
        <el-form :model="typeForm" label-width="100px">
          <el-form-item label="分类名称">
            <el-input v-model="typeForm.name" placeholder="如：月嫂、护工、保安" />
          </el-form-item>
          <el-form-item label="分类编码">
            <el-input v-model="typeForm.code" placeholder="如：MATERNITY（可不填，自动生成）" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showTypeDialog=false">取消</el-button>
          <el-button type="primary" @click="saveJobType">保存分类</el-button>
        </template>
      </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Briefcase, Delete, Edit, SwitchButton } from '@element-plus/icons-vue'
import { cityMap, educationMap, formatCodeList, jobTypeMap, skillMap } from '@/utils/display'

const page = ref({records:[], total:0})
const q = reactive({pageNo:1,pageSize:1000,keyword:'',cityCode:'',areaCode:'',salaryMin:null,salaryMax:null,publishStatus:null})
const show = ref(false)
const showTypeDialog = ref(false)
const skillValues = ref([])
const jobTypeOptions = ref([
  { code: 'CLEANER', name: '保洁' },
  { code: 'NANNY', name: '保姆' }
])
const typeForm = reactive({ name: '', code: '' })
const form = reactive({id:null,title:'',jobTypeCode:'CLEANER',cityCode:'深圳',areaCode:'南山',salaryMin:6000,salaryMax:8000,educationCode:'HIGH',minAge:25,maxAge:45,minExperienceYears:1,requiredSkills:'COOKING',description:''})
const activeActionId = ref(null)
const expandedDescIds = ref([])

const jobTypeText = (code) => {
  const hit = jobTypeOptions.value.find(i => i.code === code)
  return hit?.name || jobTypeMap[code] || code || '-'
}

const load = async () => {
  const params = {
    pageNo: q.pageNo,
    pageSize: q.pageSize,
    keyword: q.keyword || undefined,
    publishStatus: q.publishStatus
  }
  const res = (await request.get('/api/admin/job/page', { params })).data
  let records = res.records || []
  if (q.cityCode) {
    const kwCity = String(q.cityCode).trim()
    records = records.filter(i => String(cityMap[i.cityCode] || i.cityCode || '').includes(kwCity))
  }
  if (q.areaCode) {
    const kwArea = String(q.areaCode).trim()
    records = records.filter(i => String(cityMap[i.areaCode] || i.areaCode || '').includes(kwArea))
  }
  if (q.salaryMin !== null && q.salaryMin !== undefined) {
    records = records.filter(i => Number(i.salaryMin) >= Number(q.salaryMin))
  }
  if (q.salaryMax !== null && q.salaryMax !== undefined) {
    records = records.filter(i => Number(i.salaryMax) <= Number(q.salaryMax))
  }
  page.value = { ...res, records, total: records.length }
}

const resetForm = () => {
  Object.assign(form,{id:null,title:'',jobTypeCode:'CLEANER',cityCode:'深圳',areaCode:'南山',salaryMin:6000,salaryMax:8000,educationCode:'HIGH',minAge:25,maxAge:45,minExperienceYears:1,requiredSkills:'COOKING',description:''})
  skillValues.value = ['COOKING']
}

const openAdd = () => {
  resetForm()
  show.value=true
}

const edit = (row) => {
  Object.assign(form,row)
  skillValues.value = row.requiredSkills ? row.requiredSkills.split(',') : []
  show.value=true
}

const save = async () => {
  form.requiredSkills = skillValues.value.join(',')
  if(form.id) await request.put(`/api/admin/job/${form.id}`,form)
  else await request.post('/api/admin/job',form)
  ElMessage.success('保存成功')
  show.value=false
  load()
}

const pub = async (row) => {
  await request.put(`/api/admin/job/${row.id}/publish`,null,{params:{publishStatus:row.publishStatus===1?0:1}})
  ElMessage.success('操作成功')
  load()
}

const del = async (row) => {
  await ElMessageBox.confirm('确认删除该岗位?')
  await request.delete(`/api/admin/job/${row.id}`)
  ElMessage.success('删除成功')
  activeActionId.value = null
  load()
}

const toggleActionMenu = (id) => {
  activeActionId.value = activeActionId.value === id ? null : id
}

const isDescExpanded = (id) => expandedDescIds.value.includes(id)

const toggleDesc = (id) => {
  if (isDescExpanded(id)) {
    expandedDescIds.value = expandedDescIds.value.filter((i) => i !== id)
    return
  }
  expandedDescIds.value.push(id)
}

const resetQuery = () => {
  q.keyword = ''
  q.cityCode = ''
  q.areaCode = ''
  q.salaryMin = null
  q.salaryMax = null
  q.publishStatus = null
  load()
}

const saveJobType = () => {
  const name = (typeForm.name || '').trim()
  if (!name) return ElMessage.warning('请填写分类名称')
  let code = (typeForm.code || '').trim().toUpperCase()
  if (!code) {
    code = `CAT_${Date.now().toString(36).toUpperCase()}`
  }
  if (jobTypeOptions.value.some(i => i.code === code)) return ElMessage.warning('分类编码已存在')
  jobTypeOptions.value.push({ code, name })
  form.jobTypeCode = code
  localStorage.setItem('job_type_custom_options', JSON.stringify(jobTypeOptions.value))
  typeForm.name = ''
  typeForm.code = ''
  showTypeDialog.value = false
  ElMessage.success('岗位分类已新增')
}

onMounted(() => {
  const cache = localStorage.getItem('job_type_custom_options')
  if (cache) {
    try {
      const arr = JSON.parse(cache)
      if (Array.isArray(arr) && arr.length) {
        jobTypeOptions.value = arr
      }
    } catch {}
  }
  resetForm()
  load()
})

</script>

<style scoped>
.page-wrap {
  background: transparent;
}
.job-card-list {
  margin-top: 12px;
}
.job-card {
  position: relative;
  border-radius: 20px;
  margin-bottom: 24px;
  border: 1px solid #dcdfe6;
  background: #ffffff;
  overflow: hidden;
  transition: all .2s ease;
  box-shadow: none;
}
.job-card-up:hover {
  transform: translateY(-2px);
  border-color: #409eff;
  box-shadow: 0 6px 24px rgba(64, 158, 255, 0.08);
}
.job-card-down {
  background: #fafafa;
  border-color: #e5e6eb;
}
.job-card-down .job-card-head,
.job-card-down .job-city-line,
.job-card-down .job-panel {
  opacity: 0.85;
}
.job-card-down:hover {
  transform: translateY(-2px);
  border-color: #c9cdd4;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.04);
}
.job-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 0;
  height: 100%;
}
.job-card-up::before {
  background: #409eff;
}
.job-card-down::before {
  background: #c9cdd4;
}
.job-card :deep(.el-card__body) {
  padding: 24px;
}
.header-actions {
  display: flex;
  gap: 8px;
}
.job-head-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.job-icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ecf5ff;
}
.job-icon {
  color: #409eff;
  font-size: 24px;
}
.job-card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}
.job-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
}
.job-title {
  font-size: 18px;
  font-weight: 600;
  color: #1d2129;
}
.job-card-down .job-icon-wrap {
  background: #f2f3f5;
}
.job-card-down .job-icon {
  color: #86909c;
}
.job-card-down .city-dot {
  background: #86909c;
}
.job-card-down .salary-value {
  background: #f2f3f5;
  color: #86909c;
}
.job-card-down .job-panel {
  background: transparent;
}
.job-card-down .panel-block + .panel-block {
  border-top-color: #e5e6eb;
}
.job-card-down .fab-main {
  background: #ffffff;
  color: #86909c;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e6eb !important;
}
.job-meta {
  margin-top: 6px;
  color: #86909c;
  font-size: 13px;
}
.job-city-line {
  margin-top: 16px;
  color: #4e5969;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.city-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #409eff;
}
.job-panel {
  margin-top: 20px;
  background: #ffffff;
  border-radius: 16px;
  border: none;
  overflow: hidden;
}
.panel-block {
  padding: 16px 20px;
}
.panel-block + .panel-block {
  border-top: none;
  padding-top: 0;
}
.panel-title {
  font-size: 12px;
  color: #86909c;
  font-weight: 600;
  margin-bottom: 12px;
}
.job-info-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px 12px;
}
.info-item {
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}
.info-item-exp {
  justify-content: center;
}
.info-item-skill-center {
  justify-content: flex-end;
}
.info-item-age {
  justify-content: flex-end;
}
.info-item-full {
  grid-column: auto;
}
.info-item-skill {
  grid-column: 3 / 4;
}
.info-item-salary {
  grid-column: 1 / 3;
}
.info-label {
  color: #86909c;
  font-size: 12px;
  line-height: 1;
  flex-shrink: 0;
}
.info-value {
  color: #1d2129;
  font-size: 13px;
  font-weight: 500;
  line-height: 1.2;
  min-width: 0;
}
.salary-value {
  display: inline-flex;
  align-items: center;
  height: 24px;
  padding: 0 10px;
  max-width: 100%;
  border-radius: 6px;
  background: #ecf5ff;
  color: #409eff;
  font-weight: 600;
  font-size: 13px;
  white-space: nowrap;
}
.info-item-skill .info-value {
  text-align: right;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.job-skills {
  font-size: 13px;
  color: #1d2129;
}
.job-salary {
  font-size: 14px;
  font-weight: 600;
  color: #409eff;
}
.job-desc {
  color: #4e5969;
  font-size: 13px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.job-desc-expanded {
  -webkit-line-clamp: initial;
}
.desc-toggle {
  margin-top: 8px;
  border: none;
  padding: 0;
  background: transparent;
  color: #409eff;
  font-size: 12px;
  cursor: pointer;
}
.job-actions {
  position: absolute;
  top: 16px;
  right: 16px;
  z-index: 5;
}
.action-fab {
  position: relative;
  width: 132px;
  height: 32px;
  display: flex;
  justify-content: flex-end;
  align-items: flex-start;
}
.fab-main {
  width: 32px !important;
  height: 32px !important;
  padding: 0 !important;
  border: none !important;
  position: relative;
  z-index: 2;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}
.fab-panel {
  position: absolute;
  right: 40px;
  top: 0;
  display: flex;
  gap: 8px;
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: 4px;
  padding: 4px 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}
.fab-item {
  width: 44px;
  border: none;
  background: transparent;
  padding: 4px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  border-radius: 4px;
  transition: background .2s;
}
.fab-item:hover {
  background: #f2f3f5;
}
.fab-item .el-icon {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}
.fab-item span {
  font-size: 12px;
  line-height: 1;
}
.fab-edit {
  color: #1890ff;
}
.fab-edit .el-icon {
  background: #e6f7ff;
  color: #1890ff;
}
.fab-publish-down {
  color: #fa8c16;
}
.fab-publish-down .el-icon {
  background: #fff7e6;
  color: #fa8c16;
}
.fab-publish-up {
  color: #409eff;
}
.fab-publish-up .el-icon {
  background: #ecf5ff;
  color: #409eff;
}
.fab-delete {
  color: #f5222d;
}
.fab-delete .el-icon {
  background: #fff1f0;
  color: #f5222d;
}
@media (max-width: 1400px) {
  .job-info-grid {
    grid-template-columns: 1fr 1fr;
  }
  .info-item-salary {
    grid-column: 1 / 2;
  }
  .info-item-skill {
    grid-column: 2 / 3;
  }
}
@media (max-width: 768px) {
  .job-info-grid {
    grid-template-columns: 1fr;
  }
  .info-item-full {
    grid-column: auto;
  }
  .info-item-skill {
    grid-column: auto;
  }
  .info-item-salary {
    grid-column: auto;
  }
}
</style>
