package com.ruoyi.uni.service.channel;

import com.ruoyi.alse.domain.PaymentResultDTO;

import java.math.BigDecimal;
import java.util.Map;

public interface UniPayService {

    /**
     * 创建支付订单
     * @param amount 金额
     * @param channelCode 支付通道代码
     * @return 支付结果
     */
    PaymentResultDTO createPayment(BigDecimal amount, String channelCode, String remark);

    /**
     * 处理支付回调
     * @param channelCode 支付通道代码
     * @param params 回调参数
     * @return 处理结果
     */
    boolean handlePaymentCallback(String channelCode, Map<String, String> params);
}
