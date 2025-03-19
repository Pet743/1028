package com.ruoyi.uni.model.DTO.respone.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("会话响应DTO")
public class ConversationResponseDTO {

    @ApiModelProperty("会话ID")
    private Long conversationId;

    @ApiModelProperty("会话类型：0-单聊，1-群聊")
    private Integer conversationType;

    @ApiModelProperty("会话名称（群聊有效）")
    private String name;

    @ApiModelProperty("对方用户ID（单聊）")
    private Long targetUserId;

    @ApiModelProperty("对方用户名称（单聊）")
    private String targetUserName;

    @ApiModelProperty("对方用户头像（单聊）")
    private String targetUserAvatar;

    @ApiModelProperty("最后一条消息内容")
    private String lastMessage;

    @ApiModelProperty("最后一条消息类型")
    private Integer lastMessageType;

    @ApiModelProperty("最后一条消息时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastMessageTime;

    @ApiModelProperty("未读消息数")
    private Integer unreadCount;
}