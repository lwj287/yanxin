package com.yanxin.recruit.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    // 动态生成密钥，每次重启服务都会改变
    private String secret;

    @Value("${jwt.expireMinutes}")
    private Long expireMinutes;

    @PostConstruct
    public void init() {
        // 在 Bean 初始化时生成一个随机且足够长的字符串作为密钥
        this.secret = UUID.randomUUID().toString() + UUID.randomUUID().toString();
    }

    public String createToken(Long userId, String username, String role) {
        long now = System.currentTimeMillis();
        Date expireAt = new Date(now + expireMinutes * 60 * 1000);
        return Jwts.builder()
                .setSubject(username)
                .claim("uid", userId)
                .claim("role", role)
                .setIssuedAt(new Date(now))
                .setExpiration(expireAt)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
