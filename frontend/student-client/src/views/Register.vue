<template>
  <div class="login-box">
    <h2 style="text-align: center">新用户注册</h2>

    <el-form :model="form" label-width="100px">
      <el-form-item label="用户名">
        <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
      </el-form-item>

      <el-form-item label="密码">
        <el-input v-model="form.password" type="password" placeholder="请输入密码"></el-input>
      </el-form-item>

      <el-form-item label="确认密码">
        <el-input v-model="form.confirmPass" type="password" placeholder="请再次输入密码"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" style="width: 100%" @click="register">注册</el-button>
      </el-form-item>

      <!-- 加个链接跳回登录页 -->
      <div style="text-align: right">
        <el-link type="primary" @click="$router.push('/login')">已有账号？去登录</el-link>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import request from "@/utils/request"
import { ElMessage } from "element-plus"
import { useRouter } from 'vue-router'

const router = useRouter()
const form = reactive({
  username: '',
  password: '',
  confirmPass: ''
})

const register = () => {
  // 1. 前端先校验两次密码是否一致
  if (form.password !== form.confirmPass) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }

  // 2. 发请求
  request.post('/register', form).then(res => {
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login') // 注册完了跳回登录页
    } else {
      ElMessage.error(res.msg)
    }
  })
}
</script>

<style scoped>
/* 样式可以直接复用 Login.vue 的 */
.login-box {
  width: 400px;
  margin: 150px auto;
  padding: 40px;
  border: 1px solid #ddd;
  border-radius: 10px;
  box-shadow: 0 0 10px #ddd;
}
</style>