# Student Management System (教务管理系统)

基于 Spring Boot 3 + Vue 3 的前后端分离后台管理系统。
支持国际化、JWT 认证、动态路由与权限控制。

## 🛠 技术栈
- **Backend**: Spring Boot 3, MyBatis-Plus, JWT, MySQL
- **Frontend**: Vue 3, Element Plus, Axios, Vue Router, i18n

## ✨ 主要功能
1. **用户认证**: 登录、注册、修改密码 (MD5加密 + JWT令牌)
2. **业务管理**: 学生/教师信息的增删改查
3. **国际化**: 支持 中文 / English / 日本語 切换
4. **安全机制**: 统一异常处理、拦截器鉴权

## 🚀 快速开始
1. 导入数据库 `student.sql`
2. 后端修改 `application.yml` 中的数据库密码
3. 前端运行 `npm install` && `npm run dev`
