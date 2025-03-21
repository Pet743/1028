package com.ruoyi.alse.service;

import java.util.List;
import com.ruoyi.alse.domain.AlseChatMessage;
import com.ruoyi.uni.model.DTO.request.message.MessageListRequestDTO;
import com.ruoyi.uni.model.DTO.request.message.SendMessageRequestDTO;
import com.ruoyi.uni.model.DTO.respone.message.ConversationListResponseDTO;
import com.ruoyi.uni.model.DTO.respone.message.ConversationResponseDTO;
import com.ruoyi.uni.model.DTO.respone.message.MessageListResponseDTO;
import com.ruoyi.uni.model.DTO.respone.message.MessageResponseDTO;

/**
 * 聊天消息Service接口
 * 
 * @author ruoyi
 * @date 2025-03-19
 */
public interface IAlseChatMessageService 
{

    /**
     * 获取或创建与指定用户的会话
     *
     * @param userId 当前用户ID
     * @param targetUserId 目标用户ID
     * @return 会话对象
     */
    ConversationResponseDTO getOrCreateConversation(Long userId, Long targetUserId);

    /**
     * 获取会话消息列表
     *
     * @param requestDTO 消息列表请求
     * @return 消息列表
     */
    List<MessageListResponseDTO>getMessageList(MessageListRequestDTO requestDTO);

    /**
     * 发送消息
     *
     * @param senderId 发送者ID
     * @param requestDTO 发送消息请求
     * @return 已发送的消息
     */
    MessageResponseDTO sendMessage(Long senderId, SendMessageRequestDTO requestDTO);


    /**
     * 标记会话消息为已读
     *
     * @param userId 用户ID
     * @param conversationId 会话ID
     * @return 标记数量
     */
    int markAsRead(Long userId, Long conversationId);
}
