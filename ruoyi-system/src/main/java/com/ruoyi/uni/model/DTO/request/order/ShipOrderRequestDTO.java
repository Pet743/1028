package com.ruoyi.uni.model.DTO.request.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("订单发货请求")
public class ShipOrderRequestDTO {

    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;

    @ApiModelProperty(value = "订单ID", required = true)
    private Long orderId;

    @NotBlank(message = "物流公司不能为空")
    @ApiModelProperty(value = "物流公司", required = true)
    private String logisticsCompany;

    @NotBlank(message = "物流单号不能为空")
    @ApiModelProperty(value = "物流单号", required = true)
    private String logisticsNo;
}