<template>
  <div class="common-layout">
    <el-container>

      <!-- 1. 左侧菜单栏 -->
      <el-aside width="200px" class="aside-menu">
        <div class="logo">管理系统</div>

        <!-- router: 开启路由模式，index 就是 path -->
        <el-menu
            background-color="#2F4037"
            text-color="#BFCBD9"
            active-text-color="#42b983"
            :default-active="$route.path"
            router
            style="border: none;"
        >
          <el-menu-item index="/dashboard">
            <el-icon><Notebook /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <!-- 菜单项：选课管理 -->
          <el-menu-item index="/notice">
            <el-icon><Notebook /></el-icon>
            <span>公告管理</span>
          </el-menu-item>
          <!-- 菜单项：学生管理 -->
          <el-menu-item index="/student">
            <el-icon><User /></el-icon>
            <span>学生管理</span>
          </el-menu-item>

          <!-- 菜单项：教师管理 -->
          <el-menu-item index="/teacher">
            <el-icon><Avatar /></el-icon>
            <span>教师管理</span>
          </el-menu-item>

          <!-- 菜单项：选课中心 -->
          <el-menu-item index="/select-course">
            <el-icon><collection /></el-icon>
            <span>选课中心</span>
          </el-menu-item>

          <!-- 菜单项：选课管理 -->
          <el-menu-item index="/course">
            <el-icon><Notebook /><collection /></el-icon>
            <span>选课管理</span>
          </el-menu-item>

        </el-menu>
      </el-aside>

      <el-container>
        <!-- 2. 顶部 Header -->
        <el-header class="header">
          <div class="header-left">
            <!-- 这里可以放面包屑，暂时留空 -->
          </div>
          <div class="header-right">
            <!-- 昵称和退出按钮 (把 StudentManage 里的那部分搬过来) -->
            <span>你好，{{ user.username || user.nickname }}</span>
            <el-button type="text" style="color: white; margin-left: 10px;" @click="logout">退出</el-button>
            <el-button type="text" style="color: white; margin-left: 10px;" @click="openPassDialog">修改密码</el-button>
          </div>
        </el-header>

        <!-- 3. 中间内容区 (关键！) -->
        <el-main>
          <!-- 这里显示 StudentManage 或 TeacherManage -->
          <router-view />
        </el-main>
      </el-container>

    </el-container>

    <!-- 修改密码的弹窗 -->
    <el-dialog v-model="passDialogVisible" title="修改密码" width="400px">
      <el-form :model="passForm" label-width="100px">
        <el-form-item label="原密码">
          <el-input v-model="passForm.oldPassword" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passForm.newPassword" type="password" show-password></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive  } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { User, Avatar, Collection, Notebook} from '@element-plus/icons-vue' // 需要安装图标库，如果没有先忽略icon
import { ElMessage } from 'element-plus'
import request from '@/utils/request' // 👈 加上这行！


const router = useRouter()
const userStr = localStorage.getItem('user')
const user = ref(userStr ? JSON.parse(userStr) : {})

const logout = () => {
  // 1. 先发请求通知后端 (调用刚才写的 /logout 接口)
  // 注意：这里不用传参数，因为 request.js 会自动把 token 放在 header 里带过去
  request.post('/logout').then(res => {
    // 不管后端成功失败，前端都要清空
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    ElMessage.success('安全退出')
    router.push('/login')
  }).catch(err => {
    // 万一断网了，也要强制退出
    localStorage.removeItem('token')
    router.push('/login')
  })
}

// 修改密码模块
// 2. 修改密码相关变量
const passDialogVisible = ref(false)
const passForm = reactive({
  oldPassword: '',
  newPassword: ''
})

// 打开弹窗
const openPassDialog = () => {
  passForm.oldPassword = ''
  passForm.newPassword = ''
  passDialogVisible.value = true
}

// 提交修改
const savePassword = () => {
  if (!passForm.oldPassword || !passForm.newPassword) {
    ElMessage.warning("密码不能为空")
    return
  }

  // 构造发给后端的参数
  const data = {
    username: user.value.username, // 从缓存里拿用户名
    oldPassword: passForm.oldPassword,
    newPassword: passForm.newPassword
  }

  request.put('/password', data).then(res => {
    if (res.code === 200) {
      ElMessage.success("修改成功，请重新登录")
      logout() // 修改成功后，强制退出，让用户重新登录
    } else {
      ElMessage.error(res.msg)
    }
  })
}

</script>

<style scoped>
.common-layout, .el-container {
  height: 100vh;
}

.aside-menu {
  background-color: #2F4037;
  color: white;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-weight: bold;
  font-size: 20px;
  color: white;
  border-bottom: 1px solid #42b983;
}

.header {
  background-color: #42b983; /* ✅ 主题绿 */
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1); /* 加点阴影更有层次感 */
}

/* 去掉菜单右侧的锯齿边框 */
.el-menu {
  border-right: none;
}
</style>