package com.ruoyi.alse.service.impl;

import java.util.List;

import com.ruoyi.alse.domain.AlseChatConversation;
import com.ruoyi.alse.domain.AlseUser;
import com.ruoyi.alse.mapper.AlseChatConversationMapper;
import com.ruoyi.alse.service.IAlseUserService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.uni.converter.UniChatConverter;
import com.ruoyi.uni.model.DTO.request.message.MessageListRequestDTO;
import com.ruoyi.uni.model.DTO.request.message.SendMessageRequestDTO;
import com.ruoyi.uni.model.DTO.respone.message.ConversationResponseDTO;
import com.ruoyi.uni.model.DTO.respone.message.MessageResponseDTO;
import com.ruoyi.uni.model.Enum.MessageContentTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.alse.mapper.AlseChatMessageMapper;
import com.ruoyi.alse.domain.AlseChatMessage;
import com.ruoyi.alse.service.IAlseChatMessageService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 聊天服务实现类
 */
@Service
@Slf4j
public class AlseChatMessageServiceImpl implements IAlseChatMessageService {

    @Autowired
    private AlseChatConversationMapper conversationMapper;

    @Autowired
    private AlseChatMessageMapper messageMapper;

    @Autowired
    private IAlseUserService userService;

    /**
     * 获取或创建聊天会话
     */
    @Override
    @Transactional
    public ConversationResponseDTO getOrCreateConversation(Long userId, Long targetUserId) {
        // 验证用户
        AlseUser currentUser = userService.selectAlseUserByUserId(userId);
        if (currentUser == null) {
            throw new ServiceException("用户不存在");
        }

        AlseUser targetUser = userService.selectAlseUserByUserId(targetUserId);
        if (targetUser == null) {
            throw new ServiceException("目标用户不存在");
        }

        // 查找现有会话
        AlseChatConversation conversation = conversationMapper.selectConversationByUsers(userId, targetUserId);

        // 如果会话不存在，创建新会话
        if (conversation == null) {
            conversation = new AlseChatConversation();
            conversation.setUserId1(userId);
            conversation.setUserId2(targetUserId);
            conversation.setLastMessage("");
            conversation.setLastMessageTime(DateUtils.getNowDate());
            conversation.setUnreadCount1(0L);
            conversation.setUnreadCount2(0L);
            conversation.setStatus("0");
            conversation.setDelFlag("0");
            conversation.setCreateTime(DateUtils.getNowDate());
            conversation.setUpdateTime(DateUtils.getNowDate());
            conversation.setCreateBy(currentUser.getUsername());
            conversation.setUpdateBy(currentUser.getUsername());

            conversationMapper.insertAlseChatConversation(conversation);
        }

        // 转换为返回对象
        return UniChatConverter.convertToConversationResponseDTO(conversation,
                userId.equals(conversation.getUserId1()) ? targetUser : currentUser,
                userId.equals(conversation.getUserId1()) ? conversation.getUnreadCount1() : conversation.getUnreadCount2());
    }

    /**
     * 获取用户的会话列表
     */
    @Override
    public List<ConversationResponseDTO> getConversationList(Long userId) {
        // 查询用户所有的会话
        List<AlseChatConversation> conversations = conversationMapper.selectAlseChatConversationsByUserId(userId);

        if (conversations.isEmpty()) {
            return Collections.emptyList();
        }

        // 转换为DTO对象
        List<ConversationResponseDTO> result = new ArrayList<>(conversations.size());

        for (AlseChatConversation conversation : conversations) {
            // 确定目标用户ID
            Long targetUserId = conversation.getUserId1().equals(userId)
                    ? conversation.getUserId2() : conversation.getUserId1();

            // 查询目标用户信息
            AlseUser targetUser = userService.selectAlseUserByUserId(targetUserId);
            if (targetUser == null) {
                continue;
            }

            // 转换并添加到结果列表
            result.add(UniChatConverter.convertToConversationResponseDTO(conversation, targetUser, userId));
        }

        return result;
    }

