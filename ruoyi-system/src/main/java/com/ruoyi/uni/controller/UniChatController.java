package com.ruoyi.uni.controller;

import com.ruoyi.alse.service.IAlseChatMessageService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.uni.model.DTO.request.message.GetConversationRequestDTO;
import com.ruoyi.uni.model.DTO.request.message.MessageListRequestDTO;
import com.ruoyi.uni.model.DTO.request.message.ReadMessageRequestDTO;
import com.ruoyi.uni.model.DTO.request.message.SendMessageRequestDTO;
import com.ruoyi.uni.model.DTO.respone.message.ConversationResponseDTO;
import com.ruoyi.uni.model.DTO.respone.message.MessageResponseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聊天接口
 */
@RestController
@RequestMapping("/api/chat")
@Api(tags = "即时通讯接口")
@Slf4j
public class UniChatController {

    @Autowired
    private IAlseChatMessageService chatService;


    /**
     * 获取或创建聊天会话
     */
    @PostMapping("/conversation")
    @ApiOperation("获取或创建聊天会话")
    public AjaxResult getOrCreateConversation(@Validated @RequestBody GetConversationRequestDTO requestDTO) {
        Long userId = SecurityUtils.getUserId();
        ConversationResponseDTO conversation = chatService.getOrCreateConversation(userId, requestDTO.getTargetUserId());
        return AjaxResult.success(conversation);
    }

    /**
     * 获取会话列表
     */
    @GetMapping("/conversations")
    @ApiOperation("获取会话列表")
    public AjaxResult getConversationList() {
        Long userId = SecurityUtils.getUserId();
        List<ConversationResponseDTO> conversations = chatService.getConversationList(userId);
        return AjaxResult.success(conversations);
    }

    /**
     * 发送消息
     */
    @PostMapping("/message")
    @ApiOperation("发送消息")
    @Log(title = "发送聊天消息", businessType = BusinessType.OTHER)
    public AjaxResult sendMessage(@Validated @RequestBody SendMessageRequestDTO requestDTO) {
        Long userId = SecurityUtils.getUserId();
        MessageResponseDTO message = chatService.sendMessage(userId, requestDTO);
        return AjaxResult.success(message);
    }

    /**
     * 获取消息列表
     */
    @GetMapping("/messages")
    @ApiOperation("获取消息列表")
    public AjaxResult getMessageList(@Validated MessageListRequestDTO requestDTO) {
        Long userId = SecurityUtils.getUserId();
        List<MessageResponseDTO> messages = chatService.getMessageList(userId, requestDTO);
        return AjaxResult.success(messages);
    }

    /**
     * 标记消息已读
     */
    @PostMapping("/read")
    @ApiOperation("标记消息已读")
    public AjaxResult markAsRead(@Validated @RequestBody ReadMessageRequestDTO requestDTO) {
        Long userId = SecurityUtils.getUserId();
        int count = chatService.markAsRead(userId, requestDTO.getConversationId());
        return AjaxResult.success().put("count", count);
    }

}
