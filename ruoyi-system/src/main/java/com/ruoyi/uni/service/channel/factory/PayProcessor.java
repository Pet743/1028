package com.ruoyi.uni.service.channel.factory;

import com.ruoyi.uni.config.PayChannelConfig;
import com.ruoyi.alse.domain.PaymentResultDTO;

import com.ruoyi.uni.model.Enum.PayChannelEnum;

import java.math.BigDecimal;
import java.util.Map;

public interface PayProcessor {

    /**
     * 获取支持的支付通道
     */
    PayChannelEnum getSupportedChannel();

    /**
     * 处理支付
     * @param amount 支付金额
     * @param orderNo 订单号
     * @param channelConfig 通道配置
     * @return 支付结果
     */
    PaymentResultDTO processPayment(BigDecimal amount, String orderNo, PayChannelConfig channelConfig);

    /**
     * 处理支付回调
     * @param params 回调参数
     * @return 处理结果
     */
    boolean handlePaymentCallback(Map<String, String> params);

    /**
     * 从回调参数中获取订单号
     */
    String getOrderNoFromCallback(Map<String, String> params);
}
