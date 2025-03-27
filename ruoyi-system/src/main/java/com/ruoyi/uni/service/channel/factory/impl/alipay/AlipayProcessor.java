package com.ruoyi.uni.service.channel.factory.impl.alipay;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
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
    public PaymentResultDTO processPayment(BigDecimal amount, String orderNo, PayChannelConfig channelConfig, String remark) {
        log.info("处理支付宝支付，订单号: {}, 商户号: {}", orderNo, channelConfig.getMerchantId());

        try {
            // 从通道配置中获取参数
            Map<String, String> params = channelConfig.getParamMap();
            if (params == null || params.isEmpty()) {
                throw new ServiceException("支付宝通道参数配置为空");
            }

            // 提取支付宝配置参数
            String appId = params.get("appId");
            String privateKey = params.get("privateKey");
            String alipayPublicKey = params.get("alipayPublicKey");
            String notifyUrl = params.get("notifyUrl");
            String returnUrl = params.get("returnUrl");
            String serverUrl = params.getOrDefault("serverUrl", "https://openapi.alipay.com/gateway.do");
            String format = params.getOrDefault("format", "json");
            String charset = params.getOrDefault("charset", "UTF-8");
            String signType = params.getOrDefault("signType", "RSA2");

            log.info("使用支付宝通道参数: appId={}, notifyUrl={}", appId, notifyUrl);

            // 初始化支付宝客户端
            AlipayClient alipayClient = new DefaultAlipayClient(
                    serverUrl, appId, privateKey, format, charset, alipayPublicKey, signType);

            // 创建支付请求
            AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
            alipayRequest.setReturnUrl(returnUrl);
            alipayRequest.setNotifyUrl(notifyUrl);

            // 构建业务参数
            AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
            model.setOutTradeNo(orderNo);
            model.setSubject("订单支付");
            model.setTotalAmount(amount.toPlainString());
            model.setProductCode("QUICK_WAP_WAY");
            model.setTimeoutExpress(channelConfig.getTimeout() + "s");

            alipayRequest.setBizModel(model);

            // 调用SDK发送请求
            AlipayTradeWapPayResponse response = alipayClient.pageExecute(alipayRequest);

            if (response.isSuccess()) {
                // 构建支付结果
                PaymentResultDTO resultDTO = new PaymentResultDTO();
                resultDTO.setOrderNo(orderNo);
                resultDTO.setPaymentMethod(PayChannelEnum.ALIPAY.getCode());
                resultDTO.setTotalAmount(amount);
                resultDTO.setPaymentStatus(1); // 待支付
                resultDTO.setPaymentUrl(response.getBody());
                resultDTO.setRedirectType(1); // 链接跳转
                resultDTO.setChannelId(channelConfig.getMerchantId());

                return resultDTO;
            } else {
                log.error("创建支付宝订单失败: {}, {}", response.getCode(), response.getMsg());
                throw new ServiceException("创建支付宝订单失败: " + response.getMsg());
            }
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

        // 验签逻辑
        boolean signVerified = verifySignature(params);
        if (!signVerified) {
            log.error("支付宝回调签名验证失败");
            return false;
        }

        // 判断交易是否成功
        boolean success = "TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus);

        // 处理其他业务逻辑...

        return success;
    }

    /**
     * 验证支付宝回调签名
     */
    private boolean verifySignature(Map<String, String> params) {
        try {
            // 从数据库或缓存获取配置
            // 这里简化处理，实际应该通过订单号查询对应的通道配置
            PayChannelConfig channelConfig = getChannelConfigByOrderNo(params.get("out_trade_no"));
            if (channelConfig == null) {
                log.error("未找到订单对应的支付通道配置");
                return false;
            }

            Map<String, String> channelParams = channelConfig.getParamMap();
            String alipayPublicKey = channelParams.get("alipayPublicKey");

            return AlipaySignature.rsaCheckV1(
                    params,
                    alipayPublicKey,
                    channelParams.getOrDefault("charset", "UTF-8"),
                    channelParams.getOrDefault("signType", "RSA2"));
        } catch (Exception e) {
            log.error("验证支付宝回调签名异常", e);
            return false;
        }
    }

    /**
     * 根据订单号获取支付通道配置
     * 实际实现中，应该通过订单号从Redis或数据库中查询对应的通道配置
     */
    private PayChannelConfig getChannelConfigByOrderNo(String orderNo) {
        // 实际业务中，应该通过以下逻辑实现：
        // 1. 从Redis中获取订单对应的通道ID
        // 2. 通过通道ID查询通道配置
        // 这里简化为返回null
        return null;
    }

    @Override
    public String getOrderNoFromCallback(Map<String, String> params) {
        return params.get("out_trade_no");
    }
}