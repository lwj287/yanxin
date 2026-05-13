import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', component: () => import('@/views/auth/Login.vue') },
  { path: '/', redirect: '/admin/jobs' },
  {
    path: '/admin',
    component: () => import('@/layout/AdminLayout.vue'),
    children: [
      { path: 'jobs', component: () => import('@/views/admin/JobManage.vue') },
      { path: 'resumes', component: () => import('@/views/admin/ResumeManage.vue') },
      { path: 'interviews', component: () => import('@/views/admin/InterviewManage.vue') },
      { path: 'onboarding', component: () => import('@/views/admin/OnboardingManage.vue') },
      { path: 'stats', component: () => import('@/views/admin/StatDashboard.vue') }
    ]
  }
]

const router = createRouter({ history: createWebHistory(), routes })
router.beforeEach((to, from, next) => {
  if (to.path === '/login') return next()
  if (!sessionStorage.getItem('token')) return next('/login')
  next()
})

export default router
