package com.ruoyi.uni.controller;

import com.ruoyi.alse.service.IAlseChatMessageService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.uni.model.ChatMessage;
import com.ruoyi.uni.model.DTO.request.message.GetConversationRequestDTO;
import com.ruoyi.uni.model.DTO.request.message.MessageListRequestDTO;
import com.ruoyi.uni.model.DTO.request.message.ReadMessageRequestDTO;
import com.ruoyi.uni.model.DTO.request.message.SendMessageRequestDTO;
import com.ruoyi.uni.model.DTO.respone.message.ConversationResponseDTO;
import com.ruoyi.uni.model.DTO.respone.message.MessageResponseDTO;
import com.ruoyi.uni.service.ChatWebSocketServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 聊天接口
 */
@RestController
@RequestMapping("/api/chat")
@Api(tags = "即时通讯接口")
@Slf4j
public class UniChatController extends BaseController {

    @Autowired
    private IAlseChatMessageService chatService;

    /**
     * 获取或创建聊天会话
     */
    @PostMapping("/conversation")
    @ApiOperation("获取或创建聊天会话")
    public AjaxResult getOrCreateConversation(@Validated @RequestBody GetConversationRequestDTO requestDTO) {
        // 从DTO中获取userId
        Long userId = requestDTO.getUserId();
        ConversationResponseDTO conversation = chatService.getOrCreateConversation(userId, requestDTO.getTargetUserId());
        return AjaxResult.success(conversation);
    }

    /**
     * 获取会话列表
     */
    @GetMapping("/conversations")
    @ApiOperation("获取会话列表")
    public AjaxResult getConversationList(@RequestParam Long userId) {
        // 从请求参数中获取userId
        List<ConversationResponseDTO> conversations = chatService.getConversationList(userId);
        return AjaxResult.success(conversations);
    }

    /**
     * 发送消息
     */
    @PostMapping("/message")
    @ApiOperation("发送消息")
    public AjaxResult sendMessage(@Validated @RequestBody SendMessageRequestDTO requestDTO) {
        if (requestDTO == null) {
            return AjaxResult.error("请求参数不能为空");
        }

        // 从DTO中获取userId
        Long userId = requestDTO.getUserId();
        if (userId == null) {
            return AjaxResult.error("发送者ID不能为空");
        }

        Long receiverId = requestDTO.getReceiverId();
        if (receiverId == null) {
            return AjaxResult.error("接收者ID不能为空");
        }

        try {
            // 发送消息（service层会处理自动创建会话）
            MessageResponseDTO message = chatService.sendMessage(userId, requestDTO);

            // 通过WebSocket推送消息给接收者
            try {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(1); // 聊天消息
                chatMessage.setMessageId(message.getMessageId().toString());
                chatMessage.setSenderId(userId);
                chatMessage.setReceiverId(receiverId);
                chatMessage.setConversationId(message.getConversationId()); // 使用返回的会话ID
                chatMessage.setContent(requestDTO.getContent());
                chatMessage.setContentType(requestDTO.getContentType());
                chatMessage.setSendTime(new Date());

                if (requestDTO.getMediaUrl() != null) {
                    chatMessage.setContent(requestDTO.getMediaUrl());
                }

                // 推送给接收者
                ChatWebSocketServer.sendToUser(receiverId, chatMessage);
                log.info("已通过WebSocket向用户{}推送消息", receiverId);
            } catch (Exception e) {
                log.error("通过WebSocket推送消息失败: {}", e.getMessage(), e);
                // 消息已保存到数据库，WebSocket推送失败不影响API返回
            }

            return AjaxResult.success(message);
        } catch (Exception e) {
            log.error("发送消息时发生错误: {}", e.getMessage(), e);
            return AjaxResult.error("发送消息失败: " + e.getMessage());
        }
    }

    /**
     * 获取消息列表
     */
    @GetMapping("/messages")
    @ApiOperation("获取消息列表")
    public AjaxResult getMessageList(@Validated MessageListRequestDTO requestDTO) {
        // 从DTO中获取userId
        Long userId = requestDTO.getUserId();
        List<MessageResponseDTO> messages = chatService.getMessageList(userId, requestDTO);
        return AjaxResult.success(messages);
    }

    /**
     * 标记消息已读
     */
    @PostMapping("/read")
    @ApiOperation("标记消息已读")
    public AjaxResult markAsRead(@Validated @RequestBody ReadMessageRequestDTO requestDTO) {
        if (requestDTO == null) {
            return AjaxResult.error("请求参数不能为空");
        }

        // 从DTO中获取userId
        Long userId = requestDTO.getUserId();
        if (userId == null) {
            return AjaxResult.error("用户ID不能为空");
        }

        Long conversationId = requestDTO.getConversationId();
        if (conversationId == null) {
            return AjaxResult.error("会话ID不能为空");
        }

        // 如果需要对方的用户ID，应该由前端直接传入，而不是在后端查询
        Long targetUserId = requestDTO.getTargetUserId();
        if (targetUserId == null) {
            return AjaxResult.error("对方用户ID不能为空");
        }

        try {
            // 调用服务标记消息已读
            log.info("用户{}标记会话{}的消息为已读", userId, conversationId);
            int count = chatService.markAsRead(userId, conversationId);

            if (count <= 0) {
                log.warn("用户{}标记会话{}的消息已读失败，可能没有未读消息或会话不存在", userId, conversationId);
                return AjaxResult.success().put("count", 0).put("message", "没有未读消息或会话不存在");
            }

            // 通过WebSocket通知发送者消息已读
            try {
                // 直接使用前端传入的targetUserId
                // 构建已读通知消息
                ChatMessage readNotification = new ChatMessage();
                readNotification.setType(2); // 通知消息
                readNotification.setSenderId(userId);
                readNotification.setReceiverId(targetUserId);
                readNotification.setConversationId(conversationId);
                readNotification.setContent("已读消息通知");
                readNotification.setContentType(1); // 系统通知类型
                readNotification.setSendTime(new Date());

                // 推送给对方
                ChatWebSocketServer.sendToUser(targetUserId, readNotification);
                log.info("已向用户{}发送会话{}的已读通知", targetUserId, conversationId);

                return AjaxResult.success()
                        .put("count", count)
                        .put("notified", true)
                        .put("message", "标记已读成功，并已通知发送者");

            } catch (Exception e) {
                log.error("通过WebSocket推送已读通知失败: {}", e.getMessage(), e);
                // 已读状态已更新到数据库，WebSocket推送失败不影响API返回
                return AjaxResult.success()
                        .put("count", count)
                        .put("notified", false)
                        .put("message", "标记已读成功，但通知发送者失败");
            }
        } catch (Exception e) {
            log.error("标记消息已读时发生错误: {}", e.getMessage(), e);
            return AjaxResult.error("标记消息已读失败: " + e.getMessage());
        }
    }
}