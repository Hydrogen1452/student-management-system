package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Course;
import com.example.demo.entity.StudentCourse;
import com.example.demo.mapper.CourseMapper;
import com.example.demo.mapper.StudentCourseMapper;
import com.example.demo.service.StudentCourseService;
import com.example.demo.service.CourseService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentCourseServiceImpl extends ServiceImpl<StudentCourseMapper, StudentCourse> implements StudentCourseService {
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private StudentCourseMapper studentCourseMapper;
    @Resource
    private CourseService courseService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class) // 👈 核心！
    public void selectCourse(Integer studentId, Integer courseId) {

        // 1. 查课程 (这里为了演示并发，我们手动写 SQL 查，不用 MP 的 getById)
        Course course = courseMapper.selectById(courseId);
        if (course.getCurrentCount() >= course.getMaxCount()) {
            throw new RuntimeException("课程已满");
        }

        // 2. 查重
        long exists = count(
                new LambdaQueryWrapper<StudentCourse>()
                        .eq(StudentCourse::getStudentId, studentId)
                        .eq(StudentCourse::getCourseId, courseId)
        );
        if (exists > 0) {
            throw new RuntimeException("已经选过了");
        }

        // 3. 插入记录
        StudentCourse sc = new StudentCourse();
        sc.setStudentId(studentId);
        sc.setCourseId(courseId);
        studentCourseMapper.insert(sc);

        // 4. 【核心优化】使用 SQL 乐观锁更新
        // 我们需要在 CourseService 里写一个自定义 update 方法，或者用 Wrapper
        boolean updateResult = courseService.update(
                new LambdaUpdateWrapper<Course>()
                        .setSql("current_count = current_count + 1") // 每次 +1
                        .eq(Course::getId, courseId)
                        .lt(Course::getCurrentCount, course.getMaxCount()) // 核心条件：只有没满的时候才加
        );

        if (!updateResult) {
            throw new RuntimeException("手慢了，没抢到！");
            // 抛出异常会触发 @Transactional 回滚，刚才插入的记录也会被撤销！
        }

        // 操作成功后，删除课程列表的缓存
        // 这样下次查询列表时，就会重新去数据库拉取最新的“剩余名额”
        stringRedisTemplate.delete("course:list");
    }

    public void deleteCourse(Integer studentId, Integer courseId) {
        // 1. 先查有没有这条记录 (防止重复退课)
        QueryWrapper<StudentCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId).eq("course_id", courseId);

        long count = baseMapper.selectCount(wrapper);
        if (count == 0) {
            throw new RuntimeException("你没选这门课，退啥？");
        }

        // 2. 删除选课记录
        baseMapper.delete(wrapper);

        // 3. 归还库存 (current_count - 1)
        // 依然用 SQL 更新，防止并发覆盖，并且加一个 current_count > 0 的兜底条件
        boolean updateResult = courseService.update(
                new LambdaUpdateWrapper<Course>()
                        .setSql("current_count = current_count - 1")
                        .eq(Course::getId, courseId)
                        .gt(Course::getCurrentCount, 0) // 👈 兜底：不能减成负数
        );

        if (!updateResult) {
            throw new RuntimeException("退课失败，系统异常");
        }

        // 4. 删缓存！(别忘了)
        stringRedisTemplate.delete("course:list");
    }
}

