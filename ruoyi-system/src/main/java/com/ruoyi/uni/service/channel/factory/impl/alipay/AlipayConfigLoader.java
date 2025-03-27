package com.ruoyi.uni.service.channel.factory.impl.alipay;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.uni.config.AlipayProperties;
import com.ruoyi.uni.config.PayChannelConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝配置加载器
 * 用于加载支付宝配置并合并全局配置
 */
@Component
@Slf4j
public class AlipayConfigLoader {

    @Autowired
    private AlipayProperties alipayProperties;

    @Autowired
    private RedisCache redisCache;

    private static final String ORDER_CHANNEL_MAP_KEY = "pay:order:channel";
    private static final String CHANNEL_CONFIG_CACHE_KEY = "pay:channel:config";

    /**
     * 合并全局配置和通道配置
     * @param channelConfig 通道配置
     */
    public void mergeGlobalConfig(PayChannelConfig channelConfig) {
        if (channelConfig == null) {
            return;
        }

        Map<String, String> paramMap = channelConfig.getParamMap();
        if (paramMap == null) {
            paramMap = new HashMap<>();
            channelConfig.setParamMap(paramMap);
        }

        // 应用ID使用通道配置优先，否则使用全局配置
        if (!paramMap.containsKey("appId") && alipayProperties.getAppId() != null) {
            paramMap.put("appId", alipayProperties.getAppId());
        }

        // 合并其他全局配置
        if (!paramMap.containsKey("serverUrl") && alipayProperties.getServerUrl() != null) {
            paramMap.put("serverUrl", alipayProperties.getServerUrl());
        }

        if (!paramMap.containsKey("format") && alipayProperties.getFormat() != null) {
            paramMap.put("format", alipayProperties.getFormat());
        }

        if (!paramMap.containsKey("charset") && alipayProperties.getCharset() != null) {
            paramMap.put("charset", alipayProperties.getCharset());
        }

        if (!paramMap.containsKey("signType") && alipayProperties.getSignType() != null) {
            paramMap.put("signType", alipayProperties.getSignType());
        }

        if (!paramMap.containsKey("notifyUrl") && alipayProperties.getNotifyUrl() != null) {
            paramMap.put("notifyUrl", alipayProperties.getNotifyUrl());
        }

        if (!paramMap.containsKey("returnUrl") && alipayProperties.getReturnUrl() != null) {
            paramMap.put("returnUrl", alipayProperties.getReturnUrl());
        }

        log.debug("合并支付宝全局配置完成: {}", channelConfig.getMerchantId());
    }

    /**
     * 根据订单号获取支付通道配置
     */
    public PayChannelConfig getConfigByOrderNo(String orderNo) {
        String channelId = redisCache.getCacheMapValue(ORDER_CHANNEL_MAP_KEY, orderNo);
        if (channelId == null) {
            return null;
        }

        Map<String, PayChannelConfig> configMap = redisCache.getCacheMap(CHANNEL_CONFIG_CACHE_KEY);
        PayChannelConfig config = configMap != null ? configMap.get(channelId) : null;

        if (config != null) {
            mergeGlobalConfig(config);
        }

        return config;
    }
}