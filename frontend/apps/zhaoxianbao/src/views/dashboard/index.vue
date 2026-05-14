<template>
  <div class="page-wrap">
    <div class="header-action">
      <div class="page-header" style="margin-bottom:0">
        <div>
          <div class="page-title">数据统计看板</div>
          <div class="page-subtitle">按时间筛选并图表化展示招聘趋势</div>
        </div>
        <div class="toolbar query-left" style="margin-bottom:0">
          <el-date-picker
            v-model="startDate"
            type="date"
            placeholder="开始日期"
            value-format="YYYY-MM-DD"
            style="width: 140px"
          />
          <el-date-picker
            v-model="endDate"
            type="date"
            placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 140px"
          />
          <el-button class="query-btn" type="primary" @click="load">筛选</el-button>
          <el-button class="query-btn" @click="resetRange">重置</el-button>
        </div>
      </div>
    </div>

    <el-row :gutter="20">
        <el-col :xl="18" :lg="18" :md="16" :sm="24" :xs="24">
          <el-card class="panel-card">
            <template #header>招聘趋势分析</template>
            <div class="charts-container">
              <div class="chart-wrapper">
                <div class="chart-title">投递趋势</div>
                <div ref="trendApplyRef" class="chart-box-small"></div>
              </div>
              <div class="chart-wrapper">
                <div class="chart-title">面试趋势</div>
                <div ref="trendInterviewRef" class="chart-box-small"></div>
              </div>
              <div class="chart-wrapper">
                <div class="chart-title">入职趋势</div>
                <div ref="trendOnboardRef" class="chart-box-small"></div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xl="6" :lg="6" :md="8" :sm="24" :xs="24">
          <div class="right-stack">
            <div class="stat-card">
              <div class="stat-label">总投递量</div>
              <div class="stat-value">{{ data.applyCount || 0 }}</div>
            </div>
            <div class="stat-card">
              <div class="stat-label">面试量</div>
              <div class="stat-value">{{ data.interviewCount || 0 }}</div>
            </div>
            <div class="stat-card">
              <div class="stat-label">入职量</div>
              <div class="stat-value">{{ data.onboardCount || 0 }}</div>
            </div>

            <el-card class="panel-card">
              <template #header>数据分析</template>
              <div class="insight-item">
              <div class="insight-title">面试率（面试/投递）</div>
              <div class="insight-line">
                <span class="insight-progress p1" :style="{ width: rateNum(data.interviewCount, data.applyCount) + '%' }"></span>
              </div>
              <div class="insight-value">{{ rateText(data.interviewCount, data.applyCount) }}</div>
            </div>
            <div class="insight-item">
              <div class="insight-title">入职率（入职/面试）</div>
              <div class="insight-line">
                <span class="insight-progress p2" :style="{ width: rateNum(data.onboardCount, data.interviewCount) + '%' }"></span>
              </div>
              <div class="insight-value">{{ rateText(data.onboardCount, data.interviewCount) }}</div>
            </div>
            <div class="insight-item">
                <div class="insight-title">转化率（入职/投递）</div>
                <div class="insight-line">
                  <span class="insight-progress p3" :style="{ width: rateNum(data.onboardCount, data.applyCount) + '%' }"></span>
                </div>
                <div class="insight-value">{{ rateText(data.onboardCount, data.applyCount) }}</div>
              </div>
            </el-card>
          </div>
        </el-col>
      </el-row>
  </div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import request from '@/api/request'
import * as echarts from 'echarts'

const data = reactive({ applyCount: 0, interviewCount: 0, onboardCount: 0, attendRate: 0, onboardRate: 0, labels: [], trendApply: [], trendInterview: [], trendOnboard: [] })
const startDate = ref('')
const endDate = ref('')
const trendApplyRef = ref(null)
const trendInterviewRef = ref(null)
const trendOnboardRef = ref(null)
let chartApply = null
let chartInterview = null
let chartOnboard = null

