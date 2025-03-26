package com.ruoyi.uni.service.factory.Impl;

import com.ruoyi.alse.domain.AlseOrder;
import com.ruoyi.alse.domain.AlseProduct;
import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.uni.model.DTO.respone.order.PaymentResultOrderDTO;
import com.ruoyi.uni.model.Enum.PaymentMethodEnum;
import com.ruoyi.uni.service.PaymentProcessor;
import com.ruoyi.uni.service.WechatPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@Slf4j
public class WechatPaymentProcessor implements PaymentProcessor {

    @Autowired
    private WechatPayService wechatPayService; // 假设已有微信支付服务

    @Override
    public Integer getSupportedPaymentMethod() {
        return PaymentMethodEnum.WECHAT.getCode();
    }

    @Override
    public PaymentResultOrderDTO processPayment(AlseOrder order, AlseProduct product, AlseUser buyer) {
        log.info("处理微信支付，订单号: {}", order.getOrderNo());

        try {
            // 这里是微信支付处理的示例，实际实现会有所不同
            Map<String, Object> wechatPayResult = wechatPayService.createOrder(
                    order.getOrderNo(),
                    product.getProductTitle(),
                    order.getTotalAmount()// 假设用户有绑定微信
            );

            // 构建支付结果
            PaymentResultOrderDTO resultDTO = new PaymentResultOrderDTO();
            resultDTO.setOrderId(order.getOrderId());
            resultDTO.setOrderNo(order.getOrderNo());
            resultDTO.setPaymentMethod(PaymentMethodEnum.WECHAT.getCode());
            resultDTO.setTotalAmount(order.getTotalAmount());
            resultDTO.setPaymentStatus(0); // 待支付

            // 根据微信支付结果，填充不同的支付信息
            if (wechatPayResult.containsKey("codeUrl")) {
                // 扫码支付
                resultDTO.setQrCodeContent((String) wechatPayResult.get("codeUrl"));
                resultDTO.setRedirectType(2); // 二维码
            } else if (wechatPayResult.containsKey("mwebUrl")) {
                // H5支付
                resultDTO.setPaymentUrl((String) wechatPayResult.get("mwebUrl"));
                resultDTO.setRedirectType(1); // 链接
            } else if (wechatPayResult.containsKey("prepayId")) {
                // JSAPI支付
                resultDTO.setPrepayId((String) wechatPayResult.get("prepayId"));
                resultDTO.setPaymentParams(wechatPayResult);
                resultDTO.setRedirectType(4); // APP参数
            }

            return resultDTO;
        } catch (Exception e) {
            log.error("创建微信支付订单失败", e);
            throw new ServiceException("创建微信支付订单失败: " + e.getMessage());
        }
    }

    @Override
    public boolean handlePaymentCallback(Map<String, String> params) {
        // 实现微信支付回调处理逻辑
        // 这里略去实现细节，实际使用时需要根据微信支付回调参数验证签名并更新订单状态
        return true;
    }
}