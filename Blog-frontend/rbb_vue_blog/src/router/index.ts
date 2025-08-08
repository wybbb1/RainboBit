import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/categories',
      name: 'categories',
      component: () => import('@/views/Categories.vue')
    },
    {
      path: '/categories/:id',
      name: 'category-detail',
      component: () => import('@/views/CategoryDetail.vue')
    },
    {
      path: '/archives',
      name: 'archives',
      component: () => import('@/views/Archives.vue')
    },
    // {
    //   path: '/tags',
    //   name: 'tags',
    //   component: () => import('@/views/Tags.vue')
    // },
    {
        path: '/tags/:name',
        name: 'tag-detail',
        component: () => import('@/views/TagDetail.vue')
    },
    {
      path: '/links',
      name: 'links',
      component: () => import('@/views/Links.vue')
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('@/views/About.vue')
    },
    {
      path: '/article/:id',
      name: 'article-detail',
      component: () => import('@/views/ArticleDetail.vue')
    }
  ]
})

export default router

