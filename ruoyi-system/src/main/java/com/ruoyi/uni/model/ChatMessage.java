package com.ruoyi.uni.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * WebSocket消息对象
 */
@Data
public class ChatMessage {

    /**
     * 消息类型:
     * 1: 聊天消息
     * 2: 通知消息
     * 3: 系统消息
     * 4: 心跳消息
     * 5: 错误消息
     */
    private Integer type;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 内容类型:
     * 1: 文本
     * 2: 图片
     * 3: 语音
     * 4: 视频
     */
    private Integer contentType;

    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;
}