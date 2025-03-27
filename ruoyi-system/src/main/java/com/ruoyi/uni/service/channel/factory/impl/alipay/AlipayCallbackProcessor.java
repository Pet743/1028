package com.ruoyi.uni.service.channel.factory.impl.alipay;

import com.ruoyi.uni.config.PayChannelConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 支付宝回调处理器
 */
@Component
@Slf4j
public class AlipayCallbackProcessor {

    @Autowired
    private AlipayConfigLoader alipayConfigLoader;

    /**
     * 处理支付宝回调
     */
    public boolean processCallback(Map<String, String> params) {
        log.info("支付宝回调开始" + params);
        String orderNo = params.get("out_trade_no");
        if (orderNo == null) {
            log.error("回调参数缺少订单号");
            return false;
        }

        // 加载订单对应的支付通道配置
        PayChannelConfig config = alipayConfigLoader.getConfigByOrderNo(orderNo);
        if (config == null) {
            log.error("未找到订单对应的支付通道配置: {}", orderNo);
            return false;
        }

        try {
            // 使用通道配置处理回调验签和业务逻辑
            // ...

            return true;
        } catch (Exception e) {
            log.error("处理支付宝回调异常", e);
            return false;
        }
    }
}
