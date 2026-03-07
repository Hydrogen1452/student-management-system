package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@TableName("student_course")
public class StudentCourse {
        @TableId(type = IdType.AUTO)
        private Integer id;
        private Integer studentId;
        private Integer courseId;
        // 1. 定义类型为 LocalDateTime
        // 2. 加这个注解，不然前端收到的可能是个奇怪的数组 [2024, 2, 9, ...]
        //    或者带 T 的字符串 2024-02-09T12:00:00
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private LocalDateTime createTime;

        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public Integer getStudentId() {
                return studentId;
        }

        public void setStudentId(Integer studentId) {
                this.studentId = studentId;
        }

        public Integer getCourseId() {
                return courseId;
        }

        public void setCourseId(Integer courseId) {
                this.courseId = courseId;
        }

        public LocalDateTime getCreateTime() {
                return createTime;
        }

        public void setCreateTime(LocalDateTime createTime) {
                this.createTime = createTime;
        }
}
