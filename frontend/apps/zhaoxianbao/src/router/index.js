import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', component: () => import('@/views/login/index.vue') },
  { path: '/', redirect: '/admin/dashboard' },
  {
    path: '/admin',
    component: () => import('@/layout/AdminLayout.vue'),
    children: [
      { path: 'dashboard', component: () => import('@/views/dashboard/index.vue') },
      { path: 'jobs', component: () => import('@/views/job/index.vue') },
      { path: 'resumes', component: () => import('@/views/resume/index.vue') },
      { path: 'interviews', component: () => import('@/views/interview/index.vue') },
      { path: 'onboarding', component: () => import('@/views/onboarding/index.vue') }
    ]
  }
]

const router = createRouter({ history: createWebHistory(), routes })
router.beforeEach((to, from, next) => {
  // if (to.path === '/login') return next()
  // if (!sessionStorage.getItem('token')) return next('/login')
  next()
})

export default router
