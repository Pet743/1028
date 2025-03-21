package com.ruoyi.uni.model.DTO.request.wallet;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("收款方式请求DTO")
public class PaymentMethodRequestDTO {
    private Long userId;

    @ApiModelProperty("收款方式ID（新增时不需要传）")
    private String paymentMethodId;

    @ApiModelProperty("收款方式类型(1:支付宝 2:微信 3:银行卡)")
    @NotNull(message = "收款方式类型不能为空")
    private Integer paymentType;

    @ApiModelProperty("收款人姓名")
    @NotBlank(message = "收款人姓名不能为空")
    private String payeeName;

    @ApiModelProperty("支付方式账号")
    @NotBlank(message = "支付方式账号不能为空")
    private String paymentAccount;

    @ApiModelProperty("是否为默认收款方式(0:否 1:是)")
    private Integer isDefault = 0;
}