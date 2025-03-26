package com.ruoyi.alse.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.alse.mapper.AlsePayChannelConfigMapper;
import com.ruoyi.alse.domain.AlsePayChannelConfig;
import com.ruoyi.alse.service.IAlsePayChannelConfigService;

/**
 * 支付通道配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
@Service
public class AlsePayChannelConfigServiceImpl implements IAlsePayChannelConfigService 
{
    @Autowired
    private AlsePayChannelConfigMapper alsePayChannelConfigMapper;

    /**
     * 查询支付通道配置
     * 
     * @param id 支付通道配置主键
     * @return 支付通道配置
     */
    @Override
    public AlsePayChannelConfig selectAlsePayChannelConfigById(Long id)
    {
        return alsePayChannelConfigMapper.selectAlsePayChannelConfigById(id);
    }

    /**
     * 查询支付通道配置列表
     * 
     * @param alsePayChannelConfig 支付通道配置
     * @return 支付通道配置
     */
    @Override
    public List<AlsePayChannelConfig> selectAlsePayChannelConfigList(AlsePayChannelConfig alsePayChannelConfig)
    {
        return alsePayChannelConfigMapper.selectAlsePayChannelConfigList(alsePayChannelConfig);
    }

    /**
     * 新增支付通道配置
     * 
     * @param alsePayChannelConfig 支付通道配置
     * @return 结果
     */
    @Override
    public int insertAlsePayChannelConfig(AlsePayChannelConfig alsePayChannelConfig)
    {
        alsePayChannelConfig.setCreateTime(DateUtils.getNowDate());
        return alsePayChannelConfigMapper.insertAlsePayChannelConfig(alsePayChannelConfig);
    }

    /**
     * 修改支付通道配置
     * 
     * @param alsePayChannelConfig 支付通道配置
     * @return 结果
     */
    @Override
    public int updateAlsePayChannelConfig(AlsePayChannelConfig alsePayChannelConfig)
    {
        alsePayChannelConfig.setUpdateTime(DateUtils.getNowDate());
        return alsePayChannelConfigMapper.updateAlsePayChannelConfig(alsePayChannelConfig);
    }

    /**
     * 批量删除支付通道配置
     * 
     * @param ids 需要删除的支付通道配置主键
     * @return 结果
     */
    @Override
    public int deleteAlsePayChannelConfigByIds(Long[] ids)
    {
        return alsePayChannelConfigMapper.deleteAlsePayChannelConfigByIds(ids);
    }

    /**
     * 删除支付通道配置信息
     * 
     * @param id 支付通道配置主键
     * @return 结果
     */
    @Override
    public int deleteAlsePayChannelConfigById(Long id)
    {
        return alsePayChannelConfigMapper.deleteAlsePayChannelConfigById(id);
    }
}
