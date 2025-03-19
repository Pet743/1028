/*
package com.ruoyi.uni.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.uni.model.DTO.respone.message.MessageResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint("/websocket/chat/{userId}")
@Component
@Slf4j
public class WebSocketServer {


    */
/**
     * 用户ID与会话映射
     *//*

    private static final ConcurrentHashMap<String, Session> userSessions = new ConcurrentHashMap<>();

    */
/**
     * 序列化工具
     *//*

    private static ObjectMapper objectMapper;

    */
/**
     * Redis工具
     *//*

    private static RedisCache redisCache;

    public void setObjectMapper(ObjectMapper objectMapper) {
        WebSocketServer.objectMapper = objectMapper;
    }

    @Autowired
    public void setRedisCache(RedisCache redisCache) {
        WebSocketServer.redisCache = redisCache;
    }
    */
/**
    连接建立成功调用的方法
     *//*

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        if (userId != null && !"".equals(userId)) {
            log.info("用户连接: {}, sessionId: {}", userId, session.getId());
            userSessions.put(userId, session);
        } else {
            log.warn("用户连接异常，无法获取userId");
        }
    }

    */
/*
     连接关闭调用的方法
     *//*

    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        if (userId != null && !"".equals(userId)) {
            log.info("用户断开连接: {}", userId);
            userSessions.remove(userId);
        }
    }

    */
/*
    收到客户端消息后调用的方法
     *//*

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userId") String userId) {
        if (!StringUtils.hasText(message)) {
            return;
        }
        log.info("收到用户{}的消息: {}", userId, message);
        // 这里一般是心跳消息，实际的消息发送通过REST API处理
        if ("heartbeat".equals(message)) {
            try {
                session.getBasicRemote().sendText("pong");
            } catch (IOException e) {
                log.error("发送心跳响应失败", e);
            }
        }
    }

    */
/**
     * 出现错误调用的方法
     *//*

    @OnError
    public void onError(Session session, Throwable error, @PathParam("userId") String userId) {
        log.error("WebSocket发生错误，用户: {}，错误信息: {}", userId, error.getMessage());
    }

    */
/**
     * 发送消息给指定用户
     *
     * @param userId  用户ID
     * @param message 消息对象
     *//*

    public static void sendMessage(String userId, MessageResponseDTO message) {
        if (userId == null || "".equals(userId) || message == null) {
            return;
        }
        Session session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String messageText = objectMapper.writeValueAsString(message);
                log.info("向用户{}发送消息: {}", userId, messageText);
                session.getBasicRemote().sendText(messageText);
            } catch (Exception e) {
                log.error("发送消息给用户{}失败: {}", userId, e.getMessage());
            }
        } else {
            log.info("用户{}不在线，消息将在用户上线后推送", userId);
            // 可以考虑将消息存入Redis，用户上线后推送
            cacheOfflineMessage(userId, message);
        }
    }

    */
/**
     * 缓存离线消息
     *//*

    private static void cacheOfflineMessage(String userId, MessageResponseDTO message) {
        if (redisCache == null) {
            log.warn("Redis缓存未初始化，无法缓存离线消息");
            return;
        }
        try {
            String key = "chat:offline:msg:" + userId;
            redisCache.setCacheList(key, message);
            // 设置过期时间，例如7天
            redisCache.expire(key, 7 * 24 * 60 * 60);
        } catch (Exception e) {
            log.error("缓存离线消息失败", e);
        }
    }

    */
/**
     * 推送离线消息
     *//*

    public static void pushOfflineMessages(String userId) {
        if (redisCache == null || !userSessions.containsKey(userId)) {
            return;
        }
        try {
            String key = "chat:offline:msg:" + userId;
            List<MessageResponseDTO> messages = redisCache.getCacheList(key);
            if (messages != null && !messages.isEmpty()) {
                for (MessageResponseDTO message : messages) {
                    sendMessage(userId, message);
                }
                // 清除已推送的离线消息
                redisCache.deleteObject(key);
            }
        } catch (Exception e) {
            log.error("推送离线消息失败", e);
        }
    }

    */
/**
     * *获取当前在线用户数
     *//*

    public static int getOnlineCount() {
        return userSessions.size();
    }

    */
/**
     * 判断用户是否在线
     *//*

    public static boolean isUserOnline(String userId) {
        return userSessions.containsKey(userId);
    }
}
*/
