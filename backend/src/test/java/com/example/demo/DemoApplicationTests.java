package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Test
	void testRedis() {
		// 1. 存数据 (key, value)
		redisTemplate.opsForValue().set("my_name", "Hydrogen1452");
		System.out.println("存进去了！");

		// 2. 取数据
		String value = redisTemplate.opsForValue().get("my_name");
		System.out.println("取出来了： " + value);

		// 3. 验证
		assert "Hydrogen1452".equals(value);
	}
}
