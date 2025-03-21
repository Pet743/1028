package com.ruoyi.alse.service;

import java.util.List;
import com.ruoyi.alse.domain.AlseChatConversation;
import com.ruoyi.uni.model.DTO.respone.message.ConversationListResponseDTO;

/**
 * 聊天会话Service接口
 * 
 * @author ruoyi
 * @date 2025-03-19
 */
public interface IAlseChatConversationService 
{


    /**
     * 获取用户会话列表
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<ConversationListResponseDTO> getConversationList(Long userId);


    /**
     * 查询聊天会话
     * 
     * @param conversationId 聊天会话主键
     * @return 聊天会话
     */
    public AlseChatConversation selectAlseChatConversationByConversationId(Long conversationId);

    /**
     * 查询聊天会话列表
     * 
     * @param alseChatConversation 聊天会话
     * @return 聊天会话集合
     */
    public List<AlseChatConversation> selectAlseChatConversationList(AlseChatConversation alseChatConversation);

    /**
     * 新增聊天会话
     * 
     * @param alseChatConversation 聊天会话
     * @return 结果
     */
    public int insertAlseChatConversation(AlseChatConversation alseChatConversation);

    /**
     * 修改聊天会话
     * 
     * @param alseChatConversation 聊天会话
     * @return 结果
     */
    public int updateAlseChatConversation(AlseChatConversation alseChatConversation);

    /**
     * 批量删除聊天会话
     * 
     * @param conversationIds 需要删除的聊天会话主键集合
     * @return 结果
     */
    public int deleteAlseChatConversationByConversationIds(Long[] conversationIds);

    /**
     * 删除聊天会话信息
     * 
     * @param conversationId 聊天会话主键
     * @return 结果
     */
    public int deleteAlseChatConversationByConversationId(Long conversationId);
}
