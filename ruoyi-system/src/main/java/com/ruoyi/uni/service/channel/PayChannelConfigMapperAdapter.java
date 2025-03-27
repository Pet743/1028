package com.ruoyi.uni.service.channel;

import com.ruoyi.alse.domain.AlsePayChannelConfig;
import com.ruoyi.alse.mapper.AlsePayChannelConfigMapper;
import com.ruoyi.uni.config.PayChannelConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 支付通道配置适配器，用于将AlsePayChannelConfig转换为PayChannelConfig
 */
@Component
public class PayChannelConfigMapperAdapter {

    @Autowired
    private AlsePayChannelConfigMapper alsePayChannelConfigMapper;

    /**
     * 查询所有启用的支付通道配置
     */
    public List<PayChannelConfig> selectAllEnabled() {
        AlsePayChannelConfig query = new AlsePayChannelConfig();
        query.setEnabled(1); // 查询启用状态的通道
        query.setStatus("0"); // 状态正常

        List<AlsePayChannelConfig> channelConfigs = alsePayChannelConfigMapper.selectAlsePayChannelConfigList(query);

        return channelConfigs.stream()
                .map(this::convertToPayChannelConfig)
                .collect(Collectors.toList());
    }

    /**
     * 根据通道代码查询所有启用的配置
     */
    public List<PayChannelConfig> selectEnabledByChannelCode(String channelCode) {
        AlsePayChannelConfig query = new AlsePayChannelConfig();
        query.setChannelCode(channelCode);
        query.setEnabled(1); // 查询启用状态的通道
        query.setStatus("0"); // 状态正常

        List<AlsePayChannelConfig> channelConfigs = alsePayChannelConfigMapper.selectAlsePayChannelConfigList(query);

        return channelConfigs.stream()
                .map(this::convertToPayChannelConfig)
                .collect(Collectors.toList());
    }

    /**
     * 将AlsePayChannelConfig转换为PayChannelConfig
     */
    private PayChannelConfig convertToPayChannelConfig(AlsePayChannelConfig alseConfig) {
        PayChannelConfig config = new PayChannelConfig();

        config.setId(alseConfig.getId());
        config.setChannelCode(alseConfig.getChannelCode());
        config.setChannelName(alseConfig.getChannelName());
        config.setMerchantId(alseConfig.getMerchantId());
        config.setMerchantName(alseConfig.getMerchantName());
        config.setWeight(alseConfig.getWeight() != null ? alseConfig.getWeight().intValue() : 0);
        config.setTimeout(alseConfig.getTimeout() != null ? alseConfig.getTimeout().intValue() : 300);
        config.setConcurrentLimit(alseConfig.getConcurrentLimit() != null ? alseConfig.getConcurrentLimit().intValue() : 10);
        config.setEnabled(alseConfig.getEnabled() != null && alseConfig.getEnabled() == 1);
        config.setParams(alseConfig.getChannelParams());
        config.setRemark(alseConfig.getRemark());

        return config;
    }
}