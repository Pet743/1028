package com.ruoyi.uni.converter;


import com.ruoyi.alse.domain.AlseChatConversation;
import com.ruoyi.alse.domain.AlseChatMessage;
import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.uni.model.DTO.respone.message.ConversationResponseDTO;
import com.ruoyi.uni.model.DTO.respone.message.MessageResponseDTO;

/**
 * 聊天相关对象转换器
 */
public class UniChatConverter {

    /**
     * 将会话对象转换为会话响应DTO
     */
    public static ConversationResponseDTO convertToConversationResponseDTO(
            AlseChatConversation conversation, AlseUser targetUser, Long currentUserId) {

        ConversationResponseDTO dto = new ConversationResponseDTO();
        dto.setConversationId(conversation.getConversationId());
        dto.setConversationType(conversation.getConversationType());
        dto.setName(conversation.getName());

        // 设置目标用户信息
        dto.setTargetUserId(targetUser.getUserId());
        dto.setTargetUserName(StringUtils.isNotBlank(targetUser.getNickname()) ?
                targetUser.getNickname() : targetUser.getUsername());
        dto.setTargetUserAvatar(targetUser.getAvatar());

        // 设置最后消息信息
        dto.setLastMessage(conversation.getLastMessage());
        dto.setLastMessageTime(conversation.getLastMessageTime());

        // 设置未读消息数
        if (currentUserId.equals(conversation.getUserId1())) {
            dto.setUnreadCount(Math.toIntExact(conversation.getUnreadCount1()));
        } else {
            dto.setUnreadCount(Math.toIntExact(conversation.getUnreadCount2()));
        }

        return dto;
    }

    /**
     * 将消息对象转换为消息响应DTO
     */
    public static MessageResponseDTO convertToMessageResponseDTO(
            AlseChatMessage message, AlseUser sender, boolean isSelf) {

        MessageResponseDTO dto = new MessageResponseDTO();
        dto.setMessageId(message.getMessageId());
        dto.setConversationId(message.getConversationId());
        dto.setSenderId(message.getSenderId());
        dto.setSenderName(StringUtils.isNotBlank(sender.getNickname()) ?
                sender.getNickname() : sender.getUsername());
        dto.setSenderAvatar(sender.getAvatar());
        dto.setReceiverId(message.getReceiverId());
        dto.setContent(message.getContent());
        dto.setContentType(message.getContentType());
        dto.setMediaUrl(message.getMediaUrl());
        dto.setReadStatus(message.getReadStatus());
        dto.setIsSelf(isSelf);
        dto.setSendTime(message.getSendTime());

        return dto;
    }
}