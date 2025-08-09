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
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue'),
      meta: { requiresGuest: true } // 已登录用户不能访问
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/Register.vue'),
      meta: { requiresGuest: true } // 已登录用户不能访问
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/Profile.vue'),
      meta: { requiresAuth: true } // 需要登录才能访问
    },
    {
      path: '/debug',
      name: 'debug',
      component: () => import('@/views/Debug.vue')
    }
  ]
})

// 路由守卫 - 延迟执行，确保Store已经初始化
router.beforeEach(async (to, from, next) => {
  // 动态导入Store以避免循环依赖
  const { useUserStore } = await import('@/stores/user')
  const userStore = useUserStore()
  
  console.log('路由守卫检查:', {
    to: to.path,
    requiresAuth: to.meta.requiresAuth,
    requiresGuest: to.meta.requiresGuest,
    isLoggedIn: userStore.isLoggedIn
  })
  
  // 检查是否需要登录才能访问的页面
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    console.log('需要登录，跳转到登录页')
    next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
    return
  }
  
  // 检查已登录用户是否试图访问登录/注册页面
  if (to.meta.requiresGuest && userStore.isLoggedIn) {
    console.log('已登录用户访问登录页，跳转到首页')
    next('/')
    return
  }
  
  next()
})

export default router

