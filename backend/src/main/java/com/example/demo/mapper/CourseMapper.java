package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.example.demo.entity.Course;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}
