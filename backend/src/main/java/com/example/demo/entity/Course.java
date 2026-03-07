package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("course")
public class Course {
        @TableId(type = IdType.AUTO)
        private Integer id;
        private String name;
        private Integer score;
        private Integer teacherId;
        private Integer maxCount;
        private Integer currentCount;

        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public Integer getScore() {
                return score;
        }

        public void setScore(Integer score) {
                this.score = score;
        }

        public Integer getTeacherId() {
                return teacherId;
        }

        public void setTeacherId(Integer teacherId) {
                this.teacherId = teacherId;
        }

        public Integer getMaxCount() {
                return maxCount;
        }

        public void setMaxCount(Integer maxCount) {
                this.maxCount = maxCount;
        }

        public Integer getCurrentCount() {
                return currentCount;
        }

        public void setCurrentCount(Integer currentCount) {
                this.currentCount = currentCount;
        }
}
