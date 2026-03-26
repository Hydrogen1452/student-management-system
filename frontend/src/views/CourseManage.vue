<template>
  <div style="margin: 50px auto; width: 800px;">
    <h1>课程列表</h1>

    <el-button type="primary" @click="loadData">刷新数据</el-button>
    <el-button type="primary" plain @click="openAddDialog">添加课程</el-button>

    <br><br>

    <!-- el-table: 专门展示数据的表格 -->
    <!-- :data="students" 意思是：把 students 数组给表格，它自动遍历 -->
    <el-table :data="courses" border style="width: 100%">

      <!-- prop="id": 对应 Student 对象的 id 属性 -->
      <el-table-column prop="id" label="ID" sortable width="80" />

      <!-- prop="name": 对应 Student 对象的 name 属性 -->
      <el-table-column prop="name" label="姓名" width="180" />

      <!-- prop="name": 对应 Student 对象的 name 属性 -->
      <el-table-column prop="score" label="学分" />
      <!-- prop="age": 对应 Student 对象的 age 属性 -->
      <el-table-column prop="teacherName" label="授课教师" />

      <!-- prop="age": 对应 Student 对象的 age 属性 -->
      <el-table-column prop="maxCount" label="人数" />

      <!-- 操作列 -->
      <el-table-column label="操作" width="180">
        <!-- #default="scope" 是 Vue 的插槽语法，
             scope.row 就代表当前这一行的学生对象 (student) -->
        <template #default="scope">

          <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">
            删除
          </el-button>
          <el-button type="primary" size="small" @click="handleEdit(scope.row)">
            编辑
          </el-button>

        </template>
      </el-table-column>

    </el-table>

    <!--编辑表格的弹窗-->
    <!-- :title 绑定一个变量 -->
    <el-dialog v-model="dialogFormVisible" :title="dialogTitle" width="500">
      <el-form :model="form" rules="rules">
        <el-form-item label="课程名称" :label-width="formLabelWidth">
          <el-input v-model="form.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="授课老师" :label-width="formLabelWidth">
          <!-- 绑定 form.teacherId -->
          <el-select v-model="form.teacherId" placeholder="请选择老师">
            <!-- 循环显示老师名字，但背后存的是 ID -->
            <el-option
                v-for="item in teachers"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学分" :label-width="formLabelWidth">
          <el-input v-model="form.score" autocomplete="off" />
        </el-form-item>
        <el-form-item label="人数" :label-width="formLabelWidth">
          <el-input v-model.number="form.maxCount" type="maxCount"autocomplete="off"></el-input>
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



// 获取当前用户信息
const userStr = localStorage.getItem('user')
// 防止没登录或者数据坏了报错，加个默认值
const user = ref(userStr ? JSON.parse(userStr) : {})


const teachers = ref([])

// 加载所有老师（复用你之前的接口）
const loadTeachers = () => {
  request.get('/teacher/list').then(res => {
    teachers.value = res.data
  })
}

//列表展示模块
// 定义一个响应式变量，用来存数据
// 初始值是个空数组
const courses = ref([])

// 定义请求数据的函数
const loadData = () => {
  // 发送 GET 请求给 Java
  request.get('/course/list')
      .then(res => {
        // res.data 是 Axios 包的一层壳
        // res.data.data 才是我们后端 Result 里写的 data (那个 List)
        console.log("Java 返回的数据：", res.data)

        // 把拿到的数据赋值给 courses，页面会自动刷新
        courses.value = res.data
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
        request.delete(`/course/${id}`)
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
const dialogTitle = ref('添加课程') // 默认标题
const dialogFormVisible = ref(false)
const formLabelWidth = '140px'
const form = reactive({
  id: null,
  name: '',       // 课程名
  score: '',      // 学分
  maxCount: '',   // 👈 注意：C 要大写！跟后端对应
  teacherId: ''   // 👈 重点！我们要存老师的 ID，而不是名字
})

// 定义添加函数
const handleSave = () => { // 建议把 handleAdd 改名为 handleSave
  if (!form.name || !form.score || !form.maxCount || !form.teacherId) {
    ElMessage.error('请将表单填写完整')
    return
  }

  // --- 分支判断 ---
  if (form.id) {
    // A. 有 ID -> 修改 (PUT)
    // 注意：后端要写一个 @PutMapping("/teacher/update") 的接口
    // 但为了省事，如果你后端还没写 update 接口，这里先演示逻辑
    // 假设你后端有一个 saveOrUpdate 或者你复用 add 接口(如果后端支持)
    // *正规做法：发 PUT 请求*
    request.put('/course/update', form)
        .then(handleResponse) // 封装一下回调
  } else {
    // B. 无 ID -> 新增 (POST)
    request.post('/course/add', form)
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
  dialogTitle.value = '修改教师'

  // 2. 回显数据 (关键！)
  // 把当前行的数据 (row) 复制到表单 (form) 里
  // 注意：一定要有 id，这样后端才知道是修改谁
  form.id = row.id
  form.name = row.name
  form.score = row.score
  form.maxCount = row.maxCount
  form.teacherId = row.teacherId

  // 3. 打开弹窗
  dialogFormVisible.value = true
}

const openAddDialog = () => {
  // 1. 改标题
  dialogTitle.value = '添加课程'

  // 2. 清空表单 (重要！)
  form.id = null
  form.name = ''
  form.score = null
  form.maxcount = null
  form.teacherId = null

  // 3. 打开弹窗
  dialogFormVisible.value = true
}

// 别忘了在 onMounted 里调用它
onMounted(() => {
  loadData()     // 查课程
  loadTeachers() // 查老师 (为了给下拉框用)
})
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