const formatDate = (d) => {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

const setDefaultRange = () => {
  const end = new Date()
  const start = new Date()
  // 默认近7天（含今天）
  start.setDate(end.getDate() - 6)
  startDate.value = formatDate(start)
  endDate.value = formatDate(end)
}

const getParams = () => ({
  startDate: startDate.value || undefined,
  endDate: endDate.value || undefined
})

const rateNum = (a, b) => {
  const x = Number(a || 0)
  const y = Number(b || 0)
  if (!y) return 0
  return Math.max(0, Math.min(100, Number(((x / y) * 100).toFixed(1))))
}

const rateText = (a, b) => `${rateNum(a, b)}%`

const load = async () => {
  Object.assign(data, (await request.get('/api/admin/stat/overview', { params: getParams() })).data)
  await nextTick()
  renderChart()
}

const resetRange = async () => {
  setDefaultRange()
  await load()
}

const renderChart = () => {
  if (trendApplyRef.value) {
    if (!chartApply) chartApply = echarts.init(trendApplyRef.value)
    chartApply.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 34, right: 16, bottom: 26, top: 10 },
      xAxis: { type: 'category', data: data.labels || [] },
      yAxis: { type: 'value', minInterval: 1, splitLine: { lineStyle: { color: '#eef2f7' } } },
      series: [{
        name: '投递',
        type: 'line',
        color: '#409eff',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: { width: 3 },
        areaStyle: { color: 'rgba(64, 158, 255, 0.10)' },
        data: data.trendApply || []
      }]
    })
  }

  if (trendInterviewRef.value) {
    if (!chartInterview) chartInterview = echarts.init(trendInterviewRef.value)
    chartInterview.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 34, right: 16, bottom: 26, top: 10 },
      xAxis: { type: 'category', data: data.labels || [] },
      yAxis: { type: 'value', minInterval: 1, splitLine: { lineStyle: { color: '#eef2f7' } } },
      series: [{
        name: '面试',
        type: 'line',
        color: '#1890ff',
        smooth: true,
        symbol: 'diamond',
        symbolSize: 7,
        lineStyle: { width: 2, type: 'solid' },
        areaStyle: { color: 'rgba(24, 144, 255, 0.10)' },
        data: data.trendInterview || []
      }]
    })
  }

  if (trendOnboardRef.value) {
    if (!chartOnboard) chartOnboard = echarts.init(trendOnboardRef.value)
    chartOnboard.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 34, right: 16, bottom: 26, top: 10 },
      xAxis: { type: 'category', data: data.labels || [] },
      yAxis: { type: 'value', minInterval: 1, splitLine: { lineStyle: { color: '#eef2f7' } } },
      series: [{
        name: '入职',
        type: 'line',
        color: '#fa8c16',
        smooth: true,
        symbol: 'triangle',
        symbolSize: 7,
        lineStyle: { width: 2, type: 'solid' },
        areaStyle: { color: 'rgba(250, 140, 22, 0.10)' },
        data: data.trendOnboard || []
      }]
    })
  }
}

const onResize = () => { 
  if (chartApply) chartApply.resize()
  if (chartInterview) chartInterview.resize()
  if (chartOnboard) chartOnboard.resize()
}

onMounted(async () => {
  setDefaultRange()
  await load()
  window.addEventListener('resize', onResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  if (chartApply) { chartApply.dispose(); chartApply = null }
  if (chartInterview) { chartInterview.dispose(); chartInterview = null }
  if (chartOnboard) { chartOnboard.dispose(); chartOnboard = null }
})
</script>

<style scoped>
.page-wrap {
  background: transparent;
  padding: 24px;
}
.right-stack {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.stat-card {
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: 16px;
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.stat-label {
  color: var(--muted);
  font-size: 14px;
}
.stat-value {
  font-size: 32px;
  line-height: 1.2;
  font-weight: 600;
  color: #1d2129;
}
.panel-card {
  border-radius: 16px;
  border: 1px solid var(--border);
  background: #ffffff;
  box-shadow: none;
}
.panel-card :deep(.el-card__header) {
  padding: 16px 24px;
  border-bottom: 1px solid var(--border);
  font-weight: 600;
  color: #1d2129;
}
.charts-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.chart-wrapper {
  width: 100%;
}
.chart-title {
  font-size: 14px;
  font-weight: 600;
  color: #4e5969;
  margin-bottom: 8px;
  padding-left: 8px;
  border-left: 3px solid #409eff;
}
.chart-box-small {
  width: 100%;
  height: 180px;
}
.insight-item {
  margin-bottom: 24px;
}
.insight-item:last-child {
  margin-bottom: 0;
}
.insight-title {
  font-size: 13px;
  color: #4e5969;
}
.insight-line {
  height: 6px;
  background: #f2f3f5;
  border-radius: 3px;
  margin-top: 8px;
  overflow: hidden;
}
.insight-progress {
  display: block;
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s ease;
}
.insight-progress.p1 { background: #409eff; }
.insight-progress.p2 { background: #1890ff; }
.insight-progress.p3 { background: #fa8c16; }
.insight-value {
  margin-top: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #1d2129;
  text-align: right;
}
</style>
