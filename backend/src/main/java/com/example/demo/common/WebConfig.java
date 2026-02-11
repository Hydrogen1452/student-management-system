package com.example.demo.common;

import com.example.demo.common.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 👈 这一行很重要，告诉 Spring 这是个配置类
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册我们刚才写的安检员
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")  // 1. 拦截所有路径
                .excludePathPatterns("/login", "/register"); // 2. 放行登录和注册 (不然连登录都登不进去了)
    }
}