package com.example.demo.common;

import com.example.demo.common.interceptor.LoginInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 👈 这一行很重要，告诉 Spring 这是个配置类
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate; // 👈 注入 Redis

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册我们刚才写的安检员
        registry.addInterceptor(new LoginInterceptor(stringRedisTemplate))
                .addPathPatterns("/**")  // 1. 拦截所有路径
                .excludePathPatterns(
                        "/login",
                        "/register",
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-ui.html", // 旧版路径
                        "/swagger-ui/**",   // 新版 UI 资源
                        "/v3/api-docs/**"   // API 定义 JSON
                        ); // 2. 放行登录和注册 (不然连登录都登不进去了)
    }
}