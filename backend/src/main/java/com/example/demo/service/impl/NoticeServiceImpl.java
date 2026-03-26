package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Notice;
import com.example.demo.mapper.NoticeMapper;
import com.example.demo.service.NoticeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Override
    public List<Notice> getAllNoticesSorted() {
        // 使用 MyBatis-Plus 的 LambdaQueryWrapper 直接在数据库层面排序
        return this.list(new LambdaQueryWrapper<Notice>()
                .orderByDesc(Notice::getCreateTime)); // 按创建时间倒序
    }
}


