package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.SysUser;
import com.example.demo.common.Result;
import com.example.demo.service.SysUserService;
import com.example.demo.utils.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private SysUserService sysUserService;

    // 登录接口
    // 接收一个 SysUser 对象 (里面包含 username 和 password)
    @PostMapping("/login")
    public Result<SysUser> login(@RequestBody SysUser user) {
        // 1. 查用户
        SysUser dbUser = sysUserService.getOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, user.getUsername())
        );

        if (dbUser == null) {
            return Result.error("用户名不存在");
        }

        // 2. 校验密码 (国际通用写法)
        // Spring 的 DigestUtils.md5DigestAsHex 需要 byte[] 数组，所以要用 .getBytes()
        // 这里的逻辑是：拿到明文 -> 转字节 -> MD5加密 -> 转16进制字符串
        String inputPasswordMd5 = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());

        // 3. 比对 (不变)
        if (!inputPasswordMd5.equals(dbUser.getPassword())) {
            return Result.error("密码错误");
        }

        // --- 登录成功后的新逻辑 ---

        // 1. 准备要藏在 Token 里的信息
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", dbUser.getId());
        claims.put("username", dbUser.getUsername());

        // 2. 生成 Token
        String token = JwtUtils.generateToken(claims);

        // 3. 把 Token 和 用户信息一起返回给前端
        // 我们在 dbUser 对象里可能没有 token 字段，
        // 简单粗暴的做法：用 Map 或者新建一个 LoginVo 类返回。
        // 为了省事，我们这里直接把 token 放进 Result 的 data 里，
        // 或者让 SysUser 暂时加个 @TableField(exist=false) 的 token 字段。

        // 把生成的 token 塞进这个临时的口袋里
        dbUser.setToken(token);
        dbUser.setPassword(null);

        // 把 dbUser 返回给前端
        // 前端收到的 JSON 就会是： { "id":1, "username":"admin", "token":"xxxxx" }
        return Result.success(dbUser);
    }

    //注册接口
    @PostMapping("/register")
    public Result<SysUser> register(@RequestBody SysUser user) {
        // 1. 校验参数是否为空 (简单校验)
        if (user.getUsername() == null || user.getPassword() == null) {
            return Result.error("账号或密码不能为空");
        }

        // 2. 查重：去数据库看看有没有同名的
        // select count(*) from sys_user where username = 'xxx'
        long count = sysUserService.count(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, user.getUsername())
        );

        if (count > 0) {
            return Result.error("用户名已存在，请换一个");
        }

        // 3. 密码加密 (这一步最重要！不然存进去是明文，登录时是密文，永远登不上)
        // 使用 Spring 自带的 DigestUtils
        String md5Pwd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(md5Pwd);

        // 4. 设置默认昵称 (可选)
        if (user.getNickname() == null) {
            user.setNickname("普通用户");
        }

        // 5. 存入数据库
        sysUserService.save(user);

        return Result.success(null);
    }

    /**
     * 修改密码接口
     * 参数 Map 包含: username, oldPassword, newPassword
     */
    @PutMapping("/password")
    public Result<SysUser> updatePassword(@RequestBody Map<String, String> map) {
        String username = map.get("username");
        String oldPassword = map.get("oldPassword");
        String newPassword = map.get("newPassword");

        // 1. 参数校验
        if (username == null || oldPassword == null|| newPassword == null){
            return Result.error("参数不能为空");
        }

        // 2. 查询用户
        SysUser dbUser = sysUserService.getOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
        if (dbUser == null) {
            return Result.error("用户不存在");
        }

        // 3. 校验旧密码 (注意：数据库里是加密的，所以要把输入的旧密码也加密后再比对)
        String oldMd5 = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (!oldMd5.equals(dbUser.getPassword())) {
            return Result.error("原密码错误");
        }

        // 4. 更新新密码
        String newMd5 = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        dbUser.setPassword(newMd5);
        sysUserService.updateById(dbUser);

        return Result.success(null);
    }
}