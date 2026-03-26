package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Notice;

import java.util.List;

public interface NoticeService extends IService<Notice>{
    // 必须要在这里声明一下，实现类里的 @Override 才会生效
    List<Notice> getAllNoticesSorted();
}
