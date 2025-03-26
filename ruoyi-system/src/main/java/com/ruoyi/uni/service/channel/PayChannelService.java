package com.ruoyi.uni.service.channel;

import com.ruoyi.uni.config.PayChannelConfig;

import java.util.List;

public interface PayChannelService {

    /**
     * 查询所有启用的支付通道配置
     */
    List<PayChannelConfig> getAllEnabledChannels();

    /**
     * 根据通道代码查询所有启用的配置
     */
    List<PayChannelConfig> getEnabledChannelsByCode(String channelCode);

    /**
     * 刷新缓存
     */
    void refreshCache();

    /**
     * 选择支付通道
     */
    PayChannelConfig selectChannel(String channelCode, String orderNo);

    /**
     * 支付完成处理
     */
    void paymentCompleted(String orderNo, boolean success);
}