<template>
  <div class="page-wrap">
    <div class="header-action">
      <div class="page-header">
        <div>
          <div class="page-title">入职管理</div>
          <div class="page-subtitle">录入人员档案与办理入职</div>
        </div>
        <el-button type="primary" @click="openAdd">新增记录</el-button>
      </div>

      <div class="toolbar query-left">
        <el-input v-model="q.resumeId" placeholder="简历编号" clearable style="width: 140px" />
        <el-select v-model="q.onboardStatus" clearable placeholder="入职状态" style="width: 140px">
          <el-option label="待材料" value="PENDING_DOCS" />
          <el-option label="待入职" value="WAIT_ONBOARD" />
          <el-option label="已入职" value="ONBOARDED" />
        </el-select>
        <el-select v-model="q.docsVerified" clearable placeholder="材料审核" style="width: 130px">
          <el-option label="未审核" :value="0" />
          <el-option label="已审核" :value="1" />
        </el-select>
        <el-button class="query-btn" type="primary" @click="load">查询</el-button>
        <el-button class="query-btn" @click="resetQuery">重置</el-button>
      </div>
    </div>

    <div class="list-container">
      <el-table :data="pagedList" stripe>
        <el-table-column prop="id" label="编号" width="80"/>
        <el-table-column prop="resumeId" label="简历编号" min-width="100" />
        <el-table-column prop="hrId" label="HR编号" min-width="100" />
        <el-table-column label="入职状态" min-width="120"><template #default="{row}">{{ onboardStatusMap[row.onboardStatus] || row.onboardStatus }}</template></el-table-column>
        <el-table-column label="材料审核" min-width="110"><template #default="{row}">{{ yesNo(row.docsVerified) }}</template></el-table-column>
        <el-table-column prop="onboardingDate" label="入职日期" min-width="130" />
        <el-table-column prop="contractNo" label="合同编号" min-width="160" />
        <el-table-column label="操作" width="360" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="edit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="del(row)">删除</el-button>
            <el-button size="small" @click="verify(row)">审核材料</el-button>
            <el-button size="small" type="primary" @click="confirm(row)">确认入职</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pager.pageNo"
        v-model:page-size="pager.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pager.total"
        @size-change="onPageSizeChange"
        @current-change="onPageChange"
      />
    </div>

    <el-dialog v-model="show" :title="form.id ? '编辑入职记录' : '新增入职记录'" width="620px">
        <el-form :model="form" label-width="100px">
          <el-row :gutter="12">
            <el-col :span="12"><el-form-item label="简历编号"><el-input-number v-model="form.resumeId" :min="1" style="width:100%" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="HR编号"><el-input-number v-model="form.hrId" :min="1" style="width:100%" /></el-form-item></el-col>
          </el-row>
          <el-row :gutter="12">
            <el-col :span="12"><el-form-item label="入职状态"><el-select v-model="form.onboardStatus" style="width:100%"><el-option label="待材料" value="PENDING_DOCS" /><el-option label="待入职" value="WAIT_ONBOARD" /><el-option label="已入职" value="ONBOARDED" /></el-select></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="材料审核"><el-select v-model="form.docsVerified" style="width:100%"><el-option label="未审核" :value="0" /><el-option label="已审核" :value="1" /></el-select></el-form-item></el-col>
          </el-row>
          <el-row :gutter="12">
            <el-col :span="12"><el-form-item label="入职日期"><el-date-picker v-model="form.onboardingDate" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="合同编号"><el-input v-model="form.contractNo" /></el-form-item></el-col>
          </el-row>
          <el-form-item label="备注"><el-input type="textarea" v-model="form.remark" :rows="3" /></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="show=false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </template>
      </el-dialog>
  </div>
</template>

<script setup>
import { computed, reactive, ref, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onboardStatusMap, yesNo } from '@/utils/display'
const list = ref([])
const pager = reactive({ pageNo: 1, pageSize: 10, total: 0 })
const show = ref(false)
const q = reactive({ resumeId: '', onboardStatus: '', docsVerified: '' })
const form = reactive({ id: null, resumeId: null, hrId: 2, onboardStatus: 'PENDING_DOCS', docsVerified: 0, onboardingDate: '', contractNo: '', remark: '' })

const load = async () => {
  const params = {
    resumeId: q.resumeId || undefined,
    onboardStatus: q.onboardStatus || undefined,
    docsVerified: q.docsVerified === '' ? undefined : q.docsVerified
  }
  list.value = (await request.get('/api/admin/onboarding/page', { params })).data
  pager.total = list.value.length
  pager.pageNo = 1
}
const pagedList = computed(() => {
  const start = (pager.pageNo - 1) * pager.pageSize
  return list.value.slice(start, start + pager.pageSize)
})

const resetQuery = () => {
  q.resumeId = ''
  q.onboardStatus = ''
  q.docsVerified = ''
  load()
}

const resetForm = () => {
  Object.assign(form, { id: null, resumeId: null, hrId: 2, onboardStatus: 'PENDING_DOCS', docsVerified: 0, onboardingDate: '', contractNo: '', remark: '' })
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
  if (form.id) await request.put(`/api/admin/onboarding/${form.id}`, form)
  else await request.post('/api/admin/onboarding', form)
  ElMessage.success('保存成功')
  show.value = false
  load()
}

const del = async (row) => {
  await ElMessageBox.confirm('确认删除该入职记录?')
  await request.delete(`/api/admin/onboarding/${row.id}`)
  ElMessage.success('删除成功')
  load()
}

const verify = async (row) => { await request.post(`/api/admin/onboarding/${row.id}/verify-docs`); ElMessage.success('审核完成'); load() }
const confirm = async (row) => { await request.post(`/api/admin/onboarding/${row.id}/confirm`, null, { params: { contractNo: `HT-${row.id}` } }); ElMessage.success('入职完成'); load() }
const onPageSizeChange = (size) => { pager.pageSize = size; pager.pageNo = 1 }
const onPageChange = (pageNo) => { pager.pageNo = pageNo }
onMounted(load)
</script>

<style scoped>
.page-wrap {
  background: transparent;
}
</style>
