package com.example.demo.utils;// 你的包名

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    // 1. 这是一个“私钥”
    // 就像皇帝玉玺的缺口，只有服务器知道。
    // 黑客如果想伪造 Token，因为他不知道这串字符，造出来的 Token 只要一验就露馅。
    // 在真实项目中，这个应该写在配置文件里。
    private static final String SECRET_KEY = "MyJapanCareer2024";

    // 过期时间：12小时 (毫秒单位)
    private static final long EXPIRE_TIME = 12 * 60 * 60 * 1000;

    /**
     * 生成 Token (盖章)
     * @param claims 你想在印章里藏什么信息（比如用户名、用户ID）
     * @return 字符串类型的 Token
     */


    // 2. 生成 Token 的方法
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                // 3. 把你给的信息（用户名、ID）写在印章里
                .setClaims(claims)
                // 4. 设定有效期（比如12小时后，这个印章就作废了）
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                // 5. 【最关键的一步】盖章！
                // 使用 HS512 算法，配合那个只有我们知道的 SECRET_KEY 进行加密签名。
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                // 6. 压缩成一串字符串返回
                .compact();
    }

    // 在 JwtUtils 类里加这个方法
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY) // 刚才定义的那个密钥
                .parseClaimsJws(token)
                .getBody();
    }
}
