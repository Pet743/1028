package com.ruoyi.uni.model.DTO.request.order;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("创建订单请求")
public class CreateOrderRequestDTO {
    @NotNull(message = "用户ID不能为空")
    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;

    @NotNull(message = "商品ID不能为空")
    @ApiModelProperty(value = "商品ID", required = true)
    private Long productId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量最小为1")
    @ApiModelProperty(value = "购买数量", required = true)
    private Long quantity;

    @NotNull(message = "支付方式不能为空")
    @ApiModelProperty(value = "支付方式(1:支付宝 2:微信 3:银行卡 4:钱包)", required = true)
    private Integer paymentMethod;

    @ApiModelProperty(value = "支付账号")
    private String paymentAccount;

    @NotNull(message = "收货地址ID不能为空")
    @ApiModelProperty(value = "收货地址ID", required = true)
    private Long shippingAddressId;

    @ApiModelProperty(value = "订单备注")
    private String remark;
}