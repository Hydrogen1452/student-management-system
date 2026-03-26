<template>
  <div class="dashboard-container">

    <!-- 1. 数据卡片区 -->
    <div class="card-group">
      <el-card class="box-card" style="background: #409EFF">
        <div class="card-header">学生总数</div>
        <div class="card-num">120</div>
      </el-card>
      <el-card class="box-card" style="background: #67C23A">
        <div class="card-header">教师总数</div>
        <div class="card-num">15</div>
      </el-card>
      <el-card class="box-card" style="background: #E6A23C">
        <div class="card-header">课程总数</div>
        <div class="card-num">8</div>
      </el-card>
      <el-card class="box-card" style="background: #F56C6C">
        <div class="card-header">选课人次</div>
        <div class="card-num">350</div>
      </el-card>
    </div>

    <!-- 2. 下半部分：最新公告 -->
    <div style="margin-top: 20px;">
      <el-card>
        <template #header>
          <span>📢 最新公告</span>
        </template>
        <!-- 这里可以复用你的 Notice 表格，但把操作列去掉 -->
        <el-table :data="notices" border style="width: 100%">

              <el-table-column prop="title" label="标题" width="180" />

              <el-table-column prop="content" label="内容" width="180" />

              <!-- prop="author": 对应 Notice 对象的 author 属性 -->
              <el-table-column prop="author" label="发布者" />

              <!-- prop="createTime": 对应 Notice 对象的 createTime 属性 -->
              <el-table-column prop="createTime" label="发布时间" />
        </el-table>
      </el-card>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import request from "@/utils/request"; //  改用 request
import Login from "@/views/Login.vue"; //  引入登录组件
import { useRouter } from 'vue-router'

const router = useRouter() //  初始化

// --- 登录状态控制 ---
const isLogin = ref(false)

// 检查是否已登录
const checkLogin = () => {
  const token = localStorage.getItem('token')
  if (token) {
    isLogin.value = true
    loadData() // 如果有 token，直接加载数据
  } else {
    isLogin.value = false
  }
}

// 3. 【关键】页面加载完毕后，立刻执行检查
onMounted(() => {
  checkLogin()
})

// 登录成功后的回调
const onLoginSuccess = () => {
  isLogin.value = true
  loadData()
}


// 获取当前用户信息
const userStr = localStorage.getItem('user')
// 防止没登录或者数据坏了报错，加个默认值
const user = ref(userStr ? JSON.parse(userStr) : {})


//列表展示模块
// 定义一个响应式变量，用来存数据
// 初始值是个空数组
const notices = ref([])

// 定义请求数据的函数
const loadData = () => {
  // 发送 GET 请求给 Java
  request.get('/notice/list')
      .then(res => {
        // res.data 是 Axios 包的一层壳
        // res.data.data 才是我们后端 Result 里写的 data (那个 List)
        console.log("Java 返回的数据：", res.data)

        // 把拿到的数据赋值给 students，页面会自动刷新
        notices.value = res.data
      })
      .catch(err => {
        alert("请求失败")
        console.error(err)
      })
}


</script>
<style scoped>
.card-group {
  display: flex;
  justify-content: space-between;
  gap: 20px;
}
.box-card {
  width: 25%;
  color: white;
  text-align: center;
}
.card-header {
  font-size: 16px;
  opacity: 0.8;
}
.card-num {
  font-size: 30px;
  font-weight: bold;
  margin-top: 10px;
}
</style>