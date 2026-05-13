import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('../layout/index.vue'),
      redirect: '/suoguanjia/dashboard',
      children: [
        {
          path: '/suoguanjia/dashboard',
          name: 'Dashboard',
          component: () => import('@/views/dashboard/index.vue'),
          meta: { title: '首页概览' }
        },
        {
          path: '/suoguanjia/device',
          name: 'Device',
          component: () => import('@/views/device/index.vue'),
          meta: { title: '设备管理' }
        },
        {
          path: '/suoguanjia/auth',
          name: 'Auth',
          component: () => import('@/views/auth/index.vue'),
          meta: { title: '权限分配' }
        },
        {
          path: '/suoguanjia/log',
          name: 'Log',
          component: () => import('@/views/log/index.vue'),
          meta: { title: '操作日志' }
        }
      ]
    }
  ]
})

export default router
