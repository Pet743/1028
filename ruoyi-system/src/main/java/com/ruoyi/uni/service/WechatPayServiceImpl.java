package com.ruoyi.uni.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
public class WechatPayServiceImpl implements WechatPayService {
    @Override
    public Map<String, Object> createOrder(String outTradeNo, String subject, BigDecimal totalAmount) {
        return null;
    }

    @Override
    public String createNativePayOrder(String outTradeNo, String subject, BigDecimal totalAmount) {
        return null;
    }

    @Override
    public String createH5PayOrder(String outTradeNo, String subject, BigDecimal totalAmount, String ip) {
        return null;
    }

    @Override
    public Map<String, String> createJsapiPayOrder(String outTradeNo, String subject, BigDecimal totalAmount, String openId) {
        return null;
    }

    @Override
    public Map<String, Object> queryOrder(String outTradeNo) {
        return null;
    }

    @Override
    public boolean closeOrder(String outTradeNo) {
        return false;
    }

    @Override
    public Map<String, Object> refund(String outTradeNo, String outRefundNo, BigDecimal totalAmount, BigDecimal refundAmount, String reason) {
        return null;
    }

    @Override
    public Map<String, Object> verifyPayNotify(String notifyData) {
        return null;
    }

    @Override
    public Map<String, Object> verifyRefundNotify(String notifyData) {
        return null;
    }
}
