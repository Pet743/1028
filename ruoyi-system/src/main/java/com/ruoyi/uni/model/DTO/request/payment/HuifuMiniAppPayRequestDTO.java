package com.ruoyi.uni.model.DTO.request.payment;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 汇付微信小程序支付请求DTO
 */
@Data
@ApiModel("汇付微信小程序支付请求")
public class HuifuMiniAppPayRequestDTO {

    @NotBlank(message = "支付金额不能为空")
    @ApiModelProperty("支付金额")
    private String totalAmount;

}