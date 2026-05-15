import { createRouter, createWebHistory } from 'vue-router';
import Layout from '../layout/index.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: Layout,
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('../views/dashboard/index.vue'),
          meta: { title: '聚合数据大屏' }
        }
      ]
    }
  ]
});

export default router;
