package com.ruoyi.alse.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.alse.mapper.AlseUserMapper;
import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.alse.service.IAlseUserService;

/**
 * 用户Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-14
 */
@Service
public class AlseUserServiceImpl implements IAlseUserService 
{
    @Autowired
    private AlseUserMapper alseUserMapper;

    /**
     * 查询用户
     * 
     * @param userId 用户主键
     * @return 用户
     */
    @Override
    public AlseUser selectAlseUserByUserId(Long userId)
    {
        return alseUserMapper.selectAlseUserByUserId(userId);
    }

    /**
     * 查询用户列表
     * 
     * @param alseUser 用户
     * @return 用户
     */
    @Override
    public List<AlseUser> selectAlseUserList(AlseUser alseUser)
    {
        return alseUserMapper.selectAlseUserList(alseUser);
    }

    /**
     * 新增用户
     * 
     * @param alseUser 用户
     * @return 结果
     */
    @Override
    public int insertAlseUser(AlseUser alseUser)
    {
        alseUser.setCreateTime(DateUtils.getNowDate());
        return alseUserMapper.insertAlseUser(alseUser);
    }

    /**
     * 修改用户
     * 
     * @param alseUser 用户
     * @return 结果
     */
    @Override
    public int updateAlseUser(AlseUser alseUser)
    {
        alseUser.setUpdateTime(DateUtils.getNowDate());
        return alseUserMapper.updateAlseUser(alseUser);
    }

    /**
     * 批量删除用户
     * 
     * @param userIds 需要删除的用户主键
     * @return 结果
     */
    @Override
    public int deleteAlseUserByUserIds(Long[] userIds)
    {
        return alseUserMapper.deleteAlseUserByUserIds(userIds);
    }

    /**
     * 删除用户信息
     * 
     * @param userId 用户主键
     * @return 结果
     */
    @Override
    public int deleteAlseUserByUserId(Long userId)
    {
        return alseUserMapper.deleteAlseUserByUserId(userId);
    }
}
