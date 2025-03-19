package com.ruoyi.uni.model.DTO.request.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("实名认证请求DTO")
public class VerifyIdentityRequestDTO {

    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;

    @ApiModelProperty(value = "真实姓名", required = true)
    private String realName;

    @ApiModelProperty(value = "身份证号码", required = true)
    private String idCardNo;

    @ApiModelProperty(value = "身份证正面图片路径", required = true)
    private String idCardFrontImg;

    @ApiModelProperty(value = "身份证反面图片路径", required = true)
    private String idCardBackImg;
}