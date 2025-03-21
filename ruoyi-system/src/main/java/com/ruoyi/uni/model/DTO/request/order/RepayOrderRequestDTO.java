package com.ruoyi.uni.model.DTO.request.order;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class RepayOrderRequestDTO implements Serializable {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "订单ID不能为空")
    private Long orderId;
}