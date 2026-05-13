import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('../layout/index.vue'),
      redirect: '/zhijian/dashboard',
      children: [
        {
        path: '/zhijian/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页概览' }
      },
        {
        path: '/zhijian/service-type',
        name: 'ServiceType',
        component: () => import('@/views/service-type/index.vue'),
        meta: { title: '服务类型管理' }
      },
      {
        path: '/zhijian/template',
        name: 'Template',
        component: () => import('@/views/template/index.vue'),
        meta: { title: '质检模板配置' }
      },
      {
        path: '/zhijian/task',
        name: 'Task',
        component: () => import('@/views/task/index.vue'),
        meta: { title: '质检任务处理' }
      },
      {
        path: '/zhijian/appeal',
        name: 'Appeal',
        component: () => import('@/views/appeal/index.vue'),
        meta: { title: '申诉处理' }
      }
      ]
    }
  ]
})

export default router
