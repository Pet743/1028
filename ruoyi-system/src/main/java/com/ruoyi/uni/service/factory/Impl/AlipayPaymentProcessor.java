package com.ruoyi.uni.service.factory.Impl;

import com.ruoyi.alse.domain.AlseOrder;
import com.ruoyi.alse.domain.AlseProduct;
import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.uni.model.DTO.request.payment.PaymentRequestDTO;
import com.ruoyi.uni.model.DTO.respone.order.PaymentResultDTO;
import com.ruoyi.uni.model.Enum.OrderStatusEnum;
import com.ruoyi.uni.model.Enum.PaymentMethodEnum;
import com.ruoyi.uni.service.PaymentProcessor;
import com.ruoyi.uni.service.ALiPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;
import com.alipay.api.AlipayApiException;

@Component
@Slf4j
public class AlipayPaymentProcessor implements PaymentProcessor {

    @Autowired
    private ALiPaymentService ALiPaymentService;

    @Override
    public Integer getSupportedPaymentMethod() {
        return PaymentMethodEnum.ALIPAY.getCode();
    }

    @Override
    public PaymentResultDTO processPayment(AlseOrder order, AlseProduct product, AlseUser buyer) {
        log.info("处理支付宝支付，订单号: {}", order.getOrderNo());

        try {
            // 构建支付请求参数
            PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO();
            paymentRequestDTO.setTotalMoney(order.getTotalAmount().toPlainString());

            // 调用支付宝支付接口
            String paymentUrl = ALiPaymentService.backPaymentReturnUrl(paymentRequestDTO);

            // 构建支付结果
            PaymentResultDTO resultDTO = new PaymentResultDTO();
            resultDTO.setOrderId(order.getOrderId());
            resultDTO.setOrderNo(order.getOrderNo());
            resultDTO.setPaymentMethod(PaymentMethodEnum.ALIPAY.getCode());
            resultDTO.setTotalAmount(order.getTotalAmount());
            resultDTO.setPaymentStatus(OrderStatusEnum.PENDING_PAYMENT.getCode()); // 待支付
            resultDTO.setPaymentUrl(paymentUrl);
            resultDTO.setRedirectType(1); // 链接跳转

            return resultDTO;
        } catch (AlipayApiException e) {
            log.error("创建支付宝订单失败", e);
            throw new ServiceException("创建支付宝订单失败: " + e.getMessage());
        }
    }

    @Override
    public boolean handlePaymentCallback(Map<String, String> params) {
        // 实现支付宝回调处理逻辑
        // 这里略去实现细节，实际使用时需要根据支付宝回调参数验证签名并更新订单状态
        return true;
    }
}
