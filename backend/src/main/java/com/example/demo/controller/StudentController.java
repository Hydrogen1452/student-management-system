package com.example.demo.controller;
import java.util.List;

import com.example.demo.entity.Student;
import com.example.demo.common.Result;
import com.example.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
public class StudentController {

    // 现在注入的是“厨师” (Service)，而不是“采购员” (Mapper)
    @Autowired
    private StudentService studentService;

    // 注意返回值变成了 Result<List<Student>>
    @GetMapping("/student/list")
    public Result<List<Student>> list() {
        List<Student> students = studentService.list();
        // 用盒子包装起来返回
        return Result.success(students);
    }

    @PostMapping("/student/add")
    public Result<String> addStudent(@RequestBody Student student) {
        boolean success = studentService.save(student);
        if (success) {
            return Result.success("插入成功，ID: " + student.getId());
        } else {
            return Result.error("插入失败");
        }
    }

    @DeleteMapping("/student/{id}")
    public Result<String> deleteStudent(@PathVariable Integer id) {
        // Service 里的方法叫 removeById
        boolean success = studentService.removeById(id);
        if (success) {
            return Result.success(); // 也就是 code=200, msg="success"
        } else {
            return Result.error("删除失败，可能没有这个ID");
        }
    }

    @PutMapping("/student/update")
    public Result<String> updateStudent(@RequestBody Student student) {
        // updateById 会返回一个 int，代表“影响了多少行数据”
        boolean success = studentService.updateById(student);
        if (success) {
            return Result.success();
        } else {
            return Result.error("更新失败");
        }
    }
}




