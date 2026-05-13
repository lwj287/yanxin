import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('../layout/index.vue'),
      redirect: '/staff',
      children: [
        {
          path: 'staff',
          name: 'Staff',
          component: () => import('../views/staff/index.vue'),
          meta: { title: '员工管理' }
        },
        {
          path: 'auth',
          name: 'Auth',
          component: () => import('../views/auth/index.vue'),
          meta: { title: '实名认证审核' }
        },
        {
          path: 'contract',
          name: 'Contract',
          component: () => import('../views/contract/index.vue'),
          meta: { title: '电子合同管理' }
        },
        {
          path: 'insurance',
          name: 'Insurance',
          component: () => import('../views/insurance/index.vue'),
          meta: { title: '保险产品管理' }
        },
        {
          path: 'order',
          name: 'Order',
          component: () => import('../views/order/index.vue'),
          meta: { title: '保险订单管理' }
        },
        {
          path: 'claim',
          name: 'Claim',
          component: () => import('../views/claim/index.vue'),
          meta: { title: '理赔审核管理' }
        },
        {
          path: 'statistics',
          name: 'Statistics',
          component: () => import('../views/statistics/index.vue'),
          meta: { title: '数据统计' }
        }
      ]
    }
  ]
})

export default router
