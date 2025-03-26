package com.ruoyi.alse.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentResultDTO {
    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 支付金额
     */
    private BigDecimal totalAmount;

    /**
     * 支付状态
     */
    private Integer paymentStatus;

    /**
     * 支付链接
     */
    private String paymentUrl;

    /**
     * 跳转类型: 1-链接跳转, 2-表单提交, 3-二维码
     */
    private Integer redirectType;

    /**
     * 通道ID
     */
    private String channelId;
}