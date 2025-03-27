package com.ruoyi.uni.service.channel.impl;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.uni.config.PayChannelConfig;
import com.ruoyi.uni.model.Enum.PayChannelEnum;
import com.ruoyi.uni.service.channel.PayChannelConfigMapperAdapter;
import com.ruoyi.uni.service.channel.PayChannelService;
import com.ruoyi.uni.service.channel.factory.impl.alipay.AlipayConfigLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@Slf4j
public class PayChannelServiceImpl implements PayChannelService {

    @Autowired
    private PayChannelConfigMapperAdapter payChannelConfigMapperAdapter;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private AlipayConfigLoader alipayConfigLoader;

    private static final String CHANNEL_CONFIG_CACHE_KEY = "pay:channel:config";
    private static final String ORDER_CHANNEL_MAP_KEY = "pay:order:channel";

    /**
     * 订单超时定时任务
     */
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
        // 设置RedisCache
        PayChannelConfig.setRedisCache(redisCache);

        // 刷新缓存
        refreshCache();
    }
    @Override
    public List<PayChannelConfig> getAllEnabledChannels() {
        // 从Redis获取所有通道配置
        Map<String, PayChannelConfig> channelMap = redisCache.getCacheMap(CHANNEL_CONFIG_CACHE_KEY);
        if (channelMap == null || channelMap.isEmpty()) {
            refreshCache();
            channelMap = redisCache.getCacheMap(CHANNEL_CONFIG_CACHE_KEY);
        }

        // 如果仍然为空，则直接从数据库获取
        if (channelMap == null || channelMap.isEmpty()) {
            return payChannelConfigMapperAdapter.selectAllEnabled();
        }

        // 转换为通道配置列表
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public List<PayChannelConfig> getEnabledChannelsByCode(String channelCode) {
        return getAllEnabledChannels().stream()
                .filter(config -> config.getChannelCode().equals(channelCode))
                .collect(Collectors.toList());
    }

    @Override
    public void refreshCache() {
        List<PayChannelConfig> channels = payChannelConfigMapperAdapter.selectAllEnabled();

        // 清除旧缓存
        redisCache.deleteObject(CHANNEL_CONFIG_CACHE_KEY);

        // 设置新缓存
        Map<String, PayChannelConfig> channelMap = new HashMap<>();
        for (PayChannelConfig channel : channels) {
            channelMap.put(channel.getId().toString(), channel);
        }

        if (!channelMap.isEmpty()) {
            redisCache.setCacheMap(CHANNEL_CONFIG_CACHE_KEY, channelMap);
        }

        log.info("刷新支付通道配置缓存，共{}个通道", channels.size());
    }

    @Override
    public PayChannelConfig selectChannel(String channelCode, String orderNo) {
        List<PayChannelConfig> channels = getEnabledChannelsByCode(channelCode);
        if (channels.isEmpty()) {
            throw new ServiceException("不支持的支付方式：" + channelCode);
        }

        // 按权重选择通道
        PayChannelConfig selectedChannel = weightedRandomSelect(channels);

        // 如果所选通道不可用，尝试其他通道
        if (!selectedChannel.isAvailable()) {
            selectedChannel = findAvailableChannel(channels);
        }

        if (selectedChannel == null) {
            throw new ServiceException("暂无可用的" + getChannelName(channelCode) + "支付通道");
        }

        // 增加待支付计数（同时更新Redis）
        if (!selectedChannel.incrementPending()) {
            throw new ServiceException("支付通道繁忙，请稍后再试");
        }

        // 记录订单与通道的关系
        redisCache.setCacheMapValue(ORDER_CHANNEL_MAP_KEY, orderNo, selectedChannel.getId().toString());

        // 设置超时任务
        scheduleTimeout(orderNo, selectedChannel);

        // 根据通道加载配置
        if (PayChannelEnum.ALIPAY.getName().equals(selectedChannel.getChannelName())) {
            alipayConfigLoader.mergeGlobalConfig(selectedChannel);
        }
        if (PayChannelEnum.HUIFU_MINIAPP.getName().equals(selectedChannel.getChannelName())) {
            alipayConfigLoader.mergeGlobalConfig(selectedChannel);
        }
        // 其他支付方式的配置加载

        return selectedChannel;
    }



    /**
     * 按权重随机选择通道
     */
    private PayChannelConfig weightedRandomSelect(List<PayChannelConfig> channels) {
        int totalWeight = channels.stream()
                .filter(PayChannelConfig::getEnabled)
                .mapToInt(PayChannelConfig::getWeight)
                .sum();

        if (totalWeight <= 0) {
            return null;
        }

        int randomWeight = new Random().nextInt(totalWeight) + 1;
        int current = 0;

        for (PayChannelConfig channel : channels) {
            if (!channel.getEnabled()) continue;

            current += channel.getWeight();
            if (randomWeight <= current) {
                return channel;
            }
        }

        return channels.get(0);
    }

    /**
     * 查找可用通道
     */
    private PayChannelConfig findAvailableChannel(List<PayChannelConfig> channels) {
        return channels.stream()
                .filter(PayChannelConfig::isAvailable)
                .findFirst()
                .orElse(null);
    }

    /**
     * 设置订单支付超时任务
     */
    private void scheduleTimeout(String orderNo, PayChannelConfig channel) {
        scheduler.schedule(() -> {
            String channelId = redisCache.getCacheMapValue(ORDER_CHANNEL_MAP_KEY, orderNo);
            if (channelId != null && channelId.equals(channel.getId().toString())) {
                redisCache.deleteCacheMapValue(ORDER_CHANNEL_MAP_KEY, orderNo);
                // 减少待支付计数（同时更新Redis）
                channel.decrementPending();
                log.info("订单{}支付超时，释放{}通道资源", orderNo, channel.getMerchantId());
            }
        }, channel.getTimeout(), TimeUnit.SECONDS);
    }

    @Override
    public void paymentCompleted(String orderNo, boolean success) {
        String channelId = redisCache.getCacheMapValue(ORDER_CHANNEL_MAP_KEY, orderNo);
        if (channelId != null) {
            redisCache.deleteCacheMapValue(ORDER_CHANNEL_MAP_KEY, orderNo);

            Map<String, PayChannelConfig> channelMap = redisCache.getCacheMap(CHANNEL_CONFIG_CACHE_KEY);
            PayChannelConfig channel = channelMap != null ? channelMap.get(channelId) : null;

            if (channel != null) {
                // 减少待支付计数（同时更新Redis）
                channel.decrementPending();
                log.info("订单{}支付{}，释放{}通道资源",
                        orderNo, success ? "成功" : "失败", channel.getMerchantId());
            }
        }
    }

    private String getChannelName(String channelCode) {
        PayChannelEnum channelEnum = PayChannelEnum.getByCode(channelCode);
        return channelEnum != null ? channelEnum.getName() : channelCode;
    }
}