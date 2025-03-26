package com.ruoyi.uni.service.channel.factory.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.uni.config.PayChannelConfig;
import com.ruoyi.alse.domain.PaymentResultDTO;
import com.ruoyi.uni.model.Enum.PayChannelEnum;
import com.ruoyi.uni.service.channel.factory.PayProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@Slf4j
public class AlipayProcessor implements PayProcessor {

    @Override
    public PayChannelEnum getSupportedChannel() {
        return PayChannelEnum.ALIPAY;
    }

    @Override
    public PaymentResultDTO processPayment(BigDecimal amount, String orderNo, PayChannelConfig channelConfig) {
        log.info("处理支付宝支付，订单号: {}, 商户号: {}", orderNo, channelConfig.getMerchantId());

        try {
            // 从通道配置中获取参数
            Map<String, String> params = channelConfig.getParamMap();
            String appId = params.get("appId");
            String privateKey = params.get("privateKey");

            log.info("使用支付宝通道参数: appId={}", appId);

            // 构建支付请求参数并调用支付接口
            // 这里简化处理，实际应使用支付宝SDK创建支付订单
            String paymentUrl = "https://openapi.alipay.com/gateway.do?orderNo=" + orderNo
                    + "&amount=" + amount.toPlainString()
                    + "&appId=" + appId;

            // 构建支付结果
            PaymentResultDTO resultDTO = new PaymentResultDTO();
            resultDTO.setOrderNo(orderNo);
            resultDTO.setPaymentMethod(PayChannelEnum.ALIPAY.getCode());
            resultDTO.setTotalAmount(amount);
            resultDTO.setPaymentStatus(1); // 待支付
            resultDTO.setPaymentUrl(paymentUrl);
            resultDTO.setRedirectType(1); // 链接跳转
            resultDTO.setChannelId(channelConfig.getMerchantId());

            return resultDTO;
        } catch (Exception e) {
            log.error("创建支付宝订单失败", e);
            throw new ServiceException("创建支付宝订单失败: " + e.getMessage());
        }
    }

    @Override
    public boolean handlePaymentCallback(Map<String, String> params) {
        // 实现支付宝回调处理逻辑
        String orderNo = params.get("out_trade_no");
        String tradeStatus = params.get("trade_status");

        log.info("支付宝回调，订单号: {}, 交易状态: {}", orderNo, tradeStatus);

        // 判断交易是否成功
        boolean success = "TRADE_SUCCESS".equals(tradeStatus);

        // 处理其他业务逻辑...

        return success;
    }

    @Override
    public String getOrderNoFromCallback(Map<String, String> params) {
        return params.get("out_trade_no");
    }
}