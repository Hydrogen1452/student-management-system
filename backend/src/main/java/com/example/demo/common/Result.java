package com.example.demo.common;

public class Result<T> {
    private Integer code; // 200成功，其他失败
    private String msg;   // 提示消息
    private T data;       // 数据（因为不知道是Student还是List，所以用T代表万能类型）

    // --- 1. 成功的包装方法 ---
    // 如果只需要返回数据，调用这个
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.msg = "success";
        result.data = data;
        return result;
    }

    // 如果只是通知成功，不需要返回数据，调用这个
    public static <T> Result<T> success() {
        return success(null);
    }

    // --- 2. 失败的包装方法 ---
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.code = 500; // 这里的500代表业务出错
        result.msg = msg;
        return result;
    }

    // --- Getter 和 Setter (必须有！) ---
    // 用 Alt+Insert 生成，或者直接复制下面
    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
}
