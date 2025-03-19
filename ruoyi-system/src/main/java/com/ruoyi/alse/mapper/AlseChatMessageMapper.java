package com.ruoyi.alse.mapper;

import java.util.List;
import java.util.Date;
import com.ruoyi.alse.domain.AlseChatMessage;
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
