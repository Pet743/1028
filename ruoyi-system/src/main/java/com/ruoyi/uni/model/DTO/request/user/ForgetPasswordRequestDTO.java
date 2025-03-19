package com.ruoyi.uni.model.DTO.request.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("忘记密码请求DTO")
public class ForgetPasswordRequestDTO {

    @ApiModelProperty("用户名（手机号或邮箱）")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @ApiModelProperty("验证码")
    @NotBlank(message = "验证码不能为空")
    private String verifyCode;

    @ApiModelProperty("新密码")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;

    @ApiModelProperty("确认新密码")
    @NotBlank(message = "确认新密码不能为空")
    private String confirmNewPassword;
}