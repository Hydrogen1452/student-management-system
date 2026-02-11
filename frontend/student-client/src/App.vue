<template>
  <!-- 路由出口：电梯到了几楼，这里就显示几楼的内容 -->
    <router-view />
</template>

<script setup>
// 这里什么都不用写了！
// 之前的 checkLogin 逻辑，我们已经转移到了 router/index.js 的“路由守卫”里了
</script>


<style scoped>
.main {
  text-align: center;
  margin-top: 50px;
}
</style>

<!-- 3. style: 这里写 CSS 样式  -->
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