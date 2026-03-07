package com.example.demo.controller;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.demo.entity.Teacher;
import com.example.demo.common.Result;
import com.example.demo.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@Tag(name = "教师管理")
@RestController
public class TeacherController {

   @Autowired
    private TeacherService teacherService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 定义缓存的 Key (方便管理)
    private static final String TEACHER_LIST_KEY = "teacher:list";

    @Operation(summary = "查询所有教师")
    @GetMapping("/teacher/list")
    public Result<List<Teacher>> List() {
        // 1. 先查 Redis
        String jsonStr = stringRedisTemplate.opsForValue().get(TEACHER_LIST_KEY);

        // 2. 如果 Redis 有数据，直接转成 List 返回 (命中缓存)
        if (StringUtils.isNotBlank(jsonStr)) {
            // 把 JSON 字符串转回 List<Teacher> 对象
            // 这里推荐用 Fastjson2，假设你用的是 Fastjson2
            List<Teacher> list = JSON.parseArray(jsonStr, Teacher.class);
            System.out.println("查询走缓存了！🚀");
            return Result.success(list);
        }

        // 3. 如果 Redis 没有，去查 MySQL
        List<Teacher> list = teacherService.list();

        // 4. 查到后，存入 Redis (建立缓存)
        if (list != null && !list.isEmpty()) {
            // 把 List 转成 JSON 字符串存进去
            String json = JSON.toJSONString(list);
            // 设置过期时间 1 小时 (避免数据一直不更新)
            stringRedisTemplate.opsForValue().set(TEACHER_LIST_KEY, json, 1, TimeUnit.HOURS);
            System.out.println("查询走数据库了，已写入缓存！🐢");
        }

        return Result.success(list);
    }

    @Operation(summary = "删除教师")
    @DeleteMapping("/teacher/{id}")
    public Result<String> deleteTeacher(@PathVariable Integer id) {
        boolean success = teacherService.removeById(id);
        if(success){
            // 👇 【核心】删缓存！让下次查询重新去数据库拉最新数据
            stringRedisTemplate.delete(TEACHER_LIST_KEY);
            return Result.success("删除成功");
        }else{
            return Result.error("删除失败");
        }
    }

    @Operation(summary = "添加教师")
    @PostMapping("/teacher/add")
    public Result<String> addTeacher(@RequestBody Teacher teacher) {
        boolean success = teacherService.save(teacher);
        if(success){
            stringRedisTemplate.delete(TEACHER_LIST_KEY);
            return Result.success("插入成功，ID: "+ teacher.getId());
        }else{
            return Result.error("插入失败");
        }
    }

    @Operation(summary = "修改教师信息")
    @PutMapping("/teacher/update")
    public Result<String> updateTeacher(@RequestBody Teacher teacher) {
        boolean success = teacherService.updateById(teacher);
        if(success){
            stringRedisTemplate.delete(TEACHER_LIST_KEY);
            return Result.success();
        }else{
            return Result.error("更新失败");
        }
    }
}
