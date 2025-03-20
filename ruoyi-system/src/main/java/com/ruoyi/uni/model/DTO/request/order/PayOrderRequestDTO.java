package com.ruoyi.uni.model.DTO.request.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("支付订单请求")
public class PayOrderRequestDTO {

    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;

    @ApiModelProperty(value = "订单ID", required = true)
    private Long orderId;

    @NotNull(message = "支付方式不能为空")
    @ApiModelProperty(value = "支付方式(1:支付宝 2:微信 3:银行卡 4:钱包)", required = true)
    private Integer paymentMethod;

    @ApiModelProperty(value = "支付账号")
    private String paymentAccount;
}