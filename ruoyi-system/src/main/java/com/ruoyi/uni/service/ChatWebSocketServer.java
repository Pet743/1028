package com.ruoyi.uni.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.uni.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务端
 */
@ServerEndpoint("/websocket/chat/{userId}")
@Component
@Slf4j
public class ChatWebSocketServer {

    // 存储用户会话，key为用户ID
    private static final Map<Long, ChatWebSocketServer> SESSIONS = new ConcurrentHashMap<>();

    // 当前连接会话
    private Session session;

    // 用户ID
    private Long userId;

    // Jackson对象映射器
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 建立连接时调用
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userIdStr) {
        log.info("收到WebSocket连接请求，userId: {}", userIdStr);

        try {
            // 直接从路径参数获取userId
            Long userId = Long.parseLong(userIdStr);

            this.session = session;
            this.userId = userId;

            // 如果已有相同userId的连接，先关闭旧连接
            ChatWebSocketServer oldSession = SESSIONS.get(userId);
            if (oldSession != null) {
                try {
                    oldSession.session.close();
                } catch (IOException e) {
                    log.error("关闭旧会话失败", e);
                }
            }

            // 存储会话
            SESSIONS.put(this.userId, this);

            log.info("用户{}连接成功，当前在线用户数: {}", this.userId, SESSIONS.size());

            // 发送连接成功消息
            ChatMessage connectMessage = new ChatMessage();
            connectMessage.setType(3); // 系统消息类型
            connectMessage.setContent("连接成功");
            sendMessage(connectMessage);

        } catch (NumberFormatException e) {
            log.error("无效的用户ID: {}", userIdStr);
            try {
                sendError("无效的用户ID");
                session.close();
            } catch (IOException ex) {
                log.error("关闭WebSocket连接失败", ex);
            }
        } catch (IOException e) {
            log.error("发送连接成功消息失败", e);
        }
    }

    /**
     * 关闭连接时调用
     */
    @OnClose
    public void onClose() {
        if (this.userId != null) {
            // 移除会话
            SESSIONS.remove(this.userId);
            log.info("用户{}断开连接，当前在线用户数: {}", this.userId, SESSIONS.size());
        }
    }

    /**
     * 收到客户端消息时调用
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        if (message == null || message.isEmpty()) {
            return;
        }

        log.info("收到用户{}的消息: {}", this.userId, message);

        try {
            // 解析消息
            ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);

            // 设置发送者ID
            chatMessage.setSenderId(this.userId);

            // 处理不同类型的消息
            switch (chatMessage.getType()) {
                case 1: // 聊天消息
                    processChat(chatMessage);
                    break;
                case 4: // 心跳消息
                    processHeartbeat(chatMessage);
                    break;
                default:
                    log.warn("未知消息类型: {}", chatMessage.getType());
            }
        } catch (Exception e) {
            log.error("处理WebSocket消息失败", e);
            try {
                sendError("消息格式错误");
            } catch (IOException ex) {
                log.error("发送错误消息失败", ex);
            }
        }
    }

    /**
     * 处理聊天消息
     */
    private void processChat(ChatMessage message) throws IOException {
        // 获取接收者的会话
        ChatWebSocketServer receiverSession = SESSIONS.get(message.getReceiverId());

        // 如果接收者在线，直接发送消息
        if (receiverSession != null) {
            receiverSession.sendMessage(message);
        }

        // 注意：这里应该调用聊天服务保存消息到数据库
        // 但由于我们没有提供相应的代码，这里只是记录一下
        log.info("消息需要保存到数据库: {}", message);
    }

    /**
     * 处理心跳消息
     */
    private void processHeartbeat(ChatMessage message) throws IOException {
        // 回复心跳消息
        ChatMessage heartbeatResponse = new ChatMessage();
        heartbeatResponse.setType(4);
        heartbeatResponse.setContent("pong");
        sendMessage(heartbeatResponse);
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket发生错误", error);
    }

    /**
     * 发送消息
     */
    public void sendMessage(ChatMessage message) throws IOException {
        if (this.session != null && this.session.isOpen()) {
            String messageText = objectMapper.writeValueAsString(message);
            this.session.getBasicRemote().sendText(messageText);
        }
    }

    /**
     * 发送错误消息
     */
    private void sendError(String errorMessage) throws IOException {
        ChatMessage error = new ChatMessage();
        error.setType(5); // 错误消息类型
        error.setContent(errorMessage);
        sendMessage(error);
    }

    /**
     * 向指定用户发送消息
     * @return 消息是否发送成功
     */
    public static boolean sendToUser(Long userId, ChatMessage message) {
        if (userId == null) {
            log.warn("无效的用户ID");
            return false;
        }

        ChatWebSocketServer session = SESSIONS.get(userId);
        if (session != null && session.session.isOpen()) {
            try {
                session.sendMessage(message);
                log.info("已成功向用户{}推送消息", userId);
                return true;
            } catch (IOException e) {
                log.error("向用户{}发送消息失败: {}", userId, e.getMessage(), e);
                return false;
            }
        } else {
            log.info("用户{}不在线，WebSocket消息未推送", userId);
            return false;
        }
    }

    /**
     * 广播消息给所有用户
     */
    public static void broadcast(ChatMessage message) {
        SESSIONS.values().forEach(session -> {
            try {
                session.sendMessage(message);
            } catch (IOException e) {
                log.error("广播消息失败", e);
            }
        });
    }
}