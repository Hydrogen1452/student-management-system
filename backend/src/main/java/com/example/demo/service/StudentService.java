package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Student;

// IService 是 MyBatis-Plus 提供的通用 Service 接口，里面有 save, update, list 等方法
public interface StudentService extends IService<Student> {
    // 这里暂时是空的，因为 IService 已经帮我们定义好了所有基础方法
}