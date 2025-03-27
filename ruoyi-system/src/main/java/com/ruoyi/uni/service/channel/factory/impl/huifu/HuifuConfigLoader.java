package com.ruoyi.uni.service.channel.factory.impl.huifu;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.uni.config.HuifuProperties;
import com.ruoyi.uni.config.PayChannelConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 汇付配置加载器
 * 用于加载汇付配置并合并全局配置
 */
@Component
@Slf4j
public class HuifuConfigLoader {

    @Autowired
    private HuifuProperties huifuProperties;

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

        // 系统ID
        if (!paramMap.containsKey("sysId") && huifuProperties.getSysId() != null) {
            paramMap.put("sysId", huifuProperties.getSysId());
        }

        // 产品ID
        if (!paramMap.containsKey("productId") && huifuProperties.getProductId() != null) {
            paramMap.put("productId", huifuProperties.getProductId());
        }

        // 商户号
        if (!paramMap.containsKey("huifuId") && huifuProperties.getHuifuId() != null) {
            paramMap.put("huifuId", huifuProperties.getHuifuId());
        }

        // 私钥
        if (!paramMap.containsKey("privateKey") && huifuProperties.getPrivateKey() != null) {
            paramMap.put("privateKey", huifuProperties.getPrivateKey());
        }

        // 公钥
        if (!paramMap.containsKey("publicKey") && huifuProperties.getPublicKey() != null) {
            paramMap.put("publicKey", huifuProperties.getPublicKey());
        }

        // 小程序预下单接口地址
        if (!paramMap.containsKey("miniappPreorderUrl") && huifuProperties.getMiniappPreorderUrl() != null) {
            paramMap.put("miniappPreorderUrl", huifuProperties.getMiniappPreorderUrl());
        }

        // 回调地址
        if (!paramMap.containsKey("notifyUrl") && huifuProperties.getNotifyUrl() != null) {
            paramMap.put("notifyUrl", huifuProperties.getNotifyUrl());
        }

        log.debug("合并汇付全局配置完成: {}", channelConfig.getMerchantId());
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