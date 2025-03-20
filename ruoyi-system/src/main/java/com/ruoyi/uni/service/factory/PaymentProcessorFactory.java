package com.ruoyi.uni.service.factory;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.uni.service.PaymentProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PaymentProcessorFactory {

    private final Map<Integer, PaymentProcessor> processorMap = new ConcurrentHashMap<>();

    @Autowired
    public PaymentProcessorFactory(List<PaymentProcessor> processors) {
        for (PaymentProcessor processor : processors) {
            processorMap.put(processor.getSupportedPaymentMethod(), processor);
        }
    }

    /**
     * 获取支付处理器
     */
    public PaymentProcessor getProcessor(Integer paymentMethod) {
        PaymentProcessor processor = processorMap.get(paymentMethod);
        if (processor == null) {
            throw new ServiceException("不支持的支付方式：" + paymentMethod);
        }
        return processor;
    }
}