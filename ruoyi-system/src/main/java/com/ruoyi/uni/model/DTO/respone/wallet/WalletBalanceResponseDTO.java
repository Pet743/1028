package com.ruoyi.uni.model.DTO.respone.wallet;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("钱包余额响应DTO")
public class WalletBalanceResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("钱包余额")
    private String walletBalance;

    @ApiModelProperty("总收入")
    private String totalIncome;

    @ApiModelProperty("总支出")
    private String totalExpense;

    @ApiModelProperty("余额是否正确")
    private Boolean balanceCorrect;
}