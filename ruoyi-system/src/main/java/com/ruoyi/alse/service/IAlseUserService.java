package com.ruoyi.alse.service;

import java.util.List;
import com.ruoyi.alse.domain.AlseUser;

/**
 * 用户Service接口
 * 
 * @author ruoyi
 * @date 2025-03-14
 */
public interface IAlseUserService 
{
    /**
     * 查询用户
     * 
     * @param userId 用户主键
     * @return 用户
     */
    public AlseUser selectAlseUserByUserId(Long userId);

    /**
     * 查询用户列表
     * 
     * @param alseUser 用户
     * @return 用户集合
     */
    public List<AlseUser> selectAlseUserList(AlseUser alseUser);

    /**
     * 新增用户
     * 
     * @param alseUser 用户
     * @return 结果
     */
    public int insertAlseUser(AlseUser alseUser);

    /**
     * 修改用户
     * 
     * @param alseUser 用户
     * @return 结果
     */
    public int updateAlseUser(AlseUser alseUser);

    /**
     * 批量删除用户
     * 
     * @param userIds 需要删除的用户主键集合
     * @return 结果
     */
    public int deleteAlseUserByUserIds(Long[] userIds);

    /**
     * 删除用户信息
     * 
     * @param userId 用户主键
     * @return 结果
     */
    public int deleteAlseUserByUserId(Long userId);
}
