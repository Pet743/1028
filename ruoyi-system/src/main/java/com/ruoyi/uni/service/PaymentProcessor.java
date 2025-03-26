package com.ruoyi.uni.service;

import com.ruoyi.alse.domain.AlseOrder;
import com.ruoyi.alse.domain.AlseProduct;
import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.uni.model.DTO.respone.order.PaymentResultOrderDTO;

import java.util.Map;

/**
 * 支付处理器接口
 */
public interface PaymentProcessor {

    /**
     * 获取支持的支付方式
     */
    Integer getSupportedPaymentMethod();

    /**
     * 处理支付
     * @param order 订单信息
     * @param product 商品信息
     * @param buyer 买家信息
     * @return 支付结果
     */
    PaymentResultOrderDTO processPayment(AlseOrder order, AlseProduct product, AlseUser buyer);

    /**
     * 处理支付回调
     * @param params 回调参数
     * @return 处理结果
     */
    boolean handlePaymentCallback(Map<String, String> params);
}