    /**
     * 发送消息
     */
    @Override
    @Transactional
    public MessageResponseDTO sendMessage(Long senderId, SendMessageRequestDTO requestDTO) {
        Long conversationId = requestDTO.getConversationId();
        Long receiverId = requestDTO.getReceiverId();

        // 检查会话
        AlseChatConversation conversation = null;

        if (conversationId != null) {
            // 如果提供了会话ID，检查会话是否存在
            conversation = conversationMapper.selectAlseChatConversationByConversationId(conversationId);
        } else {
            // 如果没有提供会话ID，则尝试查找现有会话
            conversation = conversationMapper.selectConversationByUsers(senderId, receiverId);
        }

        // 如果会话不存在，则创建新会话
        if (conversation == null) {
            // 查询用户信息
            AlseUser sender = userService.selectAlseUserByUserId(senderId);
            if (sender == null) {
                throw new ServiceException("发送者不存在");
            }

            AlseUser receiver = userService.selectAlseUserByUserId(receiverId);
            if (receiver == null) {
                throw new ServiceException("接收者不存在");
            }

            // 创建新会话
            conversation = new AlseChatConversation();
            conversation.setUserId1(senderId);
            conversation.setUserId2(receiverId);
            conversation.setLastMessage("");
            conversation.setLastMessageTime(DateUtils.getNowDate());
            conversation.setUnreadCount1(0L);
            conversation.setUnreadCount2(0L);
            conversation.setStatus("0");
            conversation.setDelFlag("0");
            conversation.setCreateTime(DateUtils.getNowDate());
            conversation.setUpdateTime(DateUtils.getNowDate());
            conversation.setCreateBy(sender.getUsername());
            conversation.setUpdateBy(sender.getUsername());

            conversationMapper.insertAlseChatConversation(conversation);

            // 更新会话ID到请求DTO
            conversationId = conversation.getConversationId();
            requestDTO.setConversationId(conversationId);
        } else {
            conversationId = conversation.getConversationId();

            // 验证用户是否属于该会话
            if (!conversation.getUserId1().equals(senderId) && !conversation.getUserId2().equals(senderId)) {
                throw new ServiceException("您不是该会话的成员");
            }

            // 验证接收者是否是会话中的另一个成员
            if (!conversation.getUserId1().equals(receiverId) && !conversation.getUserId2().equals(receiverId)) {
                throw new ServiceException("接收者不是该会话的成员");
            }
        }

        // 查询发送者信息
        AlseUser sender = userService.selectAlseUserByUserId(senderId);
        if (sender == null) {
            throw new ServiceException("发送者不存在");
        }

        // 创建消息对象
        AlseChatMessage message = new AlseChatMessage();
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(requestDTO.getContent());
        message.setContentType(requestDTO.getContentType());
        message.setMediaUrl(requestDTO.getMediaUrl());
        message.setReadStatus(0); // 未读
        message.setSendTime(DateUtils.getNowDate());
        message.setCreateTime(DateUtils.getNowDate());
        message.setUpdateTime(DateUtils.getNowDate());
        message.setCreateBy(sender.getUsername());
        message.setUpdateBy(sender.getUsername());
        message.setStatus("0");
        message.setDelFlag("0");

        // 保存消息
        messageMapper.insertAlseChatMessage(message);

        // 更新会话最后一条消息
        String displayContent = message.getContent();
        if (message.getContentType() != null && message.getContentType() != MessageContentTypeEnum.TEXT.getCode()) {
            displayContent = "[" + MessageContentTypeEnum.getDesc(message.getContentType()) + "]";
        }
        conversationMapper.updateLastMessage(
                conversationId, displayContent, message.getSendTime());

        // 增加接收者的未读消息数
        try {
            int rows = conversationMapper.increaseUnreadCount(conversationId, receiverId);
            if (rows > 0) {
                log.info("已更新会话{}的未读消息计数，接收者: {}", conversationId, receiverId);
            } else {
                log.warn("未读消息计数未更新，可能是会话{}不存在", conversationId);
            }
        } catch (Exception e) {
            // 捕获未读计数更新异常，但不影响消息发送流程
            log.error("更新未读消息计数失败: {}", e.getMessage(), e);
        }

        // 转换为返回对象
        return UniChatConverter.convertToMessageResponseDTO(message, sender, true);
    }

    /**
     * 获取会话的消息列表
     */
    @Override
    public List<MessageResponseDTO> getMessageList(Long userId, MessageListRequestDTO requestDTO) {
        // 检查会话是否存在
        AlseChatConversation conversation = conversationMapper.selectAlseChatConversationByConversationId(
                requestDTO.getConversationId());

        if (conversation == null) {
            throw new ServiceException("会话不存在");
        }

        // 验证用户是否属于该会话
        if (!conversation.getUserId1().equals(userId) && !conversation.getUserId2().equals(userId)) {
            throw new ServiceException("您不是该会话的成员");
        }

        // 查询消息列表
        List<AlseChatMessage> messages = messageMapper.selectAlseChatMessagesByConversationId(
                requestDTO.getConversationId(), requestDTO.getBeforeMessageId(), requestDTO.getPageSize());

        if (messages.isEmpty()) {
            return Collections.emptyList();
        }

        // 转换为DTO对象
        List<MessageResponseDTO> result = new ArrayList<>(messages.size());

        for (AlseChatMessage message : messages) {
            // 查询发送者信息
            AlseUser sender = userService.selectAlseUserByUserId(message.getSenderId());
            if (sender == null) {
                continue;
            }

            // 判断消息是否是自己发送的
            boolean isSelf = message.getSenderId().equals(userId);

            // 转换并添加到结果列表
            result.add(UniChatConverter.convertToMessageResponseDTO(message, sender, isSelf));
        }

        // 按发送时间升序排序
        return result.stream()
                .sorted((m1, m2) -> m1.getSendTime().compareTo(m2.getSendTime()))
                .collect(Collectors.toList());
    }

    /**
     * 标记会话消息为已读
     */
    @Override
    @Transactional
    public int markAsRead(Long userId, Long conversationId) {
        // 检查会话是否存在
        AlseChatConversation conversation = conversationMapper.selectAlseChatConversationByConversationId(conversationId);

        if (conversation == null) {
            throw new ServiceException("会话不存在");
        }

        // 验证用户是否属于该会话
        if (!conversation.getUserId1().equals(userId) && !conversation.getUserId2().equals(userId)) {
            throw new ServiceException("您不是该会话的成员");
        }

        // 更新消息已读状态
        Date now = DateUtils.getNowDate();
        int count = messageMapper.updateMessagesReadStatus(conversationId, userId, now);

        // 清空未读消息计数
        if (conversation.getUserId1().equals(userId)) {
            conversationMapper.clearUnreadCount(conversationId, conversation.getUserId1());
        } else {
            conversationMapper.clearUnreadCount(conversationId, conversation.getUserId2());
        }

        return count;
    }
}