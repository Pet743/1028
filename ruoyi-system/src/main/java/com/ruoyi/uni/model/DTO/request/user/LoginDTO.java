package com.ruoyi.uni.model.DTO.request.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoginDTO {
    @ApiModelProperty(value = "用户名（手机号或邮箱）", required = true)
    private String username;

    @ApiModelProperty(value = "密码", required = true)
    private String password;
}