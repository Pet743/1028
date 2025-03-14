package com.ruoyi.alse.service;

import java.util.List;
import com.ruoyi.alse.domain.AlseUserAddress;

/**
 * 用户地址Service接口
 * 
 * @author ruoyi
 * @date 2025-03-10
 */
public interface IAlseUserAddressService 
{
    /**
     * 查询用户地址
     * 
     * @param addressId 用户地址主键
     * @return 用户地址
     */
    public AlseUserAddress selectAlseUserAddressByAddressId(Long addressId);

    /**
     * 查询用户地址列表
     * 
     * @param alseUserAddress 用户地址
     * @return 用户地址集合
     */
    public List<AlseUserAddress> selectAlseUserAddressList(AlseUserAddress alseUserAddress);

    /**
     * 新增用户地址
     * 
     * @param alseUserAddress 用户地址
     * @return 结果
     */
    public int insertAlseUserAddress(AlseUserAddress alseUserAddress);

    /**
     * 修改用户地址
     * 
     * @param alseUserAddress 用户地址
     * @return 结果
     */
    public int updateAlseUserAddress(AlseUserAddress alseUserAddress);

    /**
     * 批量删除用户地址
     * 
     * @param addressIds 需要删除的用户地址主键集合
     * @return 结果
     */
    public int deleteAlseUserAddressByAddressIds(Long[] addressIds);

    /**
     * 删除用户地址信息
     * 
     * @param addressId 用户地址主键
     * @return 结果
     */
    public int deleteAlseUserAddressByAddressId(Long addressId);
}
