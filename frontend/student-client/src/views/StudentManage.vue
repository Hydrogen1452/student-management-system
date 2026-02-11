<template>
  <div style="margin: 50px auto; width: 800px;">
    <h1>{{ $t('student.title') }}</h1>

    <el-button type="primary" @click="loadData">{{ $t('student.refresh') }}</el-button>
    <el-button type="primary" plain @click="openAddDialog">{{ $t('student.add') }}</el-button>

    <br><br>

    <!-- el-table: 专门展示数据的表格 -->
    <!-- :data="students" 意思是：把 students 数组给表格，它自动遍历 -->
    <el-table :data="students" border style="width: 100%">

      <!-- prop="id": 对应 Student 对象的 id 属性 -->
      <el-table-column prop="id" label="ID" sortable width="80" />

      <!-- prop="name": 对应 Student 对象的 name 属性 -->
      <el-table-column prop="name" label="姓名" width="180" />

      <!-- prop="age": 对应 Student 对象的 age 属性 -->
      <el-table-column prop="age" label="年龄" />

      <!-- prop="gender": 对应 Student 对象的 gender 属性 -->
      <el-table-column prop="gender" label="性别" width="100" />

      <!-- 操作列 -->
      <el-table-column label="操作" width="180">
        <!-- #default="scope" 是 Vue 的插槽语法，
             scope.row 就代表当前这一行的学生对象 (student) -->
        <template #default="scope">

          <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">
            {{ $t('student.table.delete') }}
          </el-button>
          <el-button type="primary" size="small" @click="handleEdit(scope.row)">
            {{ $t('student.table.edit') }}
          </el-button>

        </template>
      </el-table-column>

    </el-table>

    <!--编辑表格的弹窗-->
    <!-- :title 绑定一个变量 -->
    <el-dialog v-model="dialogFormVisible" :title="dialogTitle" width="500">
      <el-form :model="form" rules="rules">
        <el-form-item label="学生姓名" :label-width="formLabelWidth">
          <el-input v-model="form.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="年龄" :label-width="formLabelWidth">
          <el-input v-model.number="form.age" type="number"autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="性别" :label-width="formLabelWidth">
          <el-radio-group v-model="form.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">Cancel</el-button>
          <el-button type="primary" @click="handleSave">
            提交
          </el-button>
        </div>
      </template>
    </el-dialog>

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
import { ref, onMounted, reactive } from 'vue'
import request from "@/utils/request"; //  改用 request
import { ElMessage, ElMessageBox } from "element-plus";
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

// 修改密码模块
// 1. 获取当前用户信息
const userStr = localStorage.getItem('user')
// 防止没登录或者数据坏了报错，加个默认值
const user = ref(userStr ? JSON.parse(userStr) : {})

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


//列表展示模块
// 定义一个响应式变量，用来存学生数据
// 初始值是个空数组
const students = ref([])

// 定义请求数据的函数
const loadData = () => {
  // 发送 GET 请求给 Java
  request.get('/student/list')
      .then(res => {
        // res.data 是 Axios 包的一层壳
        // res.data.data 才是我们后端 Result 里写的 data (那个 List)
        console.log("Java 返回的数据：", res.data)

        // 把拿到的数据赋值给 students，页面会自动刷新
        students.value = res.data
      })
      .catch(err => {
        alert("请求失败，请检查后端是否启动！")
        console.error(err)
      })
}

// 删除模块
// 删除逻辑
const handleDelete = (id) => {
  // 1. 弹出确认框 (防止误删)
  ElMessageBox.confirm(
      '你确定要删除吗？', // 提示内容
      '警告', // 标题
      {
        confirmButtonText: 'ok',
        cancelButtonText: 'cancel',
        type: 'warning',
      }
  )
      .then(() => {
        // 2. 如果用户点了“确定”，才发请求给 Java
        request.delete(`/student/${id}`)
            .then(res => {
              if (res.code === 200) {
                // 3. 成功后，提示一下
                ElMessage.success('删除成功')
                // 4. 关键一步：重新加载数据！(局部刷新)
                loadData()
              } else {
                ElMessage.error(res.msg)
              }
            })
      })
      .catch(() => {
        // 用户点了取消，啥也不干
      })
}

// 保存模块
const dialogTitle = ref('添加学生') // 默认标题
const dialogFormVisible = ref(false)
const formLabelWidth = '140px'
const form = reactive({
  id: null, // 新增了 id 字段
  name: '',
  age: '',
  gender: ''
})

// 定义添加函数
const handleSave = () => { // 建议把 handleAdd 改名为 handleSave
  if (!form.name || !form.age) {
    ElMessage.error('请输入姓名和年龄')
    return
  }

  // --- 分支判断 ---
  if (form.id) {
    // A. 有 ID -> 修改 (PUT)
    // 注意：后端要写一个 @PutMapping("/student/update") 的接口
    // 但为了省事，如果你后端还没写 update 接口，这里先演示逻辑
    // 假设你后端有一个 saveOrUpdate 或者你复用 add 接口(如果后端支持)
    // *正规做法：发 PUT 请求*
    request.put('/student/update', form)
        .then(handleResponse) // 封装一下回调
  } else {
    // B. 无 ID -> 新增 (POST)
    request.post('/student/add', form)
        .then(handleResponse)
  }
}

// 抽取的公共回调函数
const handleResponse = (res) => {
  if (res.code === 200) {
    ElMessage.success('操作成功')
    dialogFormVisible.value = false
    loadData()
  } else {
    ElMessage.error(res.msg)
  }
}

const handleEdit = (row) => {
  // 1. 改标题
  dialogTitle.value = '修改学生'

  // 2. 回显数据 (关键！)
  // 把当前行的数据 (row) 复制到表单 (form) 里
  // 注意：一定要有 id，这样后端才知道是修改谁
  form.id = row.id
  form.name = row.name
  form.age = row.age

  // 3. 打开弹窗
  dialogFormVisible.value = true
}

const openAddDialog = () => {
  // 1. 改标题
  dialogTitle.value = '添加学生'

  // 2. 清空表单 (重要！)
  form.id = null
  form.name = ''
  form.age = null

  // 3. 打开弹窗
  dialogFormVisible.value = true
}

</script>

<style scoped>
.main {
  text-align: center;
  margin-top: 50px;
}
</style>

<!-- 3. style: 这里写 CSS 样式 (页面的衣服) -->
<style scoped>
.main {
  text-align: center;
  margin-top: 50px;
  color: #2c3e50;
}
button {
  padding: 10px 20px;
  background-color: #42b983;
  color: white;
  border: none;
  cursor: pointer;
}
</style>
