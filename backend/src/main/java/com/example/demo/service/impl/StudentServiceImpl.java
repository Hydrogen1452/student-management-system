package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Student;
import com.example.demo.mapper.StudentMapper;
import com.example.demo.service.StudentService;
import org.springframework.stereotype.Service;

// 1. @Service: 告诉 Spring，我是厨师(业务逻辑层)，请把我注册到容器里
// 2. extends ServiceImpl<Mapper, Entity>: 这是 MyBatis-Plus 的黑科技。
//    它自动帮你把 Mapper 和 Service 连起来了，你不用自己去写 studentMapper.insert() 了。
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    // 目前这里可以一行代码都不写！
    // 因为父类 ServiceImpl 已经帮你实现了 增删改查 的所有逻辑。
}
