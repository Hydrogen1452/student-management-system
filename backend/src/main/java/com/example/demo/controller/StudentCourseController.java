package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.common.Result;
import com.example.demo.entity.StudentCourse;
import com.example.demo.service.CourseService;
import com.example.demo.service.StudentCourseService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/student-course")
public class StudentCourseController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private StudentCourseService studentCourseService;
    @Resource
    private CourseService courseService;

    // 参数：接收 studentId 和 courseId
    // 可以新建一个 DTO 类，或者直接用 Map，或者用 StudentCourse 实体接收
    @PostMapping("/select")
    public Result select(@RequestBody StudentCourse sc) {
        try {
            studentCourseService.selectCourse(sc.getStudentId(), sc.getCourseId());
            return Result.success("选课成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/my-list/{studentId}")
    public Result myList(@PathVariable Integer studentId) {
        // select * from student_course where student_id = ?
        List<StudentCourse> list = studentCourseService.list(
                new LambdaQueryWrapper<StudentCourse>().eq(StudentCourse::getStudentId, studentId)
        );
        // 只返回课程 ID 的集合
        List<Integer> courseIds = list.stream().map(StudentCourse::getCourseId).collect(Collectors.toList());
        return Result.success(courseIds);
    }

    // 退课接口
    @PostMapping("/delete") // 或者用 DeleteMapping，传 JSON 比较安全
    public Result delete(@RequestBody StudentCourse sc) {
        studentCourseService.deleteCourse(sc.getStudentId(), sc.getCourseId());
        return Result.success("退课成功");
    }
}