import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue')
    },
    {
      path: '/teams',
      name: 'teams',
      component: () => import('../views/TeamListView.vue')
    },
    {
      path: '/teams/:teamId/projects',
      name: 'team-projects',
      component: () => import('../views/TeamProjectListView.vue')
    },
    {
      path: '/projects',
      name: 'projects',
      component: () => import('../views/ProjectListView.vue')
    },
    {
      path: '/projects/:id',
      name: 'project-detail',
      component: () => import('../views/ProjectDetailView.vue')
    },
    {
      path: '/api-manager',
      name: 'api-manager',
      component: () => import('../views/ApiManagerView.vue')
    },
    {
      path: '/api-design',
      name: 'api-design',
      component: () => import('../views/ApiDesignView.vue')
    },
    {
      path: '/api-test',
      name: 'api-test',
      component: () => import('../views/ApiTestView.vue')
    },
    {
      path: '/mock',
      name: 'mock',
      component: () => import('../views/MockView.vue')
    },
    {
      path: '/docs',
      name: 'docs',
      component: () => import('../views/DocsView.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    }
  ]
})

export default router
