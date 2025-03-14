package com.ruoyi.uni.model.DTO.request.user;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String username;        // 用户名（手机号或邮箱）
    private String password;        // 密码
    private String confirmPassword; // 确认密码

}
