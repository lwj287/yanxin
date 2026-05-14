import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页概览' }
      },
      {
        path: 'service-type',
        name: 'ServiceType',
        component: () => import('@/views/service-type/index.vue'),
        meta: { title: '服务类型管理' }
      },
      {
        path: 'template',
        name: 'Template',
        component: () => import('@/views/template/index.vue'),
        meta: { title: '质检模板配置' }
      },
      {
        path: 'task',
        name: 'Task',
        component: () => import('@/views/task/index.vue'),
        meta: { title: '质检任务处理' }
      },
      {
        path: 'appeal',
        name: 'Appeal',
        component: () => import('@/views/appeal/index.vue'),
        meta: { title: '申诉处理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
