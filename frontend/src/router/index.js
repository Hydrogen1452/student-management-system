import { createRouter, createWebHistory } from 'vue-router'
import AppLayout from '@/layout/AppLayout.vue'

// 1. 定义路由规则：访问什么路径，显示什么组件
const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login.vue') // 你的登录组件路径
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/Register.vue')
    },
    {
        path: '/',
        component: AppLayout, // 根路径显示 Layout
        redirect: '/student', // 默认跳到学生页
        children: [ // 子路由：显示在 Layout 的 <router-view> 里
            {
                path: 'student', // 访问 /student
                name: 'StudentManage',
                component: () => import('@/views/StudentManage.vue')
            },
            {
                path: 'teacher', // 访问 /teacher
                name: 'TeacherManage',
                component: () => import('@/views/TeacherManage.vue')
            }
        ]
    }
]

// 2. 创建路由实例
const router = createRouter({
    history: createWebHistory(), // 使用 HTML5 模式
    routes
})

// 3. 【路由守卫】(前端的安检员)
// 每次切换页面前，都会经过这里
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')

    // 如果要去的地方不是登录页，且没有 token(排除注册页)
    if (to.path !== '/login' && to.path !== '/register' && !token) {
        next('/login')
    } else {
        next()
    }
})

export default router