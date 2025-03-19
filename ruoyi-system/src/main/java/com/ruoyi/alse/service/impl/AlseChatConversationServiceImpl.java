package com.ruoyi.alse.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.alse.mapper.AlseChatConversationMapper;
import com.ruoyi.alse.domain.AlseChatConversation;
import com.ruoyi.alse.service.IAlseChatConversationService;

/**
 * 聊天会话Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-19
 */
@Service
public class AlseChatConversationServiceImpl implements IAlseChatConversationService 
{
    @Autowired
    private AlseChatConversationMapper alseChatConversationMapper;

    /**
     * 查询聊天会话
     * 
     * @param conversationId 聊天会话主键
     * @return 聊天会话
     */
    @Override
    public AlseChatConversation selectAlseChatConversationByConversationId(Long conversationId)
    {
        return alseChatConversationMapper.selectAlseChatConversationByConversationId(conversationId);
    }

    /**
     * 查询聊天会话列表
     * 
     * @param alseChatConversation 聊天会话
     * @return 聊天会话
     */
    @Override
    public List<AlseChatConversation> selectAlseChatConversationList(AlseChatConversation alseChatConversation)
    {
        return alseChatConversationMapper.selectAlseChatConversationList(alseChatConversation);
    }

    /**
     * 新增聊天会话
     * 
     * @param alseChatConversation 聊天会话
     * @return 结果
     */
    @Override
    public int insertAlseChatConversation(AlseChatConversation alseChatConversation)
    {
        alseChatConversation.setCreateTime(DateUtils.getNowDate());
        return alseChatConversationMapper.insertAlseChatConversation(alseChatConversation);
    }

    /**
     * 修改聊天会话
     * 
     * @param alseChatConversation 聊天会话
     * @return 结果
     */
    @Override
    public int updateAlseChatConversation(AlseChatConversation alseChatConversation)
    {
        alseChatConversation.setUpdateTime(DateUtils.getNowDate());
        return alseChatConversationMapper.updateAlseChatConversation(alseChatConversation);
    }

    /**
     * 批量删除聊天会话
     * 
     * @param conversationIds 需要删除的聊天会话主键
     * @return 结果
     */
    @Override
    public int deleteAlseChatConversationByConversationIds(Long[] conversationIds)
    {
        return alseChatConversationMapper.deleteAlseChatConversationByConversationIds(conversationIds);
    }

    /**
     * 删除聊天会话信息
     * 
     * @param conversationId 聊天会话主键
     * @return 结果
     */
    @Override
    public int deleteAlseChatConversationByConversationId(Long conversationId)
    {
        return alseChatConversationMapper.deleteAlseChatConversationByConversationId(conversationId);
    }
}
