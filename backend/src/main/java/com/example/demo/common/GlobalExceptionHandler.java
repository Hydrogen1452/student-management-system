package com.example.demo.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 1. @RestControllerAdvice:
// 意思是：我是“全局大管家”，所有 Controller 里的异常都归我管。
// 这里的 RestControllerAdvice = ControllerAdvice + ResponseBody，
// 表示处理完异常后，直接返回 JSON 数据，而不是跳转页面。
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 2. @ExceptionHandler(Exception.class):
    // 意思是：不管出了什么错（只要是 Exception 的子类），都进这个方法处理。
    // e 就是那个具体的错误信息（比如“/ by zero”）。
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {

        // 打印错误日志到控制台（方便程序员自己排查，定位问题，但不要给用户看）
        e.printStackTrace();

        // 3. 返回优雅的 JSON 给用户
        // 告诉用户“服务器出错了”，并附带具体的错误信息
        return Result.error("服务器内部错误：" + e.getMessage());
    }
}
