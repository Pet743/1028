package com.ruoyi.alse.mapper;

import java.util.List;
import java.util.Date;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.alse.domain.AlseChatConversation;

/**
 * 聊天会话Mapper接口
 * 
 * @author ruoyi
 * @date 2025-03-19
 */
public interface AlseChatConversationMapper 
{
    /**
     * 查询聊天会话
     *
     * @param conversationId 聊天会话主键
     * @return 聊天会话
     */
    public AlseChatConversation selectAlseChatConversationByConversationId(Long conversationId);

    /**
     * 查询用户相关的聊天会话
     *
     * @param userId 用户ID
     * @return 聊天会话集合
     */
    public List<AlseChatConversation> selectAlseChatConversationsByUserId(Long userId);

    /**
     * 查询两个用户之间的单聊会话
     *
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @return 聊天会话
     */
    public AlseChatConversation selectAlseChatConversationByUserIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

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
     * 更新会话最后一条消息信息
     *
     * @param conversationId 会话ID
     * @param lastMessage 最后一条消息内容
     * @param lastMessageTime 最后一条消息时间
     * @return 结果
     */
    public int updateLastMessage(@Param("conversationId") Long conversationId,
                                 @Param("lastMessage") String lastMessage,
                                 @Param("lastMessageTime") Date lastMessageTime);

    /**
     * 增加指定用户的未读消息数
     *
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 结果
     */
    public int increaseUnreadCount(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    /**
     * 清空指定用户的未读消息数
     *
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 结果
     */
    public int clearUnreadCount(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    /**
     * 删除聊天会话
     *
     * @param conversationId 聊天会话主键
     * @return 结果
     */
    public int deleteAlseChatConversationByConversationId(Long conversationId);

    /**
     * 批量删除聊天会话
     *
     * @param conversationIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAlseChatConversationByConversationIds(Long[] conversationIds);
}
