package com.ruoyi.alse.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 聊天会话对象 alse_chat_conversation
 * 
 * @author ruoyi
 * @date 2025-03-19
 */
public class AlseChatConversation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 会话ID */
    private Long conversationId;

    /** 会话类型：0-单聊，1-群聊 */
    @Excel(name = "会话类型：0-单聊，1-群聊")
    private Integer conversationType;

    /** 会话名称（群聊时有效） */
    @Excel(name = "会话名称", readConverterExp = "群=聊时有效")
    private String name;

    /** 用户1 ID（单聊时） */
    @Excel(name = "用户1 ID", readConverterExp = "单=聊时")
    private Long userId1;

    /** 用户2 ID（单聊时） */
    @Excel(name = "用户2 ID", readConverterExp = "单=聊时")
    private Long userId2;

    /** 最后一条消息内容 */
    @Excel(name = "最后一条消息内容")
    private String lastMessage;

    /** 最后一条消息时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最后一条消息时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastMessageTime;

    /** 用户1未读消息数 */
    @Excel(name = "用户1未读消息数")
    private Long unreadCount1;

    /** 用户2未读消息数 */
    @Excel(name = "用户2未读消息数")
    private Long unreadCount2;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public void setConversationId(Long conversationId) 
    {
        this.conversationId = conversationId;
    }

    public Long getConversationId() 
    {
        return conversationId;
    }

    public void setConversationType(Integer conversationType) 
    {
        this.conversationType = conversationType;
    }

    public Integer getConversationType() 
    {
        return conversationType;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setUserId1(Long userId1) 
    {
        this.userId1 = userId1;
    }

    public Long getUserId1() 
    {
        return userId1;
    }

    public void setUserId2(Long userId2) 
    {
        this.userId2 = userId2;
    }

    public Long getUserId2() 
    {
        return userId2;
    }

    public void setLastMessage(String lastMessage) 
    {
        this.lastMessage = lastMessage;
    }

    public String getLastMessage() 
    {
        return lastMessage;
    }

    public void setLastMessageTime(Date lastMessageTime) 
    {
        this.lastMessageTime = lastMessageTime;
    }

    public Date getLastMessageTime() 
    {
        return lastMessageTime;
    }

    public void setUnreadCount1(Long unreadCount1) 
    {
        this.unreadCount1 = unreadCount1;
    }

    public Long getUnreadCount1() 
    {
        return unreadCount1;
    }

    public void setUnreadCount2(Long unreadCount2) 
    {
        this.unreadCount2 = unreadCount2;
    }

    public Long getUnreadCount2() 
    {
        return unreadCount2;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("conversationId", getConversationId())
            .append("conversationType", getConversationType())
            .append("name", getName())
            .append("userId1", getUserId1())
            .append("userId2", getUserId2())
            .append("lastMessage", getLastMessage())
            .append("lastMessageTime", getLastMessageTime())
            .append("unreadCount1", getUnreadCount1())
            .append("unreadCount2", getUnreadCount2())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
