package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Teacher;
import com.example.demo.mapper.TeacherMapper;
import com.example.demo.service.TeacherService;
import org.springframework.stereotype.Service;


@Service
public class TeacherIServiceimpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
}
