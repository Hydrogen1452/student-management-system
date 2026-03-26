package com.example.demo.common.interceptor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import com.example.demo.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;


public class LoginInterceptor implements HandlerInterceptor {

    // 👇 1. 定义一个变量接收 Redis
    private StringRedisTemplate stringRedisTemplate;

    // 👇 2. 通过构造函数传进来
    public LoginInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

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
            JwtUtils.parseToken(token);
        } catch (Exception e) {
            response.setStatus(401);
            response.getWriter().write("401 Unauthorized: Token is invalid");
            return false; // 拦截
        }

        //  4. 【核心新增】去 Redis 查 Token 是否存在/过期
        String redisKey = "login:token:" + token;
        String userId = stringRedisTemplate.opsForValue().get(redisKey);

        if (userId == null) {
            // Redis 里没查到，说明过期了，或者用户已经登出了
            response.setStatus(401);
            response.getWriter().write("401 Unauthorized: Token expired");
            return false; // 拦截！
        }

        // 5. 【可选优化】自动续期
        // 如果用户还在操作，就给他续命 30 分钟
        stringRedisTemplate.expire(redisKey, 30, TimeUnit.MINUTES);

        return true; // 放行
    }
}