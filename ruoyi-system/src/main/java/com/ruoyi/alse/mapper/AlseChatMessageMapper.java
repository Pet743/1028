package com.ruoyi.alse.mapper;

import java.util.List;
import java.util.Date;

import com.ruoyi.alse.domain.AlseChatConversation;
import com.ruoyi.alse.domain.AlseChatMessage;
import com.ruoyi.uni.model.DTO.request.message.MessageListRequestDTO;
import org.apache.ibatis.annotations.Param;

/**
 * 聊天消息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-03-19
 */
public interface AlseChatMessageMapper 
{



    /**
     * 查询会话消息列表
     *
     * @param requestDTO 查询条件
     * @return 消息列表
     */
    List<AlseChatMessage> selectMessageList(MessageListRequestDTO requestDTO);

    /**
     * 根据会话ID查询最新消息
     *
     * @param conversationId 会话ID
     * @return 消息列表
     */
    List<AlseChatMessage> selectLatestMessagesByConversationId(Long conversationId);

    /**
     * 根据会话ID和消息ID查询历史消息（ID小于给定值的消息）
     *
     * @param conversationId 会话ID
     * @param messageId 消息ID
     * @return
     */
    List<AlseChatMessage> selectMessagesByConversationIdAndBeforeId(@Param("conversationId") Long conversationId,
                                                                    @Param("messageId") Long messageId);

    /**
     * 查询聊天消息
     *
     * @param messageId 聊天消息主键
     * @return 聊天消息
     */
    public AlseChatMessage selectAlseChatMessageByMessageId(Long messageId);

    /**
     * 查询会话的聊天消息列表
     *
     * @param conversationId 会话ID
     * @param beforeMessageId 在此消息ID之前的消息
     * @param limit 限制数量
     * @return 聊天消息集合
     */
    public List<AlseChatMessage> selectAlseChatMessagesByConversationId(
            @Param("conversationId") Long conversationId,
            @Param("beforeMessageId") Long beforeMessageId,
            @Param("limit") Integer limit);

    /**
     * 查询聊天消息列表
     *
     * @param alseChatMessage 聊天消息
     * @return 聊天消息集合
     */
    public List<AlseChatMessage> selectAlseChatMessageList(AlseChatMessage alseChatMessage);

    /**
     * 新增聊天消息
     *
     * @param alseChatMessage 聊天消息
     * @return 结果
     */
    public int insertAlseChatMessage(AlseChatMessage alseChatMessage);

    /**
     * 修改聊天消息
     *
     * @param alseChatMessage 聊天消息
     * @return 结果
     */
    public int updateAlseChatMessage(AlseChatMessage alseChatMessage);

    /**
     * 批量更新消息的已读状态
     *
     * @param conversationId 会话ID
     * @param receiverId 接收者ID
     * @param readTime 读取时间
     * @return 结果
     */
    public int updateMessagesReadStatus(
            @Param("conversationId") Long conversationId,
            @Param("receiverId") Long receiverId,
            @Param("readTime") Date readTime);

    /**
     * 删除聊天消息
     *
     * @param messageId 聊天消息主键
     * @return 结果
     */
    public int deleteAlseChatMessageByMessageId(Long messageId);

    /**
     * 批量删除聊天消息
     *
     * @param messageIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAlseChatMessageByMessageIds(Long[] messageIds);
}
