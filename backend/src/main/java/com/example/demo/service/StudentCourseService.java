package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.StudentCourse;

public interface StudentCourseService extends IService<StudentCourse> {
    // 定义一个选课方法
    void selectCourse(Integer studentId, Integer courseId);
    // 定义一个退课方法
    void deleteCourse(Integer studentId, Integer courseId);
}

