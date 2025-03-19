package com.ruoyi.alse.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 聊天消息对象 alse_chat_message
 * 
 * @author ruoyi
 * @date 2025-03-19
 */
public class AlseChatMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 消息ID */
    private Long messageId;

    /** 会话ID */
    @Excel(name = "会话ID")
    private Long conversationId;

    /** 发送者ID */
    @Excel(name = "发送者ID")
    private Long senderId;

    /** 接收者ID（单聊时） */
    @Excel(name = "接收者ID", readConverterExp = "单=聊时")
    private Long receiverId;

    /** 消息内容 */
    @Excel(name = "消息内容")
    private String content;

    /** 消息类型：0-文本，1-图片，2-语音，3-视频，4-文件 */
    @Excel(name = "消息类型：0-文本，1-图片，2-语音，3-视频，4-文件")
    private Integer contentType;

    /** 媒体文件URL */
    @Excel(name = "媒体文件URL")
    private String mediaUrl;

    /** 读取状态：0-未读，1-已读 */
    @Excel(name = "读取状态：0-未读，1-已读")
    private Integer readStatus;

    /** 发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发送时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date sendTime;

    /** 读取时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "读取时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date readTime;

    /** 状态（0正常 1撤回） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=撤回")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public void setMessageId(Long messageId) 
    {
        this.messageId = messageId;
    }

    public Long getMessageId() 
    {
        return messageId;
    }

    public void setConversationId(Long conversationId) 
    {
        this.conversationId = conversationId;
    }

    public Long getConversationId() 
    {
        return conversationId;
    }

    public void setSenderId(Long senderId) 
    {
        this.senderId = senderId;
    }

    public Long getSenderId() 
    {
        return senderId;
    }

    public void setReceiverId(Long receiverId) 
    {
        this.receiverId = receiverId;
    }

    public Long getReceiverId() 
    {
        return receiverId;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setContentType(Integer contentType) 
    {
        this.contentType = contentType;
    }

    public Integer getContentType() 
    {
        return contentType;
    }

    public void setMediaUrl(String mediaUrl) 
    {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaUrl() 
    {
        return mediaUrl;
    }

    public void setReadStatus(Integer readStatus) 
    {
        this.readStatus = readStatus;
    }

    public Integer getReadStatus() 
    {
        return readStatus;
    }

    public void setSendTime(Date sendTime) 
    {
        this.sendTime = sendTime;
    }

    public Date getSendTime() 
    {
        return sendTime;
    }

    public void setReadTime(Date readTime) 
    {
        this.readTime = readTime;
    }

    public Date getReadTime() 
    {
        return readTime;
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
            .append("messageId", getMessageId())
            .append("conversationId", getConversationId())
            .append("senderId", getSenderId())
            .append("receiverId", getReceiverId())
            .append("content", getContent())
            .append("contentType", getContentType())
            .append("mediaUrl", getMediaUrl())
            .append("readStatus", getReadStatus())
            .append("sendTime", getSendTime())
            .append("readTime", getReadTime())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
