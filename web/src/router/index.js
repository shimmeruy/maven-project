import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/HomePage.vue')
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('../views/SearchPage.vue')
  },
  {
    path: '/map/:tableId',
    name: 'Map',
    component: () => import('../views/MapPage.vue')
  },
  {
    path: '/graph',
    name: 'Graph',
    component: () => import('../views/GraphPage.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
