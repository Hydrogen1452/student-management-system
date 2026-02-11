package com.example.demo.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration: 告诉 Spring Boot 这是一个配置类，启动时要加载它
@Configuration // 1. 告诉 Spring Boot：“这是一份正式的配置文件，启动时请务必生效。”
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        // registry.addMapping("/**")
        // 意思：豪宅里【所有的房间】（接口路径），都适用这条规则。
        // 如果你写 "/student/**"，那就只有学生接口允许访问。
        registry.addMapping("/**")

                // .allowedOriginPatterns("*")
                // 意思：允许【任何人】来访问。
                // "*" 代表通配符。
                // 如果你想严谨一点，可以写 "http://localhost:5173"，那就只允许这个前端来。
                .allowedOriginPatterns("*")

                // .allowedMethods(...)
                // 意思：允许他们做什么动作？
                // GET(看), POST(拿), PUT(改), DELETE(砸)... 统统允许。
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                // .allowCredentials(true)
                // 意思：允许带【身份证】（Cookie/Session）进来吗？
                // true = 允许。这对以后做登录功能很重要。
                .allowCredentials(true)

                // .maxAge(3600)
                // 意思：这张通行证的有效期是 3600 秒（1小时）。
                // 在这1小时内，保安不用每次都跑来问主人“能不能放行”，直接放进去就行（提高性能）。
                .maxAge(3600);
    }
}