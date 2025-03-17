package com.ruoyi.uni.model.DTO.respone.order;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("订单详情信息")
public class OrderDetailResponseDTO extends OrderResponseDTO {

    @ApiModelProperty(value = "关联钱包流水ID")
    private Long walletTransactionId;

    @ApiModelProperty(value = "订单备注")
    private String remark;
}