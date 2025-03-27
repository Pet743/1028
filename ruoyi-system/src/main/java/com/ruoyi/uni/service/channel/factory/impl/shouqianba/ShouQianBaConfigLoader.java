package com.ruoyi.uni.service.channel.factory.impl.shouqianba;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.uni.config.PayChannelConfig;
import com.ruoyi.uni.config.ShouqianbaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 收钱吧配置加载器
 * 用于加载收钱吧配置并合并全局配置
 */
@Component
@Slf4j
public class ShouQianBaConfigLoader {

    @Autowired
    private ShouqianbaProperties shouqianbaProperties;

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

        // API域名地址
        if (!paramMap.containsKey("apiDomain") && shouqianbaProperties.getApiDomain() != null) {
            paramMap.put("apiDomain", shouqianbaProperties.getApiDomain());
        }

        // 服务商序列号
        if (!paramMap.containsKey("vendorSn") && shouqianbaProperties.getVendorSn() != null) {
            paramMap.put("vendorSn", shouqianbaProperties.getVendorSn());
        }

        // 服务商密钥
        if (!paramMap.containsKey("vendorKey") && shouqianbaProperties.getVendorKey() != null) {
            paramMap.put("vendorKey", shouqianbaProperties.getVendorKey());
        }

        // 应用编号
        if (!paramMap.containsKey("appId") && shouqianbaProperties.getAppId() != null) {
            paramMap.put("appId", shouqianbaProperties.getAppId());
        }

        // 激活码
        if (!paramMap.containsKey("code") && shouqianbaProperties.getCode() != null) {
            paramMap.put("code", shouqianbaProperties.getCode());
        }

        // 设备ID
        if (!paramMap.containsKey("deviceId") && shouqianbaProperties.getDeviceId() != null) {
            paramMap.put("deviceId", shouqianbaProperties.getDeviceId());
        }

        // 客户终端编号
        if (!paramMap.containsKey("clientSn") && shouqianbaProperties.getClientSn() != null) {
            paramMap.put("clientSn", shouqianbaProperties.getClientSn());
        }

        // 终端名称
        if (!paramMap.containsKey("terminalName") && shouqianbaProperties.getTerminalName() != null) {
            paramMap.put("terminalName", shouqianbaProperties.getTerminalName());
        }

        // 操作员名称
        if (!paramMap.containsKey("operator") && shouqianbaProperties.getOperator() != null) {
            paramMap.put("operator", shouqianbaProperties.getOperator());
        }

        // WAP网关地址
        if (!paramMap.containsKey("wapGateway") && shouqianbaProperties.getWapGateway() != null) {
            paramMap.put("wapGateway", shouqianbaProperties.getWapGateway());
        }

        log.debug("合并收钱吧全局配置完成: {}", channelConfig.getMerchantId());
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