package com.ruoyi.alse.service;

import java.util.List;
import com.ruoyi.alse.domain.AlsePayChannelConfig;

/**
 * 支付通道配置Service接口
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
public interface IAlsePayChannelConfigService 
{
    /**
     * 查询支付通道配置
     * 
     * @param id 支付通道配置主键
     * @return 支付通道配置
     */
    public AlsePayChannelConfig selectAlsePayChannelConfigById(Long id);

    /**
     * 查询支付通道配置列表
     * 
     * @param alsePayChannelConfig 支付通道配置
     * @return 支付通道配置集合
     */
    public List<AlsePayChannelConfig> selectAlsePayChannelConfigList(AlsePayChannelConfig alsePayChannelConfig);

    /**
     * 新增支付通道配置
     * 
     * @param alsePayChannelConfig 支付通道配置
     * @return 结果
     */
    public int insertAlsePayChannelConfig(AlsePayChannelConfig alsePayChannelConfig);

    /**
     * 修改支付通道配置
     * 
     * @param alsePayChannelConfig 支付通道配置
     * @return 结果
     */
    public int updateAlsePayChannelConfig(AlsePayChannelConfig alsePayChannelConfig);

    /**
     * 批量删除支付通道配置
     * 
     * @param ids 需要删除的支付通道配置主键集合
     * @return 结果
     */
    public int deleteAlsePayChannelConfigByIds(Long[] ids);

    /**
     * 删除支付通道配置信息
     * 
     * @param id 支付通道配置主键
     * @return 结果
     */
    public int deleteAlsePayChannelConfigById(Long id);
}
