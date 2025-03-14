package com.ruoyi.web.controller.tool;

import com.ruoyi.common.exception.TokenException;
import com.ruoyi.uni.util.JwtUtil;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class TokenValidationAspect {

    private final HttpServletRequest request;

    public TokenValidationAspect(HttpServletRequest request) {
        this.request = request;
    }

    // 前置通知，在方法执行前进行token校验
    @Before("execution(public * com.ruoyi.*.controller..*(..)) && !@annotation(com.ruoyi.annotation.NoTokenValidation)")
    public void validateToken() {
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            throw new TokenException("Token 缺失");
        }

        // 这里假设你有一个验证 Token 的方法，验证 Token 是否有效
        boolean isValid = JwtUtil.isValidToken(token);  // 假设 JwtUtil.isValidToken 方法验证 token
        if (!isValid) {
            throw new TokenException("Token 无效或已过期");
        }
    }
}