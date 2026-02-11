<template>
  <div class="login-box">
        <!-- 🌐 国际化下拉菜单 -->
        <div style="text-align: right; margin-bottom: 20px;">
          <el-dropdown @command="changeLang">
        <span class="el-dropdown-link" style="cursor: pointer; color: #409EFF; display: flex; align-items: center; justify-content: flex-end;">
          <!-- 这里放个简单的 emoji 地球，或者你可以写文字 -->
          🌐 {{ $t('language') }}
          <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
            <!-- 下拉菜单内容 -->
            <template #dropdown>
              <el-dropdown-menu>
                <!-- command 属性就是传给函数的值 -->
                <el-dropdown-item command="zh">中文</el-dropdown-item>
                <el-dropdown-item command="en">English</el-dropdown-item>
                <el-dropdown-item command="ja">日本語</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

    <!-- 2. 标题替换 -->
    <h2 style="text-align: center">{{ $t('login.title') }}</h2>

    <el-form :model="form" label-width="100px"> <!-- label宽度稍微调大点，因为日语有的词长 -->
      <!-- 3. 表单 Label 替换 -->
      <el-form-item :label="$t('login.username')">
        <el-input v-model="form.username" placeholder="admin"></el-input>
      </el-form-item>

      <el-form-item :label="$t('login.password')">
        <el-input v-model="form.password" type="password" placeholder="123456"></el-input>
      </el-form-item>

      <el-form-item>
        <!-- 4. 按钮文字替换 -->
        <el-button type="primary" style="width: 100%" @click="login">
          {{ $t('login.btn') }}
        </el-button>
        <!-- 4. 注册 -->
        <div style="text-align: right; margin-top: 10px">
          <el-link type="warning" @click="$router.push('/register')">没有账号？去注册</el-link>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import request from "@/utils/request.js"; // 👈 引入刚才写的工具
import { ElMessage } from "element-plus";
import {useRouter} from "vue-router";
import { useI18n } from 'vue-i18n'



const router = useRouter() // 获取路由器
const { locale, t } = useI18n() //  解构出 locale (控制语言) 和 t (翻译函数)

// 定义父组件传过来的事件，相当于“回调”
const form = reactive({
  username: '',
  password: ''
})

// 3. 切换语言的函数
//  (Element Plus 的 dropdown 会自动把 command 传进来)
const changeLang = (lang) => {
  locale.value = lang
  localStorage.setItem('lang', lang) // 记住用户的选择
  ElMessage.success('Switch Language: ' + lang) // 提示一下
}


const login = () => {
  request.post('/login', form).then(res => {
    if (res.code === 200) {
      // t('login.success') 就是去字典里查词
      ElMessage.success(t('login.success'))
      // 1. 存 Token (以前写的)
      localStorage.setItem('token', res.data.token)
      // 2. 【新增】存用户信息 (把整个 user 对象存进去，方便取昵称)
      // 注意：localStorage 只能存字符串，所以要用 JSON.stringify 包一下
      localStorage.setItem('user', JSON.stringify(res.data))
      router.push('/')
    } else {
      ElMessage.error(res.msg)
    }
  })
}

</script>

<style scoped>
.login-box {
  width: 400px;
  margin: 150px auto;
  padding: 40px;
  border: 1px solid #ddd;
  border-radius: 10px;
  box-shadow: 0 0 10px #ddd;
}
</style>