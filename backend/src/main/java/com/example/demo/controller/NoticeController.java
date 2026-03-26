package com.example.demo.controller;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.demo.common.Result;
import com.example.demo.entity.Notice;
import com.example.demo.service.NoticeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/notice")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final String NOTICE_LIST_KEY = "notice:list";

    @GetMapping("/list")
    public Result<List<Notice>> list() {
        // 1. 先查 Redis
        String jsonStr = stringRedisTemplate.opsForValue().get(NOTICE_LIST_KEY);

        // 2. 如果 Redis 有数据，直接转成 List 返回 (命中缓存)
        if (StringUtils.isNotBlank(jsonStr)) {
            // 把 JSON 字符串转回 List<Notice> 对象
            // 这里推荐用 Fastjson2，假设你用的是 Fastjson2
            List<Notice> list = JSON.parseArray(jsonStr, Notice.class);
            System.out.println("查询走缓存了！🚀");
            // 你得调用你自己写的那个带排序的方法啊！
            return Result.success(noticeService.getAllNoticesSorted());
        }

        // 3. 如果 Redis 没有，去查 MySQL
        List<Notice> list = noticeService.getAllNoticesSorted();

        // 4. 查到后，存入 Redis (建立缓存)
        if (list != null && !list.isEmpty()) {
            // 把 List 转成 JSON 字符串存进去
            String json = JSON.toJSONString(list);
            // 设置过期时间 1 小时 (避免数据一直不更新)
            stringRedisTemplate.opsForValue().set(NOTICE_LIST_KEY, json, 1, TimeUnit.HOURS);
            System.out.println("查询走数据库了，已写入缓存！🐢");
        }

        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<String> addNotice(@RequestBody Notice notice) {
        boolean success = noticeService.save(notice);
        if (success) {
            stringRedisTemplate.delete(NOTICE_LIST_KEY);
            return Result.success("插入成功，ID: " + notice.getId());
        } else {
            return Result.error("插入失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteNotice(@PathVariable Integer id) {
        // Service 里的方法叫 removeById
        boolean success = noticeService.removeById(id);
        if (success) {
            // 👇 【核心】只有数据库真的删了，才去删缓存
            // 为什么要删缓存？防止数据库没了，Redis 里还有旧数据（脏读）
            stringRedisTemplate.delete(NOTICE_LIST_KEY);
            return Result.success(); // 也就是 code=200, msg="success"
        } else {
            return Result.error("删除失败，可能没有这个ID");
        }
    }

    @PutMapping("/update")
    public Result<String> updateNotice(@RequestBody Notice notice) {
        // updateById 会返回一个 int，代表“影响了多少行数据”
        boolean success = noticeService.updateById(notice);
        if (success) {
            stringRedisTemplate.delete(NOTICE_LIST_KEY);
            return Result.success();
        } else {
            return Result.error("更新失败");
        }
    }

}

