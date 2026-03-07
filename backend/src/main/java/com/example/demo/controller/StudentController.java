package com.example.demo.controller;
import java.util.List;
import com.example.demo.entity.Student;
import com.example.demo.common.Result;
import com.example.demo.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import java.util.concurrent.TimeUnit;

@Tag(name = "学生管理") // 👈 这一组接口叫什么名字

@RestController
public class StudentController {

    // 现在注入的是“厨师” (Service)，而不是“采购员” (Mapper)
    @Autowired
    private StudentService studentService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 定义缓存的 Key (方便管理)
    private static final String STUDENT_LIST_KEY = "student:list";

    @Operation(summary = "查询所有学生") // 👈 这个接口干嘛的
    @GetMapping("/student/list")
    public Result<List<Student>> list() {
        // 1. 先查 Redis
        String jsonStr = stringRedisTemplate.opsForValue().get(STUDENT_LIST_KEY);

        // 2. 如果 Redis 有数据，直接转成 List 返回 (命中缓存)
        if (StringUtils.isNotBlank(jsonStr)) {
            // 把 JSON 字符串转回 List<Student> 对象
            // 这里推荐用 Fastjson2，假设你用的是 Fastjson2
            List<Student> list = JSON.parseArray(jsonStr, Student.class);
            System.out.println("查询走缓存了！🚀");
            return Result.success(list);
        }

        // 3. 如果 Redis 没有，去查 MySQL
        List<Student> list = studentService.list();

        // 4. 查到后，存入 Redis (建立缓存)
        if (list != null && !list.isEmpty()) {
            // 把 List 转成 JSON 字符串存进去
            String json = JSON.toJSONString(list);
            // 设置过期时间 1 小时 (避免数据一直不更新)
            stringRedisTemplate.opsForValue().set(STUDENT_LIST_KEY, json, 1, TimeUnit.HOURS);
            System.out.println("查询走数据库了，已写入缓存！🐢");
        }

        return Result.success(list);
    }

    @Operation(summary = "添加学生")
    @PostMapping("/student/add")
    public Result<String> addStudent(@RequestBody Student student) {
        boolean success = studentService.save(student);
        if (success) {
            // 👇 【核心】删缓存！让下次查询重新去数据库拉最新数据
            stringRedisTemplate.delete(STUDENT_LIST_KEY);
            return Result.success("插入成功，ID: " + student.getId());
        } else {
            return Result.error("插入失败");
        }
    }

    @Operation(summary = "删除学生")
    @DeleteMapping("/student/{id}")
    public Result<String> deleteStudent(@PathVariable Integer id) {
        // Service 里的方法叫 removeById
        boolean success = studentService.removeById(id);
        if (success) {
            // 👇 【核心】只有数据库真的删了，才去删缓存
            // 为什么要删缓存？防止数据库没了，Redis 里还有旧数据（脏读）
            stringRedisTemplate.delete(STUDENT_LIST_KEY);
            return Result.success(); // 也就是 code=200, msg="success"
        } else {
            return Result.error("删除失败，可能没有这个ID");
        }
    }

    @Operation(summary = "修改学生信息")
    @PutMapping("/student/update")
    public Result<String> updateStudent(@RequestBody Student student) {
        // updateById 会返回一个 int，代表“影响了多少行数据”
        boolean success = studentService.updateById(student);
        if (success) {
            // 👇 【核心】删缓存！
            stringRedisTemplate.delete(STUDENT_LIST_KEY);
            return Result.success();
        } else {
            return Result.error("更新失败");
        }
    }
}




