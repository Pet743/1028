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
import com.ruoyi.uni.model.DTO.respone.message.ConversationListResponseDTO;
import com.ruoyi.uni.model.DTO.respone.message.ConversationResponseDTO;
import com.ruoyi.uni.model.DTO.respone.message.MessageListResponseDTO;
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

/**
 * 聊天服务实现类
 */
@Service
@Slf4j
public class AlseChatMessageServiceImpl implements IAlseChatMessageService {

    @Autowired
    private AlseChatConversationMapper conversationMapper;

    @Autowired
    private AlseChatMessageMapper alseChatMessageMapper;

    @Autowired
    private IAlseUserService alseUserService;

    /**
     * 获取或创建聊天会话
     */
    @Override
    @Transactional
    public ConversationResponseDTO getOrCreateConversation(Long userId, Long targetUserId) {
        // 参数验证
        if (userId == null || targetUserId == null) {
            throw new ServiceException("用户ID不能为空");
        }

        if (userId.equals(targetUserId)) {
            throw new ServiceException("不能与自己创建会话");
        }

        // 验证用户
        AlseUser currentUser = alseUserService.selectAlseUserByUserId(userId);
        if (currentUser == null) {
            throw new ServiceException("用户不存在");
        }

        AlseUser targetUser = alseUserService.selectAlseUserByUserId(targetUserId);
        if (targetUser == null) {
            throw new ServiceException("目标用户不存在");
        }

        // 查找现有会话
        log.info("查询用户{}和用户{}之间的会话", userId, targetUserId);
        AlseChatConversation conversation = conversationMapper.selectConversationBySenderAndReceiver(userId, targetUserId);

        // 如果会话不存在，创建新会话
        if (conversation == null) {
            log.info("未找到现有会话，创建新会话");
            conversation = new AlseChatConversation();
            conversation.setUserId1(userId);         // 当前用户ID设为user_id1
            conversation.setUserId2(targetUserId);   // 目标用户ID设为user_id2
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

            int rows = conversationMapper.insertAlseChatConversation(conversation);
            if (rows <= 0) {
                throw new ServiceException("创建会话失败");
            }
            log.info("已创建新会话: {}", conversation.getConversationId());
        } else {
            log.info("找到现有会话: {}", conversation.getConversationId());
        }

        // 确定返回对象中的目标用户和未读消息数
        boolean isCurrentUserIdOne = userId.equals(conversation.getUserId1());
        Long unreadCount = isCurrentUserIdOne ? conversation.getUnreadCount1() : conversation.getUnreadCount2();

        // 转换为返回对象
        ConversationResponseDTO result = UniChatConverter.convertToConversationResponseDTO(
                conversation, targetUser, unreadCount);

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
            AlseUser sender = alseUserService.selectAlseUserByUserId(senderId);
            if (sender == null) {
                throw new ServiceException("发送者不存在");
            }

            AlseUser receiver = alseUserService.selectAlseUserByUserId(receiverId);
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
        AlseUser sender = alseUserService.selectAlseUserByUserId(senderId);
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
        alseChatMessageMapper.insertAlseChatMessage(message);

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
        int count = alseChatMessageMapper.updateMessagesReadStatus(conversationId, userId, now);

        // 清空未读消息计数
        if (conversation.getUserId1().equals(userId)) {
            conversationMapper.clearUnreadCount(conversationId, conversation.getUserId1());
        } else {
            conversationMapper.clearUnreadCount(conversationId, conversation.getUserId2());
        }

        return count;
    }

    /**
     * 获取会话消息列表
     *
     * @param requestDTO 消息列表请求
     * @return 消息列表
     */
    @Override
    public List<MessageListResponseDTO> getMessageList(MessageListRequestDTO requestDTO) {
        // 查询消息列表
        List<AlseChatMessage> messageList = alseChatMessageMapper.selectMessageList(requestDTO);

        // 转换为响应DTO
        Long currentUserId = requestDTO.getUserId();
        List<MessageListResponseDTO> resultList = new ArrayList<>();

        for (AlseChatMessage message : messageList) {
            MessageListResponseDTO dto = UniChatConverter.convertToMessageDTO(message);

            // 查询并设置发送者信息
            AlseUser sender = alseUserService.selectAlseUserByUserId(message.getSenderId());
            if (sender != null) {
                dto.setSenderName(sender.getNickname());
                dto.setSenderAvatar(sender.getAvatar());
            }

            // 判断是否是当前用户发送的
            dto.setIsMine(currentUserId.equals(message.getSenderId()));

            resultList.add(dto);
        }

        return resultList;
    }
}