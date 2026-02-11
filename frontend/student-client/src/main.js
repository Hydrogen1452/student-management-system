// 1. 引入 Vue 这个框架的核心功能
// 以前你是用 <script src="vue.js"> 引入，现在是用 import 导入。
// { createApp } 是 Vue 提供的“造物主”方法，专门用来创建一个应用实例。
import { createApp } from 'vue'

// 2. 引入 Element Plus 的样式
// 注意：这里没有 var xxx = ...，直接 import '...css'。
// 意思是：把这个 CSS 文件加载进来，作用于全局。
// 就像你在 HTML 里写 <link rel="stylesheet"> 一样。
import 'element-plus/dist/index.css'

// 3. 引入 App.vue 组件
// 这里的 App 是你自己写的那个 App.vue 文件。
// 它是整个网站的“根组件”（老祖宗）。
import App from './App.vue'

// 4. 引入 Element Plus 的主程序
// 这是一个巨大的插件包，里面装满了 el-button, el-table 等组件。
import ElementPlus from 'element-plus'
import router from "@/router/index.js";
import i18n from './lang'

// --- 准备工作做完了，开始干活 ---

// 5. 创建应用实例
// 用“造物主”方法，基于 App.vue 这个骨架，捏出了一个活生生的 app 对象。
const app = createApp(App)

// 6. 安装插件 (最关键的一步)
// app.use(...) 意思是：给这个 app 安装一个“扩展包”。
// 这里就是告诉 Vue：“我要用 Element Plus，请把它的所有组件都注册好，我要随叫随用。”
app.use(ElementPlus)
//  启用翻译功能
app.use(i18n)
// 挂载路由
app.use(router)
// 7. 挂载 (Mount)
// app.mount('#app') 意思是：把捏好的这个 app，贴到 index.html 里 id="app" 的那个 div 上去。
// 从这一刻起，Vue 接管了页面，浏览器开始显示内容。
app.mount('#app')
