import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录 - 燕鑫学院' }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页概览' }
      },
      {
        path: 'course',
        name: 'CourseList',
        component: () => import('@/views/course/index.vue'),
        meta: { title: '在线课程', icon: 'VideoCamera' }
      },
      {
        path: 'exam',
        name: 'ExamList',
        component: () => import('@/views/exam/index.vue'),
        meta: { title: '考试认证', icon: 'Document' }
      },
      {
        path: 'certification',
        name: 'Certification',
        component: () => import('@/views/certification/index.vue'),
        meta: { title: '认证审批' }
      },
      {
        path: 'user',
        name: 'UserManage',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '学员管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = (to.meta.title as string) || '燕鑫学院'
  // const token = localStorage.getItem('token')
  // if (!token && to.path !== '/login') {
  //   next('/login')
  // } else {
  //   next()
  // }
  next()
})

export default router