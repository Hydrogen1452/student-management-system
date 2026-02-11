package com.example.demo.common.interceptor;

import org.springframework.util.StringUtils;
import com.example.demo.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;


public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 【新增】放行 OPTIONS 请求 (CORS 预检)
        // 浏览器发真正的请求前，会先发一个 OPTIONS 探路，这个探路请求不带 Token，必须放行
        if("OPTIONS".equals(request.getMethod().toUpperCase())) {
            return true;
        }

        // 1. 获取请求头中的 token
        String token = request.getHeader("token");

        // 2. 如果 token 是空的，直接拦截
        // 用 !StringUtils.hasText(token) (检查不为 null 且不仅仅是空格)
        if (!StringUtils.hasText(token)) {
            response.setStatus(401); // 设置状态码
            response.getWriter().write("401 Unauthorized: Token is missing");
            return false; // false 表示拦截，不让进 Controller
        }

        // 3. 验证 token 是否伪造或过期
        try {
            // 解析 token，如果报错说明是假的或过期的
            Claims claims = JwtUtils.parseToken(token);

            // 可以把解析出来的用户信息存进 request，给 Controller 用 (可选)
            request.setAttribute("currentUser", claims);

            return true; // 放行，请进！
        } catch (Exception e) {
            response.setStatus(401);
            response.getWriter().write("401 Unauthorized: Token is invalid");
            return false; // 拦截
        }
    }
}