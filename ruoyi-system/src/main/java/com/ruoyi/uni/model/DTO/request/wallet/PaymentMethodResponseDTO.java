package com.ruoyi.uni.model.DTO.request.wallet;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("收款方式响应DTO")
public class PaymentMethodResponseDTO {
    @ApiModelProperty("收款方式ID")
    private String paymentMethodId;

    @ApiModelProperty("收款方式类型(1:支付宝 2:微信 3:银行卡)")
    private Integer paymentType;

    @ApiModelProperty("收款方式类型名称")
    private String paymentTypeName;

    @ApiModelProperty("收款人姓名")
    private String payeeName;

    @ApiModelProperty("支付方式账号")
    private String paymentAccount;

    @ApiModelProperty("是否为默认收款方式(0:否 1:是)")
    private Integer isDefault;
}