package com.ruoyi.alse.mapper;

import java.util.List;
import com.ruoyi.alse.domain.AlsePayChannelConfig;

/**
 * 支付通道配置Mapper接口
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
public interface AlsePayChannelConfigMapper 
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
     * 删除支付通道配置
     * 
     * @param id 支付通道配置主键
     * @return 结果
     */
    public int deleteAlsePayChannelConfigById(Long id);

    /**
     * 批量删除支付通道配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAlsePayChannelConfigByIds(Long[] ids);
}
