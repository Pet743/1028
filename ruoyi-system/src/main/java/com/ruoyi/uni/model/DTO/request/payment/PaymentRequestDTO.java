package com.ruoyi.uni.model.DTO.request.payment;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PaymentRequestDTO {

    @NotNull(message = "订单总金额不能为空")
    private String totalMoney;


    @NotBlank(message = "sessionKey不能为空")
    private String sessionKey;

}
