<template>
  <div style="padding: 20px;">
    <h2>选课中心</h2>

    <!-- 课程列表 -->
    <el-table :data="courses" border style="width: 100%">
      <el-table-column prop="name" label="课程名称" />
      <el-table-column prop="score" label="学分" width="80" />
      <el-table-column prop="teacherName" label="授课老师" />

      <!-- 显示剩余名额 -->
      <el-table-column label="名额情况" width="150">
        <template #default="scope">
          {{ scope.row.currentCount }} / {{ scope.row.maxCount }}
        </template>
      </el-table-column>

      <!-- 操作列 -->
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <!-- 核心交互：选课按钮 -->
          <!-- 情况 A：我已经选过这门课了 -> 显示红色的“退课”按钮 -->
          <el-button
              v-if="myCourseIds.includes(scope.row.id)"
              type="danger"
              size="small"
              @click="deleteCourse(scope.row.id)"
          >
            退课
          </el-button>

          <!-- 情况 B：没选过 -> 显示“选课”或“已满” -->
          <el-button
              v-else
              type="primary"
              size="small"
              :disabled="scope.row.currentCount >= scope.row.maxCount"
              @click="selectCourse(scope.row.id)"
          >
            {{ scope.row.currentCount >= scope.row.maxCount ? '已满' : '选课' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from "@/utils/request"
import { ElMessage } from 'element-plus'

const courses = ref([])

// 加载课程列表
const loadCourses = () => {
  request.get('/course/list').then(res => {
    if (res.code === 200) {
      courses.value = res.data
    }
  })
}

// 选课动作
const selectCourse = (courseId) => {
  // 构造参数
  // 注意：这里我们暂时写死 studentId=1，或者从 localStorage 获取当前登录人的 ID
  const userStr = localStorage.getItem('user')
  const user = userStr ? JSON.parse(userStr) : {}

  const data = {
    studentId: user.id, // 假设 User 表和 Student 表 ID 一致
    courseId: courseId
  }

  request.post('/student-course/select', data).then(res => {
    if (res.code === 200) {
      ElMessage.success("抢到了！")
      loadCourses() // 刷新列表，更新剩余名额
      loadMyCourses() // 刷新我的状态
    } else {
      ElMessage.error(res.msg) // 显示“手慢了”或“已选过”
    }
  })
}

// 退课动作
const deleteCourse = (courseId) => {
  // 构造参数
  // 注意：这里我们暂时写死 studentId=1，或者从 localStorage 获取当前登录人的 ID
  const userStr = localStorage.getItem('user')
  const user = userStr ? JSON.parse(userStr) : {}

  const data = {
    studentId: user.id, // 假设 User 表和 Student 表 ID 一致
    courseId: courseId
  }

  request.post('/student-course/delete', data).then(res => {
    if (res.code === 200) {
      ElMessage.success("退课成功")
      loadCourses() // 刷新列表，更新剩余名额
      loadMyCourses() // 刷新我的状态
    } else {
      ElMessage.error(res.msg) // 显示“手慢了”或“已选过”
    }
  })
}

const myCourseIds = ref([]) // 存我选过的课程ID

// 加载我看过的课
const loadMyCourses = () => {
  // 获取当前登录用户
  const userStr = localStorage.getItem('user')
  const user = userStr ? JSON.parse(userStr) : {}
  // 发请求时用 user.id
  request.get('/student-course/my-list/' + user.id).then(res => {
    myCourseIds.value = res.data // 拿到 [1, 3, 5] 这种数组
  })
}

// 在 onMounted 里调用
onMounted(() => {
  loadCourses()
  loadMyCourses()
})
</script>