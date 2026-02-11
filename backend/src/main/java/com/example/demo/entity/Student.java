package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

// 1. @TableName: 告诉 MyBatis-Plus，这个类对应数据库里的哪张表
@TableName("student")
public class Student {

    // 2. @TableId: 告诉它哪个是主键(ID)
    // type = IdType.AUTO 意思是：ID 是数据库自动生成的(自增)，Java 不用管
    @TableId(type = IdType.AUTO)

    // 以后用了 Lombok 插件可以不写下面这些，但今天先手动写一下 getter/setter
    // 快捷键提示：在 IDEA 里按 Alt + Insert -> Getter and Setter -> 全选 -> OK
    // 或者你直接把这两个变量设为 public (像上面那样)，为了教学方便先这样写。
    // 1. 这里的 private 意思是“私有的”，外面的人不能直接碰到这两个变量
    private Integer id;
    private String name;
    private Integer age;
    private String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}