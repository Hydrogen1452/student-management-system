import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建一个 axios 实例
const request = axios.create({
    baseURL: 'http://localhost:8080', // 统一的基础路径
    timeout: 5000
})

// 1. 请求拦截器：每次发请求前，先拦截一下
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';

    // 【核心】从浏览器仓库拿 Token
    const token = localStorage.getItem('token')
    if (token) {
        config.headers['token'] = token;  // 把它塞进请求头
    }

    return config
}, error => {
    return Promise.reject(error)
})

// 2. 响应拦截器：收到数据后，先过滤一下
request.interceptors.response.use(response => {
    let res = response.data;
    // 兼容性处理
    if (typeof res === 'string') {
        res = res ? JSON.parse(res) : res
    }
    return res;
}, error => {
    if (error.response.status === 401) {
        // 只要后端说票是假的，立马踢回登录页
        localStorage.removeItem('token');
        window.location.href = '/login'
    }
    return Promise.reject(error)
})

export default request