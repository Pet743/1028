package com.ruoyi.uni.model.DTO.respone.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户信息响应DTO")
public class UserInfoResponseDTO {

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "用户头像路径")
    private String avatar;

    @ApiModelProperty(value = "默认收款方式(1:支付宝 2:微信 3:银行卡)")
    private Integer paymentMethod;

    @ApiModelProperty(value = "默认收款账号信息")
    private String paymentAccount;

    @ApiModelProperty(value = "绑定的收款方式(JSON格式）")
    private String paymentMethodIds;

    @ApiModelProperty(value = "积分")
    private Long points;

    @ApiModelProperty(value = "认证状态（0-未认证，1-审核中）")
    private String verifyStatus;

    @ApiModelProperty(value = "认证状态描述")
    private String verifyStatusDesc;
}