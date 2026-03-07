package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.StudentCourse;
import com.example.demo.mapper.StudentCourseMapper;
import com.example.demo.service.StudentCourseService;
import org.springframework.stereotype.Service;


@Service
public class StudentCourseServiceImpl extends ServiceImpl<StudentCourseMapper, StudentCourse> implements StudentCourseService {
}
