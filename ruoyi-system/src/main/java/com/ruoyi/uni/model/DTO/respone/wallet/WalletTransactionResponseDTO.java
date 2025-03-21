package com.ruoyi.uni.model.DTO.respone.wallet;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class WalletTransactionResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("交易ID")
    private Long transactionId;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("交易金额")
    private String transactionAmount;  // 改为String类型

    @ApiModelProperty("收入金额")
    private String incomeAmount;  // 改为String类型

    @ApiModelProperty("支出金额")
    private String expenseAmount;  // 改为String类型

    @ApiModelProperty("支付方式编码")
    private Integer paymentMethod;

    @ApiModelProperty("支付方式描述")
    private String paymentMethodDesc;

    @ApiModelProperty("交易类型编码")
    private Integer transactionType;

    @ApiModelProperty("交易类型描述")
    private String transactionTypeDesc;

    @ApiModelProperty("交易时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date transactionTime;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("备注")
    private String remark;
}