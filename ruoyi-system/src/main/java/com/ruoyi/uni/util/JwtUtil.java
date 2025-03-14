package com.ruoyi.uni.util;

import com.ruoyi.common.exception.TokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import io.jsonwebtoken.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY = "32423ED546FGJO2232WEIJF";  // 密钥
    private static final long EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 365;  // 一年（可以根据需求调整）

    // 生成 Token
    public static String createToken(Long userId, String username) {
        return Jwts.builder()
                .setSubject(username)
                .setId(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  // 设置过期时间
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // 解析 Token
    public static Long getUserIdFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userId", Long.class);
        } catch (JwtException e) {
            throw new TokenException("无效的 Token", e);
        }
    }

    // 从请求中获取 token
    public static String getTokenFromRequest() {
        HttpServletRequest request = RequestHolder.getRequest();  // 获取请求
        return request.getHeader("Authorization");  // 假设 token 放在请求头中
    }

    // 检查 token 是否过期
    public static boolean isTokenExpired(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token);  // 解析 token
            Date expiration = claimsJws.getBody().getExpiration();
            return expiration.before(new Date());  // 如果当前时间大于过期时间，则 token 过期
        } catch (Exception e) {
            return true;  // 如果解析失败则视为过期
        }
    }

    // 校验 Token 是否有效（包括是否已过期以及是否能够解析出有效的 Claims）
    public static boolean isValidToken(String token) {
        try {
            return !isTokenExpired(token) && getUserIdFromToken(token) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
