package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Student;
import org.apache.ibatis.annotations.Mapper;

// 1. @Mapper: 告诉 Spring Boot，这是一个专门操作数据库的组件
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    // 2. extends BaseMapper<Student>:
    // 这一句话，你就自动拥有了 insert, delete, update, select 等几十个方法！
    // 哪怕这里面一行代码都不写，你也已经无敌了。
}
