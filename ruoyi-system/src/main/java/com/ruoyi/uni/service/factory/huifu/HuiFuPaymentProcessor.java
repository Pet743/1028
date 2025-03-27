package com.ruoyi.uni.service.factory.huifu;

import com.ruoyi.alse.domain.AlseOrder;
import com.ruoyi.alse.domain.AlseProduct;
import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.uni.model.DTO.respone.order.PaymentResultOrderDTO;
import com.ruoyi.uni.service.PaymentProcessor;

import java.util.Map;

public class HuiFuPaymentProcessor implements PaymentProcessor {
    @Override
    public Integer getSupportedPaymentMethod() {
        return null;
    }

    @Override
    public PaymentResultOrderDTO processPayment(AlseOrder order, AlseProduct product, AlseUser buyer) {
        return null;
    }

    @Override
    public boolean handlePaymentCallback(Map<String, String> params) {
        return false;
    }
}
