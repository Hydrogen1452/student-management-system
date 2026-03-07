package com.example.demo.controller;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.demo.common.Result;
import com.example.demo.entity.Course;
import com.example.demo.service.CourseService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 定义缓存的 Key (方便管理)
    private static final String COURSE_LIST_KEY = "course:list";

    @GetMapping("/course/list")
    public Result<List<Course>> list() {
        // 1. 先查 Redis
        String jsonStr = stringRedisTemplate.opsForValue().get(COURSE_LIST_KEY);

        // 2. 如果 Redis 有数据，直接转成 List 返回 (命中缓存)
        if (StringUtils.isNotBlank(jsonStr)) {
            // 把 JSON 字符串转回 List<Student> 对象
            // 这里推荐用 Fastjson2，假设你用的是 Fastjson2
            List<Course> list = JSON.parseArray(jsonStr, Course.class);
            System.out.println("查询走缓存了！🚀");
            return Result.success(list);
        }

        // 3. 如果 Redis 没有，去查 MySQL
        List<Course> list = courseService.list();

        // 4. 查到后，存入 Redis (建立缓存)
        if (list != null && !list.isEmpty()) {
            // 把 List 转成 JSON 字符串存进去
            String json = JSON.toJSONString(list);
            // 设置过期时间 1 小时 (避免数据一直不更新)
            stringRedisTemplate.opsForValue().set(COURSE_LIST_KEY, json, 1, TimeUnit.HOURS);
            System.out.println("查询走数据库了，已写入缓存！🐢");
        }

        return Result.success(list);
    }
}
