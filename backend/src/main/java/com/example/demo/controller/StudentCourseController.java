package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.common.Result;
import com.example.demo.entity.Course;
import com.example.demo.entity.StudentCourse;
import com.example.demo.service.CourseService;
import com.example.demo.service.StudentCourseService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
        Integer studentId = sc.getStudentId();
        Integer courseId = sc.getCourseId();

        // 1. 检查课程是否存在
        Course course = courseService.getById(courseId);
        if (course == null) {
            return Result.error("课程不存在");
        }

        // 2. 检查名额是否满了
        if (course.getCurrentCount() >= course.getMaxCount()) {
            return Result.error("手慢了，课程已满！");
        }

        // 3. 检查是否重复选课
        // select count(*) from student_course where student_id = ? and course_id = ?
        long count = studentCourseService.count(
                new LambdaQueryWrapper<StudentCourse>()
                        .eq(StudentCourse::getStudentId, studentId)
                        .eq(StudentCourse::getCourseId, courseId)
        );
        if (count > 0) {
            return Result.error("你已经选过这门课了");
        }

        // 4. 【核心动作】
        // 4.1 插入选课记录
        studentCourseService.save(sc);

        // 4.2 课程已选人数 +1
        course.setCurrentCount(course.getCurrentCount() + 1);
        courseService.updateById(course);

        // 删掉课程列表的缓存，强制下次刷新去查最新的数据库
        stringRedisTemplate.delete("course:list");

        return Result.success("选课成功！");
    }
}