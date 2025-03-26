package com.ruoyi.uni.service.channel.impl;

import com.ruoyi.alse.service.IAlseOrderService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.uni.config.PayChannelConfig;
import com.ruoyi.alse.domain.PaymentResultDTO;
import com.ruoyi.uni.model.Enum.PayChannelEnum;
import com.ruoyi.uni.service.channel.PayChannelService;
import com.ruoyi.uni.service.channel.UniPayService;
import com.ruoyi.uni.service.channel.factory.PayProcessor;
import com.ruoyi.uni.util.JsonUtils;
import com.ruoyi.uni.util.OrderSnGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UniPayServiceImpl implements UniPayService {

    @Autowired
    private PayChannelService payChannelService;

    @Autowired
    private IAlseOrderService alseOrderService;

    /**
     * 支付处理器映射
     */
    private final Map<String, PayProcessor> processorMap = new ConcurrentHashMap<>();

    @Autowired
    public UniPayServiceImpl(List<PayProcessor> processors) {
        processors.forEach(processor ->
                processorMap.put(processor.getSupportedChannel().getCode(), processor)
        );
    }

    @Override
    public PaymentResultDTO createPayment(BigDecimal amount, String channelCode) {
        // 1. 验证支付方式
        PayChannelEnum channelEnum = PayChannelEnum.getByCode(channelCode);
        if (channelEnum == null) {
            throw new ServiceException("不支持的支付方式: " + channelCode);
        }

        // 2. 获取支付处理器
        PayProcessor processor = processorMap.get(channelCode);
        if (processor == null) {
            throw new ServiceException("未找到支付处理器: " + channelCode);
        }

        // 3. 生成订单号
        String orderNo = generateOrderNo(channelCode);

        // 4. 选择支付通道
        PayChannelConfig channelConfig;
        try {
            channelConfig = payChannelService.selectChannel(channelCode, orderNo);
        } catch (ServiceException e) {
            log.error("选择支付通道失败: {}", e.getMessage());
            throw e;
        }

        // 5. 异步创建虚拟订单
        asyncCreateVirtualOrder(orderNo, amount, channelEnum.getSystem());

        // 6. 处理支付
        try {
            // 解析通道参数
            channelConfig.setParamMap(JsonUtils.parseMap(channelConfig.getParams()));

            // 处理支付
            return processor.processPayment(amount, orderNo, channelConfig);
        } catch (Exception e) {
            // 发生异常时释放通道资源
            payChannelService.paymentCompleted(orderNo, false);
            log.error("处理支付失败", e);
            throw new ServiceException("创建支付订单失败: " + e.getMessage());
        }
    }

    @Override
    public boolean handlePaymentCallback(String channelCode, Map<String, String> params) {
        PayProcessor processor = processorMap.get(channelCode);
        if (processor == null) {
            log.error("未找到支付处理器: {}", channelCode);
            return false;
        }

        try {
            boolean success = processor.handlePaymentCallback(params);

            // 获取订单号
            String orderNo = processor.getOrderNoFromCallback(params);
            if (StringUtils.isNotEmpty(orderNo)) {
                // 处理通道释放
                payChannelService.paymentCompleted(orderNo, success);
            }

            return success;
        } catch (Exception e) {
            log.error("处理支付回调失败", e);
            return false;
        }
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo(String orderMethod) {
        return OrderSnGenerator.generateClientSn(orderMethod);
    }

    /**
     * 异步创建虚拟订单
     */
    private void asyncCreateVirtualOrder(String orderNo, BigDecimal amount, Integer paymentMethod) {
        CompletableFuture.runAsync(() -> {
            try {
                alseOrderService.createVirtualOrder(orderNo, amount, paymentMethod);
                log.info("异步创建虚拟订单成功: {}", orderNo);
            } catch (Exception e) {
                log.error("异步创建虚拟订单失败: {}", e.getMessage());
            }
        });
    }
}