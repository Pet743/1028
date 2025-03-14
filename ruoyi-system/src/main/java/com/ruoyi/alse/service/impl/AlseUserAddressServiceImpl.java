package com.ruoyi.alse.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.alse.mapper.AlseUserAddressMapper;
import com.ruoyi.alse.domain.AlseUserAddress;
import com.ruoyi.alse.service.IAlseUserAddressService;

/**
 * 用户地址Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-10
 */
@Service
public class AlseUserAddressServiceImpl implements IAlseUserAddressService 
{
    @Autowired
    private AlseUserAddressMapper alseUserAddressMapper;

    /**
     * 查询用户地址
     * 
     * @param addressId 用户地址主键
     * @return 用户地址
     */
    @Override
    public AlseUserAddress selectAlseUserAddressByAddressId(Long addressId)
    {
        return alseUserAddressMapper.selectAlseUserAddressByAddressId(addressId);
    }

    /**
     * 查询用户地址列表
     * 
     * @param alseUserAddress 用户地址
     * @return 用户地址
     */
    @Override
    public List<AlseUserAddress> selectAlseUserAddressList(AlseUserAddress alseUserAddress)
    {
        return alseUserAddressMapper.selectAlseUserAddressList(alseUserAddress);
    }

    /**
     * 新增用户地址
     * 
     * @param alseUserAddress 用户地址
     * @return 结果
     */
    @Override
    public int insertAlseUserAddress(AlseUserAddress alseUserAddress)
    {
        alseUserAddress.setCreateTime(DateUtils.getNowDate());
        return alseUserAddressMapper.insertAlseUserAddress(alseUserAddress);
    }

    /**
     * 修改用户地址
     * 
     * @param alseUserAddress 用户地址
     * @return 结果
     */
    @Override
    public int updateAlseUserAddress(AlseUserAddress alseUserAddress)
    {
        alseUserAddress.setUpdateTime(DateUtils.getNowDate());
        return alseUserAddressMapper.updateAlseUserAddress(alseUserAddress);
    }

    /**
     * 批量删除用户地址
     * 
     * @param addressIds 需要删除的用户地址主键
     * @return 结果
     */
    @Override
    public int deleteAlseUserAddressByAddressIds(Long[] addressIds)
    {
        return alseUserAddressMapper.deleteAlseUserAddressByAddressIds(addressIds);
    }

    /**
     * 删除用户地址信息
     * 
     * @param addressId 用户地址主键
     * @return 结果
     */
    @Override
    public int deleteAlseUserAddressByAddressId(Long addressId)
    {
        return alseUserAddressMapper.deleteAlseUserAddressByAddressId(addressId);
    }
}
