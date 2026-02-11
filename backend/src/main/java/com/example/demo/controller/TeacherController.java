package com.example.demo.controller;
import java.util.List;

import com.example.demo.entity.Teacher;
import com.example.demo.common.Result;
import com.example.demo.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TeacherController {

   @Autowired
    private TeacherService teacherService;


    @GetMapping("/teacher/list")
    public Result<List<Teacher>> List() {
        // 1. 调用业务层方法查询所有教师信息
        List<Teacher> teacherList = teacherService.list();
        // 2. 返回查询结果
        return Result.success(teacherList);
    }

    @DeleteMapping("/teacher/{id}")
    public Result<String> deleteTeacher(@PathVariable Integer id) {
        boolean success = teacherService.removeById(id);
        if(success){
            return Result.success("删除成功");
        }else{
            return Result.error("删除失败");
        }
    }

    @PostMapping("/teacher/add")
    public Result<String> addTeacher(@RequestBody Teacher teacher) {
        boolean success = teacherService.save(teacher);
        if(success){
            return Result.success("插入成功，ID: "+ teacher.getId());
        }else{
            return Result.error("插入失败");
        }
    }

    @PutMapping("/teacher/update")
    public Result<String> updateTeacher(@RequestBody Teacher teacher) {
        boolean success = teacherService.updateById(teacher);
        if(success){
            return Result.success();
        }else{
            return Result.error("更新失败");
        }
    }
}
