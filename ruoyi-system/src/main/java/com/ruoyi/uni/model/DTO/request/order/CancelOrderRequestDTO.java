package com.ruoyi.uni.model.DTO.request.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("取消订单请求")
public class CancelOrderRequestDTO {

    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;

    @ApiModelProperty(value = "订单ID", required = true)
    private Long orderId;
}